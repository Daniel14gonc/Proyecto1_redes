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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

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

    public Connection() {
        messages = new HashMap<String, ArrayList<String>>();
        groupMessages = new HashMap<String, ArrayList<String>>();
        groupChatCredentials = new HashMap<String, MultiUserChat>();
        screenCleaner = "\n\n\n\n\n\n\n";
        scanner = new Scanner(System.in);
        currentChatUser = "";
    }

    public void connect(String server) {
        if (connection == null) {
            try {
                config = XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(server)
                        .setHost(server)
                        .setPort(5222)
                        .build();
                connection = new XMPPTCPConnection(config);
                connection.connect();
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
        // chatManager = null;
    }

    private void resetChatManager() {
        chatManager = ChatManager.getInstanceFor(connection);
        chatListener = null;
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
            connection.login(username, password);
            System.out.println("Registro exitoso e inicio de sesion exitosos.");
            return 0;
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Credenciales invalidas. No te pudimos registrar.");
            return -1;
        }

    }

    private void addStanzaListener() {
        StanzaFilter presenceFilter = new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return stanza instanceof Presence && ((Presence) stanza).getType().equals(Presence.Type.subscribe);
            }
        };

        connection.addAsyncStanzaListener((stanza) -> {
            Presence presence = (Presence) stanza;
            String from = presence.getFrom().toString();
            if (presence.getType().equals(Presence.Type.subscribe)) {
                System.out.println(yellow + "Has recibido una solicitud de suscripción de: " + from + ". Aceptada automaticamente." + reset);
                Presence subscribedPresence = new Presence(Presence.Type.subscribed);
                subscribedPresence.setTo(presence.getFrom());
                try {
                    connection.sendStanza(subscribedPresence);
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (presence.getType().equals(Presence.Type.subscribed)) {
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
            }
            System.out.print("\n> ");
        }, presenceFilter);
    }

    public int login(String username, String password) {
        try {
            if (!connection.isConnected()) {
                connection = new XMPPTCPConnection(config);
                connection.connect();
            }
            connection.login(username, password);
            sendAvailableStanza();
            roster = Roster.getInstanceFor(connection);
            addStanzaListener();
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.reloadAndWait();
            currentUser = username;
            System.out.println("Inicio de sesion exitoso.");
            resetChatManager();
            return 0;
        } catch (Exception e) {
            System.out.println("Credenciales invalidas. No puedes iniciar sesion");
            return -1;
        }

    }

    public int logout() {
        try {
            Thread.sleep(150);
        } catch (Exception e) {
            System.out.println("AcA?");
            e.printStackTrace();
        }
        removeChatListener();
        messages.clear();
        connection.disconnect();
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

            // Print the list of contacts and their groups
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
        // String userJID = "gon20293@alumchat.xyz";
        try {
            /*BareJid jid = JidCreate.bareFrom(userJID);
            presence.setTo(jid);*/
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
        @Override
        public void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat) {
            if (chat.getXmppAddressOfChatPartner().toString().equals(currentChatUser)) {
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
