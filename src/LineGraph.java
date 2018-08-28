import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;


public class LineGraph extends Application {

  private XYChart.Series series, series2, series3;
  private AtomicInteger tick = new AtomicInteger(0);
  private static LineGraph lineGraph;

  public static LineGraph returnThis() {
    return lineGraph;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Latency Graph");
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Time");
    yAxis.setLabel("Latency / seconds)");
    final LineChart<Number, Number> lineChart =
        new LineChart<Number, Number>(xAxis, yAxis);
    lineChart.setTitle("Latency over Time");
    series = new XYChart.Series();
    series.setName("Total");
    series2 = new XYChart.Series();
    series2.setName("Controller to Server");
    series3 = new XYChart.Series();
    series3.setName("Car to Server");

    Scene scene = new Scene(lineChart, 800, 600);
    lineChart.getData().add(series);
    lineChart.getData().add(series2);
    lineChart.getData().add(series3);
    primaryStage.setScene(scene);
    primaryStage.show();

    XboxInput xboxInput = new XboxInput();
    Thread updateThread = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(1000);
          //Discarding the first 5 plots because they're not normally
          // representative
          if (!(xboxInput.isFirstFive())) {
            int time = tick.incrementAndGet();
            Platform.runLater(() -> series.getData().add(new XYChart.Data<>
                (time, xboxInput.getTotal())));
            Platform.runLater(() -> series2.getData().add(new XYChart.Data<>
                (time, xboxInput.getControllerToServer())));
            Platform.runLater(() -> series3.getData().add(new XYChart.Data<>
                (time, xboxInput.getCarToServer())));
        }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    updateThread.setDaemon(true);
    updateThread.start();
  }

  public static void main(String[] args) throws Exception {
    
    launch(args);
  }


}
