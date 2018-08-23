import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class UDPServer {


  public static void main(String[] args) {
    UDPServer server = new UDPServer();
  }

  private byte[] receiveData = new byte[1024];
  private byte[] sendData;
  private DatagramSocket outputSocket;
  private DatagramPacket receivePacket;
  private String serverAddress = "192.168.1.21";
  private InetAddress IPAddress;
  private int port = 3323;
  private String command;
  private boolean connected = false;
  private double pingTime;
  private static double total;
  private LineGraph graph;

  public UDPServer() {
  }

  public static double getTotal() {
    return total;
  }

  public void startRunning() {

    new Thread() {
      @Override
      public void run() {
        javafx.application.Application.launch(LineGraph.class);
      }
    }.start();
    graph = LineGraph.returnThis();

    try {

      outputSocket = new DatagramSocket(5555);
      //Sends a packet to the intermediate server.
      String sentence = "Connected to XBOX CONTROLLER";
      IPAddress = InetAddress.getByName(serverAddress);
      while (!connected) {
        //Send and receive to establish connection
        checkConnection(sentence);
      }

      System.out.println("Connected to the server: confirmed");

      listenToServer();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setPower(String command) {
    this.command = command;
    sendCommands();
  }

  public void sendCommands() {
    //Setup server side socket
    try {
      //Send a packet back
      sendData = new byte[1024];
      sendData = command.getBytes();
      DatagramPacket sendPacket =
          new DatagramPacket(sendData, sendData.length, IPAddress, port);
      outputSocket.send(sendPacket);
      System.out.println("SENT: " + command);
      double conToServ = ping();
      System.out.println("PING: Controller To Server: " + conToServ);
      this.total = pingTime + conToServ;
      System.out.println("TOTAL LATENCY: " + total);
      System.out.println();
      System.out.println();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void listenToServer() {
    try {
      while (true) {

        //keepAlive();
        receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        outputSocket.receive(receivePacket);
        String message = new String(receivePacket.getData());
        //        System.out.println("FROM SERVER: " + message);
//        System.out.println("Ping from Client to Server: " + message.substring
//            (0, 7));
//        ping = Double.parseDouble(message.substring(0,7));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public double ping() throws IOException {
    Date now = new Date();
    //Milliseconds
    long sendTime = now.getTime();
    long receiveTime = 0;
    String message = "PING";
    byte[] buffer = new byte[1024];
    buffer = message.getBytes();
    DatagramPacket ping = new DatagramPacket(buffer, buffer.length, IPAddress,
        port);
    outputSocket.send(ping);

    try {
      receiveData = new byte[1024];
      receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      outputSocket.receive(receivePacket);
      String pingString = new String
          (receivePacket.getData());
      System.out.println("PING: Client to Server: " + pingString);
      pingString = pingString.trim();
      if (pingString.length() == 0) {
        System.out.println("Caught empty string. Ignoring result... "
            +
            "-------------------------------------------------------------------------------------------------");
      } else {
        pingTime = Double.parseDouble(pingString);
        pingTime = pingTime / 2;
      }
      now = new Date();
      receiveTime = now.getTime();
    } catch (IOException e) {
      System.out.println("Timeout Error");
    }


    //Time in milliseconds
    return (receiveTime - sendTime) * 0.001;

  }


  public void sendPacket(String message) throws IOException {
    sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData,
        sendData.length,
        IPAddress, port);
    outputSocket.send(sendPacket);
    System.out.println("packet sent");
    sendData = new byte[1024];
  }

  public void checkConnection(String message) throws IOException {
    //Checks that the controller is still connected to the server

    sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData,
        sendData.length,
        IPAddress, port);
    outputSocket.send(sendPacket);
    System.out.println("packet sent");
    sendData = new byte[1024];

    //Receive a packet from xbox controller server
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    outputSocket.receive(receivePacket);
    message = new String(receivePacket.getData());
    System.out.println("TEST CONNECTION:" + message);
    connected = true;
  }

}
