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
        System.out.println("Enter \n\t1 to register \n\t2 to deregister\n\t3 to publish\n\t4 to remove\n\t5 to retrieve-all\n\t6 to retrieve by name\n\t7 to search for specific file\n\t8 to download a file\n\t9 to update your contact info\n\t");
        int in = Integer.parseInt(bufferedReader.readLine());
        String message;
        switch (in) {
            case 1://REGISTER
                System.out.println("Register Name:");
                name = bufferedReader.readLine();
                int tcp = 3333;
                message = "REGISTER|" + (++RQ) + "|" + name + "|" + tcp+"|";
                return message;
                
            case 2://DEREGISTER
                System.out.println("Deregister Name:");
                String derregistername = bufferedReader.readLine();
                message = "DE-REGISTER|" + (++RQ) + "|" + derregistername+"|";
                return message;
                
            case 3://PUBLISH
                File folder = new File("./Files/");
                File[] files = folder.listFiles();
                String [] listofFiles;
                String inputPublish;
                String sendingFile = "";
                if (files != null){
                     System.out.println("Publish file Names:");
                     inputPublish = bufferedReader.readLine();
                     listofFiles = inputPublish.split(",");
                   
                        for(int i = 0; i < listofFiles.length; i++){
                            for(File file : files )
                            {
                                if(file.getName().equals( listofFiles[i])){
                                  
                                    sendingFile += listofFiles[i] + " ";
                                }
                            }
                        }

                        if (sendingFile == ""){
                            String error = "File does not exist, please try again";
                            System.out.println(error);
                            return "error";
                        }
                   
                  
                    System.out.println("Sent from the server: " );
                    
                    message = "PUBLISH|" + (++RQ) + "|" + name + "|" + sendingFile+"|";
                    return message;
                    }
                else{
                    System.out.println("There are no files in ./Files/ directory");
                }
    
            
             
            case 4://REMOVE

                String [] listofFilesRemoved;
                String inputRemove;
                System.out.println("Remove file Names:");
                inputRemove = bufferedReader.readLine();
                String sendingFileRemoved = "";
                listofFilesRemoved = inputRemove.split(",");
                for (int i=0;i<listofFilesRemoved.length;i++){
                    sendingFileRemoved += listofFilesRemoved[i] + " ";
                }
                message = "REMOVE|" + (++RQ) + "|" + name + "|" + sendingFileRemoved+"|";
                return message;

            case 5://RETRIEVE-ALL
                message = "RETRIEVE-ALL|" + (++RQ)+"|" ;
                return message;

            case 6://RETRIEVE specific
                System.out.println("client name to search by");
                String tempName= bufferedReader.readLine();
                if (tempName==null){
                    tempName=" ";
                }
                message = "RETRIEVE-INFOT|"+(++RQ)+"|"+tempName+"|";
                return message;

            case 7://SEARCH specific
                System.out.println("search by file name");
                String filename= bufferedReader.readLine();
                message = "SEARCH-FILE|"+(++RQ)+"|"+filename+"|";
                return message;

            case 8://DOWNLOAD a file
                System.out.println("Enter the name of the file you wish to download:");
                
                String fileNameDownload = bufferedReader.readLine();

                message = "DOWNLOAD|"+(++RQ)+"|"+fileNameDownload+"|";
                Path fileNamePath = Path.of(fileNameDownload);
                String content = Files.readString(fileNamePath);

                if(content == null){
                    return message = "DOWNLOAD-ERROR|"+(++RQ)+"|content does not exist";
                }

                List<String> chunks = new ArrayList<String>();
    
                while(chunks.size()*200 < content.length()){
                    if(chunks.size()*200+200 > content.length()){
                        chunks.add(new String(content.substring(chunks.size()*200,content.length())));
                    }else{
                        chunks.add(new String(content.substring(chunks.size()*200, chunks.size()*200+200)));
                    }
                }

                for(int i = 0; i< chunks.size(); i++){
                    Integer chunkNumber = i;
                    
                    //last chunk validation
                    if(i == chunks.size()-1){
                         return message = "FILE-END|"+(++RQ)+"|"+fileNameDownload+"|"+chunkNumber+"|"+chunks; //TODO: need to modify for individual chunks
                    }else{
                        return message = "FILE|"+(++RQ)+"|"+fileNameDownload+"|"+chunkNumber+"|"+chunks; //TODO: need to modify for individual chunks
                    }
                }

                System.out.println(content);
                // return message;
            case 9://UPDATE use contact info
                //add yes and no
                System.out.println("update TCP port");
                int tcpPort=Integer.parseInt(bufferedReader.readLine());
                System.out.println("update UDP port");
                int udpPort=Integer.parseInt(bufferedReader.readLine());
                System.out.println("update internet address");
                InetAddress inet= InetAddress.getByName(bufferedReader.readLine());
                message = "UPDATE-CONTACT|"+(++RQ)+"|"+name+"|"+inet+"|"+udpPort+"|"+tcpPort;
                return message;

            default:
                break;
        }
        return "INVALID INPUT";
    }


    public static void main(String[] args) throws IOException {

        DatagramSocket datagramSocket = new DatagramSocket();

        // Get the IP address of the server
        InetAddress inetAddress = InetAddress.getByName(SERVER_IP);
        Client client = new Client(datagramSocket, inetAddress);
        client.sendThenReceive();
    }
}