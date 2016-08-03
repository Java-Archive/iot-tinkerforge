package org.rapidpm.book.iot.tinkerforge.temperature;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * Created by Sven Ruppert on 29.12.2014.
 */
public class TemperaturBasicDemo {

  private static final String host = "localhost";
  private static final int port = 4223;
  private static final String UID = "dXj";
  public static void main(String args[]) throws Exception {
    WaitForQ waitForQ = new WaitForQ();
    IPConnection ipcon = new IPConnection();
    BrickletTemperature temp = new BrickletTemperature(UID, ipcon);
    ipcon.connect(host, port);
    temp.setTemperatureCallbackPeriod(1000);
    temp.addTemperatureListener(temperature -> out.println("Temperature: (01)" + temperature/100.0 + " °C"));
    temp.addTemperatureListener(temperature -> out.println("Temperature: (02)" + temperature/100.0 + " °C"));
    waitForQ.addShutDownAction(() -> {
      try {
        ipcon.disconnect();
      } catch (NotConnectedException ignored) {
      }
    });
    waitForQ.addShutDownAction(()-> exit(0));
    waitForQ.waitForQ();
  }
}
