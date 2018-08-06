import java.io.*;
import java.net.*;

public class UDPServer {


    public static void main(String[] args) {
        UDPServer server = new UDPServer();
    }

    private byte[] receiveData = new byte[1024];
    private byte[] sendData;
    private DatagramSocket outputSocket;
    private DatagramPacket receivePacket;
    private String serverAddress = "192.168.1.21";
    private InetAddress IPAddress;
    private int port = 3323;
    private String command;
    private boolean connected = false;

    public UDPServer() {
    }

    public void startRunning() {
        try {

            outputSocket = new DatagramSocket(5555);
            //Sends a packet to the intermediate server.
            String sentence = "Connected to XBOX CONTROLLER";
            IPAddress = InetAddress.getByName(serverAddress);
            while (!connected) {
                //Send and receive to establish connection
                checkConnection(sentence);
            }

            System.out.println("Connected to the server: confirmed");


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
            sendData = new byte[1024];
            sendData = command.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            outputSocket.send(sendPacket);
            System.out.println("SENT: " + command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendPacket(String message) throws IOException {
        sendData = new byte[1024];
        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length,
                IPAddress, port);
        outputSocket.send(sendPacket);
        System.out.println("packet sent");
        sendData = new byte[1024];
    }

    public void checkConnection(String message) throws IOException {
        //Checks that the controller is still connected to the server

        sendData = new byte[1024];
        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length,
                IPAddress, port);
        outputSocket.send(sendPacket);
        System.out.println("packet sent");
        sendData = new byte[1024];

        //Receive a packet from xbox controller server
        receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        outputSocket.receive(receivePacket);
        message = new String(receivePacket.getData());
        System.out.println("TEST CONNECTION:" + message);
        connected = true;
    }

}
