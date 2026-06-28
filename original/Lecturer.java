package project4.ofekFanianAndTalOshri;

import java.io.Serializable; // 🟡 הוספה
import java.util.ArrayList;
import java.util.List;

public abstract class Lecturer implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String id;
    protected Degree degree;
    protected String degreeName;
    protected double salary;
    protected Department department;
    protected List<Committee> committees;

    public Lecturer(String name, String id, Degree degree, String degreeName, double salary) {
        this.name = name;
        this.id = id;
        this.degree = degree;
        this.degreeName = degreeName;
        this.salary = salary;
        this.department = null;
        this.committees = new ArrayList<>();
    }

    public String getName() { return name; }

    public String getId() { return id; }

    public Degree getDegree() { return degree; }

    public double getSalary() { return salary; }

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) {
        if (this.department != null) {
            System.out.println("Lecturer is already assigned to a department. Replacing assignment.");
        }
        this.department = department;
    }

    public void addCommittee(Committee committee) {
        if (committee == null || committees.contains(committee)) return;
        committees.add(committee);
    }

    public void removeCommittee(Committee committee) {
        if (committee == null) return;
        committees.remove(committee);
    }

    protected abstract int getNumArticles();

    public abstract int compareTo(Lecturer l2);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Lecturer)) return false;
        Lecturer other = (Lecturer) obj;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name)
                .append(", ID: ").append(id)
                .append(", Degree: ").append(degree)
                .append(" (").append(degreeName).append(")")
                .append(", Salary: ").append(salary);

        if (department != null) {
            sb.append(", Department: ").append(department.getName());
        } else {
            sb.append(", Department: None");
        }

        if (committees.isEmpty()) {
            sb.append(", Committees: None");
        } else {
            sb.append(", Committees: ");
            for (int i = 0; i < committees.size(); i++) {
                sb.append(committees.get(i).getName());
                if (i < committees.size() - 1) sb.append(", ");
            }
        }

        return sb.toString();
    }
}
