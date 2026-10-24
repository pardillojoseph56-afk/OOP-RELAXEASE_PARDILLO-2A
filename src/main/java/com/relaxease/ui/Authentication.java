package com.relaxease.ui;

import com.relaxease.dao.UserDao;
import com.relaxease.model.User;

import java.util.Optional;
import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    // one shared UserDao and one shared Scanner are passed in from Main
    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== RelaxEase: Massage Booking System ===");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register Account ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password are required.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken.");
            return;
        }

        System.out.print("Select Role [1] ADMIN | [2] RECEPTIONIST | [3] CLIENT: ");
        String role;
        switch (scanner.nextLine().trim()) {
            case "1" -> role = "ADMIN";
            case "2" -> role = "RECEPTIONIST";
            case "3" -> role = "CLIENT";
            default -> role = null;
        }

        if (role == null) {
            System.out.println("Invalid role selection!");
            return;
        }

        if (userDao.register(username, password, role)) {
            System.out.println("Registered! Your account status is PENDING.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.login(username, password);
        if (result.isPresent()) {
            routeByRole(result.get());   // returns here after the user logs out
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // sends the user to the menu that matches their role
    private void routeByRole(User user) {
        switch (user.roles()) {
            case "ADMIN" -> new AdminMenu(userDao, scanner, user).show();
            case "RECEPTIONIST" -> new ReceptionistMenu(userDao, scanner, user).show();
            case "CLIENT" -> new ClientMenu(userDao, scanner, user).show();
            default -> System.out.println("Unknown role: " + user.roles());
        }
    }
}
