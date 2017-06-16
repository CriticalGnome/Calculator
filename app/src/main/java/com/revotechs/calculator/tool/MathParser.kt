package com.revotechs.calculator.tool

import java.util.HashMap

/**

 * Класс предназначен для интерпретирования арифметических выражений методом рекурсивного спуска.
 * Метод рекурсивного спуска для интерпретирования математических выражений.
 * Поддерживаются тригонометрические функции с одним/двумя параметрами.

 * **Поддерживаемые операции:**

 * Побитовое И            &
 * Побитовое ИЛИ          |
 * Побитовое НЕ           ~
 * Сложение               +
 * Вычитание              -
 * Умножение              *
 * Деление                /
 * Остаток от деления     %
 * Целочисленное деление  \
 * Возведение в степень   ^

 * **Поддерживаемые функции:**

 * sin(x)   - синус
 * cos(x)   - косинус
 * tan(x)   - тангенс
 * ctg(x)   - котангенс
 * sinh(x)  - гиперболический синус
 * cosh(x)  - гиперболический косинус
 * tanh(x)  - гиперболический тангенс
 * sec(x)   - секанс
 * cosec(x) - косеканс
 * abs(x)   - модуль числа
 * sqrt(x)  - квадратный корень
 * ln(x)    - натуральный логарифм
 * lg(x)    - десятичный логарифм
 * log(x,y) - логарифм x по основанию y
 * xor(x,y) - побитовое исключающее ИЛИ

 * Класс модифицирован автором ShamaN.

 * @author shurik
 * *
 * @version 1.1
 */
class MathParser {
    init {
        `var` = HashMap<String, Double>()
        setVariable("pi", Math.PI)
        setVariable("e", Math.E)
    }

    //    /**
    //     * Заменяет значение существующей переменной
    //     * @param varName имя переменной
    //     * @param varValue значение переменной
    //     */
    //    public void replaceVariable(String varName, Double varValue) {
    //        var.replace(varName, varValue);
    //    }

    /**

     * @param varName переменная
     * *
     * @return Возвращает значение переменной varName
     * *
     * @throws Exception ругаемся на отсутствие переменной
     */
    @Throws(Exception::class)
    private fun getVariable(varName: String): Double? {
        if (!`var`.containsKey(varName)) {
            throw Exception("Error:Try get unexists " +
                    "variable '" + varName + "'")
        }
        return `var`[varName]
    }

    /**
     * Парсим математическое выражение
     * @param s математическое выражение
     * *
     * @return результат
     */
    fun parse(s: String): Double {
        val result: Result
        try {
            result = binaryFunc(s)
        } catch (e: Exception) {
            return 0.0
        }

        if (!result.rest.isEmpty())
            return 0.0
        return result.acc
    }


    @Throws(Exception::class)
    private fun binaryFunc(s: String): Result {

        var cur: Result

        if (s[0] == '~') {
            cur = plusMinus(s.substring(1))

            cur.acc = cur.acc.toInt().inv().toDouble()
            return cur
        }

        cur = plusMinus(s)
        var acc = cur.acc

        cur.rest = skipSpaces(cur.rest)

        while (cur.rest.isNotEmpty()) {
            if (!(cur.rest[0] == '&' ||
                    cur.rest[0] == '|' ||
                    cur.rest[0] == '~'))
                break

            val sign = cur.rest[0]
            val next = cur.rest.substring(1)
            cur = plusMinus(next)


            if (sign == '&')
                acc = (acc.toInt() and cur.acc.toInt()).toDouble()
            else
                acc = (acc.toInt() or cur.acc.toInt()).toDouble()
        }

        return Result(acc, cur.rest)

    }

    @Throws(Exception::class)
    private fun plusMinus(s: String): Result {

        var cur = mulDiv(s)
        var acc = cur.acc

        cur.rest = skipSpaces(cur.rest)

        while (cur.rest.isNotEmpty()) {
            if (!(cur.rest[0] == '+' || cur.rest[0] == '-'))
                break

            val sign = cur.rest[0]
            val next = cur.rest.substring(1)

            cur = binaryFunc(next)

            if (sign == '+')
                acc += cur.acc
            else
                acc -= cur.acc
        }
        return Result(acc, cur.rest)
    }


    @Throws(Exception::class)
    private fun mulDiv(s: String): Result {
        var cur = exponentiation(s)
        var acc = cur.acc

        cur.rest = skipSpaces(cur.rest)


        while (true) {
            if (cur.rest.isEmpty())
                return cur

            val sign = cur.rest[0]
            if (sign != '*' && sign != '/' && sign != '%' && sign != '\\')
                return cur

            val next = cur.rest.substring(1)
            val right = exponentiation(next)
            when (sign) {
                '*' -> acc *= right.acc
                '/' -> acc /= right.acc
                '%' // остаток от деления
                -> acc %= right.acc
                '\\' // целочисленное деление
                -> acc = (acc - acc % right.acc) / right.acc
            }
            cur = Result(acc, right.rest)
        }
    }


    @Throws(Exception::class)
    private fun exponentiation(s: String): Result {
        var cur = bracket(s)
        val acc = cur.acc

        cur.rest = skipSpaces(cur.rest)

        while (true) {

            if (cur.rest.isEmpty()) return cur
            if (cur.rest[0] != '^') break

            val next = cur.rest.substring(1)
            cur = bracket(next)
            cur.acc = Math.pow(acc, cur.acc)
        }
        return cur
    }


