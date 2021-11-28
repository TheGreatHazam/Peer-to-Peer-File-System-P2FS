import java.net.InetAddress;
import java.util.ArrayList;

public class ClientHandler {
    private int RQId;
    private int UDPPort;
    private int TCPPort;
    private InetAddress address;
    private  String name;
    private ArrayList<String> fileList;

    public ClientHandler(int RQId, int UDPPort, int TCPPort, InetAddress address, String name) {
        this.RQId = RQId;
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.address = address;
        this.name = name;
    }

    public int getRQId() {
        return RQId;
    }

    public void setRQId(int RQId) {
        this.RQId = RQId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<String> fileList) {
        this.fileList = fileList;
    }

    @Override
    public String toString() {
        return "ClientHandler{" +
                "RQId=" + RQId +
                ", UDPPort=" + UDPPort +
                ", TCPPort=" + TCPPort +
                ", address=" + address +
                ", name='" + name + '\'' +
                ", fileList=" + fileList +
                "\n";
    }
}
