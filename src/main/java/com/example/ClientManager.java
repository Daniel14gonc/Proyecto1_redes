package com.example;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.ArrayList;

public class ClientManager {
    private Connection connection;
    private Menu menu;

    private boolean terminate;

    private boolean loggedIn = true;

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
        while (loggedIn && !terminate) {
            option = menu.showActionsMenu();
            System.out.println(option);
            handleAction(option);
        }
    }

    private void handleAction(int option) {
        switch (option) {
            case 1:
                showRoster();
                break;
            case 6:
                handleStatusMessageChange();
                break;
            case 10:
                logout();
                break;
            case 11:
                deleteAccount();
                break;
            case 12:
                loggedIn = false;
                terminate = true;
                break;
            default:
                System.out.println("");
                break;
        }
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

    private void handleStatusMessageChange() {
        String message = menu.getStatusMessage();
        connection.setStatusMessage(message);
    }

    private void showRoster() {
        menu.showRoster(connection.getRoster());
    }

    private int register() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        reset_state();
        return connection.register(username, password);
    }

    private int login() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        reset_state();
        return connection.login(username, password);
    }

    private void logout() {
        connection.logout();
        loggedIn = false;
    }

    private void deleteAccount() {
        connection.deleteAccount();
    }

    private void reset_state() {
        terminate = false;
        loggedIn = true;
    }
}
