package io.github.dansager.travelplanner.data_structures;

import org.joda.time.DateTime;

public class Expense {
    private String name = "";
    private String type = "";
    private String typeSpecific = "";
    private String currency = "";
    private int cost = 0;
    private DateTime startDate;
    private DateTime endDate;

    public Expense(String name,String type,String currency,int cost,DateTime startDate) {
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.cost = cost;
        this.startDate = startDate;
    }

    public String setName (String s) {
        return name = s;
    }

    public String setType (String s) {
        return type = s;
    }

    public String setTypeSpecific (String s) { return typeSpecific = s; }

    public DateTime setStartDate (DateTime d) {
        return startDate = d;
    }

    public DateTime setEndDate (DateTime d) {
        return endDate = d;
    }

    public String setCurrency (String s) {
        return currency = s;
    }

    public int setCost (int c) {
        return cost = c;
    }

    public String getName () { return name; }

    public String getType () { return type; }

    public String getTypeSpecific () { return typeSpecific; }

    public DateTime getStartDate () {
        return startDate;
    }

    public DateTime getEndDate () {
        return endDate;
    }

    public String getCurrency () { return currency; }

    public int getCost () {
        return cost;
    }
}
