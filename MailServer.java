import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Class which represents the Server of the connection
public class MailServer {
    private static List<Account> accounts; // List of the accounts registered in the server

    static List<Account> getAccounts() {
        return accounts;
    }

    // Initializing some Accounts to begin with
    private static void initializeAccounts()
    {
        accounts = new ArrayList<>();
        accounts.add(new Account("andreas.petrou@csd.auth.gr", "123"));
        accounts.add(new Account("leonidas.petrou@csd.auth.gr", "123"));
        accounts.add(new Account("nikolas.petrou@csd.auth.gr", "123"));
        accounts.add(new Account("kostas.andreou@csd.auth.gr", "123"));

        String exampleMailMainBody = "Long time no see it's me ";


        // Adding some emails to the new Accounts
        for(int i = 0; i < accounts.size(); i++)
        {
            Account senderAccount = accounts.get(i);

            // This will give only the first part of the sender's username in order to show that it's a normal email
            int nameEnd = senderAccount.getUsername().indexOf("."); // This finds the first occurrence of "."
            String nameOfSender = "";
            if (nameEnd != -1)
                nameOfSender= senderAccount.getUsername().substring(0 , nameEnd);

            for (int j = 0; j < accounts.size(); j++)
            {
                if (i != j)
                {
                    Account receiverAccount = accounts.get(j);

                    // Adding email to receiver's mailbox
                    receiverAccount.getMailbox().add(new Email(senderAccount.getUsername(), receiverAccount.getUsername(), "It's me " + nameOfSender + "!", exampleMailMainBody +  nameOfSender + "."));
                }
            }
        }

    }

    public static void main (String[] args) {
        // Arguments supply the port that the server is running

        try{
            int serverPort = Integer.parseInt(args[0]); // Gets the port that the server is running
            initializeAccounts(); // Initializing some Accounts to begin with
            ServerSocket listenSocket = new ServerSocket(serverPort);  // Socket
            System.out.println("Server is running on port: " + serverPort);

            // Listening and waiting for a client to connect
            while(true) {
                Socket clientSocket = listenSocket.accept(); // Accepts the client
                System.out.println("Request from client: " + clientSocket.getRemoteSocketAddress());
                new Connection(clientSocket); // Establishes a new server-client socket connection
            }
        } catch(IOException e) {System.out.println("Listen socket: "+e.getMessage());}
    }
}

// Establishes a new server-client socket connection
// Running on a different Thread in order to have more than one client connected
class Connection extends Thread {

    // Strings that are used for the user interface messages
    private static final String MAIL_SERVER_STRING = "--------" + '\n' + "MailServer:" + '\n' + "--------" + '\n';
    private static final String USER_CERTIFICATE_OPTIONS_STRING =  "Hello, you connected as a guest."
            + '\n' + "==========" + '\n' + "> LogIn" + '\n' + "> Register" + '\n' + "> Exit" + '\n'+"==========";
    private static final String MAIL_BROWSING_OPTIONS_STRING = "==========" + '\n' + "> NewEmail" + '\n' + "> ShowEmails" + '\n'+ "> ReadEmail" + '\n'
            + "> DeleteEmail" + '\n' +"> LogOut" + '\n' + "> Exit" + '\n'+"==========";

    private DataInputStream in; // Stream for input
    private DataOutputStream out; // Stream for output
    private Socket clientSocket; // The socket of the connection

