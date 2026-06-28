package project4.ofekFanianAndTalOshri;

import java.util.Comparator;

public class CommitteeMembersCountComparator implements Comparator<Committee> {
    @Override
    public int compare(Committee c1, Committee c2) {
        return Integer.compare(c1.getNumMembers(), c2.getNumMembers());
    }
}