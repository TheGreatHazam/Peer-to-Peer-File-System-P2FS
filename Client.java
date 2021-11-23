import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Client {
    private static final int PORT_NUMBER = 1234;
    private static final String SERVER_IP = "localhost";//local host
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private static int RQ = 0;
    private String name;

    public Client(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    public void sendThenReceive() {

        while (true) {
            byte[] receiveBuffer = new byte[1024];
            byte[] sendBuffer = new byte[1024];

            try {
                String message = input();
                sendBuffer = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, PORT_NUMBER);
                datagramSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(receivePacket);
                String messageFromServer = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("server:" + messageFromServer);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }

    private String input() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter \n\t1 to register \n\t2 to deregister\n\t3 to publish");
        int in = Integer.parseInt(bufferedReader.readLine());
        String message;
        switch (in) {
            case 1:
                System.out.println("Register Name");
                name = bufferedReader.readLine();
                int tcp = 3333;
                message = "REGISTER|" + (++RQ) + "|" + name + "|" + 3333;
                return message;
            case 2:
                System.out.println("Deregister Name");
                String derregistername = bufferedReader.readLine();
                message = "DE-REGISTER|" + (++RQ) + "|" + derregistername;
                return message;
            case 3:
                // System.out.println("Enter files to publish seperated by space");
                // String filenames = bufferedReader.readLine();
                String ListofFiles = "";
                File folder = new File("./Files");
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        ListofFiles +=file.getName()+" ";
                    }
                }
                message = "PUBLISH|" + (++RQ) + "|" + name + "|" + ListofFiles;
                return message;


        }

        return "IVALID INPUT";
    }


    public static void main(String[] args) throws IOException {

        DatagramSocket datagramSocket = new DatagramSocket();

        // Get the IP address of the server
        InetAddress inetAddress = InetAddress.getByName(SERVER_IP);
        Client client = new Client(datagramSocket, inetAddress);
        client.sendThenReceive();
    }
}
