package com.example;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.debugger.ConsoleDebugger;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.*;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.time.Instant;

public class Connection {

    private AbstractXMPPConnection connection;
    private  Roster roster;
    private XMPPTCPConnectionConfiguration config;

    private static HashMap<String, ArrayList<String>> messages;
    private static HashMap<String, ArrayList<String>> groupMessages;
    private static HashMap<String, MultiUserChat> groupChatCredentials;
    private String screenCleaner;
    private static final String red = "\u001B[31m";
    private static final String green = "\u001B[32m";
    private static final String yellow = "\u001B[33m";
    private static final String blue = "\u001B[34m";
    private static final String reset = "\u001B[0m";
    private static Semaphore semaphore = new Semaphore(1);
    private ChatManager chatManager;
    private Scanner scanner;
    public static String currentChatUser;
    private ChatMessageListener chatListener;
    private MultiUserChatManager manager;
    private String currentUser = null;
    private boolean stanzaListenerAdded;
    private FileTransferManager fileManager;

    public Connection() {
        messages = new HashMap<String, ArrayList<String>>();
        groupMessages = new HashMap<String, ArrayList<String>>();
        groupChatCredentials = new HashMap<String, MultiUserChat>();
        screenCleaner = "\n\n\n\n\n\n\n";
        scanner = new Scanner(System.in);
        currentChatUser = "";
        stanzaListenerAdded = false;
    }

