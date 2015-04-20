package org.rapidpm.book.iot.tinkerforge.weatherlcd;

import com.tinkerforge.*;
import eu.hansolo.enzo.clock.Clock;
import eu.hansolo.enzo.clock.ClockBuilder;
import eu.hansolo.enzo.common.Section;
import eu.hansolo.enzo.gauge.SimpleGauge;
import eu.hansolo.enzo.gauge.SimpleGaugeBuilder;
import eu.hansolo.enzo.sevensegment.SevenSegment;
import eu.hansolo.enzo.sevensegment.SevenSegment.SegmentStyle;
import eu.hansolo.enzo.sevensegment.SevenSegmentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

import static javafx.application.Platform.runLater;

/**
 * Created by Sven Ruppert on 16.08.2014.
 */
public class Main extends Application {

  private static final IPConnection ipcon = new IPConnection();


  public static final String UID_TEMPERATURE = "dXj";
  public static final int TEMPERATURE_CALLBACK_PERIOD = 1_000;
  public static final String UID_BAROMETER = "jY4";
  public static final int AIR_PRESSURE_CALLBACK_PERIOD = 1_000;
  public static final String UID_AMBIENT_LIGHT = "jy2";
  public static final int ILLUMINANCE_CALLBACK_PERIOD = 1_000;
  public static final String UID_HUMIDITY = "kfd";
  public static final int HUMIDITY_CALLBACK_PERIOD = 1_000;


  public static void main(String[] args) throws AlreadyConnectedException, IOException {
    ipcon.setAutoReconnect(true);
    ipcon.connect("127.0.0.1",4223);
    launch(args);
  }

  private BrickletTemperature temp;
  private BrickletBarometer barometer;
  private BrickletAmbientLight ambientLight;
  private BrickletHumidity humidity;

  private SevenSegment[] sevenSegmentsLine01; //AirPressure
  private SevenSegment[] sevenSegmentsLine02; //Illuminance
  private SevenSegment[] sevenSegmentsLine03; //Humidity

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("TF RaspiWeather");
//    primaryStage.setFullScreen(true);

    final AnchorPane root = new AnchorPane();
    root.setStyle("-fx-background-color: black");

    final Clock clock = ClockBuilder.create()
        .prefSize(106, 120)
        .design(Clock.Design.DB)
        .text("TinkerTime")
        .autoNightMode(true)
        .build();
     clock.setRunning(true);

    final SimpleGauge thermoMeter = SimpleGaugeBuilder.create()
        .prefSize(106, 120)
        .sections(
            new Section(-20, -10, "0"),
            new Section(-10, 0, "1"),
            new Section(0, 10, "2"),
            new Section(10, 20, "3"),
            new Section(20, 30, "4"),
            new Section(30, 40, "6")
        )
        .sectionFill0(Color.DARKBLUE)
        .sectionFill1(Color.BLUE)
        .sectionFill2(Color.LIGHTBLUE)
        .sectionFill3(Color.GREEN)
        .sectionFill4(Color.ORANGE)
        .sectionFill5(Color.RED)
        .minValue(-20)
        .maxValue(40)
        .decimals(2)
        .title("Temperature")
        .unit("C")
        .value(-20)
        .build();

    temp = new BrickletTemperature(UID_TEMPERATURE, ipcon);
    temp.addTemperatureListener(i -> runLater(() -> thermoMeter.setValue((i / 100.0))));
    temp.setTemperatureCallbackPeriod(TEMPERATURE_CALLBACK_PERIOD);


    final HBox hBox = new HBox();
    final VBox left = new VBox();
    final VBox right = new VBox();

    hBox.getChildren().addAll(left, right);

    right.getChildren().addAll(clock, thermoMeter);

    final VBox row01 = new VBox();
    final VBox row02 = new VBox();
    final VBox row03 = new VBox();

    left.getChildren().addAll(row01, row02, row03);

    sevenSegmentsLine01 = getSevenSegments(3);
    sevenSegmentsLine02 = getSevenSegments(1);
    sevenSegmentsLine03 = getSevenSegments(1);

    row01.getChildren().addAll(createLabel("Air Pressure [mbar]"), createLine(sevenSegmentsLine01));
    row02.getChildren().addAll(createLabel("Illuminance [lx]"), createLine(sevenSegmentsLine02));
    row03.getChildren().addAll(createLabel("relative Humidity [%RH]"),createLine(sevenSegmentsLine03));

