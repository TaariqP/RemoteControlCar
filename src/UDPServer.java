import java.io.*;
import java.net.*;

public class UDPServer {


  public static void main(String[] args) {
    UDPServer server = new UDPServer();
  }

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket serverSocket;
  private DatagramPacket receivePacket;
  private String serverAddress = "192.168.1.21";
  private InetAddress IPAddress;
  private int port = 3323;
  private String command;

  public UDPServer() {
  }

  public void startRunning() {
    try {

      String sentence;
      serverSocket = new DatagramSocket(5555);

      //Sends a packet to the intermediate server.
      sentence = "You are now connected to the XBOX CONTROLLER";
      sendData = sentence.getBytes();
      IPAddress = InetAddress.getByName(serverAddress);
      DatagramPacket sendPacket = new DatagramPacket(sendData,
          sendData.length,
          IPAddress, port);
      serverSocket.send(sendPacket);
      System.out.println("packet sent");

      //HardCoded Address test
//      this.IPAddress = InetAddress.getByName("10.255.55.104");
//      port = 3323;

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
      sendData = command.getBytes();
      DatagramPacket sendPacket =
          new DatagramPacket(sendData, sendData.length, IPAddress, port);
      serverSocket.send(sendPacket);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void keepAlive(){

  }

}
