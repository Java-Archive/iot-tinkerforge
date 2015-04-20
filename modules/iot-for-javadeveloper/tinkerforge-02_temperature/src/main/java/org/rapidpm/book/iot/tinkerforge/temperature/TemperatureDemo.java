package org.rapidpm.book.iot.tinkerforge.temperature;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.rapidpm.commons.javafx.chart.DateAxis;

import java.util.Date;

/**
 * Created by Sven Ruppert on 29.12.2014.
 */
public class TemperatureDemo extends Application{


  private static final String host = "localhost";
  private static final int port = 4223;
  private static final String UID = "dXj";

  public static void main(String args[]) throws Exception {
    IPConnection ipcon = new IPConnection();
    BrickletTemperature temp = new BrickletTemperature(UID, ipcon);
    ipcon.connect(host, port);
    temp.setTemperatureCallbackPeriod(1000);
    temp.addTemperatureListener(
        temperature -> {
          final double value = temperature / 100.0;
          System.out.println("Temperature: " + value + " °C");
          Platform.runLater(() -> {
            final XYChart.Data data = new XYChart.Data( new Date(), value);
            series.getData().add(data);
          });
        });

    launch(args);

    ipcon.disconnect();
  }

  public static XYChart.Series series;

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("Line Chart TinkerForge Sample");
    final DateAxis dateAxis = new DateAxis();
    final NumberAxis yAxis = new NumberAxis();
    dateAxis.setLabel("Time of Temp");
    final LineChart lineChart = new LineChart<>(dateAxis, yAxis);

    lineChart.setTitle("Temp Monitoring");

    series = new XYChart.Series();
    series.setName("My temps");

    lineChart.getData().add(series);
    Scene scene = new Scene(lineChart, 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}