    //fuehrende 0 bei weniger 7 Stellen
    barometer = new BrickletBarometer(UID_BAROMETER, ipcon);
    barometer.addAirPressureListener(i -> runLater(() -> setTheSegments(sevenSegmentsLine01, i)));
    barometer.setAirPressureCallbackPeriod(AIR_PRESSURE_CALLBACK_PERIOD);

    ambientLight= new BrickletAmbientLight(UID_AMBIENT_LIGHT, ipcon);
    ambientLight.addIlluminanceListener(i -> runLater(() -> setTheSegments(sevenSegmentsLine02, i)));
    ambientLight.setIlluminanceCallbackPeriod(ILLUMINANCE_CALLBACK_PERIOD);

    humidity = new BrickletHumidity(UID_HUMIDITY,ipcon);
    humidity.addHumidityListener(i -> runLater(() -> setTheSegments(sevenSegmentsLine03, i)));
    humidity.setHumidityCallbackPeriod(HUMIDITY_CALLBACK_PERIOD);

    root.getChildren().add(hBox);
    primaryStage.setScene(new Scene(root, 320, 240));
    primaryStage.show();
  }

  private void setTheSegments(SevenSegment[] sevenSegmentsLine, int i) {
    int[] charArray = getCharArrayFromRawValue(i);
      System.out.println("charArray = " + charArray);
    int counter = 0;
    boolean pre = true;
    boolean red = true;
    for (final int aChar : charArray) {
      final SevenSegment sevenSegment = sevenSegmentsLine[counter];
      if(aChar == 0 && pre){
        sevenSegment.setSegmentStyle(SegmentStyle.BLACK);
      } else if(aChar != 0 && pre){
        pre = false;
        sevenSegment.setSegmentStyle(SegmentStyle.RED);
      } else if(sevenSegment.isDotOn()){
        sevenSegment.setSegmentStyle(SegmentStyle.RED);
        red = false;
      } else if (! red ){
        sevenSegment.setSegmentStyle(SegmentStyle.BLUE);
      }
        sevenSegment.setCharacter(aChar);
      counter++;
    }
  }

  private SevenSegment[] getSevenSegments(int nachkommastellen) {

    final SevenSegment[] sevenSegments = new SevenSegment[7];
    sevenSegments[0] = createSevenSegmentElement(0);
    sevenSegments[1] = createSevenSegmentElement(0);
    sevenSegments[2] = createSevenSegmentElement(0);
    sevenSegments[3] = createSevenSegmentElement(0);
    sevenSegments[4] = createSevenSegmentElement(0);
    sevenSegments[5] = createSevenSegmentElement(0);
    sevenSegments[6] = createSevenSegmentElement(0);

    for (int i = sevenSegments.length - 1; i >= 0; i--) {
      SevenSegment sevenSegment = sevenSegments[i];
      if(nachkommastellen == 0){
        sevenSegment.setDotOn(true);
      }
      if(nachkommastellen > 0 ){
        sevenSegment.setSegmentStyle(SegmentStyle.BLUE);
      }
      nachkommastellen--;
    }
    return sevenSegments;
  }


  private int[] getCharArrayFromRawValue(int rawValueInt) {
    int[] ia = new int[7];
    Arrays.fill(ia, 0);
    if (rawValueInt < 10) {
      ia[6] = rawValueInt;
    } else {
      //Zahl groesser 10...
//      int number = 0;
//      while (rawValueInt / Math.pow( 10, number ) > 1) { number++; }
      int number = 7;
      for ( int j = number - 1; j >= 0; j-- ) {
        final int x = (int) (rawValueInt / Math.pow(10, j) % 10);
        ia[ia.length - j -1] = x;
      }
    }
    return ia;
  }

  private SevenSegment createSevenSegmentElement(final int value){
    return SevenSegmentBuilder.create().prefSize(26, 50)
        .character(value)
        .segmentStyle(SegmentStyle.RED)
        .dotOn(false)
        .build();
  }

  private final Insets padding = new Insets(5, 5, 5, 5);

  private  HBox createLine(SevenSegment[] sevenSegmentsLine){
    final HBox hBox = new HBox();
    hBox.setPadding(padding);
    hBox.getChildren().addAll(sevenSegmentsLine);
    return hBox;
  }

  private Label createLabel(final String text){
    final Label label = new Label(text);
    label.setTextFill(Color.STEELBLUE);
    label.setStyle("-fx-font-size: 15px");
    return label;
  }
}
