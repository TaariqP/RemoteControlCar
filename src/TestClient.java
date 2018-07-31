import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TestClient {


  private String serverIP = "127.0.0.1";
  private TestClient client;
  private Socket connection;
  private BufferedReader input;
  private BufferedWriter output;
  private int power;
  private boolean isConnected;

  public static void main(String[] args) {
    TestClient testClient = new TestClient();
//    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//      public void run() {
//        testClient.endConnection();
//      }
//    }));
  }

  public TestClient() {
    run();
  }

  public void run() {
    while (true) {
      System.out.println("Client attempting to connect");
      try {
        connection = new Socket("localhost", 5555);
        if (connection.isConnected()) {
          isConnected = true;
          break;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Connected to: " + connection.getInetAddress()
        .getHostName());
    connected();
  }

  public void endConnection() {
    System.out.println("Closing connection from Client Side");
    isConnected = false;
    try {
      input.close();
      output.close();
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void connected() {
    System.out.println("Setting up communication");
    try {
      output = new BufferedWriter(new OutputStreamWriter(connection
          .getOutputStream()));
      output.flush();
      input = new BufferedReader(new InputStreamReader(connection
          .getInputStream()));
      whileRunning();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      endConnection();
    }
  }

  public void whileRunning() throws IOException {
    do {
      String message = input.readLine();
      System.out.println(message);
    } while (isConnected);
  }


}
