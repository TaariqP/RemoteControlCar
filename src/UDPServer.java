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

  public UDPServer() {
    run();
  }

  public void run() {
    //Setup server side socket
    try {
      serverSocket = new DatagramSocket(5555);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    while (true) {
      try {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        //receive a packet on the server and form the String
        serverSocket.receive(receivePacket);
        String sentence = new String(receivePacket.getData());
        System.out.println("RECEIVED: " + sentence);

        //Get the address + port of the client
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        //Send a packet back
        DatagramPacket sendPacket =
            new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);

      } catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
