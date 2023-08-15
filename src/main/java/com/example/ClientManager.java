package com.example;

import java.util.ArrayList;

/**
 *  This class allows handles the communication between user and the connection to the server.
 * @author Daniel Gonzalez
 */

public class ClientManager {
    private Connection connection;
    private Menu menu;

    private boolean terminate;

    private boolean loggedIn = true;

    /**
     * Method for intializing the connection to server and show initial menu.
     */
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

    /**
     * Method to get the information about the creation of groupchat.
     * It shows and then handles the option
     */
    private void manageActionsAfterAuthorization() {
        int option = 0;
        while (loggedIn && !terminate) {
            connection.resetChatUser();
            option = menu.showActionsMenu();
            System.out.println(option);
            handleAction(option);
        }
    }

    /**
     * Method that handles the action chosen from user after authorization.
     */
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
            case 8:
                logout();
                break;
            case 9:
                deleteAccount();
                break;
            case 10:
                loggedIn = false;
                terminate = true;
                logout();
                break;
            default:
                System.out.println("");
                break;
        }
    }

    /**
     * Method that handles if the user wants to register or log in.
     */
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

    /**
     * Method for executing the actions needed for a user to chat with another.
     */
    private void chatWithUser() {
        String user = menu.getUserToChat();
        connection.chatWithUser(user);
    }

    /**
     * Method to send a file via the connection.
     */
    private void sendFile() {
        ArrayList<String> data = menu.getFileAndUserInfo();
        connection.sendFile(data.get(0), data.get(1));
    }

    /**
     * Method to handle the option chosen from a user in the group chat section.
     */
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
        }
    }

    /**
     * Method to ask information about group chat creation and creation of the group chat via connection.
     */
    private void createGroupChat() {
        ArrayList<String> data = menu.getGroupChatInfoCreation(1);
        connection.createGroupChat(data.get(0), data.get(1));
    }

    /**
     * Method to invite user to group chat.
     */
    private void inviteToGroupChat() {
        ArrayList<String> data = menu.inviteToGroupChat();
        connection.inviteToGroupChat(data.get(0), data.get(1));
    }

    /**
     * Method to allow user to chat in a group chat.
     */
    private void chatInGroup() {
        String groupName = menu.getGroupNameToChat();
        connection.useGroupChat(groupName);
    }

    /**
     * Method to allow user to join group chat given its name.
     */
    private void joinGroupChat() {
        ArrayList<String> data = menu.getGroupChatInfoCreation(2);
        connection.joinGroupChat(data.get(0), data.get(1));
    }

    /**
     * Method to allow user to change their status.
     */
    private void handleStatusMessageChange() {
        String[] data = menu.getStatusMessage();
        connection.setStatusMessage(data);
    }

    /**
     * Method to allow a user to subscribe to another user.
     */
    private void addUser() {
        String user = menu.getUserToSubscribe();
        connection.sendSubscription(user);
    }

    /**
     * Method to show details of a determined contact selected from the user.
     */
    private void showContactDetails() {
        String contact = menu.getContact();
        connection.getUserDetails(contact);
    }

    /**
     * Method to show the contacts of the user.
     */
    private void showRoster() {
        menu.showRoster(connection.getRoster());
    }

    /**
     * Method to register a new user into the server.
     */
    private int register() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        reset_state();
        return connection.register(username, password);
    }

    /**
     * Method to login user into the server.
     */
    private int login() {
        ArrayList<String> credentials = menu.askCredentials();
        String username = credentials.get(0);
        String password = credentials.get(1);
        reset_state();
        return connection.login(username, password);
    }

    /**
     * Method to logout user from server.
     */
    private void logout() {
        connection.logout();
        loggedIn = false;
    }

    /**
     * Method to delete account from user.
     */
    private void deleteAccount() {
        connection.deleteAccount();
        loggedIn = false;
    }

    /**
     * Method to change state of the menu.
     */
    private void reset_state() {
        terminate = false;
        loggedIn = true;
    }
}
