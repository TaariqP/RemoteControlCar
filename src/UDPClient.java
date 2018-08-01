import java.io.*;
import java.net.*;

public class UDPClient {

  private byte[] receiveData;
  private byte[] sendData;


  public static void main(String[] args) {
    UDPClient udpClient = new UDPClient();
  }

  public UDPClient() {
    run();
  }

  public void run() {
    try {
      //No port number - for any local port.
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      sendData = new byte[1024];
      receiveData = new byte[1024];

      //Read a line from the user.
      BufferedReader inFromUser =
          new BufferedReader(new InputStreamReader(System.in));
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();

      //Send a packet to the server's port
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
          IPAddress, 5555);
      clientSocket.send(sendPacket);

      //Receive a packet from server
      DatagramPacket receivePacket = new DatagramPacket(receiveData,
          receiveData.length);
      clientSocket.receive(receivePacket);

      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);
      
      clientSocket.close();

    } catch (SocketException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
