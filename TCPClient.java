
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient implements Runnable
{   
    int portNumber;
    ServerSocket tcpSocket;
    List <Thread> clientThreads;
    
        TCPClient(int serverportnumber){
            this.portNumber = serverportnumber
            tcpSocket = new ServerSocket(portNumber);
        }
        
    @Override
    public void run() {
        try {
            while (!tcpSocket.isClosed()) {
                Socket clientSocket = tcpSocket.accept();
                TCPDownload tcpDownload = new TCPDownload(clientSocket);
                Thread clientdownloadThread = new Thread(tcpDownload); 
                clientdownloadThread.start();
                clientThreads.add(ct);

            }
            for (Thread clientThread : clientThreads) {
                clientThread.join();
            }
        } catch (Exception e) {
            try {
                for (Thread clientThread : clientThreads) {
                        clientThread.join();
                    }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }  
    }
}