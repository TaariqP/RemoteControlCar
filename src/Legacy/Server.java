package Legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Server extends JFrame {

  private ServerSocket server;
  private Socket connection;
  private JButton button;
  private boolean isConnected;
  private BufferedWriter output;
  private BufferedReader input;
  private String message;


  public static void main(String[] args) {
    Server server = new Server();
    server.run();
//    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//      public void run() {
//        server.endConnection();
//      }
//    }));
  }

  public Server() {
    //Set GUI for input
//    super("Legacy.Server");
//    JTextField left0 = new JTextField();
//    JTextField right0 = new JTextField();
//    JTextField left1 = new JTextField();
//    JTextField right1 = new JTextField();
//    button = new JButton("Send Command(s)");
//    button.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        sendCommands();
//      }
//    });
  }

  public void run() {
    try {
      server = new ServerSocket(5555);
      while (true) {
        try {
          //Make the connection to the pi
          connect();
          //do stuff
        } catch (EOFException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      endConnection();
    }
  }

  private void endConnection() {
    System.out.println("Ending connection");
    isConnected = false;
    try {
      input.close();
      output.close();
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void connect() throws IOException {
    System.out.println("Waiting for Connection...");
    connection = server.accept();
//    if (connection != null) {
//      isConnected = true;
//    }
    System.out
        .println("Connected to " + connection.getInetAddress().getHostName());
    output = new BufferedWriter(new OutputStreamWriter(connection
        .getOutputStream()));
    output.flush();
    input = new BufferedReader(new InputStreamReader(connection.getInputStream
        ()));
    //TEST
    do {
      //nothing just keep the server running
    } while (connection.isConnected());
  }

  //Unnecessary check for messages


  public void setPower(String message) {
    this.message = message;
    sendCommands();
  }

  public void sendCommands() {
    //Check to make sure it's connected
    assert (isConnected) : "Not Connected";
    System.out.println("Sending Command");
    try {
      //TEST
      System.out.println("Message has been sent");
      //Test
      output.write(message + "\n");
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //isConnected = false;

  }


}
