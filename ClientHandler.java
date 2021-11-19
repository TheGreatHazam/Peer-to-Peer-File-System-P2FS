import java.net.InetAddress;
import java.util.ArrayList;

public class ClientHandler {
    private static int  counter = 1;
    private int RQId;
    private int UDPPort;
    private int TCPPort;
    private InetAddress address;
    private static String name;
    private ArrayList<String> fileList;

    public ClientHandler(int UDPPort, int TCPPort, InetAddress address, String name) {
        //Increment RQID per creating of client.
        this.RQId = counter;
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.address = address;
        this.name = name;
        counter++;
    }

    public int getUDPPort() {
        return UDPPort;
    }

    public void setUDPPort(int UDPPort) {
        this.UDPPort = UDPPort;
    }

    public int getTCPPort() {
        return TCPPort;
    }

    public void setTCPPort(int TCPPort) {
        this.TCPPort = TCPPort;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getRQID(){ return RQId;}

    public ArrayList getFileList() {
        return fileList; 
    
    }
        
    public  void setFileList(ArrayList<String> listofFiles) {
        this.fileList=listofFiles;
    }


    @Override
    public String toString() {
        return " ClientHandler: " + " " +
                "UDPPort=" + UDPPort + " " +
                "| TCPPort=" + TCPPort + " " +
                "| address=" + address + " " +
                "| Name=" + name + " " +
                "| RQID ="+ RQId + " " +
                "| fileList = " + fileList + " ";
    }
}
