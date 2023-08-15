package com.example;

/**
 * This class is the entry point to the application.
 * @author Daniel Gonzalez
 */
public class App {
    /**
     * Method initialize menu.
     */
    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();
        clientManager.manage();
    }
}