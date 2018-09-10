import java.io.*;
import java.net.*;

public class Demo2Server extends DemoServer {

  private InetAddress IPAddressCar1;
  private InetAddress IPAddressCar2;
  private int car1Port;
  private int car2Port;
  private boolean car1Connected = false;
  private boolean car2Connected = false;


  public static void main(String[] args) {
    Demo2Server server = new Demo2Server();
    server.startRunning();
  }

  public Demo2Server() {
    System.out.println("Demo 2 Legacy.Server created");
  }

  public void startRunning() {
    try {
      carSocket = new DatagramSocket(3322);
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

      sendToController("Connected to UDP Legacy.Server");

      System.out.println("Waiting for the first car...");
      //Receive a packet from the first car
      String data = receiveFromCar();
      System.out.println("RECEIVED: " + data);
      IPAddressCar1 = receivePacket.getAddress();
      car1Port = receivePacket.getPort();
      System.out.println("Now Connected to Car 1: " + IPAddressCar1 + " at "
          + "port " + carPort);
      car1Connected = true;

      //Receive packet from the second car ensure its not the first car
      do {
        receiveFromCar();
        IPAddressCar2 = receivePacket.getAddress();
        car2Port = receivePacket.getPort();
        System.out.println("Waiting for second car...");
      } while (IPAddressCar1.getHostAddress().equals(
          IPAddressCar2.getHostAddress()));

      System.out.println("Now Connected to Car 2 :" + IPAddressCar2 + " at "
          + "port " + carPort);
      car2Connected = true;

      //Send startup signals
      startupSignals();

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


  public void listenToCar() {
    try {
      while (true) {
        //Receive a packet from car
        DatagramPacket packet;
        byte[] data = new byte[1024];
        String str = "";
        while (!str.equals("STOP")) {
          packet = new DatagramPacket(data, data.length);
          carSocket.receive(packet);
          str = new String(packet.getData());
          System.out.println("FROM CAR: \"" + str + "\"");
          if (!(str.length() < 3)) {
            str = str.substring(0, 4);
          }
        }
        System.out.println(str);
        stopCars();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stopCars() {
    //Send stop command to both cars
    command = "0,0,0";
    sendCommands();
  }


  public void listenToController() {
    try {
      while (true) {
        //Receive a packet from Xbox controller server
        String command = receiveFromController();
        if (command.substring(0, 4).equals("PING")) {
          //Demo 2 does not need to produce a graph of latency
          sendToController("0.000");
        } else {
          System.out.println("FROM CONTROLLER: " + command);
          setPower(command);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendToCar(InetAddress IPAddress, int carPort) throws IOException {
    sendData = new byte[1024];
    sendData = command.getBytes();
    sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress,
            carPort);
    carSocket.send(sendPacket);
  }


  @Override
  public void sendCommands() {
    //Send command via client socket
    try {
      //Send command to Car 1
      sendToCar(IPAddressCar1, car1Port);
      System.out.println("SENDING TO CAR 1: " + command);

      //Send command to Car 2
      sendToCar(IPAddressCar2, car2Port);
      System.out.println("SENDING TO CAR 2: " + command);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}