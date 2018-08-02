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
  private InetAddress IPAddress;
  private int port;
  private String command;

  public UDPServer() {
  }

  public void startRunning() {
    try {

      String sentence;
      serverSocket = new DatagramSocket(5555);

      //Receives packet from the intermediate server to know the address
      DatagramPacket receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      serverSocket.receive(receivePacket);
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      IPAddress = receivePacket.getAddress();
      port = receivePacket.getPort();
      System.out.println("Connected to: " + IPAddress + " at port: " + port);

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

}
