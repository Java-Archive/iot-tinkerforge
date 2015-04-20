package org.rapidpm.book.iot.communication.rest.v001;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by Sven Ruppert on 22.09.2014.
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Service {
  @WebMethod
  public String work(String txt);
}

