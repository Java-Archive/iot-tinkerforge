package org.rapidpm.book.iot.tinkerforge.homesave.flickr;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.flickr4java.flickr.util.IOUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Sven Ruppert on 03.01.2015.
 */
public class FlickrDemo {
  private static Flickr flickr;
  private static TestProperties testProperties = new TestProperties();

  public static void main(String[] args) {


    REST rest = new REST();
    rest.setHost(testProperties.getHost());
    flickr = new Flickr(testProperties.getApiKey(), testProperties.getSecret(), rest);

    setAuth(Permission.READ);

    File imageFile = new File("data/pics", "pic_22721651429409.jpg");

    InputStream in = null;
    Uploader uploader = flickr.getUploader();
    PhotosInterface pint = flickr.getPhotosInterface();

    try {
      in = new FileInputStream(imageFile);
      UploadMetaData metaData = buildPrivatePhotoMetadata();
      metaData.setPublicFlag(false);
      metaData.setTitle("pic_22721651429409.jpg");
      String photoId = uploader.upload(in, metaData);
      System.out.println("photoId = " + photoId);
      try {
        pint.delete(photoId);
      } catch (FlickrException e) {
        // Ignore if user doesn't have delete permissions
        // This will leave a *private* photo in the test account's photostream!
        if (!e.getErrorCode().equals("99")) {
          throw e;
        }
      }

    } catch (FileNotFoundException | FlickrException e) {
      e.printStackTrace();
    } finally {
      IOUtilities.close(in);
    }
  }


  /**
   * Set auth parameters for API calls that need it.
   *
   * @param perms
   */
  protected static void setAuth(Permission perms) {
    Auth auth = new Auth();
    auth.setPermission(perms);
    auth.setToken(testProperties.getToken());
    auth.setTokenSecret(testProperties.getTokenSecret());

    RequestContext requestContext = RequestContext.getRequestContext();
    requestContext.setAuth(auth);
    flickr.setAuth(auth);
  }

  /**
   * Certain tests don't require authorization and calling with auth set may mask other errors.
   */
  protected static void clearAuth() {
    RequestContext requestContext = RequestContext.getRequestContext();
    requestContext.setAuth(null);
  }


  private static UploadMetaData buildPrivatePhotoMetadata() {
    UploadMetaData uploadMetaData = new UploadMetaData();
    uploadMetaData.setPublicFlag(false);
    return uploadMetaData;
  }
}