    @Throws(Exception::class)
    private fun bracket(str: String): Result {
        var s = str

        s = skipSpaces(s)
        val zeroChar = s[0]
        if (zeroChar == '(') {
            val r = binaryFunc(s.substring(1))
            if (!r.rest.isEmpty()) {
                r.rest = r.rest.substring(1)
            } else {
                throw Exception("Expected closing bracket")
            }
            return r
        }
        return functionVariable(s)
    }

    @Throws(Exception::class)
    private fun functionVariable(s: String): Result {
        var f = ""
        var i = 0
        // ищем название функции или переменной
        // имя обязательно должна начинаться с буквы
        while (i < s.length && (Character.isLetter(s[i]) || Character.isDigit(s[i]) && i > 0)) {
            f += s[i]
            i++
        }
        if (!f.isEmpty()) { // если что-нибудь нашли
            if (s.length > i && s[i] == '(') {
                // и следующий символ скобка значит - это функция
                var r = binaryFunc(s.substring(f.length + 1))

                if (!r.rest.isEmpty() && r.rest[0] == ',') {
                    // если функция с двумя параметрами
                    val acc = r.acc
                    var r2 = binaryFunc(r.rest.substring(1))

                    r2 = closeBracket(r2)
                    return processFunction(f, acc, r2)

                } else {
                    r = closeBracket(r)
                    return processFunction(f, r)
                }
            } else { // иначе - это переменная
                return Result(getVariable(f)!!, s.substring(f.length))
            }
        }
        return num(s)
    }

    @Throws(Exception::class)
    private fun closeBracket(r: Result): Result {
        if (!r.rest.isEmpty() && r.rest[0] == ')') {
            r.rest = r.rest.substring(1)
        } else
            throw Exception("Expected closing bracket")
        return r
    }

    @Throws(Exception::class)
    private fun num(str: String): Result {
        var s = str
        var i = 0
        var dot_cnt = 0
        var negative = false
        // число также может начинаться с минуса
        if (s[0] == '-') {
            negative = true
            s = s.substring(1)
        }
        // разрешаем только цифры и точку
        while (i < s.length && (Character.isDigit(s[i]) || s[i] == '.')) {
            // но также проверяем, что в числе может быть только одна точка!
            if (s[i] == '.' && ++dot_cnt > 1) {
                throw Exception("not valid number '"
                        + s.substring(0, i + 1) + "'")
            }
            i++
        }
        if (i == 0) { // что-либо похожее на число мы не нашли
            throw Exception("can't get valid number in '$s'")
        }

        var dPart = java.lang.Double.parseDouble(s.substring(0, i))
        if (negative) dPart = -dPart
        val restPart = s.substring(i)

        return Result(dPart, restPart)
    }

    @Throws(Exception::class)
    private fun processFunction(func: String, r: Result): Result {
        when (func) {
            "sin" -> return Result(Math.sin(r.acc), r.rest)
            "sinh" // гиперболический синус
            -> return Result(Math.sinh(r.acc), r.rest)
            "cos" -> return Result(Math.cos(r.acc), r.rest)
            "cosh" // гиперболический косинус
            -> return Result(Math.cosh(r.acc), r.rest)
            "tan" -> return Result(Math.tan(r.acc), r.rest)
            "tanh" // гиперболический тангенс
            -> return Result(Math.tanh(r.acc), r.rest)
            "ctg" -> return Result(1 / Math.tan(r.acc), r.rest)
            "sec" // секанс
            -> return Result(1 / Math.cos(r.acc), r.rest)
            "cosec" // косеканс
            -> return Result(1 / Math.sin(r.acc), r.rest)
            "abs" -> return Result(Math.abs(r.acc), r.rest)
            "ln" -> return Result(Math.log(r.acc), r.rest)
            "lg" // десятичный логарифм
            -> return Result(Math.log10(r.acc), r.rest)
            "sqrt" -> return Result(Math.sqrt(r.acc), r.rest)
            else -> throw Exception("function '$func' is not defined")
        }
    }

    @Throws(Exception::class)
    private fun processFunction(func: String,
                                acc: Double,
                                r: Result): Result {
        when (func) {
            "log" // логарифм x по основанию y
            -> return Result(Math.log(acc) / Math.log(r.acc),
                    r.rest)
            "xor" // исключающее или
            -> return Result((acc.toInt() xor r.acc.toInt()).toDouble(), r.rest)
            else -> throw Exception("function '" + func +
                    "' is not defined")
        }
    }

    private fun skipSpaces(s: String): String {
        return s.trim { it <= ' ' }
    }


    private inner class Result internal constructor(internal var acc: Double // Аккумулятор
                                                    , internal var rest: String // остаток строки, которую мы еще не обработали
    )

    companion object {
        private var `var` = HashMap<String, Double>()


        /**
         * Вставить новую переменную
         * @param varName имя переменной
         * *
         * @param varValue значение переменной
         */
        private fun setVariable(varName: String, varValue: Double) {
            `var`.put(varName, varValue)
        }
    }
}
