package project4.ofekFanianAndTalOshri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final int numStudents;
    private final List<Lecturer> lecturers;

    public Department(String name, int numStudents) {
        this.name = name;
        this.numStudents = numStudents;
        this.lecturers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public List<Lecturer> getLecturers() {
        return new ArrayList<>(lecturers); // מחזיר העתק כדי לא לאפשר שינוי חיצוני
    }

    public void addLecturer(Lecturer lecturer) {
        if (lecturer == null || lecturers.contains(lecturer))
            return;
        lecturers.add(lecturer);
        lecturer.setDepartment(this); // עדכון שייכות המרצה למחלקה
    }

    public void removeLecturer(Lecturer lecturer) {
        if (lecturer == null)
            return;
        if (lecturers.remove(lecturer)) {
            lecturer.setDepartment(null); // הסרה הדדית של שיוך למחלקה
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Department)) return false;
        Department other = (Department) obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Department Name: ").append(name)
                .append(", Number of Students: ").append(numStudents)
                .append(", Lecturers: ");

        if (lecturers.isEmpty()) {
            sb.append("None");
        } else {
            for (int i = 0; i < lecturers.size(); i++) {
                sb.append(lecturers.get(i).getName());
                if (i < lecturers.size() - 1)
                    sb.append(", ");
            }
        }
        return sb.toString();
    }
}
