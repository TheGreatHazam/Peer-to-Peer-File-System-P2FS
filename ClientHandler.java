import java.net.InetAddress;

public class ClientHandler {
    private int UDPPort;
    private int TCPPort;
    private InetAddress address;
    private String Name;

    public ClientHandler(int UDPPort, int TCPPort, InetAddress address, String name) {
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.address = address;
        Name = name;
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
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "ClientHandler{" +
                "UDPPort=" + UDPPort +
                ", TCPPort=" + TCPPort +
                ", address=" + address +
                ", Name='" + Name + '\'' +
                '}';
    }
}
