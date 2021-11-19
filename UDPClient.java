import javax.imageio.spi.RegisterableService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.SplittableRandom;
import java.util.jar.Attributes.Name;
import java.io.InputStreamReader;

//For clients to talk to the server
public class UDPClient {
    /* The server port to which
    the client socket is going to connect */
    public final static int SERVICE_PORT = 4040;
    public static DatagramSocket clientSocket;

    private  static String name;

    private static boolean register = false;
    private static String clientName;

    // Get the IP address of the server
    public static InetAddress IPAddress;

    // Creating corresponding buffers
    public static byte[] sendingDataBuffer = new byte[1024];
    public static byte[] receivingDataBuffer = new byte[1024];


    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    public static DatagramPacket sendingPacket;
    private static DatagramPacket receivingPacket;
    private static String receivedData;
    public static Scanner scanner = new Scanner(System.in);

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

    public static void register() {
        System.out.println("Register the clients Username");
        name = scanner.nextLine();
        sendingDataBuffer = name.getBytes();
        sendUDPPacket();
        receiveUDPPacket();
        System.out.println(receivedData);
    }

    public static void deregister() {
        System.out.println("De-Registering Client Name");
        String username = scanner.nextLine();
        sendingDataBuffer = username.getBytes();
        sendUDPPacket();
        receiveUDPPacket();
        System.out.println("Sent from the server: " + receivedData);
    }

    public static void publish(){
        String ListofFiles =name;

        File folder = new File("./Files");
        File[] files = folder.listFiles();
           if (files != null){
                for (File file: files){
                    ListofFiles += " " +file.getName();
                }
               System.out.println("sending "+ListofFiles);
                sendingDataBuffer =ListofFiles.getBytes();
                sendUDPPacket();
    }
        else{
            System.out.println("There are no files in ./Files/ directory");}
        
        receiveUDPPacket();
        System.out.println("Sent from the server: " + receivedData);
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
            deregister();
            publish();

            clientSocket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}