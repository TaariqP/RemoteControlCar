import java.util.stream.Collectors;
import net.java.games.input.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.java.games.input.Controller.Type;

public class XboxInput {

  private static UDPServer server;

  public static void main(String[] args) {
    //Two threads - one runs the server, one changes the power for the server
    Thread server_thread_running = new Thread() {
      public void run() {
        System.out.println("Server thread running");
        server = new UDPServer();
        server.startRunning();
      }
    };
    Thread controller_thread_running = new Thread() {
      public void run() {
        System.out.println("Controller thread running");
        runController();
      }
    };
    server_thread_running.start();
    controller_thread_running.start();

    try {
      server_thread_running.join();
      controller_thread_running.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized static void runController() {

    List<Controller> controllers = Arrays
        .stream(ControllerEnvironment.getDefaultEnvironment().getControllers())
        .filter(controller -> controller.getType().equals(Type.GAMEPAD))
        .collect(Collectors.toList());
    //Names of all controllers

    assert (controllers.size() > 0) : "No controllers found";

    for (Controller controller : controllers) {
      System.out.println(controller.getName() + ", " + controller.getType());
    }
    //Main xbox controller
    Controller controller = controllers.get(0);
    if (controller == null) {
      System.out.println("Could not find controller");
    }

    //Display gamepad components

    Component[] components = controller.getComponents();
    for (Component component : components) {
      System.out.println("Name: " + component.getName() + ", Identifier: "
          + component.getIdentifier() + ", Type: " + ((component
          .isRelative()) ? " Relative, " : "Absolute, ") + " Analogue or "
          + "Digital? : " +
          ((component.isAnalog()) ? " Analogue" : "Absolute"));
    }

    /*
    A = Button 0
    B = Button 1
    X = Button 2
    Y = Button 3
    LB = Button 4
    RB = Button 5
    Back = Button 6
    Start = Button 7
    Left Thumbstick button = Button 8
    Right thumbstick button = Button 9
    DPAD = HAT SWITCH values up down left right
    //FOR LT RESTS AT -1.5 (WHICH IS ACTUALLY 0) and increases to 0.996
    simialr for RT to -0.9
    LT = Z-Axis + X AXIS? ----- Goes from -1.52 to 0.996
    RT = Z-Axis + X Rotation? ---- GOES FROM 0 to -0.996 (increase)
    Left Thumstick = ROTATION(Left = X Rotation 1.0, Up = Y Rotation -1.0)
    Right thumbstick = AXIS
     */

    Event event;
    float value;
    Component current;
    StringBuilder debug;
    String position = "";

    while (true) {
      controller.poll();
      debug = new StringBuilder();
      EventQueue eventQueue = controller.getEventQueue();
      event = new Event();
      while (eventQueue.getNextEvent(event)) {
        current = event.getComponent();
        value = event.getValue();

        if ((value < 0.3) && (value > -0.3) && position.equals(current
            .getIdentifier().getName())) {
          position = "";
        }
        debug.append(current.getName() + " at: " + event.getNanos() + ", "
            + "changed to " + value);

        if (current.isAnalog()) {
          //Back Triggers and Analogue Sticks
          //Current implementation to include LT for speeding up one car


          /*TODO: Check if input is LT and measure amount. Scale according to
           TODO: power level of the car. Send command Immediately via Server
          */

          if ((value > 0.8) && !position.equals(current.getIdentifier()
              .getName())) {
            //Positive direction
            switch (current.getIdentifier().getName()) {
              case "z":
                //LT from 0 to 0.996
                position = "z";
                if (value == 0) {
                  int power = 0;
                  String message = (Integer.toString(power) + "," + Integer
                      .toString(power));
                  server.setPower(message);
                } else if (value <= 0.25) {
                  int power = 10;
                  String message = (Integer.toString(power) + "," + Integer
                      .toString(power));
                  server.setPower(message);
                } else if (value > 0.25 && value <= 0.5) {
                  int power = 20;
                  String message = (Integer.toString(power) + "," + Integer
                      .toString(power));
                  server.setPower(message);
                } else if (value > 0.5 && value <= 0.75) {
                  int power = 30;
                  String message = (Integer.toString(power) + "," + Integer
                      .toString(power));
                  server.setPower(message);
                } else if (value > 0.75 && value <= 1.0) {
                  int power = 40;
                  String message = (Integer.toString(power) + "," + Integer
                      .toString(power));
                  server.setPower(message);
                }
//
//                if (value > 0.5) {
//                  //Increase to full power for testing (guessing power)
//                  //tell server to send a power of 40.
//                  int power = 50;
//                  String message = (Integer.toString(power) + ", " + Integer
//                      .toString(power));
//                  server.setPower(message);
//                }
                break;
              case "x":
                //Left thumbstick - Right
                System.out.println("Left thumbstick Right by: " + value);
                position = "x";
                break;
              case "y":
                //Left thumbstick - Down
                System.out.println("Left thumbstick Down by: " + value);
                position = "y";
                break;
            }

          }

          if (value < -0.8 && !(position.equals(current.getIdentifier()
              .getName()))){
            //negative direction
            switch (current.getIdentifier().getName()){
              case "x":
                //Left thumbstick - Left
                System.out.println("Left thumbstick Left by: " + value);
                position = "x";
                break;
              case "y":
                //Left thumbstick - Up
                position = "y";
                System.out.println("Left thumbstick Up by: " + value);
                break;

            }
          }
        } else {
          if (value == 1.0) {
            switch (current.getIdentifier().getName()) {
              case "0":
                //A
                int power = 40;
                String message = (Integer.toString(power) + "," + Integer
                    .toString(power) + "," + Integer.toString(0));
                server.setPower(message);
                break;
            }
          }
        }
        System.out.println();
        System.out.println(debug.toString());

      }
    }
  }
}