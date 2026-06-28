package project4.ofekFanianAndTalOshri;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        College college = null;
        String filename = "college_data.bin";

        // נסיון לטעון מידע מקובץ בינארי
        try {
            college = College.loadFromFile(filename);
            System.out.println("Data loaded successfully from file.");
        } catch (Exception e) {
            college = new College(); // אם אין קובץ - יוצרים מערכת חדשה
            System.out.println("No saved data found. Starting new system.");
        }

        System.out.print("Enter the name of the college: ");
        String name = scanner.nextLine();
        if (college.getName() == null || college.getName().isEmpty()) {
            college.setName(name);
        }

        int choice = 0;
        do {
            printMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 0 -> {
                        // לפני יציאה - שומרים את המידע לקובץ בינארי
                        try {
                            college.saveToFile(filename);
                            System.out.println("Data saved successfully. Exiting the system. Goodbye!");
                        } catch (Exception e) {
                            System.out.println("Error saving data: " + e.getMessage());
                        }
                    }
                    case 1 -> addLecturer(scanner, college);
                    case 2 -> addCommittee(scanner, college);
                    case 3 -> addMemberToCommittee(scanner, college);
                    case 4 -> updateCommitteeChairman(scanner, college);
                    case 5 -> removeMemberFromCommittee(scanner, college);
                    case 6 -> addDepartment(scanner, college);
                    case 7 -> showAvgSalaryAll(college);
                    case 8 -> showAvgSalaryByDepartment(scanner, college);
                    case 9 -> showAllLecturers(college);
                    case 10 -> showAllCommittees(college);
                    case 11 -> assignLecturerToDepartment(scanner, college);
                    case 12 -> compareResearchersByArticles(scanner, college);
                    case 13 -> compareCommitteesByCriteria(scanner, college);
                    case 14 -> cloneCommittee(scanner, college);
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("0 - Exit");
        System.out.println("1 - Add Lecturer");
        System.out.println("2 - Add Committee");
        System.out.println("3 - Add Member to Committee");
        System.out.println("4 - Update Committee Chairman");
        System.out.println("5 - Remove Member from Committee");
        System.out.println("6 - Add Department");
        System.out.println("7 - Show Average Salary of All Lecturers");
        System.out.println("8 - Show Average Salary by Department");
        System.out.println("9 - Show All Lecturers");
        System.out.println("10 - Show All Committees");
        System.out.println("11 - Assign Lecturer to Department");
        System.out.println("12 - Compare Researchers by Articles");
        System.out.println("13 - Compare Committees by Criteria");
        System.out.println("14 - Clone Committee");
    }

    private static void addLecturer(Scanner scanner, College college) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter degree (FIRST/SECOND/DR/PROF): ");
        Degree degree = Degree.valueOf(scanner.nextLine().toUpperCase());

        System.out.print("Enter degree name: ");
        String degreeName = scanner.nextLine();

        System.out.print("Enter salary: ");
        double salary = Double.parseDouble(scanner.nextLine());

        Lecturer lecturer;

        switch (degree) {
            case DR -> {
                lecturer = new Doctor(name, id, degree, degreeName, salary);
                System.out.print("Enter number of articles: ");
                int num = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < num; i++) {
                    System.out.print("Article #" + (i + 1) + ": ");
                    ((Doctor) lecturer).addArticle(new Article(scanner.nextLine()));
                }
            }
            case PROF -> {
                System.out.print("Enter institution: ");
                String inst = scanner.nextLine();
                lecturer = new Professor(name, id, degree, degreeName, salary, inst);
                System.out.print("Enter number of articles: ");
                int num = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < num; i++) {
                    System.out.print("Article #" + (i + 1) + ": ");
                    ((Professor) lecturer).addArticle(new Article(scanner.nextLine()));
                }
            }
            case FIRST, SECOND -> {
                lecturer = new Lecturer(name, id, degree, degreeName, salary) {
                    @Override
                    protected int getNumArticles() {
                        return 0;
                    }

                    @Override
                    public int compareTo(Lecturer l2) {
                        return 0;
                    }
                };
            }
            default -> {
                System.out.println("Invalid degree type.");
                return;
            }
        }

        try {
            college.addLecturer(lecturer);
            System.out.println("Lecturer added successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addCommittee(Scanner scanner, College college) {
        System.out.print("Enter committee name: ");
        String name = scanner.nextLine();

        System.out.print("Enter chairman name: ");
        String chairmanName = scanner.nextLine();

        try {
            Lecturer chairman = null;
            for (Lecturer l : college.getAllLecturers()) {
                if (l.getName().equalsIgnoreCase(chairmanName) && l.getDegree().ordinal() >= Degree.DR.ordinal()) {
                    chairman = l;
                    break;
                }
            }

            if (chairman == null) {
                System.out.println("No lecturer with degree DR or higher found with that name to be chairman.");
                return;
            }

            Committee committee = new Committee(name, chairman);
            college.addCommittee(committee);
            System.out.println("Committee added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding committee: " + e.getMessage());
        }
    }


    private static void addMemberToCommittee(Scanner scanner, College college) {
        System.out.print("Enter committee name: ");
        String cname = scanner.nextLine();
        System.out.print("Enter lecturer name: ");
        String lecturerName = scanner.nextLine();

        try {
            college.addMemberToCommittee(cname, lecturerName);
            System.out.println("Lecturer added to committee.");
        } catch (AlreadyCommitteeMemberException e) {
            System.out.println("Lecturer is already a member of this committee.");
        } catch (InvalidDegreeException e) {
            System.out.println("All committee members must have the same degree.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateCommitteeChairman(Scanner scanner, College college) {
        System.out.print("Enter committee name: ");
        String cname = scanner.nextLine();
        System.out.print("Enter new chairman name: ");
        String lecturerName = scanner.nextLine();

        try {
            college.updateCommitteeChairman(cname, lecturerName);
            System.out.println("Chairman updated.");
        } catch (InvalidDegreeException e) {
            System.out.println("Invalid chairman degree: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeMemberFromCommittee(Scanner scanner, College college) {
        System.out.print("Enter committee name: ");
        String cname = scanner.nextLine();
        System.out.print("Enter lecturer name: ");
        String lecturerName = scanner.nextLine();

        try {
            college.removeMemberFromCommittee(cname, lecturerName);
            System.out.println("Member removed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addDepartment(Scanner scanner, College college) {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine();
        System.out.print("Enter number of students: ");
        int num = Integer.parseInt(scanner.nextLine());

        try {
            college.addDepartment(new Department(name, num));
            System.out.println("Department added.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showAvgSalaryAll(College college) {
        try {
            double avg = college.getAvgSalaryAll();
            System.out.println("Average salary: " + avg);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void showAvgSalaryByDepartment(Scanner scanner, College college) {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine();
        try {
            double avg = college.getAvgSalaryByDepartment(name);
            System.out.println("Average salary in department: " + avg);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void showAllLecturers(College college) {
        List<Lecturer> list = college.getAllLecturers();
        if (list.isEmpty())
            System.out.println("No lecturers.");
        else
            for (Lecturer l : list)
                System.out.println(l);
    }

    private static void showAllCommittees(College college) {
        List<Committee> list = college.getAllCommittees();
        if (list.isEmpty())
            System.out.println("No committees.");
        else
            for (Committee c : list)
                System.out.println(c);
    }

    private static void assignLecturerToDepartment(Scanner scanner, College college) {
        System.out.print("Enter lecturer name: ");
        String lname = scanner.nextLine();
        System.out.print("Enter department name: ");
        String dname = scanner.nextLine();

        try {
            college.assignLecturerToDepartment(lname, dname);
            System.out.println("Lecturer assigned to department.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void compareResearchersByArticles(Scanner scanner, College college) {
        System.out.print("Enter first lecturer name: ");
        String name1 = scanner.nextLine();
        System.out.print("Enter second lecturer name: ");
        String name2 = scanner.nextLine();

        try {
            String result = college.compareResearchersByArticles(name1, name2);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void compareCommitteesByCriteria(Scanner scanner, College college) {
        System.out.print("Enter first committee name: ");
        String name1 = scanner.nextLine();
        System.out.print("Enter second committee name: ");
        String name2 = scanner.nextLine();

        System.out.println("Choose comparison criteria:");
        System.out.println("1 - Number of members");
        System.out.println("2 - Total articles");
        int choice = Integer.parseInt(scanner.nextLine());

        try {
            String result = college.compareCommitteesByCriteria(name1, name2, choice);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cloneCommittee(Scanner scanner, College college) {
        System.out.print("Enter committee name to clone: ");
        String name = scanner.nextLine();
        try {
            Committee c = college.cloneCommittee(name);
            System.out.println("Committee cloned successfully: " + c.getName());
        } catch (Exception e) {
            System.out.println("Error cloning committee: " + e.getMessage());
        }
    }
}
