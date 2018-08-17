package io.github.dansager.travelplanner;

import java.util.Date;

public class Trip {
    private String Name;
    private int MoneySpent = 0;
    private Date startDate;

    public String setName (String s) {
        return Name = s;
    }

    public int setMoneySpent (int i) {
        return MoneySpent = i;
    }

    public Date setStartDate (Date d) {
        return startDate = d;
    }

    public int getMoneySpent() {
        return MoneySpent;
    }

    public String getName () {
        return Name;
    }

    public Date getStartDate () {
        return startDate;
    }
}