    // Constructor of connection class, gets the client socket as a parameter
    Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out =new DataOutputStream(clientSocket.getOutputStream());
            this.start(); // Starts the connection
        } catch(IOException e) {System.out.println("Connection: "+e.getMessage());}
    }

    // Running while the connection is established
    public void run(){
        try {
            boolean clientWantsToExit = false;

            // Running the program until the client wants to exit
            while(!clientWantsToExit)
            {
                // Certificates the account details, or creating a new account for the user
                Account clientAccount = userCertification();

                if (clientAccount != null) // Null for exit code
                    clientWantsToExit = mailBrowsing(clientAccount); //User browsing the email
                else
                    clientWantsToExit = true;
            }

        }catch (EOFException e){System.out.println("EOF: "+e.getMessage());
        } catch(IOException e) {System.out.println("readline: "+e.getMessage());
        }
        finally
        {
            try {
                // After the client is done
                // closing the client socket, closing the port used
                SocketAddress clientSocketAddress = clientSocket.getRemoteSocketAddress();
                String clientAddress = clientSocketAddress.toString();
                clientSocket.close();
                System.out.print("Socket for client:" + clientAddress + " closed");
            }
            catch (IOException e){/*close failed*/}}

    }

    // Method used for the mail browsing
    // Returns false if the user wishes to exit
    private boolean mailBrowsing(Account clientAccount) throws IOException {
        String dataToSend = MAIL_SERVER_STRING + "Welcome back " + clientAccount.getUsername() + "!" + '\n' + MAIL_BROWSING_OPTIONS_STRING;
        boolean clientWantsToExit = false;
        boolean clientWantsToLogOut = false;

        while(!clientWantsToLogOut){

            out.writeUTF(dataToSend);
            dataToSend = MAIL_SERVER_STRING + MAIL_BROWSING_OPTIONS_STRING;
            String choice = in.readUTF();	// Read a line of data from the stream

            switch (choice) {

                case "NewEmail": // Client wishes to to write a new email
                    if (newEmail(clientAccount.getUsername()))
                        dataToSend = MAIL_SERVER_STRING + "Mail sent successfully! " + '\n' + MAIL_BROWSING_OPTIONS_STRING;
                    else
                        dataToSend = "The selected email address for the receiver does not exists." + '\n'+ dataToSend;
                    break;

                case "ReadEmail": // Client wishes to to read an email
                    Email email = readEmail(clientAccount);
                    if (email != null)
                        dataToSend = MAIL_SERVER_STRING + "From: " + email.getSender() + '\n' + "Subject: " + email.getSubject() + '\n' + '\n' + email.getMainBody() + '\n' + MAIL_BROWSING_OPTIONS_STRING;
                    else
                        dataToSend = "The selected email ID does not exists." + '\n'+ dataToSend;
                    break;

                case "DeleteEmail": // Client wishes to to delete an email
                    if (deleteEmail(clientAccount))
                        dataToSend = MAIL_SERVER_STRING + "Email successfully deleted." + '\n' + MAIL_BROWSING_OPTIONS_STRING;
                    else
                        dataToSend = "The selected email ID does not exists." + '\n'+ dataToSend;
                    break;

                case "ShowEmails": // Client wishes to see his emails
                    dataToSend = showEmails(clientAccount);
                    break;

                case "LogOut":  // Client wishes to log out
                    clientWantsToLogOut = true;
                    break;

                case "Exit": // Client wishes to exit
                    clientWantsToExit = true;
                    clientWantsToLogOut = true;
                    break;
            }
        }
        return clientWantsToExit;
    }

    // Returns the Account of the user after the certification is done
    private Account userCertification() throws IOException {
        String dataToSend = MAIL_SERVER_STRING + USER_CERTIFICATE_OPTIONS_STRING;
        boolean clientWantsToExit = false;

        while(!clientWantsToExit){

            out.writeUTF(dataToSend);
            dataToSend = MAIL_SERVER_STRING + USER_CERTIFICATE_OPTIONS_STRING;
            String choice = in.readUTF();	// Read a line of data from the stream

            switch (choice) {
                case "LogIn": // Client wishes to log in with an existing account
                    Account clientAccount = login();
                    if (clientAccount != null)
                        return clientAccount;
                    else
                        dataToSend = "Invalid user or password." + '\n' + dataToSend;
                    break;

                case "Register": // Client wishes to make a new account
                    if (register())
                        dataToSend = "Account registered." + '\n' + dataToSend;
                    else
                        dataToSend = "The selected username is already taken." + '\n'+ dataToSend;
                    break;

                case "Exit": // Client wishes to exit
                    clientWantsToExit = true;
                    break;
            }
        }
        return null;
    }

    // Method to register a new account
    // Returns false if the username is already taken
    private boolean register() throws IOException {

        out.writeUTF(MAIL_SERVER_STRING + "Type the username you are going to be using for the your new Account: ");
        String username = in.readUTF(); // Read the username from the stream

        for (Account account : MailServer.getAccounts())// Checks if username is already taken
            if (account.getUsername().equals(username))
                return false;

        out.writeUTF(MAIL_SERVER_STRING + "Type the password you are going to be using: ");
        String password = in.readUTF(); // Read the password from the stream
        MailServer.getAccounts().add(new Account(username,password));
        return true;
    }

    // Method used for account login certification
    // Returns the Account after successful certification, otherwise returns null
    private Account login() throws IOException {

        out.writeUTF(MAIL_SERVER_STRING + "Type your username: ");
        String username = in.readUTF(); // Read the username from the stream

        out.writeUTF(MAIL_SERVER_STRING + "Type your password: ");
        String password = in.readUTF(); // Read the password from the stream

        for (Account account : MailServer.getAccounts())// Checks if the Account exists
            if (account.getUsername().equals(username) && account.getPassword().equals(password))
                return account;

        return null;
    }

    // Method used to write a new email
    // Returns true if the mail is sent, or false if the account of the receiver does not exists
    private boolean newEmail(String sender) throws IOException {

        out.writeUTF(MAIL_SERVER_STRING + "Receiver: ");
        String receiver = in.readUTF(); // Read the email address for the receiver from the stream

        Account receiverAccount = null; // The Account of the receiver
        boolean receiverExists = false;

        // Checks if an Account with that email address exists
        for (Account account : MailServer.getAccounts())
            if (account.getUsername().equals(receiver)) {
                receiverExists = true;
                receiverAccount = account;
                break;
            }

        if (!receiverExists)
            return false;
        else {
            out.writeUTF(MAIL_SERVER_STRING + "Subject: ");
            String subject = in.readUTF(); // Read the subject of the mail from the stream

            out.writeUTF(MAIL_SERVER_STRING + "Main body: ");
            String mainBody = in.readUTF(); // Read the main body of the mail from the stream

            Email email = new Email(sender, receiver, subject, mainBody);
            ArrayList<Email> receiverMailbox = (ArrayList<Email>) receiverAccount.getMailbox(); // The mailbox of the receiver's account
            receiverMailbox.add(email); // Adding email to receiver's mailbox
            return true;
        }
    }

    // Method used to return a String with how the emails will be shown on the screen
    private String showEmails(Account usersAccount) {

        // Using String.format to have a nice presentation of the emails
        String dataToSend = String.format("%s %s %-7s %-30s %s %s", MAIL_SERVER_STRING, "Id", "", "From", "Subject",'\n');

        // Gets every account's email and adding it to the String
        for (int i = 0; i < usersAccount.getMailbox().size(); i++)
        {
            Email email = usersAccount.getMailbox().get(i);

            if (email.isNew())
                dataToSend = String.format("%s %s %-7s %-30s %s %s" , dataToSend, (i+1) +"."," [New] ", email.getSender(), email.getSubject(),'\n');
            else
                dataToSend = String.format("%s %s %-7s %-30s %s %s" , dataToSend, (i+1) +".","       ", email.getSender(), email.getSubject(),'\n');
        }
        dataToSend = dataToSend + MAIL_BROWSING_OPTIONS_STRING;
        return dataToSend;
    }

    // Method used to get an id of an email from he user and return that email
    // Returns null if that id does not exists
    private Email readEmail(Account usersAccount) throws IOException {
        out.writeUTF(MAIL_SERVER_STRING + "Give the ID of the email you wish to read: ");
        try {
            int id = Integer.parseInt(in.readUTF());	// Read a line of data from the stream for the id of the email
            if (id <= usersAccount.getMailbox().size())
            {
                Email email = usersAccount.getMailbox().get(id-1);
                email.setNew(); // Sets the email as read
                return email;
            }
            return null;
        }catch (Exception e){return null;}  // Catches exception in case user gives wrong input for the id, and returns null


    }

    // Method used to get an id of an email from he user and deletes that email
    // Returns false if that id does not exists
    private boolean deleteEmail(Account usersAccount) throws IOException {
        out.writeUTF(MAIL_SERVER_STRING + "Give the ID of the email you wish to delete: ");
        try {
            int id = Integer.parseInt(in.readUTF());	// Read a line of data from the stream for the id of the email
            if (id <= usersAccount.getMailbox().size())
            {
                usersAccount.getMailbox().remove(id-1); // Removes the email from the mailbox
                return true;
            }
            return false;
        }catch (Exception e){return false;} // Catches exception in case user gives wrong input for the id, and returns false
    }
}