package project4.ofekFanianAndTalOshri;

import java.io.Serializable;

public class Professor extends Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String institutionName;

    public Professor(String name, String id, Degree degree, String degreeName, double salary, String institution) {
        super(name, id, degree, degreeName, salary);
        this.institutionName = institution;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Professor)) return false;
        Professor other = (Professor) obj;
        return super.equals(other) && this.institutionName.equals(other.institutionName);
    }

    @Override
    public String toString() {
        return super.toString() + ", Institution: " + institutionName;
    }
}
