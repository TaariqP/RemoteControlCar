import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class DemoServer {

  byte[] receiveData = new byte[1024];
  byte[] sendData = new byte[1024];
  DatagramSocket controllerSocket;
  DatagramSocket carSocket;
  DatagramPacket receivePacket;
  DatagramPacket sendPacket;
  InetAddress IPAddressController;
  InetAddress IPAddressClient;
  int carPort;
  int controllerPort;
  boolean controllerConnected = false;
  boolean carConnected = false;
  String command = "0,0,0";


  protected void sendToController(String message) throws IOException {
    assert (controllerConnected) : "Controller is no longer connected";
    sendData = new byte[1024];
    sendData = message.getBytes();
    sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddressController,
            controllerPort);
    controllerSocket.send(sendPacket);
    System.out.println("SENT TO CONTROLLER: " + message);
  }

  protected String receiveFromController() throws IOException {
    receiveData = new byte[1024];
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    controllerSocket.receive(receivePacket);
    String command = new String(receivePacket.getData());
    System.out.println("RECEIVED FROM CONTROLLER: " + command);
    return command;
  }

  protected synchronized void sendCommands() throws IOException {
    assert (carConnected) : "Car is no longer connected";
    sendData = new byte[1024];
    sendData = command.getBytes();
    sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddressClient,
            carPort);
    System.out.println("SENDING TO CAR: " + command);
    carSocket.send(sendPacket);
  }

  protected String receiveFromCar() throws IOException {
    receiveData = new byte[1024];
    receivePacket = new DatagramPacket(receiveData,
        receiveData.length);
    carSocket.receive(receivePacket);
    String sentence = new String(receivePacket.getData());
    System.out.println("RECEIVED FROM CAR: " + sentence);
    return sentence;
  }

  protected void startupSignals() {
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

  protected void setPower(String command) throws IOException {
    this.command = command;
    sendCommands();
  }
}
