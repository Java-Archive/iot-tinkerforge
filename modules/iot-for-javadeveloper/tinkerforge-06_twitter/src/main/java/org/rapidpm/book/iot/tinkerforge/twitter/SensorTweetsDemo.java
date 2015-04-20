package org.rapidpm.book.iot.tinkerforge.twitter;

import com.tinkerforge.*;
import org.rapidpm.module.se.commons.WaitForQ;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.exit;

/**
 * Created by sven on 10.01.15.
 */
public class SensorTweetsDemo {

    private static final String hostRemote = "192.168.0.200";
    private static final String hostLocal = "127.0.0.1";

    private static final int port = 4223;
    private static final int CALLBACK_PERIOD = 1_000;

    private static final IPConnection ipconLocal = new IPConnection();
    private static final IPConnection ipconRemote = new IPConnection();

    public static final String LIGHT_UID = "jy2";
    public static final String HUMIDITY_UID = "kfd";
    public static final String TEMPERATURE_UID = "dXj";
    public static final String BAROMETER_UID = "jY4";

    public static final String SCREEN_NAME = "@SvenRuppert";

    private static final ValueMessage lightMessage = new ValueMessage();
    private static final ValueMessage humidityMessage = new ValueMessage();
    private static final ValueMessage temperatureMessage = new ValueMessage();
    private static final ValueMessage barometerAirPressureMessage = new ValueMessage();

    public static void main(String[] args) throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException, TwitterException {

        ipconRemote.connect(hostRemote, port);
        ipconRemote.setAutoReconnect(true);

        final BrickletAmbientLight light = new BrickletAmbientLight(LIGHT_UID, ipconRemote);
        final BrickletHumidity humidity = new BrickletHumidity(HUMIDITY_UID, ipconRemote);
        final BrickletTemperature temperature = new BrickletTemperature(TEMPERATURE_UID, ipconRemote);
        final BrickletBarometer barometer = new BrickletBarometer(BAROMETER_UID, ipconRemote);

        light.setIlluminanceCallbackPeriod(CALLBACK_PERIOD);
        humidity.setHumidityCallbackPeriod(CALLBACK_PERIOD);
        temperature.setTemperatureCallbackPeriod(CALLBACK_PERIOD);
        barometer.setAirPressureCallbackPeriod(CALLBACK_PERIOD);
        barometer.setAltitudeCallbackPeriod(CALLBACK_PERIOD);

        ipconLocal.connect(hostLocal, port);
        ipconLocal.setAutoReconnect(true);

        //Twitter
        TwitterFactory twitterFactory = new TwitterFactory();
        final Twitter twitter = twitterFactory.createTwitter();

        twitter.updateStatus("bin wieder erreichbar ...." + LocalDateTime.now());

        DirectMessage directMessage = twitter.sendDirectMessage(SCREEN_NAME, "bin wieder erreichbar ...." + LocalDateTime.now());
        System.out.println("directMessage = " + directMessage);


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //gibt es ein command ?
                try {
                    ResponseList<DirectMessage> directMessages = twitter.getDirectMessages();
                    for (DirectMessage message : directMessages) {
                        System.out.println("message = " + message);
                        String messageText = message.getText();
                        if (messageText.equals("print All")) {
                            twitter.sendDirectMessage(SCREEN_NAME, lightMessage.toString());
                            twitter.sendDirectMessage(SCREEN_NAME, humidityMessage.toString());
                            twitter.sendDirectMessage(SCREEN_NAME, temperatureMessage.toString());
                            twitter.sendDirectMessage(SCREEN_NAME, barometerAirPressureMessage.toString());
                        }
                        if (messageText.equals("print Temp")) {
                            twitter.sendDirectMessage(SCREEN_NAME, temperatureMessage.toString());
                        }
                        if (messageText.equals("print Humidity")) {
                            twitter.sendDirectMessage(SCREEN_NAME, humidityMessage.toString());
                        }
                        if (messageText.equals("print Light")) {
                            twitter.sendDirectMessage(SCREEN_NAME, lightMessage.toString());
                        }
                        if (messageText.equals("print AirPress")) {
                            twitter.sendDirectMessage(SCREEN_NAME, barometerAirPressureMessage.toString());
                        }
                        twitter.destroyDirectMessage(message.getId());
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();

                }
            }
        }, 0, 60_000);


        light.addIlluminanceListener(illuminanceRaw -> {
            lightMessage.message = " Lux: " + (illuminanceRaw / 10.0);
            lightMessage.timestamp = LocalDateTime.now().toString();
        });
        humidity.addHumidityListener(humidityRaw -> {
            humidityMessage.message = " %RH: " + (humidityRaw / 10.0);
            humidityMessage.timestamp = LocalDateTime.now().toString();
        });
        temperature.addTemperatureListener(temperatureRaw -> {
            temperatureMessage.message = "  °C: " + (temperatureRaw / 100.0);
            temperatureMessage.timestamp = LocalDateTime.now().toString();
        });
        barometer.addAirPressureListener(airPressureRaw -> {
            barometerAirPressureMessage.message = "mBar: " + (airPressureRaw / 1000.0);
            barometerAirPressureMessage.timestamp = LocalDateTime.now().toString();
        });
        //barometer.addAltitudeListener(altitude -> barometerAltitudeQueue.add(new SensorData(masterUID, BAROMETER_UID, LocalDateTime.now().toString(), altitude)));

        createWaitForQ().waitForQ();
    }


    private static class ValueMessage {
        public String timestamp;
        public String message;

        public String toString() {
            return timestamp + " - " + message;
        }
    }


    private static WaitForQ createWaitForQ() {
        WaitForQ waitForQ = new WaitForQ();

//    waitForQ.addShutDownAction(timerTaskCommit::cancel);
//    waitForQ.addShutDownAction(timerTaskStatistics::cancel);
        waitForQ.addShutDownAction(() -> {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        waitForQ.addShutDownAction(() -> {
            try {
                ipconLocal.disconnect();
            } catch (NotConnectedException ignored) {
            }
        });
        waitForQ.addShutDownAction(() -> {
            try {
                ipconRemote.disconnect();
            } catch (NotConnectedException ignored) {
            }
        });


        waitForQ.addShutDownAction(() -> exit(0));
        return waitForQ;
    }

}

