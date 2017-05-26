package com.revotechs.calculator;

import java.util.Date;

/**
 * Project Calculator
 * <p>
 * Created on 26.05.2017
 *
 * @author CriticalGnome
 */

class HistoryItem {

    private Date date;
    private String expression;
    private String result;

    HistoryItem(Date date, String expression, String result) {
        this.date = date;
        this.expression = expression;
        this.result = result;
    }

    public Date getDate() {
        return date;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

}
