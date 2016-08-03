package org.rapidpm.book.iot.tinkerforge.ptc;

import com.tinkerforge.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Sven Ruppert on 30.12.2014.
 */
public class PTCDemo extends Application {
  private static final String host = "localhost";
  private static final int port = 4223;
  private static final int CALLBACK_PERIOD = 1_000;

  private static final IPConnection ipcon = new IPConnection();

  private static final BrickletSegmentDisplay4x7 sevenSegment = new BrickletSegmentDisplay4x7("iW3", ipcon);
  private static final BrickletPiezoSpeaker piezoSpeaker = new BrickletPiezoSpeaker("iN2", ipcon);
  private static final BrickletPTC ptc = new BrickletPTC("i2J", ipcon);

  private static final byte[] DIGITS = {0x3f, 0x06, 0x5b, 0x4f,
      0x66, 0x6d, 0x7d, 0x07,
      0x7f, 0x6f, 0x77, 0x7c,
      0x39, 0x5e, 0x79, 0x71}; // 0~9,A,b,C,d,E,F

  private static final DecimalFormat myFormatter = new DecimalFormat("0000");

  private static boolean warned = false;
  private static double highestTemp = Double.MIN_VALUE;

  public static void main(String[] args) {
    try {
      ipcon.connect(host, port);

      ptc.setTemperatureCallbackPeriod(CALLBACK_PERIOD);
      ptc.addTemperatureListener(temperature -> {
        final double celcius = temperature / 100.0;
        System.out.println("celcius = " + celcius);

        try {
          if (celcius >= -10) writeTo7Segment(celcius);
          peepIfColdEnough(celcius);
        } catch (TimeoutException
            | NotConnectedException e) {
          e.printStackTrace();
        }

        writeToChart(celcius);
      });
      launch(args);
    } catch (IOException
        | AlreadyConnectedException
        | TimeoutException
        | NotConnectedException e) {
      e.printStackTrace();
    } finally {
      try {
        ipcon.disconnect();
      } catch (NotConnectedException ignored) {
      }
    }
  }

  private static void writeTo7Segment(double celcius) throws TimeoutException, NotConnectedException {
    final char[] chars = myFormatter.format(celcius).toCharArray();
    System.out.println(chars);
    short[] segments = {
        DIGITS[chars[0] - 48],
        DIGITS[chars[1] - 48],
        DIGITS[chars[2] - 48],
        DIGITS[chars[3] - 48]};
    sevenSegment.setSegments(segments, (short) 7, true);
  }

  private static void peepIfColdEnough(double celcius) throws TimeoutException, NotConnectedException {
    if (celcius <= 45.0 && !warned && highestTemp > celcius) {
      piezoSpeaker.morseCode("-.. .-. .. -. -.- / -- .", 5_000);
      warned = true;
    } else if(!warned && highestTemp < celcius){
      highestTemp = celcius;
      System.out.println("highestTemp set to = " + highestTemp);
    }
  }

  private static void writeToChart(double celcius) {
    Platform.runLater(() -> {
      final XYChart.Data data = new XYChart.Data(new Date(), celcius);
      series.getData().add(data);
    });
  }

  public static XYChart.Series series;

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("PTC TinkerForge Sample");
    final DateAxis dateAxis = new DateAxis();
    final NumberAxis yAxis = new NumberAxis();
    dateAxis.setLabel("Time of Temp");
    final LineChart lineChart = new LineChart<>(dateAxis, yAxis);
    lineChart.setTitle("Temp Monitoring");
    series = new XYChart.Series();
    series.setName("My temps");
    lineChart.getData().add(series);
    Scene scene = new Scene(lineChart, 800, 400);
    stage.setScene(scene);
    stage.setFullScreen(false);
    stage.show();
  }
}
