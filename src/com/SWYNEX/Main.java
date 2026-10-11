package com.SWYNEX;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println(" SWYNEX STUDENT MANAGEMENT SYSTEM");
        System.out.println("==================================");

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            System.out.println("Database connected successfully!");

            boolean running = true;

            while (running) {
                System.out.println("\n========== MENU ==========");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addStudent(connection);
                        break;
                    case "2":
                        viewStudents(connection);
                        break;
                    case "3":
                        searchStudent(connection);
                        break;
                    case "4":
                        updateStudent(connection);
                        break;
                    case "5":
                        deleteStudent(connection);
                        break;
                    case "6":
                        running = false;
                        System.out.println("Thank you for using the system!");
                        break;
                    default:
                        System.out.println("Invalid choice. Enter 1 to 6.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection or operation failed.");
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

    private static void addStudent(Connection connection) {
        System.out.println("\n--- Add Student ---");

        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter student email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter student course: ");
        String course = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        String sql =
                "INSERT INTO students (name, email, course) VALUES (?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, course);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Student added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Could not add student: " + e.getMessage());
        }
    }

    private static void viewStudents(Connection connection) {
        System.out.println("\n--- Student List ---");

        String sql =
                "SELECT id, name, email, course FROM students ORDER BY id";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            boolean found = false;

            while (result.next()) {
                found = true;
                System.out.println("------------------------------");
                System.out.println("ID: " + result.getInt("id"));
                System.out.println("Name: " + result.getString("name"));
                System.out.println("Email: " + result.getString("email"));
                System.out.println("Course: " + result.getString("course"));
            }

            if (!found) {
                System.out.println("No students found.");
            }

        } catch (SQLException e) {
            System.out.println("Could not view students: " + e.getMessage());
        }
    }

    private static void searchStudent(Connection connection) {
        System.out.println("\n--- Search Student ---");

        int id = readStudentId();
        if (id <= 0) {
            System.out.println("Invalid student ID.");
            return;
        }

        String sql =
                "SELECT id, name, email, course FROM students WHERE id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    System.out.println("ID: " + result.getInt("id"));
                    System.out.println("Name: " + result.getString("name"));
                    System.out.println("Email: " + result.getString("email"));
                    System.out.println("Course: " + result.getString("course"));
                } else {
                    System.out.println("Student not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    private static void updateStudent(Connection connection) {
        System.out.println("\n--- Update Student ---");

        int id = readStudentId();
        if (id <= 0) {
            System.out.println("Invalid student ID.");
            return;
        }

        System.out.print("Enter new name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter new email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter new course: ");
        String course = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        String sql =
                "UPDATE students SET name = ?, email = ?, course = ? WHERE id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, course);
            statement.setInt(4, id);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Student not found.");
            }

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void deleteStudent(Connection connection) {
        System.out.println("\n--- Delete Student ---");

        int id = readStudentId();
        if (id <= 0) {
            System.out.println("Invalid student ID.");
            return;
        }

        System.out.print("Delete student with ID " + id + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        String sql = "DELETE FROM students WHERE id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student not found.");
            }

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    private static int readStudentId() {
        System.out.print("Enter student ID: ");
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}