import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import Files.javafiledemo;


public class Client {
    private static final int PORT_NUMBER = 1234;
    private static final String SERVER_IP = "localhost";//local host
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private static int RQ = 0;
    private static String name;
    private TCPClient tcpClient;
    static Boolean register = false;


    public Client(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress
        serverSocketTCP = new ServerSocket(3333);
        TCPClient tcpClient = new TCPClient();
        Thread clientThread = new Thread
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
                serverfeeback(messageFromServer);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }

    private void serverfeeback(String messageFromServer) {
        String[] feedback = messageFromServer.split("\\|");
//        incrementRQ(clientInfo);
        switch (feedback[0]) {
            case "REGISTERED":
                register = true;
                break;

            case "DE-REGISTERED":
                register = false;
                break;

            case "PUBLISH-DENIED":
                //publish again
                break;

            case "REMOVE-DENIED":
                //remove again
                break;

            case "UPDATE-CONFIRMED":
                register = true;
                break;
            default:
                break;
        }

    }

    private String input() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter \n\t0 setup client Information\n\t1 to register \n\t2 to deregister\n\t3 to publish\n\t4 to remove\n\t5 to retrieve-all\n\t6 to retrieve by name\n\t7 to search for specific file\n\t8 to download a file\n\t9 to update your contact info\n\t");
        String in = bufferedReader.readLine();
        String message;
        switch (in) {
            case "0":

                System.out.println("Enter the client name:");
                name = bufferedReader.readLine();
                while (name.isEmpty()) {
                    System.out.println("Re-enter client name:");
                    name = bufferedReader.readLine();
                }
                System.out.println("Enter the tcp Port:");
                Scanner scan = new Scanner(System.in);
                while (!scan.hasNextInt()) {
                    scan.next();
                }
                tcp = scan.nextInt();
                return "IGNORE";
            case "1"://REGISTER
                if (name.isEmpty()) {
                    System.out.println("invalid client name");
                    return "IGNORE";
                }

                message = "REGISTER|" + (++RQ) + "|" + name + "|" + tcp + "|";
                return message;

            case "2"://DEREGISTER
                System.out.println(register);
                if (register) {
                    message = "DE-REGISTER|" + (++RQ) + "|" + name + "|";
                    return message;
                }
                return "IGNORE";

            case "3"://PUBLISH
                if (register) {
                    File folder = new File("./Files/");
                    File[] files = folder.listFiles();
                    String[] listofFiles;
                    String inputPublish;
                    String sendingFile = "";
                    if (files != null) {
                        System.out.println("Publish file Names:");
                        inputPublish = bufferedReader.readLine();
                        listofFiles = inputPublish.split(",");

                        for (int i = 0; i < listofFiles.length; i++) {
                            for (File file : files) {
                                if (file.getName().equals(listofFiles[i])) {

                                    sendingFile += listofFiles[i] + " ";
                                }

                            }
                        }
                        for (int i = 0; i < listofFiles.length; i++) {
                            if (!sendingFile.contains(listofFiles[i])) {
                                sendingFile = "";
                            }
                        }

                        message = "PUBLISH|" + (++RQ) + "|" + name + "|" + sendingFile + "|";
                        return message;
                    } else {
                        System.out.println("There are no files in ./Files/ directory");
                        return "IGNORE";
                    }
                } else {
                    System.out.println("Not registered");
                    return "IGNORE";
                }

            case "4"://REMOVE
                if (register) {
                    String[] listofFilesRemoved;
                    String inputRemove;
                    System.out.println("Remove file Names:");
                    inputRemove = bufferedReader.readLine();
                    String sendingFileRemoved = "";
                    listofFilesRemoved = inputRemove.split(",");
                    for (int i = 0; i < listofFilesRemoved.length; i++) {
                        sendingFileRemoved += listofFilesRemoved[i] + " ";
                    }
                    for (int i = 0; i < listofFilesRemoved.length; i++) {
                        if (!sendingFileRemoved.contains(listofFilesRemoved[i])) {
                            sendingFileRemoved = "";
                        }
                    }
                    message = "REMOVE|" + (++RQ) + "|" + name + "|" + sendingFileRemoved + "|";
                    return message;
                } else {
                    System.out.println("Not registered");
                    return "IGNORE";
                }
            case "5"://RETRIEVE-ALL
                if (register) {
                    message = "RETRIEVE-ALL|" + (++RQ) + "|";
                    return message;
                } else {
                    System.out.println("Not registered");
                    return "IGNORE";
                }

            case "6"://RETRIEVE specific
                if (register) {
                    System.out.println("client name to search by");
                    String tempName = bufferedReader.readLine();
                    while (tempName.isEmpty()) {
                        System.out.println("Reenter client name to search by");
                        tempName = bufferedReader.readLine();
                    }
                    message = "RETRIEVE-INFOT|" + (++RQ) + "|" + tempName + "|";
                    return message;
                } else {
                    System.out.println("Not registered");
                    return "IGNORE";
                }

            case "7"://SEARCH specific
                if (register) {
                    System.out.println("search by file name");
                    String filename = bufferedReader.readLine();
                    message = "SEARCH-FILE|" + (++RQ) + "|" + filename + "|";
                    return message;
                } else {
                    System.out.println("Not registered");
                    return "IGNORE";
                }

            case "8"://DOWNLOAD a file
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in)); 
                System.out.println("Enter server IP: ");
                String filename =bufferReader.readLine();

                System.out.println("Enter portNumer: ");
                String serverip =bufferReader.readLine();

                System.out.println("Enter filename: ");
                int portnumber = bufferReader.readLine();                
            
                retrieveFile(serverip,portnumber,filename);

                return "IGNORE";
             
            case "9"://UPDATE use contact info
                //add yes and no
                message = "UPDATE-CONTACT|" + (++RQ) + "|" + name + "|" + tcp;
                return message;


            default:
                break;
        }
        return "INVALID INPUT";
    }

    void retrieveFile(int serverip,int portnumber, String filename){
        InetAddress inetAddress = InetAddress.getByName(serverip);
        Socket socket= new Socket(inetAddress,portnumber);

        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

        objectOutput.writeObject(filename);
        

        while()



    }


    public static void main(String[] args) throws IOException {
        tcpClient.listen();
        Thread tcpClientThread = new Thread(tcpClient);
        tcpClientThread.start();

        DatagramSocket datagramSocket = new DatagramSocket();
        // Get the IP address of the server
        InetAddress inetAddress = InetAddress.getByName(SERVER_IP);
        Client client = new Client(datagramSocket, inetAddress);
        System.out.println("Enter the client name:");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        name = bf.readLine();
        while (name.isEmpty()) {
            System.out.println("Re-enter client name:");
            name = bf.readLine();
        }
        System.out.println("Enter the tcp Port:");
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            scan.next();
        }
        client.sendThenReceive();
    }
}