package org.rapidpm.book.iot.tinkerforge.weatherfx.gui;

import com.tinkerforge.*;
import eu.hansolo.enzo.clock.Clock;
import eu.hansolo.enzo.gauge.RadialBargraph;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.rapidpm.book.iot.tinkerforge.weatherfx.BrickletReader;
import org.rapidpm.book.iot.tinkerforge.weatherfx.ConnectionData;
import org.rapidpm.book.iot.tinkerforge.weatherfx.DeviceIdentity;
import org.rapidpm.book.iot.tinkerforge.weatherfx.sensor.Barometer;
import org.rapidpm.book.iot.tinkerforge.weatherfx.sensor.Humidity;
import org.rapidpm.book.iot.tinkerforge.weatherfx.sensor.Light;
import org.rapidpm.book.iot.tinkerforge.weatherfx.sensor.Temperature;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sven Ruppert on 15.04.14.
 */
public class MainController {

  public RadialBargraph temperatureRB;
  public RadialBargraph ambientLightRB;
  public RadialBargraph barometerRB;
  public RadialBargraph humidityRB;
  public Clock clock;

  @FXML TextField tHost;
  @FXML Button bConnect;

  private ConnectionData connectionData;

  private final IPConnection ipConnection = new IPConnection();

  @FXML
  public void onAction(ActionEvent actionEvent) {
    final short connectionState = ipConnection.getConnectionState();
    switch (connectionState) {
      case IPConnection.CONNECTION_STATE_CONNECTED:  ipConnectionDeActivate(); break;
      case IPConnection.CONNECTION_STATE_DISCONNECTED: ipConnectionActivate(); break;
      case IPConnection.CONNECTION_STATE_PENDING: break;
    }
  }


  private void ipConnectionActivate(){
    String host = tHost.getText();
    connectionData = new ConnectionData(host);
    connect();
    bConnect.setText("disconnect");
  }
  private void ipConnectionDeActivate(){
    bConnect.setText("connect");
    try {
      ipConnection.disconnect();
      barometer = null;
      temperature = null;
      humidity = null;
      light = null;
      Thread.sleep(500);
      temperatureRB.setValue(0);
      humidityRB.setValue(0);
      barometerRB.setValue(0);
      ambientLightRB.setValue(0);
    } catch (NotConnectedException
        | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private Barometer barometer;
  private Temperature temperature;
  private Humidity humidity;
  private Light light;

  private void connect() {
    final BrickletReader br = new BrickletReader();
    final List<DeviceIdentity> bricklet = br.findBricklet(connectionData.getHost());

    try {
      ipConnection.connect(connectionData.getHost(), ConnectionData.DEFAULT_PORT);

      for (DeviceIdentity deviceIdentity : bricklet) {
        switch (deviceIdentity.getDeviceIdentifier()) {
          case BrickletTemperature.DEVICE_IDENTIFIER:
            temperature = new Temperature(deviceIdentity.getUid(), ConnectionData.CALLBACK_RATE, ipConnection);
            temperature.createBrickletInstance();
            temperature.initBricklet();

            temperature.bricklet.addTemperatureListener(tempVal -> {
              System.out.println("tempVal = " + tempVal);
              if (!temperatureRB.isInteractive()) {
                Platform.runLater(() -> temperatureRB.setValue(tempVal / 100));
              }
            });
            break;
          case BrickletBarometer.DEVICE_IDENTIFIER:
            barometer = new Barometer(deviceIdentity.getUid(),
                ConnectionData.CALLBACK_RATE, ipConnection);
            barometer.createBrickletInstance();
            barometer.initBricklet();
            barometer.bricklet.addAirPressureListener(airpressure -> {
              System.out.println("airpressure = " + airpressure);
              if (!barometerRB.isInteractive()) {
                Platform.runLater(() -> barometerRB.setValue(airpressure / 1000.0));
              }
            });
            break;
          case BrickletHumidity.DEVICE_IDENTIFIER:
            humidity = new Humidity(deviceIdentity.getUid(),
                ConnectionData.CALLBACK_RATE, ipConnection);
            humidity.createBrickletInstance();
            humidity.initBricklet();
            humidity.bricklet.addHumidityListener(humidityVal -> {
              System.out.println("humidityVal = " + humidityVal);
              if (!humidityRB.isInteractive()) {
                Platform.runLater(() -> humidityRB.setValue(humidityVal / 10));
              }
            });
            break;

          case BrickletAmbientLight.DEVICE_IDENTIFIER:
            light = new Light(deviceIdentity.getUid(), ConnectionData.CALLBACK_RATE, ipConnection);
            light.createBrickletInstance();
            light.initBricklet();
            light.bricklet.addIlluminanceListener(ambi -> {
              System.out.println("ambi = " + ambi);
              if (!ambientLightRB.isInteractive()) {
                Platform.runLater(() -> ambientLightRB.setValue(ambi / 10));
              }
            });
            break;
        }
      }


    } catch (IOException | AlreadyConnectedException e) {
      e.printStackTrace();
    }
  }

}
