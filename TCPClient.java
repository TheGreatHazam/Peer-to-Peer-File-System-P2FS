import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TCPClient extends Thread {

        private Socket socket;
        private Scanner scanner;
        private List<String> chunks = new ArrayList<String>();
        private ObjectInputStream objInput;
        private ObjectOutputStream objOutput;
        private int peerTCPPort = 0;
        //private String fileName = "";
        private InetAddress serverAddress;

        int peerTCPport;
        ServerSocket TCPServerSocket;
        Socket downloadSocket;
        String fileName;

        TCPClient(int peerTCPPort, String fileName)
        {
            this.peerTCPport=peerTCPPort;
            this.fileName= fileName;
        }
        
        public void run()
        {
            try{
                TCPServerSocket = new ServerSocket(peerTCPPort);
                downloadSocket = TCPServerSocket.accept();
                new ServerDownloadThread(downloadSocket, fileName).start();
            }
            
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the name of the file you wish to download:");
        fileName = br.readLine();
        TCPClient objServerDownload = new TCPClient(peerTCPPort, fileName);
    
}

class ServerDownloadThread extends Thread 
{
    Socket downloadThreadSocket;
    String fileName;

    public ServerDownloadThread(Socket downloadThreadSocket, String fileName)
    {
        this.downloadThreadSocket = downloadThreadSocket;
        this.fileName = fileName;
    }

    public void run()
    {
        try {
            ObjectOutputStream objOutput = new ObjectOutputStream(downloadThreadSocket.getOutputStream());
            ObjectInputStream objInput = new ObjectInputStream(downloadThreadSocket.getInputStream());

            String fileName = (String)objInput.readObject();
            while(true)
            {
                File mFile = new File("./Files/"+fileName);
                long length = mFile.length();

                byte [] byte_arr = new byte[(int)length];

                objOutput.writeObject((int)mFile.length());
                objOutput.flush();

                FileInputStream fileInputStream = new FileInputStream(mFile);
                BufferedInputStream objBufferInput = new BufferedInputStream(fileInputStream);

                objBufferInput.read(byte_arr,0,(int)mFile.length());

                objOutput.write(byte_arr,0,byte_arr.length);

                objOutput.flush();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }                   
    }   
}

    


