import java.io.*;
import java.net.*;

public class UDPServer3 {

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket controllerSocket;
  private DatagramSocket clientSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddressController;
  private InetAddress IPAddressClient;
  private InetAddress IPAddressCar2;
  private int clientPort = 3322;
  private int controllerPort = 5555;
  private String command;
  private boolean controllerConnected = false;
  private boolean carConnected = false;


  public static void main(String[] args) {
    UDPServer3 server = new UDPServer3();
    server.startRunning();
  }

  public UDPServer3() {
    System.out.println("UDP Server created");
  }

  public void startRunning() {
    try {
      String sentence;
      clientSocket = new DatagramSocket(3322);
      controllerSocket = new DatagramSocket(3323);
      System.out.println("Trying to connect ...");

      //Receive a packet from the controllerServer

      receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      controllerSocket.receive(receivePacket);
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      IPAddressController = receivePacket.getAddress();
      controllerPort = receivePacket.getPort();
      System.out.println("Now Connected to controller: " +
          IPAddressController +
          " at port " + controllerPort);
      controllerConnected = true;
      sendToController("Connected to UDP Server");

      //Receive a packet from the car
      receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      clientSocket.receive(receivePacket);
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      IPAddressClient = receivePacket.getAddress();
      clientPort = receivePacket.getPort();
      System.out.println("Now Connected to Car: " + IPAddressClient + " at "
          + "port " + clientPort);
      carConnected = true;


      //Receive packet from second car
      receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      clientSocket.receive(receivePacket);
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      IPAddressCar2 = receivePacket.getAddress();
      clientPort = receivePacket.getPort();
      System.out.println("Now Connected to Car: " + IPAddressClient + " at "
          + "port " + clientPort);
      carConnected = true;

      happySignal();
      listen();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendToController(String message) {
    //Send a packet to the controller
    sendData = new byte[1024];
    try {
      sendData = message.getBytes();
      DatagramPacket sendPacket =
          new DatagramPacket(sendData, sendData.length, IPAddressController,
              controllerPort);
      System.out.println("TEST CONNECTION: " + message);
      controllerSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void happySignal() {
    //Move car to indicate connection
    System.out.println("Connected to both: sending happy signal");
    if (controllerPort == 5555) {
      System.out.println("Successful connection boi");
      try {
        System.out.println("Sending startup signals");
        setPower("50,50,0");
        Thread.sleep(1000);
        setPower("-50,-50,0");
        Thread.sleep(1000);
        setPower("0,0,0");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void setPower(String command) {
    this.command = command;
    sendCommands();
  }

  public void keepAlive() throws IOException {
    //receive a packet from the car (should be every 100 ms)
    //If packet is not received
  }

  public void listen() {
    try {
      while (true) {

        //keepAlive();

        //Receive a packet from Xbox controller server
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        controllerSocket.receive(receivePacket);
        String command = new String(receivePacket.getData());
        System.out.println("FROM SERVER: " + command);
        setPower(command);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void sendCommands() {
    //Send command via client socket
    try {
      sendData = command.getBytes();
      DatagramPacket sendPacket =
          new DatagramPacket(sendData, sendData.length, IPAddressClient,
              clientPort);
      System.out.println("SENDING TO CAR: " + command);
      clientSocket.send(sendPacket);
      sendPacket = new DatagramPacket(sendData, sendData.length,
          IPAddressCar2, clientPort);
      clientSocket.send(sendPacket);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