    public void connect(String server) {
        if (connection == null) {
            try {
                config = XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(server)
                        .setHost(server)
                        .setPort(5222)
                        .setResource("resource")
                        .build();
                connection = new XMPPTCPConnection(config);
                connection.connect();
                Thread.sleep(150);
                roster = Roster.getInstanceFor(connection);
                fileManager = FileTransferManager.getInstanceFor(connection);
                createRosterListener();
                createFileTransferListener();
                addStanzaListener();
                // chatManager = ChatManager.getInstanceFor(connection);
                /*connection.addAsyncStanzaListener(new StanzaListener() {
                    @Override
                    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                        System.out.println("Received: " + packet.toXML());
                    }
                }, new StanzaFilter() {
                    @Override
                    public boolean accept(Stanza stanza) {
                        return true;
                    }
                });*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeChatListener() {
        if (chatListener != null)
            chatManager.removeIncomingListener(chatListener);
        chatListener = null;
    }

    private void resetChatManager() {
        chatManager = ChatManager.getInstanceFor(connection);
        removeChatListener();
        chatListener = new ChatMessageListener();
        chatManager.addIncomingListener(chatListener);
    }

    public int register(String username, String password) {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            Localpart localPartUsername = Localpart.from(username);
            accountManager.createAccount(localPartUsername, password);
            // connection.login(username, password);
            login(username, password);
            System.out.println("Registro exitoso e inicio de sesion exitosos.");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Credenciales invalidas. No te pudimos registrar.");
            return -1;
        }

    }

    private void addStanzaListener() {
        /*connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                System.out.println("Received: " + packet.toXML());
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return true;
            }
        });*/
        if (!stanzaListenerAdded) {
            StanzaFilter presenceFilter = new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return stanza instanceof Presence && ((Presence) stanza).getType().equals(Presence.Type.subscribe);
                }
            };

            connection.addAsyncStanzaListener((stanza) -> {
                System.out.println("Received: " + stanza.toXML());
                Presence presence = (Presence) stanza;
                String from = presence.getFrom().toString();
                if (presence.getType().equals(Presence.Type.subscribe)) {
                    System.out.println(yellow + "Has recibido una solicitud de suscripción de: " + from + ". Aceptada automaticamente." + reset);
                    Presence subscribedPresence = new Presence(Presence.Type.subscribe);
                    System.out.println(presence.getFrom());
                    subscribedPresence.setTo(presence.getFrom());
                    try {
                        connection.sendStanza(subscribedPresence);
                        Thread.sleep(150);
                    } catch (SmackException.NotConnectedException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } /*else if (presence.getType().equals(Presence.Type.subscribed)) {
                    System.out.println(yellow + "Tu solicitud de suscripción a " + from + " ha sido aceptada." + reset);
                } else if (presence.getType().equals(Presence.Type.available)) {
                    System.out.println(yellow + "El usuario " + from + " ahora está disponible." + reset);
                } else if (presence.getType().equals(Presence.Type.unavailable)) {
                    System.out.println(yellow + "El usuario " + from + " ya no está disponible." + reset);
                }

                Presence.Mode mode = presence.getMode();
                if (mode == Presence.Mode.away) {
                    System.out.println(yellow + from + " está ausente." + reset);
                } else if (mode == Presence.Mode.dnd) {
                    System.out.println(yellow + from + " no quiere que lo molesten." + reset);
                } else if (mode == Presence.Mode.available) {
                    System.out.println(yellow + from + " está en modo disponible." + reset);
                } else if (mode == Presence.Mode.chat) {
                    System.out.println(yellow + from + " está en disponible para chatear." + reset);
                }*/
                System.out.print("\n> ");
            }, presenceFilter);
            stanzaListenerAdded = true;
        }
    }

    public void createRosterListener() {
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {

            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {

            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {

            }

            public void presenceChanged(Presence presence) {
                System.out.println(yellow + "Presence changed from user " + presence.getFrom().asBareJid().toString() + ". Type: " + presence.getType() + "; Mode: " + presence.getMode() + "; Status: " + presence.getStatus() + reset);
                System.out.print("\n> ");
            }
        });
    }

    public void createFileTransferListener() {
        fileManager.addFileTransferListener(new FileTransferListener() {
            public void fileTransferRequest(FileTransferRequest request) {
                // Procesar la solicitud de transferencia de archivo entrante
                IncomingFileTransfer transfer = request.accept();
                System.out.println("te enviaron un file");
                try {
                    transfer.receiveFile(new File("file.txt")); // Cambia esto por la ruta donde quieras guardar el archivo
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int login(String username, String password) {
        try {
            if (!connection.isConnected()) {
                connection = new XMPPTCPConnection(config);
                connection.connect();
                Thread.sleep(150);
                roster = Roster.getInstanceFor(connection);
                fileManager = FileTransferManager.getInstanceFor(connection);
                createRosterListener();
                createFileTransferListener();
                addStanzaListener();
            } else {
                roster = Roster.getInstanceFor(connection);
                fileManager = FileTransferManager.getInstanceFor(connection);
            }
            connection.login(username, password);
            sendAvailableStanza();
            if (!roster.isLoaded())
                roster.reloadAndWait();
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            currentUser = username;
            System.out.println("Inicio de sesion exitoso.");
            resetChatManager();

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Credenciales invalidas. No puedes iniciar sesion");
            return -1;
        }

    }

    public int logout() {
        removeChatListener();
        messages.clear();
        connection.disconnect();
        try {
            Thread.sleep(300);
        } catch (Exception e) {

        }

        System.out.println("Se ha cerrado sesion exitosamente.\n");
        return 0;
    }

    public int deleteAccount() {
        try {

            messages.clear();
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            System.out.println("Se elimino cuenta exitosamente.");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No pudimos eliminar tu cuenta. Lo sentimos :(");
            return -1;
        }
    }

    public void sendSubscription(String user) {
        try {
            Presence presence = new Presence(Presence.Type.subscribe);
            BareJid userJid = JidCreate.bareFrom(user);
            System.out.println(userJid);
            presence.setTo(userJid);
            connection.sendStanza(presence);
            Thread.sleep(400);
        } catch (Exception e) {
            System.out.println("No pudimos enviar tu solicitud de suscripción. Prueba de nuevo.");
        }

    }

    public void getUserDetails(String user) {
        try {
            Jid jid = JidCreate.from(user);
            System.out.println(jid);

            RosterEntry entry = roster.getEntry(jid.asBareJid());
            if (entry != null) {
                System.out.println("User's JID: " + entry.getJid());

                Presence presence = roster.getPresence(jid.asBareJid());
                if (presence.isAvailable()) {
                    System.out.println("Status: Online");
                } else {
                    System.out.println("Status: Offline");
                }
                System.out.println("Presence mode: " + presence.getMode());
                System.out.println("Presence type: " + presence.getType());
            } else {
                System.out.println("Usuario no presente en roster.");
            }
        } catch (Exception e) {
            System.out.println("No pudimos obtener el usuario :(");
        }
    }

    public String getRoster() {
        try {
            roster = Roster.getInstanceFor(connection);
            roster.reloadAndWait();
            String result = "";
            result += "Contactos:\n";
            for (RosterEntry entry : roster.getEntries()) {
                if (entry.getName() != null) {
                    result += " - " + entry.getName() + " (" + entry.getUser() + ")\n";
                }
                else {
                    result += " - " + " (" + entry.getUser() + ")\n";
                }

                BareJid userJid = JidCreate.bareFrom(entry.getUser());
                Presence presence = roster.getPresence(userJid);
                if (presence.isAvailable()) {
                    result += "   * Status: Online\n";
                } else {
                    result += "   * Status: Offline\n";
                }
                result += "   * Mode: " + presence.getMode() + "\n";
                String statusMessage = (presence.getStatus() != null) ? presence.getStatus() : "none";
                result += "   * Status Message: " + statusMessage + "\n";
                for (RosterGroup group : entry.getGroups()) {
                    result += "   - Group: " + group.getName() + "\n";
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Presence.Mode getMode(String option) {
        switch (option) {
            case "2":
                return Presence.Mode.away;
            case "3":
                return Presence.Mode.chat;
            case "4":
                return Presence.Mode.xa;
            case "5":
                return Presence.Mode.dnd;
            default:
                return Presence.Mode.available;
        }
    }

    private Presence.Type getType(String option) {
        switch (option) {
            case "2":
                return Presence.Type.unavailable;
            default:
                return Presence.Type.available;
        }
    }

    private void sendAvailableStanza() {
        Presence presence = new Presence(Presence.Type.available);
        try {
            connection.sendStanza(presence);
            Thread.sleep(150);
        } catch (Exception e) {
            System.out.println("No pudimos enviar la presencia de conexion.");
        }
    }

    public void setStatusMessage(String[] data) {
        String message = data[0];
        Presence.Type type = getType(data[1]);
        Presence.Mode mode = getMode(data[2]);
        Presence presence = new Presence(type);
        presence.setStatus(message);
        presence.setMode(mode);
        try {
            connection.sendStanza(presence);
            Thread.sleep(300);
            System.out.println("Status modificado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetChatUser() {
        currentChatUser = null;
    }

    private void sendMessage(String user, String message) {
        try {
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.chatWith(JidCreate.from(user).asEntityBareJidIfPossible());
            chat.send(message);
        } catch (Exception e) {
            System.out.println("Lo sentimos, no pudimos enviar tu mensaje :(.");
        }
    }

    public void chatWithUser(String user) {
        String userJID = user;
        System.out.println(screenCleaner);
        System.out.println("Iniciando chat, escriba 'exit' para salir...");
        System.out.println(blue + "--------------- Chat with " + userJID + " ---------------" + reset);
        currentChatUser = user;
        boolean finishChat = false;
        try {
            semaphore.acquire();
            if (messages.containsKey(userJID)) {
                ArrayList<String> chatMessages = messages.get(userJID);
                for (int i = 0; i < chatMessages.size(); i++) {
                    System.out.println(chatMessages.get(i));
                }
            }
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!finishChat) {
            if (!messages.containsKey(userJID)) {
                try {
                    semaphore.acquire();
                    ArrayList<String> newMessages = new ArrayList<String>();
                    messages.put(userJID, newMessages);
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.print(green + "> " + reset);
            String message = scanner.nextLine();
            if (message.toLowerCase().equals("exit")) {
                finishChat = true;
            } else {
                sendMessage(user, message);
                messages.get(userJID).add("You: " + message);
            }
        }
        System.out.println(screenCleaner);
    }

    private String convertToBase64 (String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(String path) {
        int lastIndex = path.lastIndexOf(".");
        if (lastIndex != -1 && lastIndex < path.length() - 1) {
            return path.substring(lastIndex + 1);
        }
        return "";
    }

    public void sendFile(String user, String path) {
        try {
            String fileExtension = getFileExtension(path);
            if (fileExtension.equals("")) {
                System.out.println("La ruta de archivo no tiene la extension adecuada. No lo pudimos enviar.");
            } else {
                String fileContent = "file|" + fileExtension + "|" + convertToBase64(path);
                sendMessage(user, fileContent);
                System.out.println("Archivo enviado con exito");
            }
        } catch (Exception e) {
            System.out.println("Algo salio mal, no pudimos enviar el archivo :(");
        }
    }

    private void addGroupChatToHistory(String groupName) {
        try {
            semaphore.acquire();
            if (! groupMessages.containsKey(groupName)) {
                groupMessages.put(groupName, new ArrayList<String>());
            }
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMessageToGroupHistory(String groupName, String message) {
        try {
            semaphore.acquire();
            groupMessages.get(groupName).add(message);
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMUCListener(MultiUserChat muc, String roomName, String nickname) {
        muc.addMessageListener((from) -> {
            String body = from.getBody();
            Jid fromJid = from.getFrom();
            Resourcepart senderJid = fromJid.getResourceOrNull();
            if (senderJid != null) {
                String sender = senderJid.toString();
                String message = sender + ": " + body;
                if (sender.equals(nickname)) {
                    addMessageToGroupHistory(roomName, green + message + reset);
                } else {
                    addMessageToGroupHistory(roomName, blue + message + reset);
                }

                if (currentChatUser != null && currentChatUser.equals(roomName)) {
                    if (!sender.equals(roomName)) {
                        if (sender.equals(nickname)) {
                            System.out.println(green + message + reset);
                        } else {
                            System.out.println(blue + message + reset);
                        }
                        System.out.print("> ");
                    }
                } else {
                    System.out.println(yellow + "\nIncoming message from group " + roomName + ". User " + sender + reset + ".\n");
                    System.out.print("> ");
                }
            }

        });
    }

    private void newCredentialGroupChat(String groupName, MultiUserChat muc) {
        groupChatCredentials.put(groupName, muc);
    }

    public void createGroupChat(String chatRoomName, String nickname) {
        try {
            manager = MultiUserChatManager.getInstanceFor(connection);
            String roomName = chatRoomName + "@conference.alumchat.xyz";
            EntityBareJid roomJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(roomJid);
            Resourcepart resource = Resourcepart.from(nickname);
            muc.create(resource).makeInstant();
            addGroupChatToHistory(roomName);
            addMUCListener(muc, roomName, nickname);
            newCredentialGroupChat(roomName, muc);
            System.out.println("Hemos creado el grupo con éxito.");
        } catch (Exception e) {
            System.out.println("Algo salió mal. No pudimos crear el grupo :(");
        }
    }

    private void invite(MultiUserChat muc, String user) {
        try {
            muc.invite(JidCreate.entityBareFrom(user), "Te invito a mi grupo.");
        } catch (Exception e) {
            System.out.println("No pudimos invitarlo al grupo.");
        }
    }

    public void inviteToGroupChat(String groupName, String user) {
        groupName = groupName + "@conference.alumchat.xyz";
        if (groupChatCredentials.containsKey(groupName)) {
            MultiUserChat muc = groupChatCredentials.get(groupName);
            invite(muc, user);
            System.out.println("Se ha invitado bien al usuario.");
            System.out.print("\n> ");
        } else {
            try {
                MultiUserChat muc = groupChatCredentials.get(groupName);
                String nickname = connection.getUser().toString();
                createGroupChat(groupName, nickname);
                invite(muc, user);
                System.out.print("\n> ");
            } catch (Exception e) {
                System.out.println("No pudimos hacer la invitación al grupo :(");
            }
        }
    }

    public void deleteGroupChat(String chatRoomName) {
        try {
            manager = MultiUserChatManager.getInstanceFor(connection);
            String roomName = chatRoomName + "@conference.alumchat.xyz";
            EntityBareJid roomJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(roomJid);
            muc.destroy(null, null);
            System.out.println("Hemos borrado el grupo con éxito.");
        } catch (Exception e) {
            System.out.println("Algo salió mal. No pudimos borrar el grupo :(");
        }
    }

    public void joinGroupChat(String chatRoomName, String nickname) {
        try {
            manager = MultiUserChatManager.getInstanceFor(connection);
            String roomName = chatRoomName + "@conference.alumchat.xyz";
            EntityBareJid roomJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(roomJid);
            Resourcepart resource = Resourcepart.from(nickname);
            muc.join(resource);
            addGroupChatToHistory(roomName);
            addMUCListener(muc, roomName, nickname);
            newCredentialGroupChat(roomName, muc);
            System.out.println("Te has unido a la sala con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println("Algo salió mal. No pudimos unirte el grupo :(");
        }
    }

    private void sendGroupMessage(String messageText, MultiUserChat muc) {
        try {
            Message message = new Message();
            message.setBody(messageText);
            message.setType(Message.Type.groupchat);

            muc.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(red + "\nNo pudimos enviar tu mensaje :(\n" + reset);
            System.out.print("> ");
        }

    }

    public void useGroupChat(String chatRoomName) {
        String roomName = chatRoomName + "@conference.alumchat.xyz";
        if (groupChatCredentials.containsKey(roomName)) {
            System.out.println("Iniciando chat, escriba 'exit' para salir...");
            System.out.println(blue + "--------------- Groupchat " + chatRoomName + " ---------------" + reset);
            currentChatUser = roomName;
            boolean finishChat = false;
            try {
                semaphore.acquire();
                if (groupMessages.containsKey(roomName)) {
                    ArrayList<String> chatMessages = groupMessages.get(roomName);
                    for (int i = 0; i < chatMessages.size(); i++) {
                        System.out.println(chatMessages.get(i));
                    }
                }
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MultiUserChat muc = groupChatCredentials.get(roomName);
            String message = null;
            while(!finishChat) {
                System.out.print(green + "> " + reset);
                message = scanner.nextLine();
                if (message.toLowerCase().equals("exit")) {
                    finishChat = true;
                } else {
                    sendGroupMessage(message, muc);
                    /*String formattedMessage = "You: " + message;
                    addMessageToGroupHistory(roomName, formattedMessage);*/
                }
            }
        }
    }

    private static class ChatMessageListener implements IncomingChatMessageListener {
        private void convertBase64ToFile(String base64String, String filePath) {
            try {

                byte[] fileBytes = Base64.getDecoder().decode(base64String);
                Files.write(Paths.get(filePath), fileBytes);
                System.out.println(blue + "Hemos guardado el archivo con exito en " + filePath + reset);
            } catch (IOException e) {
                System.out.println("No pudimos guardar el archivo porque el formato estaba mal.");
            }
            System.out.print("\n> ");
        }

        private String getFileExtension(String message) {
            String extension = message.split("\\|")[1];
            return extension;
        }

        private String formFileName(String message, String user) {
            String extension = getFileExtension(message);
            Instant instant = Instant.now();
            long timestamp = instant.toEpochMilli();
            String name = "./Files/" + user + "_" + timestamp + "." + extension;
            return  name;
        }

        private String getFileContent(String message) {
            String extension = message.split("\\|")[2];
            return extension;
        }

        private boolean isFileFormatCorrect(String message) {
            try {
                String[] extension = message.split("\\|");
                if (extension.length != 3) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        public void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat) {
            if (message.getBody().substring(0, 4).equals("file")) {
                if (isFileFormatCorrect(message.getBody())) {
                    System.out.println(yellow + "File received from: " + chat.getXmppAddressOfChatPartner().toString() + reset);
                    String fileName = formFileName(message.getBody(), chat.getXmppAddressOfChatPartner().toString());
                    String fileContent = getFileContent(message.getBody());
                    convertBase64ToFile(fileContent, fileName);
                }
            }
            else if (chat.getXmppAddressOfChatPartner().toString().equals(currentChatUser)) {
                System.out.println(blue + "User: " + message.getBody() + reset);
                messages.get(chat.getXmppAddressOfChatPartner().toString()).add(blue + "User: " + message.getBody() + reset);
                System.out.println("");
                System.out.print("> ");
            } else {
                System.out.println(yellow + "Incoming message from: " + chat.getXmppAddressOfChatPartner() + reset);
                System.out.print("> ");
                try {
                    semaphore.acquire();
                    if (messages.containsKey(chat.getXmppAddressOfChatPartner().toString())) {
                        messages.get(chat.getXmppAddressOfChatPartner().toString()).add(blue + "User: " + message.getBody() + reset);
                    } else {
                        ArrayList<String> userMessages = new ArrayList<String>();
                        userMessages.add(blue + "User: " + message.getBody() + reset);
                        messages.put(chat.getXmppAddressOfChatPartner().toString(), userMessages);
                    }
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}