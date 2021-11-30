
import java.io.IOException;
import java.net.*;
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
    public String response(String messageFromClient, DatagramPacket receivePacket) throws UnknownHostException {
        String[]clientInfo= messageFromClient.split("\\|");
        switch (clientInfo[0]){
            case "REGISTER":
                return registerClient(clientInfo,receivePacket);
            case "DE-REGISTER":
                return deregisterClient(clientInfo);
            case "PUBLISH":
                return publishClient(clientInfo);
            case "REMOVE":
                return removeClient(clientInfo);
            case "RETRIEVE-ALL":
                return retrieveAllClient(clientInfo);
            case "RETRIEVE-INFOT":
                return retrieveClientbyName(clientInfo);
            case "SEARCH-FILE":
                return searchfilebyName(clientInfo);
            case "UPDATE-CONTACT":
                return updateClient(clientInfo);
            case "IVALID INPUT":
                return "IVALID INPUT";
            default:break;
        }
        return "ERROR OCCURED";

    }

    private String updateClient(String[] clientInfo) throws UnknownHostException {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                clients.get(i).setAddress(InetAddress.getByName(clientInfo[3].substring(1)));
                clients.get(i).setUDPPort(Integer.parseInt(clientInfo[4]));
                clients.get(i).setTCPPort(Integer.parseInt(clientInfo[5]));
                return"UPDATE-CONFIRMED"+clientInfo[1]+ clients.get(i).toString();

            }
        }
        return"UPDATE-DENIED"+clientInfo[1]+clientInfo[2] +" name not found";
    }

    private String searchfilebyName(String[] clientInfo) {
        ArrayList<ClientHandler> clientwithFile = new ArrayList<ClientHandler>();
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getFileList()!=null){
            for (int j=0;j<temp.getFileList().size();j++){
                if (temp.getFileList().get(j).equals(clientInfo[2])){
                    clientwithFile.add(temp);
                }
            }}
        }
        if (clientwithFile.size()==0){return"SEARCH-ERROR|"+clientInfo[1]+"|file is not found";}
        else {return"SEARCH-FILE|"+clientInfo[1]+clientwithFile.toString(); }
    }

    private String retrieveClientbyName(String[] clientInfo) {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                return"RETRIEVE"+clientInfo[1]+ clients.get(i).toString();
            }
        }


        return"RETRIEVE-ERROr"+clientInfo[1]+"Client not found";
    }

    private String retrieveAllClient(String[] clientInfo) {
        return"RETRIEVE"+clientInfo[1]+ clients.toString();
    }

    private String removeClient(String[] clientInfo) {
        String [] listofFiles = clientInfo[3].split(" ");

        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            for(int j =0;j< temp.getFileList().size() ;j++){

            }
        } 


        return "test";
    }
    //         if (temp.getFileList(clientInfo[2])) {
    //             clients.get(i).setFileList(files);
    //             clientnameMatch=true;
    //             String message = "Remove" + " | " + Integer.parseInt(clientInfo[1]);
    //             return message;
    //         }
    //     }
    //     if(!clientnameMatch){
    //         String message = "REMOVE-DENIED" + " |  | " + "NAME DOES NOT EXIST";
    //     }

    //     return "ERROR REMOVING";
    // }

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
