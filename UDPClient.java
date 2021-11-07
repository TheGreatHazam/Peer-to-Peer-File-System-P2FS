import javax.imageio.spi.RegisterableService;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.SplittableRandom;

//For clients to talk to the server
public class UDPClient {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 3030;
    public static DatagramSocket clientSocket;

    private static boolean register = false;

    // Get the IP address of the server
    public static InetAddress IPAddress;

    // Creating corresponding buffers
    public static byte[] sendingDataBuffer = new byte[1024];
    public static byte[] receivingDataBuffer = new byte[1024];

    public static DatagramPacket sendingPacket;
    private static DatagramPacket receivingPacket;
    private static String receivedData;
    public static Scanner  scanner = new Scanner(System.in);

    public static void sendUDPPacket() {
        sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, SERVICE_PORT);

        // sending UDP packet to the server
        try {
            clientSocket.send(sendingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveUDPPacket() {
        // Get the server response .i.e. capitalized sentence
        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        try {
            clientSocket.receive(receivingPacket);
            receivedData = new String(receivingPacket.getData(), 0, receivingPacket.getLength());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static void register(){
        System.out.println("Register the clients Username");
        String username = scanner.nextLine();
        sendingDataBuffer = username.getBytes();
        sendUDPPacket();
        //if receive register succesfull then it exits the loop if not resend username
        receiveUDPPacket();
        System.out.println(receivedData);
    }

    public static void main(String[] args) throws IOException {
        try {
      /* Instantiate client socket. 
      No need to bind to a specific port */
            clientSocket = new DatagramSocket();

            // Get the IP address of the servera
            IPAddress = InetAddress.getByName("localhost");


             /* Converting data to bytes and
              storing them in the sending buffer */
            String sentence = "A client is connected";
            sendingDataBuffer = sentence.getBytes();

            sendUDPPacket();
            receiveUDPPacket();
            System.out.println("Sent from the server: " + receivedData);
            register();




            clientSocket.close();


        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}