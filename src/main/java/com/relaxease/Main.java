package com.relaxease;

import com.relaxease.dao.DatabaseInitializer;
import com.relaxease.dao.UserDao;
import com.relaxease.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();            // 1. make sure all tables exist

        UserDao userDao = new UserDao();             // 2. create shared instances
        Scanner scanner = new Scanner(System.in);

        new Authentication(userDao, scanner).start();   // 3. hand over to the login/register screen

        scanner.close();
    }
}
