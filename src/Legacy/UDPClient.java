package Legacy;

import java.io.*;
import java.net.*;

public class UDPClient {

  private byte[] receiveData = new byte[1024];
  private byte[] sendData = new byte[1024];
  private DatagramSocket clientSocket;
  private DatagramPacket receivePacket;


  public static void main(String[] args) {
    UDPClient udpClient = new UDPClient();
  }

  public UDPClient() {
    //Send initial packet for server to recognise client
    startRunning();
  }


  public void startRunning() {
    try {
      clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      String sentence = "You are now connected to Legacy.UDPClient";
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData,
          sendData.length,
          IPAddress, 3322);
      clientSocket.send(sendPacket);
      //Listen for commands
      listen();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public void listen() {
    try {
      while (true) {
        //Receive a packet from server
        receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        clientSocket.receive(receivePacket);
        String command = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + command);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
