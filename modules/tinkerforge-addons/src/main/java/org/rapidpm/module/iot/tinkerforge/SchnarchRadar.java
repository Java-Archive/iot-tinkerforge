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
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Humidity;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Light;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.MotionDectector;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.SoundIntensity;

import java.util.Date;


/**
 * Created by Sven Ruppert on 10.03.14.
 */
public class SchnarchRadar {
  public static final String HOST = "192.168.0.201";  //schnarchradar
  public static final int PORT = 4223;
  private static int callbackPeriod = 1000;

//    private static final SensorDataRepository repo = new SensorDataRepository(ArangoDBRemote.database);

  public static void main(String args[]) throws Exception {

    final Light light = new Light("jxn", callbackPeriod, PORT, HOST);
    light.bricklet.addIlluminanceListener(sensorvalue -> {
      final double lux = sensorvalue / 10.0;
      final String text = "Lux   : " + lux + " Lux";
      System.out.println(text);
    });
    light.run();

    final Humidity humidity = new Humidity("kc8", callbackPeriod, PORT, HOST);
    humidity.bricklet.addHumidityListener(sensorvalue -> {
      final double tempNorm = sensorvalue / 10.0;
      final String text = "RelHum: " + tempNorm + " %RH";
      System.out.println(text);

    });
    humidity.run();

    final SoundIntensity soundIntensity = new SoundIntensity("iQj", callbackPeriod, PORT, HOST);
    soundIntensity.bricklet.addIntensityListener(sensorvalue -> {
      //                final double soundNorm = sensorvalue / 10.0;
      final String text = "Sound: " + sensorvalue + " dB";
      System.out.println(text);

    });
    soundIntensity.run();

    final MotionDectector motionDetector = new MotionDectector("kgn", callbackPeriod, PORT, HOST);
    motionDetector.bricklet.addMotionDetectedListener(() -> {
      System.out.println("Motion detected.. " + new Date());
    });
    motionDetector.run();

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
}
