package com.revotechs.calculator.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Calculator
 * <p>
 * Created on 29.05.2017
 *
 * @author CriticalGnome
 */

public class HistoryKeeper {

    private static List<HistoryItem> items = new ArrayList<>();

    public static void addItem(HistoryItem item) {
        items.add(item);
    }

    public static List<HistoryItem> getList() {
        return items;
    }

}
