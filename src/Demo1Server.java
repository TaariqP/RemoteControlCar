import java.io.*;
import java.net.*;

public class Demo1Server extends DemoServer {

  private String latency;

  public static void main(String[] args) {
    Demo1Server server = new Demo1Server();
    server.startRunning();
  }

  public Demo1Server() {
    System.out.println("Demo Server 1 created");
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

      sendToController("Connected to UDP Server");

      //Receive a packet from the car
      System.out.println("Waiting for car...");
      receiveFromCar();
      IPAddressClient = receivePacket.getAddress();
      carPort = receivePacket.getPort();
      System.out.println("Now Connected to Car: " + IPAddressClient + " at "
          + "port " + carPort);
      carConnected = true;

      startupSignals();

      Thread send = new Thread(() -> {
        System.out.println("Sending commands...");
        try {
          while (true) {
            Thread.sleep(100);
            sendCommands();
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        } finally {
          closeConnections();
        }
      });

      Thread listen_controller = new Thread(() -> {
        System.out.println("Listening to controller...");
        try {
          listenToController();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          closeConnections();
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



  public void listenToController() throws IOException {
    while (true) {
      assert (controllerConnected) : "Controller is no longer connected";
      String control = super.receiveFromController();
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
