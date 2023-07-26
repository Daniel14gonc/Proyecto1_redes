package com.example;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;

public class Connection {

    private AbstractXMPPConnection connection;

    public void connect(String server) {
        if (connection == null) {
            try {
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(server)
                        .setHost(server)
                        .setPort(5222)
                        .build();

                connection = new XMPPTCPConnection(config);
                connection.connect();
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
                connection.connect();
            }
            connection.login(username, password);
            System.out.println("Inicio de sesion exitoso.");
            return 0;
        } catch (Exception e) {
            System.out.println("Credenciales invalidas. No puedes iniciar sesion");
            return -1;
        }

    }

    public int logout() {
        connection.disconnect();
        System.out.println("Se ha cerrado sesion exitosamente.");
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

    /*public void connect(String server) {
        this.server = server;
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setXmppDomain(server)
                    .setHost(server)
                    .setPort(5222)
                    .build();

            AbstractXMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            connection.login();

            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.chatWith(JidCreate.from("echobot@alumchat.xyz").asEntityBareJidIfPossible());
            chat.send("Hello");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*XMPPTCPConnectionConfiguration config = null;
        try {
            config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("baeldung2","baeldung2")
                    .setXmppDomain("jabb3r.org")
                    .setHost("jabb3r.org")
                    .build();

            AbstractXMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            connection.login();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }*/
}
