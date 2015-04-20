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

package org.rapidpm.module.iot.tinkerforge.sensor.singlevalue;

import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Sven Ruppert on 15.02.14.
 */
public class Humidity extends TinkerForgeSensorSingleValue<BrickletHumidity> {

  public void connectBricklet() {
    bricklet = new BrickletHumidity(UID, ipcon);
  }

  public Humidity(String UID, int callbackPeriod, int port, String host) {
    super(UID, callbackPeriod, port, host);
  }

  @Override
  protected double convertRawValue(int sensorRawValue) {
    return sensorRawValue / 10.0;
  }

  public void initBricklet() {
    try {
      bricklet.setHumidityCallbackPeriod(callbackPeriod);
      bricklet.addHumidityListener(this::execute);
    } catch (TimeoutException | NotConnectedException e) {
      e.printStackTrace();
    }
  }


}
