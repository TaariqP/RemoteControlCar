import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Application;

public class ControllerServer {

  private String serverAddress = "127.0.0.1";
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
  private int ignoreCounter = 0;
  private double carToServer;
  private double conToServer;


  //Ignore first few anomalous results
  public boolean toIgnore() {
    if (ignoreCounter < 8) {
      return true;
    }
    return false;
  }

  //Get the total latencies
  public List<Double> getTheTotals() {
    return theTotals;
  }

  public ControllerServer() {
    System.out.println("UDP Controller Legacy.Server Created");
    theTotals = new ArrayList();
    contrToServPings = new ArrayList<>();
    carToServPings = new ArrayList<>();
  }

  //Returns the current total latency
  public static double getTotal() {
    return total;
  }

  //Get IPAddress from a file
  public void getIPAddress() throws IOException {
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new FileReader("ip"
          + ".txt"));
      this.serverAddress = bufferedReader.readLine();
    } finally {
      assert bufferedReader != null;
      bufferedReader.close();
    }
  }

  public void startRunning() {
    //Create the graph application in a window
    new Thread(() -> Application.launch(LineGraph.class)).start();

    //Connect to the Legacy.Server
    try {
      outputSocket = new DatagramSocket(5555);
      String sentence = "Connected to Xbox Controller";
      IPAddress = InetAddress.getByName(serverAddress);
      while (!connected) {
        checkConnection(sentence);
      }
      System.out.println("Connected to the Legacy.Server: Confirmed");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Sets the power for the car motors
  public void setPower(String command) {
    this.command = command;
    sendCommands();
  }

  public void sendCommands() {
    //Send commands to the server to send to the Car
    try {
      sendToServer(command);
      calculatePing();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Calculates the ping
  private void calculatePing() throws IOException {
    conToServer = ping();
    total = carToServer + conToServer;
    //Edge to Edge
    conToServer = conToServer / 2;
    System.out.println("PING: Controller To Legacy.Server: " + conToServer);
    carToServer = carToServer / 2;
    System.out.println("PING: Car To Legacy.Server: " + carToServer);
    total = total / 2;
    System.out.println("TOTAL LATENCY: " + total);
    if (!toIgnore()) {
      theTotals.add(total);
      carToServPings.add(carToServer);
      contrToServPings.add(conToServer);
    }
    ignoreCounter++;
  }

  //Sends a ping and receives the relevant latencies back.
  public double ping() throws IOException {
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

  //Sends a message to the server
  public void sendToServer(String message) throws IOException {
    sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData,
        sendData.length,
        IPAddress, port);
    outputSocket.send(sendPacket);
    System.out.println("SENT: " + message);
  }

  //Receives a message from the server
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

  //Checks that the server is connected by sending and receiving a message
  public void checkConnection(String message) throws IOException {
    //Send a test packet to the server
    sendToServer(message);
    //Receive a test packet from the server
    receiveFromServer();
    connected = true;
  }

  //Returns the latency between the car and server
  public double getCarToServer() {
    return carToServer;
  }

  //Returns the latency between the Controller and serve
  public double getControllerToServer() {
    return conToServer;
  }

  //Returns the list of car to server latencies
  public List<Double> getCarToServPings() {
    return carToServPings;
  }

  //Returns a list of controller to server latencies
  public List<Double> getContrToServPings() {
    return contrToServPings;
  }
}