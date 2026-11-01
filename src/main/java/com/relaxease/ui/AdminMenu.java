package com.relaxease.ui;

import com.relaxease.dao.UserDao;
import com.relaxease.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private static final List<String> VALID_ROLES = List.of("ADMIN", "RECEPTIONIST", "CLIENT");
    private static final List<String> VALID_STATUSES = List.of("APPROVED", "PENDING");

    private final UserDao userDao;
    private final Scanner scanner;
    private final User user;   // the admin who is currently logged in

    public AdminMenu(UserDao userDao, Scanner scanner, User user) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.user = user;
    }

    public void show() {
        System.out.println("\nWelcome, " + user.username() + "!");
        System.out.println("Role: " + user.roles() + " | Status: " + user.status());

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role & Status");
            System.out.println("5. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser();
                case "4" -> updateUser();
                case "5" -> {
                    System.out.println("Logged out.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n--- All System Users ---");
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        printUserTable(users);
    }

    private void viewPendingUsers() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n--- Pending Registrations ---");
        if (pending.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }
        printUserTable(pending);
    }

    private void approveUser() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n--- Approve User Account ---");
        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }
        printUserTable(pending);

        while (true) {
            int id = readIntInput("Enter User ID to approve (0 to cancel): ");

            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            // the ID must be one of the pending accounts shown above
            boolean inPending = false;
            for (User u : pending) {
                if (u.id() == id) {
                    inPending = true;
                }
            }
            if (!inPending) {
                System.out.println("User ID " + id + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(id)) {
                System.out.println("Success! User ID " + id + " is now APPROVED.");
            } else {
                System.out.println("Approval failed.");
            }
            return;
        }
    }

    private void updateUser() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n--- Update User Role & Status ---");
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        printUserTable(users);

        // step 1: choose the target user
        int targetId;
        while (true) {
            targetId = readIntInput("Enter User ID to update (0 to cancel): ");

            if (targetId == 0) {
                System.out.println("Update cancelled.");
                return;
            }
            if (targetId == user.id()) {
                System.out.println("WARNING: Modifying the currently active admin account is restricted.");
                continue;
            }

            boolean exists = false;
            for (User u : users) {
                if (u.id() == targetId) {
                    exists = true;
                }
            }
            if (exists) {
                break;
            }
            System.out.println("User ID " + targetId + " does not exist. Try again.");
        }

        // step 2: new role
        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // step 3: new status
        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
            System.out.println("Success! User ID " + targetId + " is now " + newRole + " / " + newStatus + ".");
        } else {
            System.out.println("Update failed.");
        }
    }

    // ---------- input validation wrappers ----------

    // keeps asking until the user types a whole number
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // keeps asking until a valid role is typed; returns null if the user enters 0
    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN / RECEPTIONIST / CLIENT, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_ROLES.contains(input)) {
                return input;
            }
            System.out.println("Invalid role. Please choose ADMIN, RECEPTIONIST or CLIENT.");
        }
    }

    // keeps asking until a valid status is typed; returns null if the user enters 0
    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED / PENDING, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_STATUSES.contains(input)) {
                return input;
            }
            System.out.println("Invalid status. Please choose APPROVED or PENDING.");
        }
    }

    // prints any list of users as an aligned table
    private void printUserTable(List<User> users) {
        String line = "+" + "-".repeat(7) + "+" + "-".repeat(17) + "+"
                + "-".repeat(14) + "+" + "-".repeat(12) + "+";
        System.out.println(line);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(line);
        for (User u : users) {
            System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n",
                    u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(line);
    }
}