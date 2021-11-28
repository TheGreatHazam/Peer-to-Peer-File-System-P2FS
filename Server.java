
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {
    private final static int SERVICE_PORT = 1234;
    private DatagramSocket datagramSocket;
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
    //each packet on a datagram packet is adreesssed and routed
    public String response(String messageFromClient, DatagramPacket receivePacket){
        String[]clientInfo= messageFromClient.split("\\|");
        switch (clientInfo[0]){
            case "REGISTER":
                return registerClient(clientInfo,receivePacket);
            case "DE-REGISTER":
                return deregisterClient(clientInfo);
            case "PUBLISH":
                return publishClient(clientInfo);
            case "IVALID INPUT":
                return "IVALID INPUT";
            default:break;
        }
        return "ERROR OCCURED";

    }

    private String publishClient(String[] clientInfo) {
        String []filenames =clientInfo[3].split(" ");
        ArrayList<String> files=new ArrayList<>(Arrays.asList(filenames));
        boolean clientnameMatch=false;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                clients.get(i).setFileList(files);
                clientnameMatch=true;
                String message = "PUBLISH" + " | " + Integer.parseInt(clientInfo[1]);
                return message;
            }
        }
        if(!clientnameMatch){
            String message = "PUBLISH-DENIED" + " |  | " + "NAME DOES NOT EXIST";
        }

        return "ERROR PUBLISHING";
    }

    private String deregisterClient(String[] clientInfo) {
        boolean deregister=true;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                String message = "DE-REGISTER " + " | " + Integer.parseInt(clientInfo[1]) + " | " + temp.getName();
                clients.remove(i);
                return message;

            }
        }
        if (deregister){
            String message = "DE-REGISTER  | failed";
            return message;
        }
        return "ERROR DEREGESTERING";
    }

    private String registerClient(String[] clientInfo, DatagramPacket receivePacket) {

        boolean register = true;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                register = false;
                String message = "REGISTER-DENIED" + " | "+clientInfo[1] + " | " + "USERNAME TAKEN";
                return message;

            }
        }

        if (register) {
            ClientHandler registerClient = new ClientHandler(Integer.parseInt(clientInfo[1]) ,receivePacket.getPort(), Integer.parseInt(clientInfo[3]) , receivePacket.getAddress(), clientInfo[2]);
            clients.add(registerClient);
            String message = "REGISTERED" + " | " +clientInfo[1];
            return message;

        }
        return "ERROR REGISTERING";
    }

    public void receiveThenSend(){
        while (true){
            byte[] receiveBuffer = new byte[1024];
            byte[]  sendBuffer = new byte[1024];
            try {

                DatagramPacket receivePacket=new DatagramPacket(receiveBuffer,receiveBuffer.length);
                datagramSocket.receive(receivePacket);
                String messageFromClient= new String(receivePacket.getData(),0, receivePacket.getLength());
                System.out.println("Client: "+messageFromClient);

                String messageToClient=response(messageFromClient,receivePacket);
                InetAddress inetAddress= receivePacket.getAddress();
                int port=receivePacket.getPort();
                sendBuffer=messageToClient.getBytes();
                DatagramPacket sendPacket=new DatagramPacket(sendBuffer, sendBuffer.length,inetAddress,port);
                datagramSocket.send(sendPacket);

                System.out.println(clients.toString());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    public static void main(String[] args) throws SocketException {

        // Instantiate a new DatagramSocket to receive responses from the client
        DatagramSocket datagramSocket = new DatagramSocket(SERVICE_PORT);
        Server server = new Server(datagramSocket);
        server.receiveThenSend();

    }
}
