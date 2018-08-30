import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Application;

public class ControllerServer {

  private String serverAddress = "192.168.1.21";
  private byte[] receiveData = new byte[1024];
  private byte[] sendData;
  private DatagramSocket outputSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddress;
  private int port = 3323;
  private String command;
  private boolean connected = false;
  private static double total;
  private List<Double> theTotals;
  private List<Double> carToServPings;
  private List<Double> contrToServPings;
  private int firstFiveCounter = 0;
  private double carToServer;
  private double conToServer;
  private String type;

  public ControllerServer() {
    System.out.println("UDP Controller Server Created");
    theTotals = new ArrayList();
    contrToServPings = new ArrayList<>();
    carToServPings = new ArrayList<>();
  }

  public void startRunning() {

    //Create the graph application in a window
    new Thread(() -> Application.launch(LineGraph.class)).start();

    //Connect to the Server
    try {
      outputSocket = new DatagramSocket(5555);
      IPAddress = InetAddress.getByName(serverAddress);
      while (!connected) {
        checkConnection(type);
      }
      System.out.println("Connected to the Server: Confirmed");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setPower(String command) {
    this.command = command;
    sendCommands();
  }

  public void setType(String type){
    this.type = type;
  }

  private void sendCommands() {
    //Send commands to the Server to send to the Car
    try {
      sendToServer(command);
      //Check server type to run the following command
      calculatePing();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void calculatePing() throws IOException {
    conToServer = ping();
    total = carToServer + conToServer;
    //Edge to Edge
    conToServer = conToServer / 2;
    System.out.println("PING: Controller To Server: " + conToServer);
    carToServer = carToServer / 2;
    System.out.println("PING: Car To Server: " + carToServer);
    total = total / 2;
    System.out.println("TOTAL LATENCY: " + total);
    if (!isFirstFive()) {
      theTotals.add(total);
      carToServPings.add(carToServer);
      contrToServPings.add(conToServer);
    }
    firstFiveCounter++;
  }

  private double ping() throws IOException {
    Date now = new Date();
    long sendTime = now.getTime();
    long receiveTime = 0;
    String message = "PING";
    sendToServer(message);

    try {
      String pingString = receiveFromServer();
      pingString = pingString.trim();
      if (pingString.length() == 0) {
        System.out.println("Empty String detected");
      } else {
        try {
          carToServer = Double.parseDouble(pingString);
        } catch (Exception e) {
          System.out.println("Parsing error");
          System.out.println("Ping String: " + pingString);
        }
      }
      now = new Date();
      receiveTime = now.getTime();
    } catch (IOException e) {
      System.out.println("Timeout Error");
    }
    //Time in seconds return
    return (receiveTime - sendTime) * 0.001;

  }

  public void sendToServer(String message) throws IOException {
    sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData,
        sendData.length,
        IPAddress, port);
    outputSocket.send(sendPacket);
    System.out.println("SENT: " + message);
  }

  public String receiveFromServer() throws IOException {
    String message;
    receiveData = new byte[1024];
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    outputSocket.receive(receivePacket);
    message = new String(receivePacket.getData());
    System.out.println("RECEIVED: " + message);
    return message;
  }

  public void checkConnection(String message) throws IOException {
    //Send a test packet to the server
    sendToServer(message);
    //Receive a test packet from the server
    receiveFromServer();
    connected = true;
  }

  public double getCarToServer() {
    return carToServer;
  }

  public double getControllerToServer() {
    return conToServer;
  }

  public List<Double> getCarToServPings() {
    return carToServPings;
  }

  public List<Double> getContrToServPings() {
    return contrToServPings;
  }


  public boolean isFirstFive() {
    if (firstFiveCounter < 8) {
      return true;
    }
    return false;
  }

  public static double getTotal() {
    return total;
  }

  public List<Double> getTheTotals() {
    return theTotals;
  }

}
