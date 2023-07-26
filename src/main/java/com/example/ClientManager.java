package com.example;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.ArrayList;

public class ClientManager {
    private Connection connection;
    private Menu menu;

    private boolean terminate;

    public void manage() {
        connection = new Connection();
        connection.connect("alumchat.xyz");
        menu = new Menu();
        terminate = false;
        while (!terminate) {
            manageInitialOption();
            if (!terminate)
                manageActionsAfterAuthorization();
        }
    }

    private void manageActionsAfterAuthorization() {
        int option = 0;
        // while (option != -1) {
        option = menu.showActionsMenu();
        handleAction(option);
        // }
    }

    private void handleAction(int option) {
        switch (option) {
            case 10:
                logout();
                break;
            case 11:
                deleteAccount();
                break;
        }
    }

    private void logout() {
        connection.logout();
        System.out.println("Se ha cerrado la sesion correctamente.\n");
    }

    private void deleteAccount() {
        connection.deleteAccount();
    }

    private void manageInitialOption() {
        boolean authorized = false;
        while (!authorized) {
            int initialOption = menu.showInitialMenu();
            int result = 0;

            if (initialOption == 1) {
                result = register();
            }
            if (initialOption == 2) {
                result  = login();
            }
            if (initialOption == 3) {
                result = 0;
                terminate = true;
            }

            if (result == -1) {
                System.out.println("\nOops no pudimos lograr que entraras. Probemos de nuevo.\n");
            }
            else {
                authorized = true;
            }
        }
    }

    private int register() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        return connection.register(username, password);
    }

    private int login() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        return connection.login(username, password);
    }
}
