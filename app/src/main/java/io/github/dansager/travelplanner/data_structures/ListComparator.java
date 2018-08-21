package io.github.dansager.travelplanner.data_structures;

import java.util.Comparator;

public class ListComparator implements Comparator<Trip> {

    @Override
    public int compare(Trip a, Trip b) {
        if (a.getEndDate().getYear() < b.getEndDate().getYear()) {
            return -1;
        } else if (a.getEndDate().getYear() == b.getEndDate().getYear()) {
            if (a.getEndDate().getMonth() < b.getEndDate().getMonth()) {
                return -1;
            } else if (a.getEndDate().getMonth() == b.getEndDate().getMonth()) {
                if (a.getEndDate().getDay() <= b.getEndDate().getDay()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 1;
        } else {
            return 1;
        }
    }
}
