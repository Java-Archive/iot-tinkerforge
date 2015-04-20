/*
 * Copyright [2014] [www.rapidpm.org / Sven Ruppert (sven.ruppert@rapidpm.org)]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.rapidpm.book.iot.tinkerforge.twitter;

import twitter4j.Twitter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Sven Ruppert on 12.04.2014.
 */
public class TwitterFactory {

    private twitter4j.TwitterFactory tf;
    private final ConfigurationBuilder cb = new ConfigurationBuilder();
    private Configuration configuration;

    public TwitterFactory() {
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("DI0UZvexwirEJ1wl5sw9Vfqx3")
                .setOAuthConsumerSecret("NQ0Zumw7Li1XbuwzrNHOKquznBgmAJo1sPAoqiNMShqgFufqip")
                .setOAuthAccessToken("2440296835-4dmuWPOLYUgLjn9ddcXpuloOgDAPO1W0Y5NZSg7")
                .setOAuthAccessTokenSecret("X9QYzlDKURIxGZbFWoZ7NdvhcFurBfEUVHMTLxtBSrU2x");
        configuration = cb.build();
        tf = new twitter4j.TwitterFactory(configuration);
    }

    public Twitter createTwitter() {
        return tf.getInstance();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
