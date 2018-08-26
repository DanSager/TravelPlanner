package io.github.dansager.travelplanner.data_structures;

import org.joda.time.DateTime;
import org.joda.time.DateTime;

public class Trip {
    private String name = "";
    private DateTime startDate;
    private DateTime endDate;
    private int MoneySpent = 0;

    public Trip(String name,DateTime startDate,DateTime endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String setName (String s) {
        return name = s;
    }

    public int setMoneySpent (int i) {
        return MoneySpent = i;
    }

    public DateTime setStartDate (DateTime d) {
        return startDate = d;
    }
    public DateTime setEndDate (DateTime d) {
        return endDate = d;
    }

    public int getMoneySpent() {
        return MoneySpent;
    }

    public String getName () {
        return name;
    }

    public DateTime getStartDate () {
        return startDate;
    }
    public DateTime getEndDate () {
        return endDate;
    }
}
