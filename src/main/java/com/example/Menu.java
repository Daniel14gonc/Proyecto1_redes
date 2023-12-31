package com.example;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to show user a menu in a CLI interface.
 * @author Daniel Gonzalez
 */

public class Menu {
    private Scanner scanner;
    public Menu() {
        scanner = new Scanner(System.in);
    }

    /**
     * Method to show initial menu of register, login and exit.
     * @return  it returns the option selected of the initial menu.
     */
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

    /**
     * Method for asking credentials when login.
     * @return a list with username and password
     */
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

    /**
     * Method to chat actions.
     * @return number of option chosen.
     */
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
                System.out.println("8. Cerrar sesion.");
                System.out.println("9. Eliminar cuenta.");
                System.out.println("10. Salir");
                System.out.print("> ");
                option = Integer.parseInt(scanner.nextLine());
                optionChosen = true;
            } catch (Exception e) {
                System.out.println("Error, ingresa una opcion valida.");
            }
        }

        return option;
    }

    /**
     * Method to print the roster of the user.
     * @param roster the list of the contacts.
     */
    public void showRoster(String roster) {
        System.out.println(roster);
    }

    /**
     * Method to get the new status type, mode and message.
     * @return array of 3 elements with the options selected.
     */
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

    /**
     * Method to get the user to chat.
     * @return the user specified to chat
     */
    public String getUserToChat() {
        System.out.println("Ingresa el usuario con el que quieres hablar (debes agregar el dominio, por ejemplo @alumchat.xyz).");
        System.out.print("> ");
        String user = scanner.nextLine();
        return user;
    }

    /**
     * Method to get the user and file to send.
     * @return a list of 2 elements containing the user and the file to send.
     */
    public ArrayList<String> getFileAndUserInfo() {
        System.out.println("Ingresa el nombre del usuario al que quieres mandar el archivo (recurdan incluir el dominio, por ejemplo @alumchat.xyz).");
        System.out.print("> ");
        String user = scanner.nextLine();
        System.out.println("Ingresa la ruta del archivo que quieres enviar.");
        System.out.print("> ");
        String file = scanner.nextLine();
        ArrayList<String> data = new ArrayList<String>();
        data.add(user);
        data.add(file);
        return data;
    }

    /**
     * Method to get the user to subscribe from terminal
     * @return a string containing the user to subscribe.
     */
    public String getUserToSubscribe() {
        System.out.println("Ingresa el usuario que quieres agregar a tus contactos (debes agregar el dominio, por ejemplo @alumchat.xyz).");
        System.out.print("> ");
        String user = scanner.nextLine();
        return user;
    }

    /**
     * Method to get the contact which the user wants to see details
     * @return a string with the username of the contact
     */
    public String getContact() {
        System.out.println("Ingresa el usuario del contacto del que deseas ver los detalles.");
        System.out.print("> ");
        String contact = scanner.nextLine();
        return contact;
    }

    /**
     * Method to get the option of the action in groupchat.
     * @return the number of option chosen.
     */
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
                System.out.println("5. Salir.");
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

    /**
     * Method to select a group and a user to invite to.
     * @return a list of 2 elements containing the group name and the user.
     */
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

    /**
     * Method to get the groupname where user wants to chat.
     * @return a string containing the groupname.
     */
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

    /**
     * Method to get the information about the creation of groupchat.
     * @param option it indicates whether we are creating a group or joining a group. Its values are 1 or anything else.
     * @return a list of 2 elements with the new group name and the alias the user wants to have.
     */
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
