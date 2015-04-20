package org.rapidpm.book.iot.tinkerforge.helloworld;

import com.tinkerforge.*;
import org.rapidpm.module.se.commons.WaitForQ;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.exit;

/**
 * Created by Sven Ruppert on 25.12.2014.
 */
public class HelloWorld {


  private static final String HOST = "localhost";
  private static final int PORT = 4223;
  private static final String UID = "iW3"; // Change to your UID
  private static final byte[] DIGITS = {0x3f,0x06,0x5b,0x4f,
      0x66,0x6d,0x7d,0x07,
      0x7f,0x6f,0x77,0x7c,
      0x39,0x5e,0x79,0x71}; // 0~9,A,b,C,d,E,F



  public static void main(String[] args) {
    IPConnection ipcon = new IPConnection();
    BrickletSegmentDisplay4x7 sevenSegment = new BrickletSegmentDisplay4x7(UID, ipcon);
    WaitForQ waitForQ = new WaitForQ();
    try {
      ipcon.connect(HOST, PORT);

      Timer time = new Timer();
      ScheduledTask st = new ScheduledTask(sevenSegment);
      time.schedule(st, 0, 1000);

      waitForQ.addShutDownAction(()->{
        try {
          ipcon.disconnect();
        } catch (NotConnectedException ignored) { }
      });
      waitForQ.addShutDownAction(()-> exit(0));
      waitForQ.waitForQ();

    } catch (IOException | AlreadyConnectedException e) {
      e.printStackTrace();
    }
  }

  public static class ScheduledTask extends TimerTask {

    private BrickletSegmentDisplay4x7 sevenSegment;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");

    public ScheduledTask(BrickletSegmentDisplay4x7 sevenSegment) {
      this.sevenSegment = sevenSegment;
    }

    public void run() {
      final LocalDateTime now = LocalDateTime.now(); // initialize date
      final String format = formatter.format(now);
      System.out.println("Time is :" + format); // Display current time
      final char[] chars = format.toCharArray();
      short[] segments = {
          DIGITS[chars[0]-48],
          DIGITS[chars[1]-48],
          DIGITS[chars[2]-48],
          DIGITS[chars[3]-48]};
      try {
        sevenSegment.setSegments(segments, (short)7, true);
      } catch (TimeoutException | NotConnectedException e) {
        e.printStackTrace();
      }
    }
  }

}
