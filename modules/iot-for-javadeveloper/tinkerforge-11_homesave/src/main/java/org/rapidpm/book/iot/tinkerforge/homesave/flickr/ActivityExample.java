package org.rapidpm.book.iot.tinkerforge.homesave.flickr;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.activity.ActivityInterface;
import com.flickr4java.flickr.activity.Event;
import com.flickr4java.flickr.activity.Item;
import com.flickr4java.flickr.activity.ItemList;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.IOUtilities;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Sven Ruppert on 03.01.2015.
 */
public class ActivityExample {
  static String apiKey;

  static String sharedSecret;

  Flickr f;

  REST rest;

  RequestContext requestContext;

  Properties properties = null;

  public ActivityExample() throws IOException {
    InputStream in = null;
    try {
      in = getClass().getResourceAsStream("setup.properties");
      properties = new Properties();
      properties.load(in);
    } finally {
      IOUtilities.close(in);
    }
    final String apiKey1 = properties.getProperty("apiKey");
    final String secret = properties.getProperty("secret");
    f = new Flickr(apiKey1, secret, new REST());
    requestContext = RequestContext.getRequestContext();
    Auth auth = new Auth();
    auth.setPermission(Permission.READ);
    auth.setToken(properties.getProperty("token"));
    auth.setTokenSecret(properties.getProperty("tokensecret"));
    requestContext.setAuth(auth);
    Flickr.debugRequest = false;
    Flickr.debugStream = false;
  }

  public void showActivity() throws FlickrException, IOException, SAXException {
    ActivityInterface iface = f.getActivityInterface();
    ItemList list = iface.userComments(10, 0);
    for (int j = 0; j < list.size(); j++) {
      Item item = (Item) list.get(j);
      System.out.println("Item " + (j + 1) + "/" + list.size() + " type: " + item.getType());
      System.out.println("Item-id:       " + item.getId() + "\n");
      ArrayList events = (ArrayList) item.getEvents();
      for (int i = 0; i < events.size(); i++) {
        System.out.println("Event " + (i + 1) + "/" + events.size() + " of Item " + (j + 1));
        System.out.println("Event-type: " + ((Event) events.get(i)).getType());
        System.out.println("User:       " + ((Event) events.get(i)).getUser());
        System.out.println("Username:   " + ((Event) events.get(i)).getUsername());
        System.out.println("Value:      " + ((Event) events.get(i)).getValue() + "\n");
      }
    }
    ActivityInterface iface2 = f.getActivityInterface();
    list = iface2.userPhotos(50, 0, "300d");
    for (int j = 0; j < list.size(); j++) {
      Item item = (Item) list.get(j);
      System.out.println("Item " + (j + 1) + "/" + list.size() + " type: " + item.getType());
      System.out.println("Item-id:       " + item.getId() + "\n");
      ArrayList events = (ArrayList) item.getEvents();
      for (int i = 0; i < events.size(); i++) {
        System.out.println("Event " + (i + 1) + "/" + events.size() + " of Item " + (j + 1));
        System.out.println("Event-type: " + ((Event) events.get(i)).getType());
        if (((Event) events.get(i)).getType().equals("note")) {
          System.out.println("Note-id:    " + ((Event) events.get(i)).getId());
        } else if (((Event) events.get(i)).getType().equals("comment")) {
          System.out.println("Comment-id: " + ((Event) events.get(i)).getId());
        }
        System.out.println("User:       " + ((Event) events.get(i)).getUser());
        System.out.println("Username:   " + ((Event) events.get(i)).getUsername());
        System.out.println("Value:      " + ((Event) events.get(i)).getValue());
        System.out.println("Dateadded:  " + ((Event) events.get(i)).getDateadded() + "\n");
      }
    }
  }

  public static void main(String[] args) {
    try {
      ActivityExample t = new ActivityExample();
      t.showActivity();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }
}
