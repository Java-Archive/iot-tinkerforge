package org.rapidpm.book.iot.communication.rest.v001.remote;


import org.rapidpm.book.iot.communication.rest.v001.Service;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *   1st argument service URI, refer to wsdl document above
 *   2nd argument is service name, refer to wsdl document above
 * Created by Sven Ruppert on 22.09.2014.
 */
public class ServiceRemoteProxy implements Service {

  private URL url;
  private Service realSubject;

  public ServiceRemoteProxy() {
    try {
      url = new URL("http://localhost:9999/ws/service?wsdl");
      final String namespaceURI = "http://v001.rest.communication.iot.book.rapidpm.org/";
      final String localPart = "ServiceImplService";
      QName qname = new QName(namespaceURI, localPart);
      javax.xml.ws.Service service = javax.xml.ws.Service.create(url, qname);
      realSubject = service.getPort(Service.class);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
  public String work(String txt){
    return realSubject.work(txt);
  }
}
