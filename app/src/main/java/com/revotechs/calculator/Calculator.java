package com.revotechs.calculator;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Project Calculator
 *
 * Created on 26.05.2017
 * @author CriticalGnome
 */
public class Calculator {

    private static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {
        MAIN_MATH_OPERATIONS = new HashMap<>();
        MAIN_MATH_OPERATIONS.put("*", 1);
        MAIN_MATH_OPERATIONS.put("/", 1);
        MAIN_MATH_OPERATIONS.put("+", 2);
        MAIN_MATH_OPERATIONS.put("-", 2);
    }

    String calc(String expression) {
        if (TextUtils.isEmpty(expression)) {
            expression = "0";
        }
        String result;
        try {
            result = calculateExpression(expression).toString();
        } catch (NumberFormatException e) {
            return "Wrong format";
        } catch (ArithmeticException e) {
            return "Division by zero";
        }
        if (result.contains(".")) {
            for (int i = result.length(); i > 0; i--) {
                if (result.endsWith("0")) {
                    result = result.substring(0, i);
                }
            }
        }
        if (result.endsWith(".")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН. Символы скобок могут быть изменены.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression   выражение в инфиксной форме.
     * @param operations   операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     *                     Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     *                     основных математических операторов карта будет выглядеть так:
     *                     <pre>
     *                                              *   ->   1
     *                                              /   ->   1
     *                                              +   ->   2
     *                                              -   ->   2
     *                                         </pre>
     *                     Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @param leftBracket  открывающая скобка.
     * @param rightBracket закрывающая скобка.
     * @return преобразованное выражение в ОПН.
     */
    private static String sortingStation(String expression, Map<String, Integer> operations, String leftBracket,
                                         String rightBracket) {
        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");
        // Выходная строка, разбитая на "символы" - операции и операнды..
        List<String> out = new ArrayList<>();
        // Стек операций.
        Stack<String> stack = new Stack<>();

        // Удаление пробелов из выражения.
        expression = expression.replace(" ", "");

        // Множество "символов", не являющихся операндами (операции и скобки).
        Set<String> operationSymbols = new HashSet<>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        // Индекс, на котором закончился разбор строки на прошлой итерации.
        int index = 0;
        // Признак необходимости поиска следующего элемента.
        boolean findNext = true;
        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            // Поиск следующего оператора или скобки.
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            // Оператор не найден.
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                // Обработка операторов и скобок.
                // Открывающая скобка.
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                // Закрывающая скобка.
                else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }
                    }
                    stack.pop();
                }
                // Операция.
                else {
                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(stack.peek()))) {
                        out.add(stack.pop());
                    }
                    stack.push(nextOperation);
                }
                index = nextOperationIndex + nextOperation.length();
            }
        }
        // Добавление в выходную строку операндов после последнего операнда.
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }
        // Пробразование выходного списка к выходной строке.
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        StringBuilder result = new StringBuilder();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression выражение в инфиксной форме.
     * @param operations операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     *                   Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     *                   основных математических операторов карта будет выглядеть так:
     *                   <pre>
     *                                          *   ->   1
     *                                          /   ->   1
     *                                          +   ->   2
     *                                          -   ->   2
     *                                     </pre>
     *                   Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @return преобразованное выражение в ОПН.
     */
    private static String sortingStation(String expression, Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    /**
     * Вычисляет значение выражения, записанного в инфиксной нотации. Выражение может содержать скобки, числа с
     * плавающей точкой, четыре основных математических операндов.
     *
     * @param expression выражение.
     * @return результат вычисления.
     */
    private static BigDecimal calculateExpression(String expression) throws NumberFormatException {
        String rpn = sortingStation(expression, MAIN_MATH_OPERATIONS);
        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");
        Stack<BigDecimal> stack = new Stack<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            // Операнд.
            if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {
                stack.push(new BigDecimal(token));
            } else {
                BigDecimal operand2;
                operand2 = stack.pop().setScale(8, RoundingMode.HALF_EVEN);
                BigDecimal operand1 = stack.empty() ? BigDecimal.ZERO : stack.pop().setScale(8, RoundingMode.HALF_EVEN);
                switch (token) {
                    case "*":
                        stack.push(operand1.multiply(operand2));
                        break;
                    case "/":
                        if (operand2.equals(BigDecimal.valueOf(0))) {
                            throw new ArithmeticException("Division by zero");
                        }
                        stack.push(operand1.divide(operand2, RoundingMode.HALF_EVEN));
                        break;
                    case "+":
                        stack.push(operand1.add(operand2));
                        break;
                    case "-":
                        stack.push(operand1.subtract(operand2));
                        break;
                }
            }
        }
        if (stack.size() != 1)
            throw new IllegalArgumentException("Expression syntax error.");
        return stack.pop();
    }

}
