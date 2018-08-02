import java.io.*;
import java.net.*;

public class UDPServer2 {


  public static void main(String[] args) {
    UDPServer2 server = new UDPServer2();
  }

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket serverSocket;
  private DatagramPacket receivePacket;
  private InetAddress IPAddress;
  private int port;
  private String command;

  public UDPServer2() {
    startRunning();
  }

  public void startRunning() {
    try {
      String sentence;

      String address = "192.168.1.40";
      InetAddress IPAddressServer = InetAddress.getByName(address);
      int serverPort = 5555;
      System.out.println("Connected to server: " + IPAddressServer + " at "
          + "port: " +
          serverPort);

      //Send a packet to the Xbox Controller server
      sentence = "You are now connected to UDPSERVER2";
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData,
          sendData.length,
          IPAddressServer, 5555);
      serverSocket.send(sendPacket);

      //Receive data from the car
      serverSocket = new DatagramSocket(3322);
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
