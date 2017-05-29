package com.revotechs.calculator.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Project Calculator
 * <p>
 * Created on 26.05.2017
 *
 * @author CriticalGnome
 */

public class HistoryItem {

    private String date;
    private String expression;
    private String result;

    public HistoryItem(Date date, String expression, String result) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy, H:mm:ss", Locale.getDefault());
        this.date = sdf.format(date);
        this.expression = expression;
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

}
