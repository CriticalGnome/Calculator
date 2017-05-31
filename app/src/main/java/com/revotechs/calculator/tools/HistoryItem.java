package com.revotechs.calculator.tools;

import java.io.Serializable;
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

public class HistoryItem implements Serializable {

    private Long id;
    private String date;
    private String expression;
    private String result;
    private String comment;

    public HistoryItem(Date date, String expression, String result) {
        this.date = new SimpleDateFormat("EEEE, dd MMMM yyyy, H:mm:ss", Locale.getDefault()).format(date);
        this.expression = expression;
        this.result = result;
    }

    public HistoryItem(Date date, String expression, String result, String comment) {
        this.date = new SimpleDateFormat("EEEE, dd MMMM yyyy, H:mm:ss", Locale.getDefault()).format(date);
        this.expression = expression;
        this.result = result;
        this.comment = comment;
    }

    public HistoryItem(Long id, Date date, String expression, String result) {
        this.id = id;
        this.date = new SimpleDateFormat("EEEE, dd MMMM yyyy, H:mm:ss", Locale.getDefault()).format(date);
        this.expression = expression;
        this.result = result;
    }

    public HistoryItem(Long id, String date, String expression, String result) {
        this.id = id;
        this.date = date;
        this.expression = expression;
        this.result = result;
    }

    public HistoryItem() {}

    @Override
    public String toString() {
        return "HistoryItem{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", expression='" + expression + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = new SimpleDateFormat("EEEE, dd MMMM yyyy, H:mm:ss", Locale.getDefault()).format(date);
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
