package io.github.dansager.travelplanner.data_structures;

public class Trip {
    private String name = "";
    private DateInfo startDate;
    private DateInfo endDate;
    private int MoneySpent = 0;

    public Trip(String name,DateInfo startDate,DateInfo endDate) {
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
        return name;
    }

    public DateInfo getStartDate () {
        return startDate;
    }
    public DateInfo getEndDate () {
        return endDate;
    }
}
