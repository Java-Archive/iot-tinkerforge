package org.rapidpm.book.iot.tinkerforge.weatherfx;

/**
 * Created by Sven Ruppert on 16.04.14.
 */
public class ConnectionData {
    public static final int DEFAULT_PORT = 4223;
    public static final String DEFAULT_HOST = "localhost";
    public final static int CALLBACK_RATE = 5000;

    private String host;
    private int port;

    public ConnectionData() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ConnectionData(String host) {
        this(host, DEFAULT_PORT);
    }

    public ConnectionData(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
