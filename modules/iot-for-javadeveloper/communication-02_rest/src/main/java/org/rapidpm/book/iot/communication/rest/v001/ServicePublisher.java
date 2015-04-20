package org.rapidpm.book.iot.communication.rest.v001;

import javax.xml.ws.Endpoint;

/**
 * publish to http://localhost:9999/ws/service?wsdl
 *
 * Created by Sven Ruppert on 22.09.2014.
 */

public class ServicePublisher {
  public static void main(String[] args) {
    Endpoint.publish("http://localhost:9999/ws/service", new ServiceImpl());
  }
}
