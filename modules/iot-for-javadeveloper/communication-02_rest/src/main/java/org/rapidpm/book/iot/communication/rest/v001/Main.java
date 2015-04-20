package org.rapidpm.book.iot.communication.rest.v001;


import org.rapidpm.book.iot.communication.rest.v001.remote.ServiceRemoteProxy;

/**
 * starte Endpoint zuerst !!
 *
 * Created by Sven Ruppert on 22.09.2014.
 */
public class Main {
  public static void main(String[] args) {
    Service proxy = new ServiceRemoteProxy();
    System.out.println("proxy.work() = " + proxy.work("Hello"));
  }
}
