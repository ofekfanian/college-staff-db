package college;

import college.db.CollegeDAO;
import college.db.DBConnection;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final CollegeDAO dao = new CollegeDAO();
    private static int collegeId;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the name of the college: ");
        String collegeName = scanner.nextLine().trim();

        try {
            // Connect to the database and get (or create) the college record
            collegeId = dao.getOrCreateCollege(collegeName);
            System.out.println("Connected to database. College: " + collegeName);
        } catch (Exception e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            System.out.println("Ensure PostgreSQL is running and college_db exists.");
            return;
        }

        int choice = -1;
        do {
            printMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 0  -> { DBConnection.close(); System.out.println("Goodbye!"); }
                    case 1  -> addLecturer(scanner);
                    case 2  -> addCommittee(scanner);
                    case 3  -> addMemberToCommittee(scanner);
                    case 4  -> updateCommitteeChairman(scanner);
                    case 5  -> removeMemberFromCommittee(scanner);
                    case 6  -> addDepartment(scanner);
                    case 7  -> showAvgSalaryAll();
                    case 8  -> showAvgSalaryByDepartment(scanner);
                    case 9  -> showAllLecturers();
                    case 10 -> showAllCommittees();
                    case 11 -> assignLecturerToDepartment(scanner);
                    case 12 -> compareResearchersByArticles(scanner);
                    case 13 -> compareCommitteesByCriteria(scanner);
                    case 14 -> cloneCommittee(scanner);
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- College Staff Management System ---");
        System.out.println("0  - Exit");
        System.out.println("1  - Add Lecturer");
        System.out.println("2  - Add Committee");
        System.out.println("3  - Add Member to Committee");
        System.out.println("4  - Update Committee Chairman");
        System.out.println("5  - Remove Member from Committee");
        System.out.println("6  - Add Department");
        System.out.println("7  - Show Average Salary (All Lecturers)");
        System.out.println("8  - Show Average Salary by Department");
        System.out.println("9  - Show All Lecturers");
        System.out.println("10 - Show All Committees");
        System.out.println("11 - Assign Lecturer to Department");
        System.out.println("12 - Compare Researchers by Articles");
        System.out.println("13 - Compare Committees by Criteria");
        System.out.println("14 - Clone Committee");
        System.out.print("Choose an option: ");
    }

    // ----------------------------------------------------------------
    // ADD LECTURER
    // ----------------------------------------------------------------
    private static void addLecturer(Scanner scanner) throws Exception {
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        if (dao.lecturerExists(collegeId, name)) {
            System.out.println("Lecturer already exists.");
            return;
        }

        System.out.print("Enter ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter degree (FIRST / SECOND / DR / PROF): ");
        String degree = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter degree name: ");
        String degreeName = scanner.nextLine().trim();

        System.out.print("Enter salary: ");
        double salary = Double.parseDouble(scanner.nextLine().trim());

        // institution is required only for professors
        String institution = null;
        if (degree.equals("PROF")) {
            System.out.print("Enter institution: ");
            institution = scanner.nextLine().trim();
        }

        dao.addLecturer(collegeId, id, name, degree, degreeName, salary, institution);

        // DR and PROF can have articles
        if (degree.equals("DR") || degree.equals("PROF")) {
            System.out.print("Enter number of articles: ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            for (int i = 0; i < n; i++) {
                System.out.print("  Article #" + (i + 1) + " title: ");
                dao.addArticle(id, scanner.nextLine().trim());
            }
        }

        System.out.println("Lecturer added successfully.");
    }

    // ----------------------------------------------------------------
    // ADD COMMITTEE
    // ----------------------------------------------------------------
    private static void addCommittee(Scanner scanner) throws Exception {
        System.out.print("Enter committee name: ");
        String name = scanner.nextLine().trim();
        if (dao.committeeExists(collegeId, name)) {
            System.out.println("Committee already exists.");
            return;
        }

        System.out.print("Enter chairman name: ");
        String chairmanId = dao.findLecturerIdByName(collegeId, scanner.nextLine().trim());
        if (chairmanId == null) { System.out.println("Lecturer not found."); return; }

        // Chairman must hold DR or PROF degree
        String degree = dao.getLecturerDegree(chairmanId);
        if (!degree.equals("DR") && !degree.equals("PROF")) {
            System.out.println("Chairman must have degree DR or higher.");
            return;
        }

        dao.addCommittee(collegeId, name, chairmanId);
        System.out.println("Committee added successfully.");
    }

    // ----------------------------------------------------------------
    // ADD MEMBER TO COMMITTEE
    // ----------------------------------------------------------------
    private static void addMemberToCommittee(Scanner scanner) throws Exception {
        System.out.print("Enter committee name: ");
        Integer cid = dao.findCommitteeIdByName(collegeId, scanner.nextLine().trim());
        if (cid == null) { System.out.println("Committee not found."); return; }

        System.out.print("Enter lecturer name: ");
        String lid = dao.findLecturerIdByName(collegeId, scanner.nextLine().trim());
        if (lid == null) { System.out.println("Lecturer not found."); return; }

        if (dao.isChairman(cid, lid)) {
            System.out.println("The chairman cannot be added as a regular member.");
            return;
        }
        if (dao.isAlreadyMember(cid, lid)) {
            System.out.println("Lecturer is already a member of this committee.");
            return;
        }

        // All committee members must share the same degree
        String memberDegree   = dao.getCommitteeMemberDegree(cid);
        String lecturerDegree = dao.getLecturerDegree(lid);
        if (memberDegree != null && !memberDegree.equals(lecturerDegree)) {
            System.out.println("All committee members must have the same degree.");
            return;
        }

        dao.addMemberToCommittee(cid, lid, lecturerDegree);
        System.out.println("Lecturer added to committee.");
    }

    // ----------------------------------------------------------------
    // UPDATE COMMITTEE CHAIRMAN
    // ----------------------------------------------------------------
    private static void updateCommitteeChairman(Scanner scanner) throws Exception {
        System.out.print("Enter committee name: ");
        Integer cid = dao.findCommitteeIdByName(collegeId, scanner.nextLine().trim());
        if (cid == null) { System.out.println("Committee not found."); return; }

        System.out.print("Enter new chairman name: ");
        String lid = dao.findLecturerIdByName(collegeId, scanner.nextLine().trim());
        if (lid == null) { System.out.println("Lecturer not found."); return; }

        String degree = dao.getLecturerDegree(lid);
        if (!degree.equals("DR") && !degree.equals("PROF")) {
            System.out.println("Chairman must have degree DR or higher.");
            return;
        }

        dao.updateCommitteeChairman(cid, lid);
        System.out.println("Chairman updated successfully.");
    }

    // ----------------------------------------------------------------
    // REMOVE MEMBER FROM COMMITTEE
    // ----------------------------------------------------------------
    private static void removeMemberFromCommittee(Scanner scanner) throws Exception {
        System.out.print("Enter committee name: ");
        Integer cid = dao.findCommitteeIdByName(collegeId, scanner.nextLine().trim());
        if (cid == null) { System.out.println("Committee not found."); return; }

        System.out.print("Enter lecturer name: ");
        String lid = dao.findLecturerIdByName(collegeId, scanner.nextLine().trim());
        if (lid == null) { System.out.println("Lecturer not found."); return; }

        if (!dao.isAlreadyMember(cid, lid)) {
            System.out.println("Lecturer is not a member of this committee.");
            return;
        }

        dao.removeMemberFromCommittee(cid, lid);
        System.out.println("Member removed.");
    }

    // ----------------------------------------------------------------
    // ADD DEPARTMENT
    // ----------------------------------------------------------------
    private static void addDepartment(Scanner scanner) throws Exception {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine().trim();
        if (dao.departmentExists(collegeId, name)) {
            System.out.println("Department already exists.");
            return;
        }
        System.out.print("Enter number of students: ");
        dao.addDepartment(collegeId, name, Integer.parseInt(scanner.nextLine().trim()));
        System.out.println("Department added.");
    }

    // ----------------------------------------------------------------
    // SALARY QUERIES
    // ----------------------------------------------------------------
    private static void showAvgSalaryAll() throws Exception {
        System.out.printf("Average salary of all lecturers: %.2f%n", dao.getAvgSalaryAll(collegeId));
    }

    private static void showAvgSalaryByDepartment(Scanner scanner) throws Exception {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine().trim();
        System.out.printf("Average salary in %s: %.2f%n",
                name, dao.getAvgSalaryByDepartment(collegeId, name));
    }

    // ----------------------------------------------------------------
    // SHOW ALL LECTURERS
    // ----------------------------------------------------------------
    private static void showAllLecturers() throws Exception {
        List<String[]> list = dao.getAllLecturers(collegeId);
        if (list.isEmpty()) { System.out.println("No lecturers found."); return; }
        System.out.println();
        for (String[] l : list) {
            // l = [name, id, degree, degreeName, salary, deptName, institution, numArticles]
            StringBuilder sb = new StringBuilder();
            sb.append("Name: ").append(l[0])
              .append(" | ID: ").append(l[1])
              .append(" | Degree: ").append(l[2]).append(" (").append(l[3]).append(")")
              .append(" | Salary: ").append(l[4])
              .append(" | Department: ").append(l[5]);
            if (!l[6].isEmpty())
                sb.append(" | Institution: ").append(l[6]);
            if (l[2].equals("DR") || l[2].equals("PROF"))
                sb.append(" | Articles: ").append(l[7]);
            System.out.println(sb);
        }
    }

    // ----------------------------------------------------------------
    // SHOW ALL COMMITTEES
    // ----------------------------------------------------------------
    private static void showAllCommittees() throws Exception {
        List<String[]> list = dao.getAllCommittees(collegeId);
        if (list.isEmpty()) { System.out.println("No committees found."); return; }
        System.out.println();
        for (String[] c : list) {
            // c = [committee_id, name, chairman_name, num_members]
            int cid = Integer.parseInt(c[0]);
            List<String> members = dao.getCommitteeMembers(cid);
            System.out.println("Committee: " + c[1]
                    + " | Chairman: " + c[2]
                    + " | Members (" + c[3] + "): "
                    + (members.isEmpty() ? "None" : String.join(", ", members)));
        }
    }

    // ----------------------------------------------------------------
    // ASSIGN LECTURER TO DEPARTMENT
    // ----------------------------------------------------------------
    private static void assignLecturerToDepartment(Scanner scanner) throws Exception {
        System.out.print("Enter lecturer name: ");
        String lid = dao.findLecturerIdByName(collegeId, scanner.nextLine().trim());
        if (lid == null) { System.out.println("Lecturer not found."); return; }

        System.out.print("Enter department name: ");
        Integer deptId = dao.findDeptIdByName(collegeId, scanner.nextLine().trim());
        if (deptId == null) { System.out.println("Department not found."); return; }

        dao.assignLecturerToDepartment(lid, deptId);
        System.out.println("Lecturer assigned to department.");
    }

    // ----------------------------------------------------------------
    // COMPARISONS
    // ----------------------------------------------------------------
    private static void compareResearchersByArticles(Scanner scanner) throws Exception {
        System.out.print("Enter first lecturer name: ");
        String name1 = scanner.nextLine().trim();
        System.out.print("Enter second lecturer name: ");
        String name2 = scanner.nextLine().trim();
        System.out.println(dao.compareResearchersByArticles(collegeId, name1, name2));
    }

    private static void compareCommitteesByCriteria(Scanner scanner) throws Exception {
        System.out.print("Enter first committee name: ");
        String name1 = scanner.nextLine().trim();
        System.out.print("Enter second committee name: ");
        String name2 = scanner.nextLine().trim();
        System.out.println("1 - Number of members");
        System.out.println("2 - Total articles written by members");
        System.out.print("Choose: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        System.out.println(dao.compareCommitteesByCriteria(collegeId, name1, name2, choice));
    }

    private static void cloneCommittee(Scanner scanner) throws Exception {
        System.out.print("Enter committee name to clone: ");
        dao.cloneCommittee(collegeId, scanner.nextLine().trim());
    }
}
