package org.rapidpm.book.iot.tinkerforge.homesave.flickr;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.flickr4java.flickr.util.IOUtilities;
import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import org.rapidpm.module.se.commons.WaitForQ;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.exit;
import static java.lang.System.out;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Created by Sven Ruppert on 05.01.2015.
 */
public class TinkerFlickrDemo {
  private static final String host = "192.168.0.201";
  private static final int port = 4223;

  private static final IPConnection ipcon = new IPConnection();
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:MM:ss");
  private static final TestProperties testProperties = new TestProperties();
  private static final Queue<String> queue = new ConcurrentLinkedDeque<>();

  public static void main(String[] args) throws AlreadyConnectedException, IOException {
    ipcon.connect(host, port);
    ipcon.setAutoReconnect(true);

    String format = formatter.format(LocalDateTime.now());
    System.out.println("format = " + format);

    final BrickletMotionDetector motionDectector = new BrickletMotionDetector("kgn", ipcon);
    motionDectector.addMotionDetectedListener(() -> supplyAsync(TinkerFlickrDemo::takePicture));

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        while (!queue.isEmpty()){
          String fileName = queue.poll();
          System.out.println("start with filename " + fileName);

          try {
            Stream<Path> pathStream = Files.find(new File("./").toPath(), 1, (path, basicFileAttributes) -> {
              boolean regularFile = basicFileAttributes.isRegularFile();
              boolean startsWith = path.getFileName().startsWith(fileName);
              if (regularFile && startsWith) {
                return true;
              } else return false;

            }, null);
            List<String> strings = pathStream.map(v -> v.toFile().getName()).collect(Collectors.toList());
            Collections.sort(strings);

            strings.stream().forEachOrdered(TinkerFlickrDemo::sendNextPic);


          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }, 0, 60_000);

    WaitForQ waitForQ = new WaitForQ();
    waitForQ.addShutDownAction(() -> {
          try {
            ipcon.disconnect();
          } catch (NotConnectedException ignored) { }
        }
    );
    waitForQ.addShutDownAction(() -> exit(0));
    waitForQ.waitForQ();
  }

  private static void sendNextPic(String picture) {
    File imageFile = new File(picture);

    String absolutePath = imageFile.getAbsolutePath();
    System.out.println("absolutePath = " + absolutePath);

    InputStream in = null;

    REST rest = new REST();
    rest.setHost(testProperties.getHost());
    Flickr flickr = new Flickr(testProperties.getApiKey(), testProperties.getSecret(), rest);
    System.out.println("testProperties = " + testProperties);
    setAuth(flickr, Permission.READ);

    Uploader uploader = flickr.getUploader();
    try {
      in = new FileInputStream(imageFile);
      UploadMetaData metaData = new UploadMetaData();
      metaData.setPublicFlag(false);
      metaData.setTitle(picture);
      String photoId = uploader.upload(in, metaData);
      System.out.println("photoId = " + photoId);
      boolean delete = imageFile.delete();
      System.out.println("delete = " + delete);
    } catch (FileNotFoundException | FlickrException e) {
      e.printStackTrace();
    } finally {
      IOUtilities.close(in);
    }
  }

  private static synchronized String takePicture() {
    String pFileName = "pic_" + System.nanoTime() ;
    try {
      Runtime run = Runtime.getRuntime();
      Process pr = run.exec("raspistill -n -vf -hf -drc high -ex night -w 640 -h 480 -t 5000 -tl 500 -o " + pFileName + "%04d.jpg");
      pr.waitFor();
      out.println("ok pic done... " + formatter.format(LocalDateTime.now()) + " --> " + pFileName);
      queue.add(pFileName);
    } catch (IOException
        | InterruptedException ex) {
      out.println("ex = " + ex);
      return "";
    }
    return pFileName;
  }


  /**
   * Set auth parameters for API calls that need it.
   *
   * @param flickr
   * @param perms
   */
  protected static void setAuth(Flickr flickr, Permission perms) {
    Auth auth = new Auth();
    auth.setPermission(perms);
    auth.setToken(testProperties.getToken());
    auth.setTokenSecret(testProperties.getTokenSecret());

    RequestContext requestContext = RequestContext.getRequestContext();
    requestContext.setAuth(auth);
    flickr.setAuth(auth);
  }
}
