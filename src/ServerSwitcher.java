import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerSwitcher {

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket controllerSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddressController;
  private int controllerPort = 5555;
  private boolean controllerConnected = false;
  private String type;
  private DemoServer server1 = null;
  private DemoServer server2 = null;

  public static void main(String[] args) throws IOException {
    ServerSwitcher serverSwitcher = new ServerSwitcher();
    serverSwitcher.startRunning();
  }

  private void startRunning() throws SocketException {
    controllerSocket = new DatagramSocket(3323);
    System.out.println("Waiting for Controller...");
    //Receive a packet from the controller
  }

  public ServerSwitcher() {
    System.out.println("Server Switcher started");
  }

}