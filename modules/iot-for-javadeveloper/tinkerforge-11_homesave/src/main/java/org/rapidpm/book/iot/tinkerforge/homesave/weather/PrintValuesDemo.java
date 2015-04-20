package org.rapidpm.book.iot.tinkerforge.homesave.weather;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sven Ruppert on 02.01.2015.
 */
public class PrintValuesDemo {
  public static void main(String[] args) {
    final DB weatherstation = DBMaker.newFileDB(new File("./data/values", "weatherstation"))
        .asyncWriteEnable()
        .asyncWriteQueueSize(1_000)
        .checksumEnable()
        .snapshotEnable()
        .closeOnJvmShutdown()
        .compressionEnable()
        .make();

    final BlockingQueue<SensorData> lightQueue = weatherstation.getQueue("lightQueue");
    final BlockingQueue<SensorData> humidityQueue = weatherstation.getQueue("humidityQueue");
    final BlockingQueue<SensorData> temperatureQueue = weatherstation.getQueue("temperatureQueue");
    final BlockingQueue<SensorData> barometerAirPressureQueue = weatherstation.getQueue("barometerAirPressureQueue");
    final BlockingQueue<SensorData> barometerAltitudeQueue = weatherstation.getQueue("barometerAltitudeQueue");


    SensorData sensorData = humidityQueue.poll();
    while (sensorData != null) {
      System.out.println("next = " + sensorData);
      sensorData = humidityQueue.poll();
    }
//    weatherstation.commit();



    weatherstation.close();


  }
}
