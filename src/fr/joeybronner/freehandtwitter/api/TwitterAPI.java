package fr.joeybronner.freehandtwitter.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import fr.joeybronner.freehandtwitter.util.Constants;
import fr.joeybronner.freehandtwitter.util.HttpUtil;

public class TwitterAPI {

	private String twitterApiKey;
	private String twitterAPISecret;
	
	/*
	 * Here, all relevant documents
	 * 
	 * Documentation to search tweets : https://dev.twitter.com/rest/reference/get/search/tweets
	 * 
	 */

	public TwitterAPI(String twitterAPIKey, String twitterApiSecret){
		this.twitterApiKey = twitterAPIKey;
		this.twitterAPISecret = twitterApiSecret;
	}

	public ArrayList<TwitterStatus> getTwitterTweets(String screenName) {
		ArrayList<TwitterStatus> twitterTweetArrayList = null;
		try {
			String twitterUrlApiKey = URLEncoder.encode(twitterApiKey, "UTF-8");
			String twitterUrlApiSecret = URLEncoder.encode(twitterAPISecret, "UTF-8");
			String twitterKeySecret = twitterUrlApiKey + ":" + twitterUrlApiSecret;
			String twitterKeyBase64 = Base64.encodeToString(twitterKeySecret.getBytes(), Base64.NO_WRAP);
			TwitterAuthToken twitterAuthToken = getTwitterAuthToken(twitterKeyBase64);
			twitterTweetArrayList = getTwitterTweets(screenName, twitterAuthToken);
		} catch (UnsupportedEncodingException ex) {
		} catch (IllegalStateException ex1) {
		}
		return twitterTweetArrayList;
	}

	public ArrayList<TwitterStatus> getTwitterTweets(String screenName, TwitterAuthToken twitterAuthToken) {
		ArrayList<TwitterStatus> twitterTweetArrayList = null;
		if (twitterAuthToken != null && twitterAuthToken.token_type.equals("bearer")) {
			HttpGet httpGet = new HttpGet(Constants.TWITTER_SEARCHTWEETS_URL + screenName + 
					Constants.TWITTER_SEARCH_COUNT + 
					Constants.TWITTER_SEARCH_LANG + 
					Constants.TWITTER_SEARCH_ENTITIES);
			httpGet.setHeader("Authorization", "Bearer " + twitterAuthToken.access_token);
			httpGet.setHeader("Content-Type", "application/json");
			HttpUtil httpUtil = new HttpUtil();
			String twitterTweets = httpUtil.getHttpResponse(httpGet);
			twitterTweetArrayList = convertJsonToTwitterTweet(twitterTweets);
		}
		return twitterTweetArrayList;
	}

	public TwitterAuthToken getTwitterAuthToken(String twitterKeyBase64) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(Constants.TWITTER_TOKEN_URL);
		httpPost.setHeader("Authorization", "Basic " + twitterKeyBase64);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
		HttpUtil httpUtil = new HttpUtil();
		String twitterJsonResponse = httpUtil.getHttpResponse(httpPost);
		return convertJsonToTwitterAuthToken(twitterJsonResponse);
	}

	private TwitterAuthToken convertJsonToTwitterAuthToken(String jsonAuth) {
		TwitterAuthToken twitterAuthToken = null;
		if (jsonAuth != null && jsonAuth.length() > 0) {
			try {
				Gson gson = new Gson();
				twitterAuthToken = gson.fromJson(jsonAuth, TwitterAuthToken.class);
			} catch (IllegalStateException ex) { }
		}
		return twitterAuthToken;
	}

	private ArrayList<TwitterStatus> convertJsonToTwitterTweet(String twitterTweets) {
		ArrayList<TwitterStatus> twitterTweetArrayList = new ArrayList<TwitterStatus>();
		Log.v("Joey", twitterTweets);
		if (twitterTweets != null && twitterTweets.length() > 0) {
			Gson g = new Gson();
			TwitterStatuses vc = g.fromJson(twitterTweets, TwitterStatuses.class);
			for (int i = 0; i < vc.statuses.size(); i++) {
				if (vc.statuses.get(i).getText()!=null) {
					try {
						TwitterStatus s = vc.statuses.get(i);
						twitterTweetArrayList.add(s);
					} catch (Exception e) {
						System.out.println("null object");
					}
				}
			}
		}
		return twitterTweetArrayList;
	}

	private class TwitterAuthToken {
		String token_type;
		String access_token;
	}
}
