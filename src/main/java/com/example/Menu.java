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
                System.out.println("2. Agregar usuario a contactos.");
                System.out.println("3. Mostrar detalles de contacto de usuario.");
                System.out.println("4. Habla con un usuario.");
                System.out.println("5. Chat grupal.");
                System.out.println("6. Crea o modifica tu mensaje de presencia.");
                System.out.println("7. Enviar archivo.");
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

    public String[] getStatusMessage() {
        boolean finished = false;
        String message = null;
        String type = null;
        String mode = null;
        while (!finished) {
            try {
                System.out.println("Ingresa tu nuevo tipo de presencia (escribe el número de opción).");
                System.out.println("1. Disponible");
                System.out.println("2. No disponible");
                System.out.print("> ");
                type = scanner.nextLine();
                int convert = Integer.parseInt(type);
                System.out.println("Ingresa el modo de tu status (escribe el número de opción).");
                System.out.println("1. Available");
                System.out.println("2. Away");
                System.out.println("3. Chat");
                System.out.println("4. Extended away");
                System.out.println("5. Do not disturb");
                System.out.print("> ");
                mode = scanner.nextLine();
                convert = Integer.parseInt(mode);
                System.out.println("Ingresa tu nuevo mensaje de presencia");
                System.out.print("> ");
                message = scanner.nextLine();
                finished = true;
            } catch (Exception e) {
                System.out.println("Ingresa correctamente las opciones.  ¡Problemos de nuevo!\n");
            }

        }
        String[] result = new String[3];
        result[0] = message;
        result[1] = type;
        result[2] = mode;
        return result;
    }

    public String getUserToChat() {
        System.out.println("Ingresa el usuario con el que quieres hablar (debes agregar el dominio, por ejemplo @alumchat.xyz).");
        System.out.print("> ");
        String user = scanner.nextLine();
        return user;
    }

    public String getUserToSubscribe() {
        System.out.println("Ingresa el usuario que quieres agregar a tus contactos (debes agregar el dominio, por ejemplo @alumchat.xyz).");
        System.out.print("> ");
        String user = scanner.nextLine();
        return user;
    }

    public String getContact() {
        System.out.println("Ingresa el usuario del contacto del que deseas ver los detalles.");
        System.out.print("> ");
        String contact = scanner.nextLine();
        return contact;
    }

    public int groupChat() {
        boolean wellResponseFormat = false;
        int response = 0;
        while (!wellResponseFormat) {
            try {
                System.out.println("Bienvenido al chat grupal de XMPP. Ingresa la opcion que deseas");
                System.out.println("1. Crear chat grupal.");
                System.out.println("2. Invitar a alguien a un grupo.");
                System.out.println("3. Unirte a un chat grupal.");
                System.out.println("4. Hablar en un chat grupal.");
                System.out.println("5. Eliminar chat grupal.");
                System.out.println("6. Salir.");
                System.out.print("> ");
                String contact = scanner.nextLine();
                response = Integer.parseInt(contact);
                wellResponseFormat = true;
            } catch (Exception e) {
                System.out.println("Ingresa la opcion correctamente.");
            }

        }

        return response;
    }

    public ArrayList<String> inviteToGroupChat() {
        boolean infoWellSubmitted = false;
        String groupName = null;
        String user = null;
        while (!infoWellSubmitted) {
            System.out.println("Ingresa el nombre del grupo.");
            System.out.print("> ");
            groupName = scanner.nextLine();
            System.out.println("Ingresa el usuario al que quieres invitar (recuerda agregar su dominio como @alumchat.xyz).");
            System.out.print("> ");
            user = scanner.nextLine();
            if (groupName.isEmpty() || user.isEmpty()) {
                System.out.println("Ingresa correctamente los datos. Probemos de nuevo.\n");
            } else {
                infoWellSubmitted = true;
            }
        }
        ArrayList<String> data = new ArrayList<String>();
        data.add(groupName);
        data.add(user);
        return data;
    }

    public String getGroupNameToChat() {
        boolean infoWellSubmitted = false;
        String groupName = null;
        while (!infoWellSubmitted) {
            System.out.println("Ingrese el nombre del grupo en el que desea chatear.");
            System.out.print("> ");
            groupName = scanner.nextLine();
            infoWellSubmitted = true;
        }
        return groupName;
    }

    public String getGroupChatInfoDeletion() {
        boolean infoWellSubmitted = false;
        String groupName = null;
        while (!infoWellSubmitted) {
            System.out.println("Ingrese el nombre del grupo que desea borrar.");
            System.out.print("> ");
            groupName = scanner.nextLine();
            infoWellSubmitted = true;
        }
        return groupName;
    }

    public ArrayList<String> getGroupChatInfoCreation(int option) {
        boolean infoWellSubmitted = false;
        String groupName = null;
        String nickname = null;
        while (!infoWellSubmitted) {
            if (option == 1) {
                System.out.println("Ingresa el nombre del grupo que quieres crear");
            } else {
                System.out.println("Ingresa el nombre del grupo al que te quieres unir.");
            }
            System.out.print("> ");
            groupName = scanner.nextLine();
            System.out.println("Ingresa el apodo que quieres tener en el grupo.");
            System.out.print("> ");
            nickname = scanner.nextLine();
            if (groupName.isEmpty() || nickname.isEmpty()) {
                System.out.println("Ingresa correctamente los datos. Probemos de nuevo.\n");
            } else {
                infoWellSubmitted = true;
            }
        }
        ArrayList<String> data = new ArrayList<String>();
        data.add(groupName);
        data.add(nickname);
        return data;
    }
}
