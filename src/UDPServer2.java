import java.io.*;
import java.net.*;

public class UDPServer2 {


  public static void main(String[] args) {
    UDPServer2 server = new UDPServer2();
  }

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket serverSocket;
  private DatagramSocket clientSocket;
  private DatagramPacket receivePacket;
  private String controllerAddress = "192.168.1.47";
  private InetAddress IPAddressController;
  private InetAddress IPAddressClient;
  private int clientPort = 3322;
  private int serverPort = 5555;
  private String command;

  public UDPServer2() {
    startRunning();
  }

  public void startRunning() {
    try {
      String sentence;
      clientSocket = new DatagramSocket(3322);
      serverSocket = new DatagramSocket(3323);

      IPAddressController = InetAddress.getByName(controllerAddress);

      //Send a packet to the Xbox Controller server
      sentence = "You are now connected to UDPSERVER2";
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData,
          sendData.length,
          IPAddressController, serverPort);
      serverSocket.send(sendPacket);

      //Receive data from the car
      DatagramPacket receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      clientSocket.receive(receivePacket);
      sentence = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + sentence);
      IPAddressClient = receivePacket.getAddress();
      clientPort = receivePacket.getPort();
//
//      InetAddress IPAddressClient = InetAddress.getByName("192.168.1.20");
//      clientPort = 3322;
      System.out.println("Connected to: " + IPAddressClient + " at port: " +
          clientPort);
      listen();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setPower(String command) {
    this.command = command;
    sendCommands();
  }

  public void listen() {
    try {
      while (true) {
        //Receive a packet from xbox controller server
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        serverSocket.receive(receivePacket);
        String command = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + command);
        setPower(command);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void sendCommands() {
    //Setup server side socket

    try {
      sendData = command.getBytes();
      DatagramPacket sendPacket =
          new DatagramPacket(sendData, sendData.length, IPAddressClient,
              clientPort);
      System.out.println("SENDING TO CAR: "+ command);
      clientSocket.send(sendPacket);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
