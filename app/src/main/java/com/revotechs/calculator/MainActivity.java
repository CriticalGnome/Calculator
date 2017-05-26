package com.revotechs.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Project Calculator
 *
 * Created on 26.05.2017
 * @author CriticalGnome
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String NULL_VALUE = "";
    private static String expression = NULL_VALUE;
    private static List<HistoryItem> history = new ArrayList<>();
    private static boolean comma;
    private Calculator calculator = new Calculator();

    TextView resultView;
    TextView currentView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button buttonAdd;
    Button buttonSub;
    Button buttonMul;
    Button buttonDiv;
    Button buttonComma;
    Button buttonReset;
    Button buttonEqual;
    Button buttonBack;
    Button buttonLeftBrace;
    Button buttonRightBrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFields();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initAdditionalFields();
        }

        resultView.setText(String.valueOf(calculator.calc(expression)));
        currentView.setText(String.valueOf(expression));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_reset:
                expression = NULL_VALUE;
                comma = false;
                break;
            case R.id.button_back:
                if (!expression.isEmpty()) {
                    expression = expression.substring(0, expression.length()-1);
                }
                break;
            case R.id.button_0:
                expression = expression.concat("0");
                break;
            case R.id.button_1:
                expression = expression.concat("1");
                break;
            case R.id.button_2:
                expression = expression.concat("2");
                break;
            case R.id.button_3:
                expression = expression.concat("3");
                break;
            case R.id.button_4:
                expression = expression.concat("4");
                break;
            case R.id.button_5:
                expression = expression.concat("5");
                break;
            case R.id.button_6:
                expression = expression.concat("6");
                break;
            case R.id.button_7:
                expression = expression.concat("7");
                break;
            case R.id.button_8:
                expression = expression.concat("8");
                break;
            case R.id.button_9:
                expression = expression.concat("9");
                break;
            case R.id.button_left_brace:
                expression = expression.concat("(");
                break;
            case R.id.button_right_brace:
                expression = expression.concat(")");
                break;
            case R.id.button_comma:
                if (!comma) {
                    expression = expression.concat(".");
                    comma = true;
                }
                break;
            case R.id.button_add:
                comma = false;
                expression = expression.concat("+");
                break;
            case R.id.button_sub:
                comma = false;
                expression = expression.concat("-");
                break;
            case R.id.button_mul:
                comma = false;
                expression = expression.concat("*");
                break;
            case R.id.button_div:
                comma = false;
                expression = expression.concat("/");
                break;
            case R.id.button_equal:
                comma = false;
                history.add(new HistoryItem(new Date(), expression, calculator.calc(expression)));
                expression = calculator.calc(expression);
                if (expression.contains(".")) {
                    comma = true;
                }
                break;
            case R.id.current_vew:
                Intent i = new Intent(this, HistoryActivity.class);
                i.putExtra("history", history.toArray());
                startActivity(i);
                break;
            default:
                break;

        }

        resultView.setText(String.valueOf(calculator.calc(expression)));
        currentView.setText(String.valueOf(expression));

    }

    private void initFields() {
        resultView = (TextView) findViewById(R.id.result_view);
        currentView = (TextView) findViewById(R.id.current_vew);
        button0 = (Button) findViewById(R.id.button_0);
        button1 = (Button) findViewById(R.id.button_1);
        button2 = (Button) findViewById(R.id.button_2);
        button3 = (Button) findViewById(R.id.button_3);
        button4 = (Button) findViewById(R.id.button_4);
        button5 = (Button) findViewById(R.id.button_5);
        button6 = (Button) findViewById(R.id.button_6);
        button7 = (Button) findViewById(R.id.button_7);
        button8 = (Button) findViewById(R.id.button_8);
        button9 = (Button) findViewById(R.id.button_9);
        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonSub = (Button) findViewById(R.id.button_sub);
        buttonMul = (Button) findViewById(R.id.button_mul);
        buttonDiv = (Button) findViewById(R.id.button_div);
        buttonComma = (Button) findViewById(R.id.button_comma);
        buttonReset = (Button) findViewById(R.id.button_reset);
        buttonEqual = (Button) findViewById(R.id.button_equal);
        buttonBack = (Button) findViewById(R.id.button_back);

        currentView.setOnClickListener(this);
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonSub.setOnClickListener(this);
        buttonMul.setOnClickListener(this);
        buttonDiv.setOnClickListener(this);
        buttonComma.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonEqual.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
    }

    private void initAdditionalFields() {
        buttonLeftBrace = (Button) findViewById(R.id.button_left_brace);
        buttonRightBrace = (Button) findViewById(R.id.button_right_brace);

        buttonLeftBrace.setOnClickListener(this);
        buttonRightBrace.setOnClickListener(this);
    }
}
