package io.github.dansager.travelplanner.data_structures;

public class Trip {
    private String Name = "";
    private int MoneySpent = 0;
    private DateInfo startDate;
    private DateInfo endDate;

    public String setName (String s) {
        return Name = s;
    }

    public int setMoneySpent (int i) {
        return MoneySpent = i;
    }

    public DateInfo setStartDate (DateInfo d) {
        return startDate = d;
    }
    public DateInfo setEndDate (DateInfo d) {
        return endDate = d;
    }

    public int getMoneySpent() {
        return MoneySpent;
    }

    public String getName () {
        return Name;
    }

    public DateInfo getStartDate () {
        return startDate;
    }
    public DateInfo getEndDate () {
        return endDate;
    }
}
