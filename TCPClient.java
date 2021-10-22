import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class TCPClient {
    private Socket socket;
    private Scanner scanner;
    private TCPClient(InetAddress serverAddress, int serverPort) throws Exception { //Create a client socket takes the server IP address and server port number as arguments.
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }
    private void start() throws IOException { //Send a message to the server
        String input;
        while (true) {
            input = scanner.nextLine();
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(input);
            out.flush();
        }
    }
    public static void main(String[] args) throws Exception { //connect tot the server by entering the server IP address and port number
        TCPClient client = new TCPClient(
                InetAddress.getByName(args[0]), 
                Integer.parseInt(args[1]));
        
        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress()); //send message to the server
        client.start();                
    }
}
