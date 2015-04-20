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

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import org.rapidpm.module.iot.WaitForQ;

/**
 * Created by Sven Ruppert on 11.03.14.
 */
public class TestProg {
    private static final String host = "192.168.0.200";
    private static final int port = 4223;

    // Note: To make the example code cleaner we do not handle exceptions. Exceptions you
    //       might normally want to catch are described in the documentation
    public static void main(String args[]) throws Exception {
        // Create connection and connect to brickd
        IPConnection ipcon = new IPConnection();
        ipcon.connect(host, port);

        // Register enumerate listener and print incoming information
        ipcon.addEnumerateListener((uid, connectedUid, position, hardwareVersion, firmwareVersion, deviceIdentifier, enumerationType) -> {
            System.out.println("UID:               " + uid);
            System.out.println("Enumeration Type:  " + enumerationType);

            if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
                System.out.println("");
                return;
            }

            System.out.println("Connected UID:     " + connectedUid);
            System.out.println("Position:          " + position);
            System.out.println("Hardware Version:  " + hardwareVersion[0] + "." +
                    hardwareVersion[1] + "." +
                    hardwareVersion[2]);
            System.out.println("Firmware Version:  " + firmwareVersion[0] + "." +
                    firmwareVersion[1] + "." +
                    firmwareVersion[2]);
            System.out.println("Device Identifier: " + deviceIdentifier);
            System.out.println("");
        });

        ipcon.enumerate();
        WaitForQ waitForQ = new WaitForQ();

        waitForQ.addShutDownAction(() -> {
            try {
                ipcon.disconnect();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
        });
        waitForQ.addShutDownAction(() -> System.exit(0));

        waitForQ.waitForQ();

    }
}
