import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class LineGraph extends Application {

  private XYChart.Series series;
  private AtomicInteger tick = new AtomicInteger(0);
  private static LineGraph lineGraph;

  public static LineGraph returnThis(){
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

    Scene scene  = new Scene(lineChart,800,600);
    lineChart.getData().add(series);
    primaryStage.setScene(scene);
    primaryStage.show();



    XboxInput xboxInput = new XboxInput();
    Thread updateThread = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(1000);
          Platform.runLater(() -> series.getData().add(new XYChart.Data<>
              (tick.incrementAndGet(), xboxInput.getTotal()) ));
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    updateThread.setDaemon(true);
    updateThread.start();
  }

  public void plot(double time){
    series.getData().add(new XYChart.Data(5, 20));
  }

  public static void main(String[] args) throws Exception {
    launch(args);
  }


}
