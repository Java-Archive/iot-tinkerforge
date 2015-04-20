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

package org.rapidpm.book.iot.tinkerforge.weatherfx.sensor;

import com.tinkerforge.*;

import java.util.Date;

/**
 * Created by Sven Ruppert on 21.02.14.
 */
public abstract class TinkerForgeSensor<T extends Device>  {

  protected String UID;
  protected int callbackPeriod;
  protected IPConnection ipcon;
  public T bricklet;

  public String masterUID;
  public String brickletUID;
  public String brickletType;

  public TinkerForgeSensor(final String UID, int callbackPeriod, IPConnection ipcon) {
    this.UID = UID;
    this.callbackPeriod = callbackPeriod;
    this.ipcon = ipcon;
    createBrickletInstance();
    initBricklet();
    try {
      masterUID = bricklet.getIdentity().connectedUid;
      brickletUID = bricklet.getIdentity().uid;
      brickletType = bricklet.getIdentity().deviceIdentifier + "";
    } catch ( TimeoutException
        | NotConnectedException e) {
      e.printStackTrace();
    }

  }

  public SensorDataElement getNextSensorDataElement() {
    final SensorDataElement data = new SensorDataElement();
    data.setMasterUID(masterUID);
    data.setBrickletUID(brickletUID);
    data.setBrickletType(brickletType);
    data.setDate(new Date());
    return data;
  }

  public abstract void createBrickletInstance();
  public abstract void initBricklet();
}
