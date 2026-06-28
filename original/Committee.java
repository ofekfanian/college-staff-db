package project4.ofekFanianAndTalOshri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Committee implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final List<Lecturer> members;  // משתמש ב-ArrayList
    private Lecturer chairman;
    private Degree memberDegree;  // דרגת תואר אחידה של כל החברים (לא כולל יו"ר)

    public Committee(String name, Lecturer chairman) throws InvalidChairmanException {
        this.name = name;
        this.members = new ArrayList<>();
        setChairman(chairman);
        this.memberDegree = null;  // טרם נוספו חברים
    }

    public String getName() {
        return name;
    }

    public Lecturer getChairman() {
        return chairman;
    }

    public List<Lecturer> getMembers() {
        return new ArrayList<>(members);  // מחזיר העתק למניעת שינוי חיצוני
    }

    public int getNumMembers() {
        return members.size();
    }

    public void setChairman(Lecturer lecturer) throws InvalidChairmanException {
        if (lecturer == null || lecturer.getDegree().ordinal() < Degree.DR.ordinal()) {
            throw new InvalidChairmanException("Chairman must have degree DR or higher.");
        }
        this.chairman = lecturer;
    }

    public void addMember(Lecturer lecturer) throws AlreadyCommitteeMemberException, InvalidDegreeException {
        if (lecturer == null) return;

        if (lecturer.equals(chairman)) {
            throw new InvalidDegreeException("Chairman cannot be a regular member.");
        }

        if (members.contains(lecturer)) {
            throw new AlreadyCommitteeMemberException("Lecturer is already a member of this committee.");
        }

        if (memberDegree == null) {
            memberDegree = lecturer.getDegree();
        } else if (!lecturer.getDegree().equals(memberDegree)) {
            throw new InvalidDegreeException("All committee members must have the same degree.");
        }

        members.add(lecturer);
        lecturer.addCommittee(this);
    }

    public void removeMember(Lecturer lecturer) {
        if (members.remove(lecturer)) {
            lecturer.removeCommittee(this);
            if (members.isEmpty()) {
                memberDegree = null;
            }
        }
    }

    public int getTotalArticlesByMembers() {
        int count = 0;
        for (Lecturer l : members) {
            if (l.getDegree().ordinal() >= Degree.DR.ordinal()) {
                count += l.getNumArticles();
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Committee)) return false;
        Committee other = (Committee) obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Committee Name: ").append(name)
                .append(", Chairman: ").append(chairman != null ? chairman.getName() : "None")
                .append(", Members: ");

        if (members.isEmpty()) {
            sb.append("None");
        } else {
            for (int i = 0; i < members.size(); i++) {
                sb.append(members.get(i).getName());
                if (i < members.size() - 1) sb.append(", ");
            }
        }

        return sb.toString();
    }
}
