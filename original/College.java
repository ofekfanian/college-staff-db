package project4.ofekFanianAndTalOshri;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class College implements Serializable {
    private static final long serialVersionUID = 1L; // המלצה לסדרות בינאריות
    private String name;
    private final List<Lecturer> lecturers;
    private final List<Department> departments;
    private final List<Committee> committees;

    public College() {
        lecturers = new ArrayList<>();
        departments = new ArrayList<>();
        committees = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Lecturer> getLecturers() {
        return lecturers;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public List<Committee> getCommittees() {
        return committees;
    }

    public String getWelcomeMessage() {
        return "Welcome to " + name + " Staff Management System";
    }

    public boolean addLecturer(Lecturer lecturer) {
        if (lecturer == null) return false;
        for (Lecturer l : lecturers) {
            if (l.getName().equalsIgnoreCase(lecturer.getName()))
                throw new IllegalArgumentException("Lecturer already exists.");
        }
        lecturers.add(lecturer);
        return true;
    }

    public void addCommittee(Committee committee) throws CommitteeAddException {
        if (committee == null)
            throw new CommitteeAddException("Committee is null.");
        if (committees.contains(committee))
            throw new CommitteeAddException("Committee with this name already exists.");
        committees.add(committee);
    }

    public void addMemberToCommittee(String committeeName, String lecturerName) throws AlreadyCommitteeMemberException, InvalidDegreeException, Exception {
        Committee committee = findCommitteeByName(committeeName);
        if (committee == null)
            throw new Exception("Committee not found.");
        Lecturer lecturer = findLecturerByName(lecturerName);
        if (lecturer == null)
            throw new Exception("Lecturer not found.");
        committee.addMember(lecturer);
    }

    public void updateCommitteeChairman(String committeeName, String newChairmanName) throws Exception {
        Committee committee = findCommitteeByName(committeeName);
        if (committee == null)
            throw new Exception("Committee not found.");
        Lecturer newChairman = findLecturerByName(newChairmanName);
        if (newChairman == null)
            throw new Exception("Lecturer not found.");
        committee.removeMember(newChairman);
        committee.setChairman(newChairman);
    }

    public void removeMemberFromCommittee(String committeeName, String lecturerName) throws Exception {
        Committee committee = findCommitteeByName(committeeName);
        if (committee == null)
            throw new Exception("Committee not found.");
        Lecturer lecturer = findLecturerByName(lecturerName);
        if (lecturer == null)
            throw new Exception("Lecturer not found.");
        committee.removeMember(lecturer);
    }

    public void addDepartment(Department department) {
        if (department == null) return;
        for (Department d : departments) {
            if (d.getName().equalsIgnoreCase(department.getName()))
                throw new IllegalArgumentException("Department already exists.");
        }
        departments.add(department);
    }

    public double getAvgSalaryAll() throws Exception {
        if (lecturers.isEmpty())
            throw new Exception("No lecturers to calculate.");
        double total = 0;
        for (Lecturer l : lecturers)
            total += l.getSalary();
        return total / lecturers.size();
    }

    public double getAvgSalaryByDepartment(String name) throws Exception {
        Department department = findDepartmentByName(name);
        if (department == null)
            throw new Exception("Department not found.");
        List<Lecturer> deptLecturers = department.getLecturers();
        if (deptLecturers.isEmpty())
            throw new Exception("No lecturers in this department.");
        double total = 0;
        for (Lecturer l : deptLecturers)
            total += l.getSalary();
        return total / deptLecturers.size();
    }

    public List<Lecturer> getAllLecturers() {
        return new ArrayList<>(lecturers);
    }

    public List<Committee> getAllCommittees() {
        return new ArrayList<>(committees);
    }

    public void assignLecturerToDepartment(String lecturerName, String departmentName) throws Exception {
        Lecturer lecturer = findLecturerByName(lecturerName);
        if (lecturer == null)
            throw new Exception("Lecturer not found.");
        Department department = findDepartmentByName(departmentName);
        if (department == null)
            throw new Exception("Department not found.");
        Department currentDept = lecturer.getDepartment();
        if (currentDept != null && currentDept.getName().equalsIgnoreCase(department.getName()))
            throw new Exception("Lecturer is already assigned to this department.");
        department.addLecturer(lecturer);
    }

    public void checkDegree(Lecturer lecturer) throws CheckDegreeException {
        if (lecturer == null || lecturer.getDegree().ordinal() < Degree.DR.ordinal()) {
            throw new CheckDegreeException("Lecturer must have degree DR or higher.");
        }
    }

    public String compareResearchersByArticles(String name1, String name2) throws Exception {
        Lecturer l1 = findLecturerByName(name1);
        Lecturer l2 = findLecturerByName(name2);
        if (l1 == null || l2 == null)
            throw new Exception("One or both lecturers not found.");
        checkDegree(l1);
        checkDegree(l2);
        int result = l1.compareTo(l2);
        if (result > 0)
            return l1.getName() + " wrote more articles.";
        else if (result < 0)
            return l2.getName() + " wrote more articles.";
        else
            return "Both have the same number of articles.";
    }

    public String compareCommitteesByCriteria(String name1, String name2, int choice) throws Exception {
        Committee c1 = findCommitteeByName(name1);
        Committee c2 = findCommitteeByName(name2);
        if (c1 == null || c2 == null)
            throw new Exception("One or both committees not found.");
        if (choice == 1) {
            CommitteeMembersCountComparator comp = new CommitteeMembersCountComparator();
            int result = comp.compare(c1, c2);
            if (result > 0)
                return c1.getName() + " has more members.";
            else if (result < 0)
                return c2.getName() + " has more members.";
            else
                return "Committees have the same number of members.";
        } else if (choice == 2) {
            CommitteeArticlesCountComparator comp = new CommitteeArticlesCountComparator();
            int result = comp.compare(c1, c2);
            if (result > 0)
                return c1.getName() + " has more total articles.";
            else if (result < 0)
                return c2.getName() + " has more total articles.";
            else
                return "Committees have the same number of articles.";
        } else {
            throw new Exception("Invalid choice.");
        }
    }

    public Committee cloneCommittee(String name) throws Exception {
        Committee c = findCommitteeByName(name);
        if (c == null)
            throw new Exception("Committee not found.");
        String newName = "new-" + c.getName();
        Lecturer chairman = c.getChairman();
        Committee cloned = new Committee(newName, chairman);
        for (Lecturer m : c.getMembers()) {
            try {
                cloned.addMember(m);
            } catch (AlreadyCommitteeMemberException ignored) {
            }
        }
        addCommittee(cloned);
        return cloned;
    }

    private Lecturer findLecturerByName(String name) {
        for (Lecturer l : lecturers) {
            if (l.getName().equalsIgnoreCase(name))
                return l;
        }
        return null;
    }

    private Committee findCommitteeByName(String name) {
        for (Committee c : committees) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }

    private Department findDepartmentByName(String name) {
        for (Department d : departments) {
            if (d.getName().equalsIgnoreCase(name))
                return d;
        }
        return null;
    }

    //  הוספות לשמירה וטעינה

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static College loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (College) in.readObject();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        College other = (College) obj;
        return name != null && name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "College Name: " + name + "\n" +
                "Total Lecturers: " + lecturers.size() + "\n" +
                "Total Departments: " + departments.size() + "\n" +
                "Total Committees: " + committees.size();
    }
}
