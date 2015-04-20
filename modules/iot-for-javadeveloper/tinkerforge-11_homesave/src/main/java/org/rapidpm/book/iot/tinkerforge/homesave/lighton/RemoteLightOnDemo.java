package org.rapidpm.book.iot.tinkerforge.homesave.lighton;

import com.tinkerforge.*;
import org.rapidpm.module.se.commons.WaitForQ;

import java.io.IOException;
import java.time.Instant;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * Created by Sven Ruppert on 05.01.2015.
 */
public class RemoteLightOnDemo {

  private static final String localhost = "127.0.0.1";
  private static final String remote = "192.168.0.202";  //Bewegungsmelder

  private static final int port = 4223;
  private static final int CALLBACK_PERIOD = 2_50;

  private static final IPConnection ipconLocalHost = new IPConnection();
  private static final IPConnection ipconRemote = new IPConnection();

  private static final String masterUID = "6Dct25";
  public static final String LIGHT_UID = "jy2";
  public static final String HUMIDITY_UID = "kfd";
  public static final String TEMPERATURE_UID = "dXj";
  public static final String BAROMETER_UID = "jY4";

  public static void main(String[] args) throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

    ipconLocalHost.connect(localhost, port);
    ipconLocalHost.setAutoReconnect(true);

    ipconRemote.connect(remote, port);
    ipconRemote.setAutoReconnect(true);


    final BrickletRemoteSwitch remoteSwitch = new BrickletRemoteSwitch("jNJ", ipconLocalHost);
     remoteSwitch.setRepeats((short)5);
    final BrickletMotionDetector motionDectector = new BrickletMotionDetector("kgn", ipconRemote);
    motionDectector.addMotionDetectedListener(() -> {
//      String pFileName = "pic_" + Instant.now();
//      try {
//        Runtime run = Runtime.getRuntime();
//        Process pr = run.exec("raspistill -o " + pFileName + ".jpg");
//        pr.waitFor();
//        out.println("ok pic done... " + pFileName);
//      } catch (IOException
//          | InterruptedException ex) {
//        out.println("ex = " + ex);
//      }
      //schalte Licht an
      try {
        remoteSwitch.switchSocketA((short)17, (short)18, BrickletRemoteSwitch.SWITCH_TO_ON);
        System.out.println("motionDectector = ON");
      } catch (TimeoutException | NotConnectedException ignored) { }
    });
    motionDectector.addDetectionCycleEndedListener(() -> {
      try {
        remoteSwitch.switchSocketA((short)17, (short)18, BrickletRemoteSwitch.SWITCH_TO_OFF);
        System.out.println("motionDectector = OFF");
      } catch (TimeoutException | NotConnectedException ignored) { }

    });

    WaitForQ waitForQ = new WaitForQ();
    waitForQ.addShutDownAction(() -> {
          try {
            ipconLocalHost.disconnect();
          } catch (NotConnectedException ignored) { }
        }
    );
    waitForQ.addShutDownAction(() -> {
          try {
            ipconRemote.disconnect();
          } catch (NotConnectedException ignored) { }
        }
    );
    waitForQ.addShutDownAction(() -> exit(0));
    waitForQ.waitForQ();
  }
}
