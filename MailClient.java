import java.net.*;
import java.io.*;

public class MailClient {
    public static void main (String[] args) {

        // Arguments supply hostname and server port

        Socket socket = null;
        try{
            String host = args[0]; // Hostname ip address
            int serverPort = Integer.parseInt(args[1]); // Gets the port that the server is running

            socket = new Socket(host, serverPort); // Socket

            // DataStreams
            DataInputStream in = new DataInputStream(socket.getInputStream()); // Stream for input
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Stream for output
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String dataToSend = ""; // The string which the Client will send to the server

            // Client program runs until user give an Exit message input
            while (!dataToSend.equals("Exit")){

                // Receiving from the server
                String data = in.readUTF();	// Read a line of data from the stream
                System.out.println(data) ; // Prints what the server has sent

                // Sending to the server
                dataToSend = br.readLine(); // Reads the input from the user
                out.writeUTF(dataToSend); // UTF is a string encoding
                out.flush();
            }
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){System.out.println("readline:"+e.getMessage());
        }finally {if(socket!=null) try {socket.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }}

