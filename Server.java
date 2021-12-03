
import java.io.*;
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
//        incrementRQ(clientInfo);
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
                return updateClient(clientInfo,receivePacket);
            case "IGNORE":
                return "";
            case "INVALID INPUT":
                return "INVALID INPUT";
            default:break;
        }
        return "ERROR OCCURRED";

    }

//    private void incrementRQ(String[] clientInfo) {
//        for (int i = 0; i < clients.size(); i++) {
//            ClientHandler temp = (ClientHandler) clients.get(i);
//            if (temp.getName().equals(clientInfo[2])) {
//                clients.get(i).setRQId(Integer.parseInt(clientInfo[1]));
//            }
//        }
//    }

    private String updateClient(String[] clientInfo, DatagramPacket receivePacket) throws UnknownHostException {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                clients.get(i).setAddress(receivePacket.getAddress());
                clients.get(i).setUDPPort(receivePacket.getPort());
                clients.get(i).setTCPPort(Integer.parseInt(clientInfo[3]));
                return"UPDATE-CONFIRMED|"+clientInfo[1]+"|"+ clients.get(i).toString();

            }
        }
        return"UPDATE-DENIED|"+clientInfo[1]+"|"+clientInfo[2] +"| name not found";
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
        else {return"SEARCH-FILE|"+clientInfo[1]+"|"+clientwithFile.toString(); }
    }

    private String retrieveClientbyName(String[] clientInfo) {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                return"RETRIEVE|"+clientInfo[1]+"|"+ clients.get(i).toString();
            }
        }
        return"RETRIEVE-ERROR|"+clientInfo[1]+"|Client not found";
    }

    private String retrieveAllClient(String[] clientInfo) {
        return"RETRIEVE|"+clientInfo[1]+"|"+ clients.toString();
    }

    private String removeClient(String[] clientInfo) {
        if (clientInfo.length<4){
            String message = "REMOVE-DENIED" + "|"+clientInfo[1]+"|" + "files or name ";
            return message;
        }
        String [] listofFiles = clientInfo[3].split(" ");

System.out.println(Arrays.toString(listofFiles));
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                for (int j=0;j< listofFiles.length;j++)
                {
                    System.out.println(listofFiles[j]);
                    System.out.println(clients.get(i).getFileList());


                    if (clients.get(i).getFileList().contains(listofFiles[j])){
                        clients.get(i).getFileList().remove(listofFiles[j]);
                    }
                }
            }
        }


        return "REMOVED|"+clientInfo[1]+"|";
    }


    private String publishClient(String[] clientInfo) {
        if (clientInfo.length<4){
            String message = "PUBLISH-DENIED" + "|"+clientInfo[1]+"|" + "files or name ";
            return message;
        }
        String []filenames =clientInfo[3].split(" ");
        ArrayList<String> files=new ArrayList<>(Arrays.asList(filenames));
        boolean clientnameMatch=false;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                clients.get(i).setFileList(files);
                clientnameMatch=true;
                String message = "PUBLISH" + "|" +clientInfo[1]+"|";
                return message;
            }
        }
        if(!clientnameMatch){
            String message = "PUBLISH-DENIED" + "|"+clientInfo[1]+"|" + "Client Does not exist";
            return message;
        }

        return "ERROR PUBLISHING";
    }

    private String deregisterClient(String[] clientInfo) {

        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                String message = "DE-REGISTERED" + "|" + clientInfo[1] + "|" + temp.getName();
                clients.remove(i);
                return message;

            }
        }

        return "ERROR DEREGESTERING";
    }

    private String registerClient(String[] clientInfo, DatagramPacket receivePacket) {

        boolean register = true;
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler temp = (ClientHandler) clients.get(i);
            if (temp.getName().equals(clientInfo[2])) {
                register = false;
                String message = "REGISTER-DENIED" + "|"+clientInfo[1] + "|" + "USERNAME TAKEN OR ALREADY REGISTERED";
                return message;

            }
        }

        if (register) {
            ClientHandler registerClient = new ClientHandler(Integer.parseInt(clientInfo[1]) ,receivePacket.getPort(), Integer.parseInt(clientInfo[3]) , receivePacket.getAddress(), clientInfo[2]);
            clients.add(registerClient);
            String message = "REGISTERED" + "|" +clientInfo[1]+"|";
            return message;

        }
        return "ERROR REGISTERING";
    }

    public void receiveThenSend(){
        while (true){
            byte[] receiveBuffer = new byte[1024];
            byte[]  sendBuffer;
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
                logClientInfo();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    public static void logClientInfo(){
        try{
            FileOutputStream writeData = new FileOutputStream("logClientInfo.ser");
            ObjectOutputStream writeStream = new ObjectOutputStream((writeData));

            writeStream.writeObject(clients);
            writeStream.flush();
            writeStream.close();
            System.out.println(clients.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void fetchClientInfo(){
        try{
            FileInputStream readData = new FileInputStream("logClientInfo.ser");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            clients = (ArrayList<ClientHandler>) readStream.readObject();
            readStream.close();
            System.out.println(clients.toString());
        } catch (EOFException e) {
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        // Instantiate a new DatagramSocket to receive responses from the client
        DatagramSocket datagramSocket = new DatagramSocket(SERVICE_PORT);
        Server server = new Server(datagramSocket);
        fetchClientInfo();
        server.receiveThenSend();

    }
}
