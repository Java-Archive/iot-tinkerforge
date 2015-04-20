/*
 * Copyright [2014] [www.rapidpm.org / Sven Ruppert (sven.ruppert@rapidpm.org)]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.rapidpm.module.iot.tinkerforge;


import org.rapidpm.module.iot.WaitForQ;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Barometer;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Humidity;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Light;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Temperature;
import org.rapidpm.module.iot.twitter.TwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.time.LocalDateTime;

/**
 * Created by Sven Ruppert on 15.02.14.
 */
public class WeatherStationRemote {

  public static final String HOST = "192.168.0.200";  //wetterstation
  public static final int PORT = 4223;
  private static int callbackPeriod = 5000;

//  private static final LCD20x4 lcd20x4 = new LCD20x4("jvX", "192.168.0.202", PORT);

  private static final TwitterFactory tf = new TwitterFactory();
  private static final Twitter twitter = tf.createTwitter();


  public static void main(String args[]) throws Exception {
    final Temperature temperature = new Temperature("dXj", callbackPeriod, PORT, HOST);
    temperature.bricklet.addTemperatureListener(sensorvalue -> {
      final double tempNorm = sensorvalue / 100.0;
      final String text = LocalDateTime.now() + " - Temp  : " + tempNorm + " °C";
      System.out.println("text = " + text);
      tweetIt(text);
    });

    temperature.addSensorDataAction(sensorValue -> {
      final String text = LocalDateTime.now() + " - Temp  : " + sensorValue + " °C";
      System.out.println("text = " + text);
      tweetIt(text);
    });
//    new Thread(temperature).start();
    temperature.run();


    final Barometer barometer = new Barometer("jY4", callbackPeriod, PORT, HOST);
    barometer.bricklet.addAirPressureListener(sensorvalue -> {
      final String text = LocalDateTime.now() + " - Air   : " + sensorvalue / 1000.0 + " mbar";
//      lcd20x4.printLine1(text);
      System.out.println("text = " + text);
      tweetIt(text);
    });
//    new Thread(barometer).start();
    barometer.run();

    final Light light = new Light("jy2", callbackPeriod, PORT, HOST);
    light.bricklet.addIlluminanceListener(sensorvalue -> {
      final double lux = sensorvalue / 10.0;
      final String text = LocalDateTime.now() + " - Lux   : " + lux + " Lux";
//      lcd20x4.printLine3(text);
      System.out.println("text = " + text);
      tweetIt(text);
    });
//    new Thread(light).start();
    light.run();

    final Humidity humidity = new Humidity("kfd", callbackPeriod, PORT, HOST);
    humidity.bricklet.addHumidityListener(sensorvalue -> {
      final double tempNorm = sensorvalue / 10.0;
      final String text = LocalDateTime.now() + " - RelHum: " + tempNorm + " %RH";
//      lcd20x4.printLine2(text);
      System.out.println("text = " + text);
      tweetIt(text);
    });
    humidity.bricklet.addHumidityListener(v -> tweetIt("" + v));
//    new Thread(humidity).start();
    humidity.run();

    WaitForQ waitForQ = new WaitForQ();

//    waitForQ.addShutDownAction(() -> {
//      try {
//        ipcon.disconnect();
//      } catch (NotConnectedException e) {
//        e.printStackTrace();
//      }
//    });
    waitForQ.addShutDownAction(() -> System.exit(0));

    waitForQ.waitForQ();
  }

  private static void tweetIt(String text) {
    try {
      twitter.updateStatus(text);
    } catch (TwitterException e) {
      e.printStackTrace();
    }
  }
}

