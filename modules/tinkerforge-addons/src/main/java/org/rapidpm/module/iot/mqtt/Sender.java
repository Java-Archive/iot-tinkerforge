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

package org.rapidpm.module.iot.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.rapidpm.module.iot.WaitForQ;
import org.rapidpm.module.iot.tinkerforge.sensor.singlevalue.Temperature;

import java.time.LocalDateTime;

/**
 * Created by Sven Ruppert on 01.05.2014.
 */
public class Sender {

  public static final String TOPIC = "TinkerForge/Wetterstation/";
  public static final String HOST = "192.168.0.200";  //wetterstation
  public static final String BROKER = "192.168.0.106";  //wetterstation
  public static final int PORT = 4223;
  private static int callbackPeriod = 1000;

  private static MqttClientBuilder builder = new MqttClientBuilder();


  public static void main(String[] args) {

    MqttClient sender = builder
        .uri("tcp://" + BROKER+":1883")
        .clientUIDGenerated()
        .build();
    try {
      sender.connect();

      MqttBuffer buffer = new MqttBuffer()
          .client(sender).topic(TOPIC).qos(1).retained(true);



      final Temperature temperature = new Temperature("dXj", callbackPeriod, PORT, HOST);
      temperature.bricklet.addTemperatureListener(sensorvalue -> {
        final double tempNorm = sensorvalue / 100.0;
        final String text = LocalDateTime.now() + ":" + tempNorm;
        System.out.println("text = " + text);
        buffer.sendAsync(text);
      });

//    new Thread(temperature).start();
      temperature.run();
    } catch (MqttException e) {
      e.printStackTrace();
    }

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
