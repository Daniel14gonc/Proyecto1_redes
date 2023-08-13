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
            connection.resetChatUser();
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
            case 2:
                addUser();
                break;
            case 3:
                showContactDetails();
                break;
            case 4:
                chatWithUser();
                break;
            case 5:
                handleGroupChat();
                break;
            case 6:
                handleStatusMessageChange();
                break;
            case 7:
                sendFile();
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
                logout();
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

    private void chatWithUser() {
        String user = menu.getUserToChat();
        connection.chatWithUser(user);
    }

    private void sendFile() {
        connection.sendFile();
    }

    private void handleGroupChat() {
        int option = menu.groupChat();
        if (option == 1) {
            createGroupChat();
        } else if (option == 2) {
            inviteToGroupChat();
        } else if (option == 3) {
            joinGroupChat();
        } else if (option == 4) {
            chatInGroup();
        } else if (option == 5) {
            deleteGroupChat();
        }
    }

    private void createGroupChat() {
        ArrayList<String> data = menu.getGroupChatInfoCreation(1);
        connection.createGroupChat(data.get(0), data.get(1));
    }

    private void inviteToGroupChat() {
        ArrayList<String> data = menu.inviteToGroupChat();
        connection.inviteToGroupChat(data.get(0), data.get(1));
    }

    private void chatInGroup() {
        String groupName = menu.getGroupNameToChat();
        connection.useGroupChat(groupName);
    }

    private void deleteGroupChat() {
        String groupChat = menu.getGroupChatInfoDeletion();
        connection.deleteGroupChat(groupChat);
    }

    private void joinGroupChat() {
        ArrayList<String> data = menu.getGroupChatInfoCreation(2);
        connection.joinGroupChat(data.get(0), data.get(1));
    }

    private void handleStatusMessageChange() {
        String[] data = menu.getStatusMessage();
        connection.setStatusMessage(data);
    }

    private void addUser() {
        String user = menu.getUserToSubscribe();
        connection.sendSubscription(user);
    }

    private void showContactDetails() {
        String contact = menu.getContact();
        connection.getUserDetails(contact);
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
        loggedIn = false;
    }

    private void reset_state() {
        terminate = false;
        loggedIn = true;
    }
}
