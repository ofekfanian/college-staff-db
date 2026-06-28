package project4.ofekFanianAndTalOshri;

import java.util.Comparator;

public class CommitteeArticlesCountComparator implements Comparator<Committee> {
    @Override
    public int compare(Committee c1, Committee c2) {
        return Integer.compare(c1.getTotalArticlesByMembers(), c2.getTotalArticlesByMembers());
    }
}