package com.example;

import java.util.ArrayList;
import java.util.Scanner;
public class Menu {
    private Scanner scanner;
    public Menu() {
        scanner = new Scanner(System.in);
    }

    public int showInitialMenu() {
        boolean optionChosen = false;
        int option = 0;
        while (!optionChosen) {
            try {

                System.out.println("Por favor ingresa la opcion que deseas:");
                System.out.println("1. Registrarse en el servidor");
                System.out.println("2. Login");
                System.out.println("3. Salir");
                System.out.print("> ");
                option = Integer.parseInt(scanner.nextLine().trim());
                optionChosen = true;
            } catch (Exception e){
                System.out.println("Ingresa una opcion correcta");
            }
        }
        return option;
    }

    public ArrayList<String> askCredentials() {
        System.out.println("\nIngresa tu nombre de usuario");
        System.out.print("> ");
        String username = scanner.nextLine();
        System.out.println("Ingresa tu contraseña");
        System.out.print("> ");
        String password = scanner.nextLine();
        ArrayList<String> credentials = new ArrayList<String>();
        credentials.add(username);
        credentials.add(password);
        return credentials;
    }

    public int showActionsMenu() {
        boolean optionChosen = false;
        int option = 0;
        while (!optionChosen) {
            try {
                System.out.println("\nIngresa la opcion que desees:");
                System.out.println("1. Mostrar tus contactos y su estado.");
                System.out.println("6. Crea o modifica tu mensaje de presencia.");
                System.out.println("10. Cerrar sesion.");
                System.out.println("11. Eliminar cuenta.");
                System.out.println("12. Salir");
                System.out.print("> ");
                option = Integer.parseInt(scanner.nextLine());
                optionChosen = true;
            } catch (Exception e) {
                System.out.println("Error, ingresa una opcion valida.");
            }
        }

        return option;
    }

    public void showRoster(String roster) {
        System.out.println(roster);
    }

    public String getStatusMessage() {
        System.out.println("Ingresa tu nuevo mensaje de presencia");
        System.out.print("> ");
        String message = scanner.nextLine();
        return message;
    }
}
