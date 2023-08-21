
# Project 1: XMPP chat

Welcome to my first project for the course **Computer Networks** at Universidad del Valle de Guatemala.



## Project description

This project consisted in an implementation of a client using the chat protocol XMPP. It required many options, which I will describe in the features section. The client runs on a CLI interface, and connects to a server with domain **@alumchat.xyz**.

I used some tools when buidling the project:

* 🍵 Java (version 18.0.1 or above)
* 🔥 Smack (version 4.4.0 or above)
* 🎮 Intellij
* 🧠 Maven

The programming language I chose was Java because of its relatively easy handling of async operations and for the library. Moreover, Smack is one of the best libraries out there for developing XMPP client applications. It is robust, flexible and very intuitive. Furthermore, I used Maven to build the project, because it is a tool that allows the creation and management of Java projects. It was very easy to create one. The only disadvantage was project compiling, because the commands needed are too verbose. Hence, I used Intellij, because it works too well with Maven, allowing me to run the project by just pressing a button.

You can find the project presentation at [Presentation](./Presentacion_proyecto.pdf)

## Features

### Account management
- [X] Register: The client allows a user to create a new account on the server. Once the account is created it automatically logs in the user into the chat options.
- [X] Login: If a user already has an account on the server, the client requests their credentials. Then it autenticates the user with the server and provides access to the features of the chat.
- [X] Account deletion: Whenever a user does not want their account anymore, it can be deleted from the server using the client. Albeit this option is not shown on the first part of the CLI interface, it does have relation with account management. And it is obvious that one can delete their account only when logged in.

### Chat features
- [X] Request roster status: The client allows a logged in user to see the status of their contacts. It shows the username of a contact, their status message, their status mode and if it is available or not.
- [X] Add user to contacts: By providing a username and a domain, wheter it is alumchat or not, the client allows a user to add a contact to their roster.
- [X] Show contact details: It is possible to see contact details with the client, by providing their username.
- [X] Direct message: With the client a user can chat with another user, privately, just by providing their username. It does not necessarily has to be on the user's roster.
- [X] Group chats: The client allows to create, join and chat in groupchats.
- [X] Change status: With the client a user can change their status by selecting if he/she is only or not. The user can also select their mode and their status message.
- [X] Send file: The client allows a user to send any file to another user.
- [X] Log out: The user can close their session with the server.

You can take a look of the javadoc which contains the documentation of the code here
[Javadoc](./Javadoc/index.html)

## Run the project

To use the project you have to clone it first

```bash
  git clone https://github.com/Daniel14gonc/Proyecto1_redes.git
```

Now that you have the project on your computer you have two options. If you have the IDE **Intellij** installed, you can open the project there and run it. If you do not, then here are the steps to compile and run the project on CLI.

First compile the project:

```bash
  mvn compile
```

Then execute the code:

```bash
  mvn exec:java -Dexec.mainClass=com.example.App
```

You should be using JDK 18 or above.

