package io.github.dansager.travelplanner.data_structures;

public class DateInfo {
    private int month;
    private int day;
    private int year;
    
    public DateInfo(int month,int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public boolean setMonth (int i) {
        if (i > 0 && i < 13) {
            month = i;
            return true;
        }
        return false;
    }

    public boolean setDay (int i) {
        if (i > 0 && i < 32) {
            day = i;
            return true;
        }
        return false;
    }

    public boolean setYear (int i) {
        if (i > 1970 && i < 2100) {
            year = i;
            return true;
        }
        return false;
    }

    public int getMonth () {
        return month;
    }

    public int getDay () {
        return day;
    }

    public int getYear () {
        return year;
    }

    public String getMonthDayYear () {
        return month + "/" + day + "/" + year;
    }

    public String getDayMonthYear () {
        return day + "/" + month + "/" + year;
    }

    public boolean beforeDate (DateInfo a) {
        if (this.getYear() > a.getYear()) {
            return false;
        } else if (this.getMonth() > a.getMonth() && this.getYear() == a.getYear()) {
            return false;
        } else if (this.getDay() > a.getDay() && this.getMonth() == a.getMonth() && this.getYear() == a.getYear()) {
            return false;
        } else {
            return true;
        }
    }
}

