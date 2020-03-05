Distributed client-based e-mail system using Sockets, Input / Output streams and Threads in the language
Java programming.

Program Classes:

Email Class: Indicates an email sent by a sender toa recipient.
The class has the following fields (the corresponding getters and setters have been implemented for them)
for access and modification):
• boolean isNew: Indicates whether the message has been read or not.
• String sender: Indicates the sender of the email.
• String receiver: Indicates the recipient of the email.
• String subject: Indicates the subject of the email.
• String mainbody: The text of the email.

Account Class: Indicates an email account. 
The class has the following fields (the corresponding getters and setters have been implemented for them) for access and modification):
• String username: The username.
• String password: The user's password.
• List <Email> mailbox: The user's mailbox, which is a list of Email type objects.

The MailServer class:
It denotes the server, where it will constantly run "listening" to a port for
incoming customer requests and creates a different one for each client
connection.
The class has static List <Account> accounts as a static field, where it is a list of
all email accounts are stored. For this field it has
also create the corresponding getter. There is also the static void function
initializeAccounts (), which initially creates some specific accounts.


The Connection class: Indicates the connection between the server and the client.
The class inherits the class Thread so that each connection runs on a separate thread (via run ()) and
many clients can log in at the same time. This class is located in the same .java file with MailServer (MailServer.java).
The class has the following fields and constants:
• DataInputStream in: Stream for input
• DataOutputStream out: Stream for output
• Socket clientSocket: The socket from the client side
• String MAIL_SERVER_STRING, String USER_CERTIFICATE_OPTIONS_STRING, String MAIL_BROWSING_OPTIONS_STRING: Strings used forthe ease of sending packages with messages to the client's user interface and user.
Within the class are implemented the basic methods (functions) which
email support (register (), logIn (), newEmail (), showEmails (),
readEmail (), deleteEmail (), logOut () and exit ()). Apart from these methods they have
additionally implemented userCertification () (which is used to validate
user data or to create a new account), but also
mailBrowsing () (used for browsing and using email by the user).
These two functions act as auxiliaries since they call the other key functions
mentioned above.


The MailClient class:
It declares the client, implements the user's communication with the server. The program will receives user input by sending it to the server while receiving it server data and will display it to the user.