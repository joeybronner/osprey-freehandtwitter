package fr.joeybronner.freehandtwitter.util;

import java.util.List;

import android.graphics.Typeface;

import fr.joeybronner.freehandtwitter.api.TwitterStatus;

public class Constants {

	// Twitter app credentials
	final public static String TWITTER_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    final public static String TWITTER_API_KEY = "zOzdMJLSH1uNEF37Gvzz1No3M";
    final public static String TWITTER_API_SECRET = "lwSxPa55Z3spqaMr30aqbINV802eaY1mFzsdvv8Yqxst6Rrrk4";
	
	// Twitter API's URLs
	final public static String TWITTER_USERTIMELINE_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
	final public static String TWITTER_SEARCHTWEETS_URL = "https://api.twitter.com/1.1/search/tweets.json?q=";
	final public static String TWITTER_SEARCH_COUNT = "&count=100";
	final public static String TWITTER_SEARCH_LANG = "&lang=fr";
	public static String TWITTER_RESULT_TYPE = "&result_type=popular";
	public static String TWITTER_USER_SEARCH = "";
	
	public static List<TwitterStatus> twit;
	
	public static Typeface tf;
	
	public static int SCROLL_SPEED = 50;
}
 	