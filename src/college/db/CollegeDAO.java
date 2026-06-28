package college.db;

import java.sql.*;
import java.util.*;

// Replaces binary-file storage from the original OOP project with direct PostgreSQL access
public class CollegeDAO {

    // ================================================================
    // COLLEGE
    // ================================================================

    // Returns existing college_id or inserts a new row and returns its id
    public int getOrCreateCollege(String name) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT college_id FROM college WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("college_id");
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO college (name) VALUES (?) RETURNING college_id")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("college_id");
        }
    }

    // ================================================================
    // LECTURER
    // ================================================================

    public boolean lecturerExists(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT 1 FROM lecturer WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            return ps.executeQuery().next();
        }
    }

    // institution is NULL for non-PROF degrees
    public void addLecturer(int collegeId, String id, String name, String degree,
                            String degreeName, double salary, String institution) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO lecturer (lecturer_id, college_id, name, degree, degree_name, salary, institution) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setInt(2, collegeId);
            ps.setString(3, name);
            ps.setString(4, degree);
            ps.setString(5, degreeName);
            ps.setDouble(6, salary);
            if (institution != null) ps.setString(7, institution);
            else                     ps.setNull(7, Types.VARCHAR);
            ps.executeUpdate();
        }
    }

    // Only DR and PROF lecturers can have articles (enforced in Main)
    public void addArticle(String lecturerId, String title) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO article (title, lecturer_id) VALUES (?, ?)")) {
            ps.setString(1, title);
            ps.setString(2, lecturerId);
            ps.executeUpdate();
        }
    }

    // Returns null if no lecturer with that name exists in this college
    public String findLecturerIdByName(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT lecturer_id FROM lecturer WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("lecturer_id") : null;
        }
    }

    public String getLecturerDegree(String lecturerId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT degree FROM lecturer WHERE lecturer_id = ?")) {
            ps.setString(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("degree") : null;
        }
    }

    // Returns [name, id, degree, degreeName, salary, deptName, institution, numArticles]
    public List<String[]> getAllLecturers(int collegeId) throws SQLException {
        List<String[]> result = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT l.lecturer_id, l.name, l.degree, l.degree_name, l.salary, " +
                "       COALESCE(d.name, 'None')       AS dept_name, " +
                "       COALESCE(l.institution, '')     AS institution, " +
                "       COUNT(a.article_id)             AS num_articles " +
                "FROM lecturer l " +
                "LEFT JOIN department d ON l.dept_id     = d.dept_id " +
                "LEFT JOIN article    a ON l.lecturer_id = a.lecturer_id " +
                "WHERE l.college_id = ? " +
                "GROUP BY l.lecturer_id, l.name, l.degree, l.degree_name, l.salary, d.name, l.institution " +
                "ORDER BY l.name")) {
            ps.setInt(1, collegeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("name"),
                    rs.getString("lecturer_id"),
                    rs.getString("degree"),
                    rs.getString("degree_name"),
                    String.format("%.2f", rs.getDouble("salary")),
                    rs.getString("dept_name"),
                    rs.getString("institution"),
                    rs.getString("num_articles")
                });
            }
        }
        return result;
    }

    // ================================================================
    // DEPARTMENT
    // ================================================================

    public boolean departmentExists(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT 1 FROM department WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            return ps.executeQuery().next();
        }
    }

    public void addDepartment(int collegeId, String name, int numStudents) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO department (college_id, name, num_students) VALUES (?, ?, ?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            ps.setInt(3, numStudents);
            ps.executeUpdate();
        }
    }

    // Returns null if department not found
    public Integer findDeptIdByName(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT dept_id FROM department WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("dept_id") : null;
        }
    }

    public void assignLecturerToDepartment(String lecturerId, int deptId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE lecturer SET dept_id = ? WHERE lecturer_id = ?")) {
            ps.setInt(1, deptId);
            ps.setString(2, lecturerId);
            ps.executeUpdate();
        }
    }

    public double getAvgSalaryAll(int collegeId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT AVG(salary) FROM lecturer WHERE college_id = ?")) {
            ps.setInt(1, collegeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getObject(1) != null) return rs.getDouble(1);
            throw new SQLException("No lecturers found.");
        }
    }

    public double getAvgSalaryByDepartment(int collegeId, String deptName) throws SQLException {
        Integer deptId = findDeptIdByName(collegeId, deptName);
        if (deptId == null) throw new SQLException("Department not found: " + deptName);
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT AVG(salary) FROM lecturer WHERE dept_id = ?")) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getObject(1) != null) return rs.getDouble(1);
            throw new SQLException("No lecturers in this department.");
        }
    }

    // ================================================================
    // COMMITTEE
    // ================================================================

    public boolean committeeExists(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT 1 FROM committee WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            return ps.executeQuery().next();
        }
    }

    public void addCommittee(int collegeId, String name, String chairmanId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO committee (college_id, name, chairman_id) VALUES (?, ?, ?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            ps.setString(3, chairmanId);
            ps.executeUpdate();
        }
    }

    // Returns null if committee not found
    public Integer findCommitteeIdByName(int collegeId, String name) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT committee_id FROM committee WHERE college_id = ? AND LOWER(name) = LOWER(?)")) {
            ps.setInt(1, collegeId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("committee_id") : null;
        }
    }

    // Returns the required degree for all members, or null if no members yet
    public String getCommitteeMemberDegree(int committeeId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT member_degree FROM committee WHERE committee_id = ?")) {
            ps.setInt(1, committeeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("member_degree") : null;
        }
    }

    public boolean isAlreadyMember(int committeeId, String lecturerId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT 1 FROM committee_member WHERE committee_id = ? AND lecturer_id = ?")) {
            ps.setInt(1, committeeId);
            ps.setString(2, lecturerId);
            return ps.executeQuery().next();
        }
    }

    public boolean isChairman(int committeeId, String lecturerId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT 1 FROM committee WHERE committee_id = ? AND chairman_id = ?")) {
            ps.setInt(1, committeeId);
            ps.setString(2, lecturerId);
            return ps.executeQuery().next();
        }
    }

    // Sets member_degree on first member added; all subsequent members must match
    public void addMemberToCommittee(int committeeId, String lecturerId, String degree) throws SQLException {
        if (getCommitteeMemberDegree(committeeId) == null) {
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "UPDATE committee SET member_degree = ? WHERE committee_id = ?")) {
                ps.setString(1, degree);
                ps.setInt(2, committeeId);
                ps.executeUpdate();
            }
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO committee_member (committee_id, lecturer_id) VALUES (?, ?)")) {
            ps.setInt(1, committeeId);
            ps.setString(2, lecturerId);
            ps.executeUpdate();
        }
    }

    public void removeMemberFromCommittee(int committeeId, String lecturerId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "DELETE FROM committee_member WHERE committee_id = ? AND lecturer_id = ?")) {
            ps.setInt(1, committeeId);
            ps.setString(2, lecturerId);
            ps.executeUpdate();
        }
        // Reset member_degree to NULL when the last member is removed
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM committee_member WHERE committee_id = ?")) {
            ps.setInt(1, committeeId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                try (PreparedStatement ps2 = DBConnection.getConnection().prepareStatement(
                        "UPDATE committee SET member_degree = NULL WHERE committee_id = ?")) {
                    ps2.setInt(1, committeeId);
                    ps2.executeUpdate();
                }
            }
        }
    }

    // Removes the new chairman from the members list before promoting (mirrors original logic)
    public void updateCommitteeChairman(int committeeId, String newChairmanId) throws SQLException {
        if (isAlreadyMember(committeeId, newChairmanId))
            removeMemberFromCommittee(committeeId, newChairmanId);
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE committee SET chairman_id = ? WHERE committee_id = ?")) {
            ps.setString(1, newChairmanId);
            ps.setInt(2, committeeId);
            ps.executeUpdate();
        }
    }

    // Returns [committee_id, name, chairman_name, num_members]
    public List<String[]> getAllCommittees(int collegeId) throws SQLException {
        List<String[]> result = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT c.committee_id, c.name AS cname, ch.name AS chairman_name, " +
                "       COUNT(cm.lecturer_id) AS num_members " +
                "FROM committee c " +
                "JOIN lecturer ch ON c.chairman_id = ch.lecturer_id " +
                "LEFT JOIN committee_member cm ON c.committee_id = cm.committee_id " +
                "WHERE c.college_id = ? " +
                "GROUP BY c.committee_id, c.name, ch.name " +
                "ORDER BY c.name")) {
            ps.setInt(1, collegeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("committee_id"),
                    rs.getString("cname"),
                    rs.getString("chairman_name"),
                    rs.getString("num_members")
                });
            }
        }
        return result;
    }

    public List<String> getCommitteeMembers(int committeeId) throws SQLException {
        List<String> members = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT l.name FROM committee_member cm " +
                "JOIN lecturer l ON cm.lecturer_id = l.lecturer_id " +
                "WHERE cm.committee_id = ? ORDER BY l.name")) {
            ps.setInt(1, committeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) members.add(rs.getString("name"));
        }
        return members;
    }

    // ================================================================
    // STATISTICS / COMPARISONS
    // ================================================================

    public String compareResearchersByArticles(int collegeId, String name1, String name2) throws SQLException {
        String id1 = findLecturerIdByName(collegeId, name1);
        String id2 = findLecturerIdByName(collegeId, name2);
        if (id1 == null) throw new SQLException("Lecturer not found: " + name1);
        if (id2 == null) throw new SQLException("Lecturer not found: " + name2);

        // Only DR and PROF lecturers can be compared by articles
        String deg1 = getLecturerDegree(id1), deg2 = getLecturerDegree(id2);
        if (!deg1.equals("DR") && !deg1.equals("PROF"))
            throw new SQLException(name1 + " must have degree DR or higher.");
        if (!deg2.equals("DR") && !deg2.equals("PROF"))
            throw new SQLException(name2 + " must have degree DR or higher.");

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT l.name, COUNT(a.article_id) AS num_articles " +
                "FROM lecturer l LEFT JOIN article a ON l.lecturer_id = a.lecturer_id " +
                "WHERE l.lecturer_id IN (?, ?) " +
                "GROUP BY l.lecturer_id, l.name")) {
            ps.setString(1, id1);
            ps.setString(2, id2);
            ResultSet rs = ps.executeQuery();
            Map<String, Integer> counts = new HashMap<>();
            while (rs.next()) counts.put(rs.getString("name"), rs.getInt("num_articles"));
            int c1 = counts.getOrDefault(name1, 0);
            int c2 = counts.getOrDefault(name2, 0);
            if (c1 > c2) return name1 + " wrote more articles (" + c1 + " vs " + c2 + ").";
            if (c2 > c1) return name2 + " wrote more articles (" + c2 + " vs " + c1 + ").";
            return "Both have the same number of articles (" + c1 + ").";
        }
    }

    // choice 1 = compare by member count, choice 2 = compare by total articles of members
    public String compareCommitteesByCriteria(int collegeId, String name1, String name2, int choice) throws SQLException {
        Integer cid1 = findCommitteeIdByName(collegeId, name1);
        Integer cid2 = findCommitteeIdByName(collegeId, name2);
        if (cid1 == null) throw new SQLException("Committee not found: " + name1);
        if (cid2 == null) throw new SQLException("Committee not found: " + name2);

        if (choice == 1) {
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "SELECT committee_id, COUNT(*) AS cnt FROM committee_member " +
                    "WHERE committee_id IN (?, ?) GROUP BY committee_id")) {
                ps.setInt(1, cid1);
                ps.setInt(2, cid2);
                ResultSet rs = ps.executeQuery();
                Map<Integer, Integer> m = new HashMap<>();
                while (rs.next()) m.put(rs.getInt("committee_id"), rs.getInt("cnt"));
                int c1 = m.getOrDefault(cid1, 0), c2 = m.getOrDefault(cid2, 0);
                if (c1 > c2) return name1 + " has more members (" + c1 + " vs " + c2 + ").";
                if (c2 > c1) return name2 + " has more members (" + c2 + " vs " + c1 + ").";
                return "Both have the same number of members (" + c1 + ").";
            }
        } else if (choice == 2) {
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "SELECT cm.committee_id, COUNT(a.article_id) AS total " +
                    "FROM committee_member cm " +
                    "LEFT JOIN article a ON cm.lecturer_id = a.lecturer_id " +
                    "WHERE cm.committee_id IN (?, ?) GROUP BY cm.committee_id")) {
                ps.setInt(1, cid1);
                ps.setInt(2, cid2);
                ResultSet rs = ps.executeQuery();
                Map<Integer, Integer> m = new HashMap<>();
                while (rs.next()) m.put(rs.getInt("committee_id"), rs.getInt("total"));
                int t1 = m.getOrDefault(cid1, 0), t2 = m.getOrDefault(cid2, 0);
                if (t1 > t2) return name1 + " has more total articles (" + t1 + " vs " + t2 + ").";
                if (t2 > t1) return name2 + " has more total articles (" + t2 + " vs " + t1 + ").";
                return "Both have the same total articles (" + t1 + ").";
            }
        }
        throw new SQLException("Invalid comparison choice (use 1 or 2).");
    }

    public void cloneCommittee(int collegeId, String name) throws SQLException {
        Integer cid = findCommitteeIdByName(collegeId, name);
        if (cid == null) throw new SQLException("Committee not found: " + name);

        String newName = "new-" + name;
        if (committeeExists(collegeId, newName))
            throw new SQLException("Cloned committee already exists: " + newName);

        String chairmanId, memberDegree;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT chairman_id, member_degree FROM committee WHERE committee_id = ?")) {
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            rs.next();
            chairmanId   = rs.getString("chairman_id");
            memberDegree = rs.getString("member_degree");
        }

        int newCid;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO committee (college_id, name, chairman_id, member_degree) " +
                "VALUES (?, ?, ?, ?) RETURNING committee_id")) {
            ps.setInt(1, collegeId);
            ps.setString(2, newName);
            ps.setString(3, chairmanId);
            if (memberDegree != null) ps.setString(4, memberDegree);
            else                      ps.setNull(4, Types.VARCHAR);
            ResultSet rs = ps.executeQuery();
            rs.next();
            newCid = rs.getInt("committee_id");
        }

        // Copy all members from the original committee to the clone
        List<String> members = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT lecturer_id FROM committee_member WHERE committee_id = ?")) {
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) members.add(rs.getString("lecturer_id"));
        }
        for (String lid : members) {
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "INSERT INTO committee_member (committee_id, lecturer_id) VALUES (?, ?)")) {
                ps.setInt(1, newCid);
                ps.setString(2, lid);
                ps.executeUpdate();
            }
        }
        System.out.println("Committee cloned successfully as: " + newName);
    }
}
