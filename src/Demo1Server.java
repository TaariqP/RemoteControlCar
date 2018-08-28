import java.io.*;
import java.net.*;

public class Demo1Server {

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket controllerSocket;
  private DatagramSocket clientSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddressController;
  private InetAddress IPAddressClient;
  private int clientPort = 3322;
  private int controllerPort = 5555;
  private String command = "0,0,0";
  private boolean controllerConnected = false;
  private boolean carConnected = false;
  private String latency;


  public static void main(String[] args) {
    Demo1Server server = new Demo1Server();
    server.startRunning();

  }

  public Demo1Server() {
    System.out.println("UDP Server created");
  }

  public void startRunning() {
    try {
      clientSocket = new DatagramSocket(3322);
      controllerSocket = new DatagramSocket(3323);
      System.out.println("Waiting for Controller...");

      //Receive a packet from the controller

      receiveFromController();
      IPAddressController = receivePacket.getAddress();
      controllerPort = receivePacket.getPort();
      System.out.println("Now Connected to controller: " +
          IPAddressController +
          " at port " + controllerPort);
      controllerConnected = true;

      sendToController("Connected to UDP Server");

      //Receive a packet from the car
      System.out.println("Waiting for car...");
      receiveFromCar();
      IPAddressClient = receivePacket.getAddress();
      clientPort = receivePacket.getPort();
      System.out.println("Now Connected to Car: " + IPAddressClient + " at "
          + "port " + clientPort);
      carConnected = true;

      happySignal();

      Thread send = new Thread(() -> {
        System.out.println("Sending commands...");
        try {
          while (true) {
            Thread.sleep(100);
            sendCommands();
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      });

      Thread listen_controller = new Thread(() -> {
        System.out.println("Listening to controller...");
        try {
          listenToController();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      send.start();
      listen_controller.start();

      try {
        listen_controller.join();
        send.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendToController(String message) throws IOException {
    assert (controllerConnected) : "Controller is no longer connected";
    sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddressController,
            controllerPort);
    controllerSocket.send(sendPacket);
    System.out.println("SENT TO CONTROLLER: " + message);
  }

  public String receiveFromController() throws IOException {
    assert (controllerConnected) : "Controller is no longer connected";
    receiveData = new byte[1024];
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    controllerSocket.receive(receivePacket);
    String command = new String(receivePacket.getData());
    System.out.println("RECEIVED FROM CONTROLLER: " + command);
    return command;
  }

  public synchronized void sendCommands() throws IOException {
    assert (carConnected) : "Car is no longer connected";
    sendData = new byte[1024];
    sendData = command.getBytes();
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddressClient,
            clientPort);
    System.out.println("SENDING TO CAR: " + command);
    clientSocket.send(sendPacket);
  }

  public String receiveFromCar() throws IOException {
    assert (carConnected) : "Car is no longer connected";
    receiveData = new byte[1024];
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    clientSocket.receive(receivePacket);
    String sentence = new String(receivePacket.getData());
    System.out.println("RECEIVED FROM CAR: " + sentence);
    return sentence;
  }

  public void happySignal() {
    assert (carConnected) : "Car is no longer connected";
    //Move car to indicate connection
    System.out.println("Connected to both - sending happy signal");
    if (controllerPort == 5555) {
      System.out.println("Successful connection");
      try {
        System.out.println("Sending startup signals");
        setPower("50,50,0");
        Thread.sleep(1000);
        setPower("-50,-50,0");
        Thread.sleep(1000);
        setPower("0,0,0");
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void setPower(String command) throws IOException {
    this.command = command;
    sendCommands();
  }

  public void listenToController() throws IOException {
    while (true) {
      assert (controllerConnected) : "Controller is no longer connected";
      String control = receiveFromController();
      if (control.substring(0, 4).equals("PING")) {
        getLatency();
        sendToController(latency);
      } else {
        System.out.println("FROM CONTROLLER: " + control);
        setPower(control);
      }
    }
  }

  public void getLatency() throws IOException {
    String str = receiveFromCar();
    latency = str.substring(3, 10);
  }
}
