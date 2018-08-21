package io.github.dansager.travelplanner.data_structures;

public class DateInfo {
    private int Month;
    private int Day;
    private int Year;

    public boolean setMonth (int i) {
        if (i > 0 && i < 13) {
            Month = i;
            return true;
        }
        return false;
    }

    public boolean setDay (int i) {
        if (i > 0 && i < 32) {
            Day = i;
            return true;
        }
        return false;
    }

    public boolean setYear (int i) {
        if (i > 1970 && i < 2100) {
            Year = i;
            return true;
        }
        return false;
    }

    public int getMonth () {
        return Month;
    }

    public int getDay () {
        return Day;
    }

    public int getYear () {
        return Year;
    }

    public String getMonthDayYear () {
        return Month + "/" + Day + "/" + Year;
    }

    public String getDayMonthYear () {
        return Day + "/" + Month + "/" + Year;
    }

    public boolean beforeDate (DateInfo a) {
        if (this.getYear() > a.getYear()) {
            return false;
        } else if (this.getMonth() > a.getMonth()) {
            return false;
        } else if (this.getDay() > a.getDay()) {
            return false;
        } else {
            return true;
        }
    }
}

