import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

//For communication between client and server
public class UDPServer {
    // Server UDP socket runs at this port
    public final static int SERVICE_PORT = 8000;
    private static ArrayList clients = new ArrayList<ClientHandler>();
    public static DatagramSocket serverSocket;
    public static byte[] sendingDataBuffer = new byte[1024];
    public static byte[] receivingDataBuffer = new byte[1024];

    public static DatagramPacket sendingPacket;
    private static DatagramPacket receivingPacket;
    private static String receivedData;

    public static void sendUDPPacket(InetAddress senderAddress, int senderPort) {
        // Create new UDP packet with data to send to the client
        sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
        try {
            serverSocket.send(sendingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveUDPPacket() {
        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

        // Receive data from the client and store in receivingPacket
        try {
            serverSocket.receive(receivingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Printing out the client sent data
        receivedData = new String(receivingPacket.getData(), 0, receivingPacket.getLength());
    }

    public static void registerClient() {
        receiveUDPPacket();
        System.out.println(receivedData);

        ClientHandler registerClient = new ClientHandler(receivingPacket.getPort(), 3000, receivingPacket.getAddress(), receivedData);
        clients.add(registerClient);
        System.out.println(clients.toString());
    }

    public static void main(String[] args) throws IOException {
        try {
            // Instantiate a new DatagramSocket to receive responses from the client
            serverSocket = new DatagramSocket(SERVICE_PORT);

            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for a client to connect...");
                receiveUDPPacket();
                System.out.println(receivedData);
                sendingDataBuffer = "Connection established".getBytes();
                sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                registerClient();


                // Close the socket connection
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}