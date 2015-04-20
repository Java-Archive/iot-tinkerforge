package org.rapidpm.book.iot.tinkerforge.twitter;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Sven Ruppert on 03.01.2015.
 */
public class TwitterDemo {

    public static void main(String[] args) throws TwitterException {
        TwitterFactory twitterFactory = new TwitterFactory();
        final Twitter twitter = twitterFactory.createTwitter();

//        sentStatus(twitter);


        // nun sende Picture
        Configuration conf = twitterFactory.getConfiguration();

//        uploadPicture(conf);
//        printFollowers(twitter);


        //DirektNachricht
//        DirectMessage directMessage = twitter.sendDirectMessage("@SvenRuppert", "DemoMessage..");
//        System.out.println("directMessage = " + directMessage);

        ResponseList<DirectMessage> directMessages = twitter.getDirectMessages();
        for (DirectMessage message : directMessages) {
            System.out.println("message = " + message);
            twitter.destroyDirectMessage(message.getId());
        }

//        queryForIoT(twitter);

//        searchForUsers(twitter);


    }

    private static void searchForUsers(Twitter twitter) throws TwitterException {
        ResponseList<User> users = twitter.searchUsers("iot", -1);
        for (User user : users) {
            System.out.println("user.getName() = " + user.getName());
        }
    }

    private static void queryForIoT(Twitter twitter) throws TwitterException {
        Query query = new Query();
        query.setQuery("#IOT");
        QueryResult queryResult = twitter.search(query);
        System.out.println("queryResult = " + queryResult);

        List<Status> tweets = queryResult.getTweets();
        for (Status tweet : tweets) {
            System.out.println("tweet.getText() = " + tweet.getText());
            User user = tweet.getUser();
            System.out.println("tweet.getUser() = " + user);

        }
    }

    private static void printFollowers(Twitter twitter) throws TwitterException {
        //hole alle Follower
        IDs followersIDs = twitter.getFollowersIDs(-1);
        for (long l : followersIDs.getIDs()) {
            System.out.println("l = " + l);
        }
    }

    private static void uploadPicture(Configuration conf) throws TwitterException {
        ImageUploadFactory imageUploadFactory = new ImageUploadFactory(conf);
        ImageUpload imageUpload = imageUploadFactory.getInstance();
        String upload = imageUpload.upload(new File("./data","Duke.jpeg"), "and a Duke ;-) " + LocalDateTime.now());
        System.out.println("upload = " + upload);
    }

    private static void sentStatus(Twitter twitter) throws TwitterException {
        twitter.updateStatus("und er ist wieder da...." + LocalDateTime.now());
    }
}
