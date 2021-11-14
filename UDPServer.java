import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

//For communication between client and server
public class UDPServer {
    // Server UDP socket runs at this port
    public final static int SERVICE_PORT = 4040;
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
        boolean register = true;
        receiveUDPPacket();

        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(receivedData)) {
                register = false;
                String message = "REGISTER-DENIED" + " | " + String.valueOf(temp.getRQID()) + " | " + "USERNAME TAKEN";
                sendingDataBuffer = message.getBytes();
                sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                break;
            }
        }
        if (register) {
            ClientHandler registerClient = new ClientHandler(receivingPacket.getPort(), 3000, receivingPacket.getAddress(), receivedData);
            clients.add(registerClient);
            String message = "REGISTERED" + " | " + registerClient.getRQID();
            sendingDataBuffer = message.getBytes();
            sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
        }
        System.out.println(clients.toString());
    }

    public static void deregisterClient() {
        receiveUDPPacket();

        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(receivedData)) {
                String message = "DE-REGISTER " + " | " + String.valueOf(temp.getRQID()) + " | " + temp.getName();
                sendingDataBuffer = message.getBytes();
                sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                clients.remove(i);
                break;
            }
        }
        System.out.println(clients.toString());
    }

    public static void publishClient() {
        receiveUDPPacket();
        boolean publishBool = true;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (!(temp.getName().equals(receivedData))) { //if user does not exist
                publishBool = false;
                String message = "PUBLISH-DENIED" + " | " + String.valueOf(temp.getRQID()) + " | " + "USERNAME DOES NOT EXIST";
                sendingDataBuffer = message.getBytes();
                sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                break;
            }
        }
        if (publishBool) {
            ClientHandler publishClient = new ClientHandler(receivingPacket.getPort(), 3000, receivingPacket.getAddress(), receivedData);
            System.out.println(receivedData);
            String message = "PUBLISHED" + " | " + publishClient.getRQID();
            sendingDataBuffer = message.getBytes();
            sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
        }

        System.out.println("published files to client");
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

                while (true) {
                    receiveUDPPacket();
                    System.out.println(receivedData);

                    switch (receivedData) {
                        case "Register":

                            sendingDataBuffer = "Server registerclient is running".getBytes();
                            sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                            registerClient();
                            continue;
                        case "Publish":

                            sendingDataBuffer = "Server publishclient is running".getBytes();
                            sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                            publishClient();

                            continue;
                        case "Deregister":

                            sendingDataBuffer = "Server deregisterclient is running".getBytes();
                            sendUDPPacket(receivingPacket.getAddress(), receivingPacket.getPort());
                            deregisterClient();
                            continue;
                        default:
                            break;
                    }
                }


                // Close the socket connection
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}