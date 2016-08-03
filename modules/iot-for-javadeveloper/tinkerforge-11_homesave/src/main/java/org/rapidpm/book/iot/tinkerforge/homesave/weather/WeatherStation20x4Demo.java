package org.rapidpm.book.iot.tinkerforge.homesave.weather;

import com.tinkerforge.*;
import org.mapdb.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import static java.lang.System.exit;

/**
 * Created by Sven Ruppert on 02.01.2015.
 */
public class WeatherStation20x4Demo {
  private static final String host = "192.168.0.200";
  private static final int port = 4223;
  private static final int CALLBACK_PERIOD = 2_50;

  private static final IPConnection ipcon = new IPConnection();

  private static final String masterUID = "6Dct25";
  public static final String LIGHT_UID = "jy2";
  public static final String HUMIDITY_UID = "kfd";
  public static final String TEMPERATURE_UID = "dXj";
  public static final String BAROMETER_UID = "jY4";


  public static void main(String[] args) throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

    ipcon.connect(host, port);
    ipcon.setAutoReconnect(true);

    final BrickletAmbientLight light = new BrickletAmbientLight(LIGHT_UID, ipcon);
    final BrickletHumidity humidity = new BrickletHumidity(HUMIDITY_UID, ipcon);
    final BrickletTemperature temperature = new BrickletTemperature(TEMPERATURE_UID, ipcon);
    final BrickletBarometer barometer = new BrickletBarometer(BAROMETER_UID, ipcon);

    final DB weatherstation = DBMaker.newFileDB(new File("./data/values", "weatherstation"))
        .asyncWriteEnable()
        .asyncWriteQueueSize(1_000)
        .checksumEnable()
        .snapshotEnable()
        .closeOnJvmShutdown()
        .compressionEnable()
        .make();

    final Timer timer = new Timer();
    TimerTask timerTaskCommit = new TimerTask() {
      @Override
      public void run() {
        weatherstation.commit();
        System.out.println("commit => " + LocalDateTime.now().toString());
      }
    };
    timer.schedule(timerTaskCommit, 0, 60_000);





    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        //consumiere die Queue
        //wenn erfolgreich -> commit
        //ansonsten nochmal
        //Ziel der DataStore
      }
    }, 0, 60_000);

//    final Atomic.Long lightID = weatherstation.getAtomicLong("lightID");
//    final Atomic.Long humidityID = weatherstation.getAtomicLong("humidityID");
//    final Atomic.Long temperatureID = weatherstation.getAtomicLong("temperatureID");
//    final Atomic.Long barometerAirPressureID = weatherstation.getAtomicLong("barometerAirPressureID");
//    final Atomic.Long barometerAltitudeID = weatherstation.getAtomicLong("barometerAltitudeID");


    final BlockingQueue<SensorData> lightQueue = weatherstation.getQueue("lightQueue");
    final BlockingQueue<SensorData> humidityQueue = weatherstation.getQueue("humidityQueue");
    final BlockingQueue<SensorData> temperatureQueue = weatherstation.getQueue("temperatureQueue");
    final BlockingQueue<SensorData> barometerAirPressureQueue = weatherstation.getQueue("barometerAirPressureQueue");
    final BlockingQueue<SensorData> barometerAltitudeQueue = weatherstation.getQueue("barometerAltitudeQueue");


    light.addIlluminanceListener(illuminance -> lightQueue.add(new SensorData(masterUID, LIGHT_UID, LocalDateTime.now().toString(), illuminance)));
    humidity.addHumidityListener(humidity1 -> humidityQueue.add(new SensorData(masterUID, HUMIDITY_UID, LocalDateTime.now().toString(), humidity1)));
    temperature.addTemperatureListener(temperature1 -> temperatureQueue.add(new SensorData(masterUID, TEMPERATURE_UID, LocalDateTime.now().toString(), temperature1)));
    barometer.addAirPressureListener(airPressure -> barometerAirPressureQueue.add(new SensorData(masterUID, BAROMETER_UID, LocalDateTime.now().toString(), airPressure)));
    barometer.addAltitudeListener(altitude -> barometerAltitudeQueue.add(new SensorData(masterUID, BAROMETER_UID, LocalDateTime.now().toString(), altitude)));


    light.setIlluminanceCallbackPeriod(CALLBACK_PERIOD);
    humidity.setHumidityCallbackPeriod(CALLBACK_PERIOD);
    temperature.setTemperatureCallbackPeriod(CALLBACK_PERIOD);
    barometer.setAirPressureCallbackPeriod(CALLBACK_PERIOD);
    barometer.setAltitudeCallbackPeriod(CALLBACK_PERIOD);

    WaitForQ waitForQ = createWaitForQ(weatherstation, timerTaskCommit);

    waitForQ.waitForQ();
  }

  private static WaitForQ createWaitForQ(DB weatherstation,  TimerTask timerTaskStatistics) {
    WaitForQ waitForQ = new WaitForQ();

//    waitForQ.addShutDownAction(timerTaskCommit::cancel);
    waitForQ.addShutDownAction(timerTaskStatistics::cancel);

    waitForQ.addShutDownAction(() -> {
      try {
        ipcon.disconnect();
      } catch (NotConnectedException ignored) {
      }
    });

    waitForQ.addShutDownAction(() -> {
      weatherstation.commit();
      weatherstation.compact();
      weatherstation.close();
    });

    waitForQ.addShutDownAction(() -> exit(0));
    return waitForQ;
  }


}
