package com.example;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.debugger.ConsoleDebugger;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

public class Connection {

    private AbstractXMPPConnection connection;
    private  Roster roster;
    XMPPTCPConnectionConfiguration config;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            e.printStackTrace();
            System.out.println("Credenciales invalidas. No te pudimos registrar.");
            return -1;
        }

    }

    public int login(String username, String password) {
        try {
            if (!connection.isConnected()) {
                connection = new XMPPTCPConnection(config);
                connection.connect();
                connection.addAsyncStanzaListener(new StanzaListener() {
                    @Override
                    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                        System.out.println("Received: " + packet.toXML());
                    }
                }, new StanzaFilter() {
                    @Override
                    public boolean accept(Stanza stanza) {
                        return true;
                    }
                });
            }
            connection.login(username, password);
            roster = Roster.getInstanceFor(connection);
            roster.reloadAndWait();
            System.out.println("Inicio de sesion exitoso.");
            return 0;
        } catch (Exception e) {
            System.out.println("Credenciales invalidas. No puedes iniciar sesion");
            return -1;
        }

    }

    public int logout() {
        connection.disconnect();
        System.out.println("Se ha cerrado sesion exitosamente.\n");
        return 0;
    }

    public int deleteAccount() {
        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            System.out.println("Se elimino cuenta exitosamente.");
            return 0;
        } catch (Exception e) {
            System.out.println("No pudimos eliminar tu cuenta. Lo sentimos :(");
            return -1;
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

    public void setStatusMessage(String message) {
        Presence presence = new Presence(Presence.Type.unavailable);
        presence.setStatus(message);
        presence.setMode(Presence.Mode.available);
        try {
            connection.sendStanza(presence);
            /*synchronized (this) {
                wait(150); // Esto hará que el hilo actual espere durante 10 segundos
            }*/
            Thread.sleep(150);
            System.out.println("Status modificado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
