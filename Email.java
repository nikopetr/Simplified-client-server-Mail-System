// Represents an email that is send from a sender to a receiver
 class Email {

    private boolean isNew; // Represents if the email is read or not
    private String sender; // The sender of this email
    private String receiver; // The receiver this this email
    private String subject; // The subject of this email
    private String mainBody; // The text of this email

    // Constructor of class Email
    // Gets four String parameters for the sender, receiver, subject, mainBody of the Email
    Email(String sender, String receiver, String subject, String mainBody){
        this.isNew = true;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainBody = mainBody;
    }

    // Returns true if the Email is new
    boolean isNew() {
        return isNew;
    }

    // Sets that this Email is not new anymore
    void setNew() {
        isNew = false;
    }

    // Returns a String with the name of the sender
    String getSender() {
        return sender;
    }

    // Returns a String with the name of the receiver
    String getReceiver() {
        return receiver;
    }

    // Returns a String with the subject of the Email
    String getSubject() {
        return subject;
    }

    // Returns a String with the content of the Email
    String getMainBody() {
        return mainBody;
    }
}
