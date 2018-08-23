import java.io.*;
import java.net.*;

public class Demo2Server {

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket controllerSocket;
  private DatagramSocket clientSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddressController;
  private InetAddress IPAddressClient;
  private InetAddress IPAddressCar2;
  private int car1Port = 3322;
  private int controllerPort = 5555;
  private String command;
  private boolean controllerConnected = false;
  private boolean carConnected = false;
  private boolean car2Connected = false;
  private int car2Port = 3322;


  public static void main(String[] args) {
    Demo2Server server = new Demo2Server();
    server.startRunning();
  }

  public Demo2Server() {
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
      car1Port = receivePacket.getPort();
      System.out.println("Now Connected to Car 1: " + IPAddressClient + " at "
          + "port " + car1Port);
      carConnected = true;

      //Receive packet from second car
      do {
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        clientSocket.receive(receivePacket);
        IPAddressCar2 = receivePacket.getAddress();
        System.out.println("Car 1 address: " + IPAddressClient.getHostAddress
            () + " and Car 2 address: " + IPAddressCar2.getHostAddress());
      } while (IPAddressClient.getHostAddress().equals(
          IPAddressCar2.getHostAddress()));

      System.out.println("Car 1 address: " + IPAddressClient.getHostAddress
          () + " and Car 2 address: " + IPAddressCar2.getHostAddress());
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      car2Port = receivePacket.getPort();
      System.out.println("Now Connected to Car 2 :" + IPAddressCar2 + " at "
          + "port " + car2Port);
      car2Connected = true;

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
    //If packet is not received end server
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
          System.out.println("FROM CAR: \"" + str + "\"");
          if (!(str.length() < 3)) {
            str = str.substring(0, 4);
            System.out.println(str);
          }
        }
        System.out.println("STOP RECEIVED!");
        //stop command received
        stopCars();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stopCars() {
    //Send stop command to both cars
    System.out.println("STOPPING CARS");
    command = "0,0,0";
    sendCommands();
  }

  public synchronized void listenToController() {
    try {
      while (true) {

        //keepAlive();

        //Receive a packet from Xbox controller server
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        controllerSocket.receive(receivePacket);
        String command = new String(receivePacket.getData());
        System.out.println("FROM CONTROLLER: " + command);
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
              car1Port);
      System.out.println("SENDING TO CAR 1: " + command);
      clientSocket.send(sendPacket);

      //TO CAR2
      sendPacket = new DatagramPacket(sendData, sendData.length,
          IPAddressCar2, car2Port);
      System.out.println("SENDING TO CAR 2: " + command);
      clientSocket.send(sendPacket);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
