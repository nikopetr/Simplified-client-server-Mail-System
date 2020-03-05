import java.util.ArrayList;
import java.util.List;

// Represents the account of a user
 class Account {
    private String username; // The username of the account
    private String password; // The password of the account
    private List<Email> mailbox; // The mailbox of the account, which is a list of emails

    // Constructor of class Account
    // Gets two String parameters for the username and of the Account
    Account(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.mailbox = new ArrayList<>();
    }

    // Returns a String with the username of the Account
    String getUsername() {
        return username;
    }

    // Returns a String with the password of the Account
    String getPassword() {
        return password;
    }

    // Returns a list of Email objects which represents the mailbox of the Account
    List<Email> getMailbox() {
        return mailbox;
    }

}
