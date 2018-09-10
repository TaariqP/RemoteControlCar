import java.io.IOException;
import java.net.DatagramSocket;

public class ServerSwitcher extends DemoServer {


  private String type;
  private DemoServer server = null;

  public static void main(String[] args) throws IOException {
    ServerSwitcher serverSwitcher = new ServerSwitcher();
    serverSwitcher.startRunning();
  }

  public void startRunning() throws IOException {
    controllerSocket = new DatagramSocket(3323);
    System.out.println("Waiting for Controller...");
    //Receive a packet from the controller

    type = receiveFromController();
    IPAddressController = receivePacket.getAddress();
    controllerPort = receivePacket.getPort();
    System.out.println("Now Connected to controller: " +
        IPAddressController +
        " at port " + controllerPort);
    controllerConnected = true;

    System.out.println(type);
    if (type.substring(0,1).equals("1")){
      server = new Demo1Server();
    }
    else{
      server = new Demo2Server();
    }

    sendToController("Switching Legacy.Server");
    controllerSocket.close();
    server.startRunning();


  }

  public ServerSwitcher() {
    System.out.println("Legacy.Server Switcher started");
  }

}