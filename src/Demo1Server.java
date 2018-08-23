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
  private String command;
  private boolean controllerConnected = false;
  private boolean carConnected = false;
  private float ping;
  private double latency;


  public static void main(String[] args) {
    Demo1Server server = new Demo1Server();
    server.startRunning();
  }

  public Demo1Server() {
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

      happySignal();



      Thread listen_controller = new Thread(() -> {
        System.out.println("Listening to controller...");
        listenToController();
      });

      Thread listen_car = new Thread(() -> {
        System.out.println("Listening to car...");
        listenToCar();
      });

      listen_controller.start();
      listen_car.start();

      try {
        listen_controller.join();
        listen_car.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

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
      System.out.println("To Controller: " + message);
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

  public void listenToController() {
    try {
      while (true) {

        //keepAlive();

        //Receive a packet from Xbox controller server
        receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        controllerSocket.receive(receivePacket);
        String command = new String(receivePacket.getData());
        if (command.substring(0, 4).equals("PING")){
        }
        else{
//        System.out.println("FROM CONTROLLER: " + command);
        setPower(command);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void listenToCar() {
    try {
      while (true) {

        //keepAlive();

        //Receive a packet from car
        DatagramPacket packet;
        byte[] data = new byte[1024];
        String str = "";
        while (!str.equals("STOP")) {
          packet = new DatagramPacket(data, data.length);
          clientSocket.receive(packet);
          str = new String(packet.getData());
//          System.out.println("FROM CAR: \"" + str + "\"");
//          System.out.println(str.substring(3,10));
//          ping = Float.valueOf(str.substring(6, 13));
          sendToController(str.substring(3,10));
        }
        System.out.println("STOP RECEIVED!");
        //stop command received
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
//      System.out.println("SENDING TO CAR: " + command);
      clientSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
