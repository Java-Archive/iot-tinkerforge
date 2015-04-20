package org.rapidpm.book.iot.tinkerforge.homesave.flickr;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.IOUtilities;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by Sven Ruppert on 03.01.2015.
 */
public class AuthExample {
  public static void auth() throws IOException, FlickrException {
    Properties properties;
    InputStream in = null;
    try {
      in = AuthExample.class.getResourceAsStream("setup.properties");
      properties = new Properties();
      properties.load(in);
    } finally {
      IOUtilities.close(in);
    }

    Flickr flickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());
    Flickr.debugStream = false;
    AuthInterface authInterface = flickr.getAuthInterface();

    Scanner scanner = new Scanner(System.in);

    Token token = authInterface.getRequestToken();
    System.out.println("token: " + token);

    String url = authInterface.getAuthorizationUrl(token, Permission.DELETE);
    System.out.println("Follow this URL to authorise yourself on Flickr");
    System.out.println(url);
    System.out.println("Paste in the token it gives you:");
    System.out.print(">>");

    String tokenKey = scanner.nextLine();
    scanner.close();

    Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
    System.out.println("Authentication success");

//    Auth auth = authInterface.checkToken(requestToken);

    final String rawResponse = requestToken.getRawResponse();
    System.out.println("rawResponse = " + rawResponse);
    // This token can be used until the user revokes it.
    System.out.println("Token: " + requestToken.getToken());
    System.out.println("Secret: " + requestToken.getSecret());
//    System.out.println("nsid: " + auth.getUser().getId());
//    System.out.println("Realname: " + auth.getUser().getRealName());
//    System.out.println("Username: " + auth.getUser().getUsername());
//    System.out.println("Permission: " + auth.getPermission().getType());
  }

  public static void main(String[] args) {
    try {
      AuthExample.auth();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }
}
