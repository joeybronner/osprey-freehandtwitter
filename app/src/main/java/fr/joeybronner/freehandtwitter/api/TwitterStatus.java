package fr.joeybronner.freehandtwitter.api;

import com.google.gson.annotations.SerializedName;

public class TwitterStatus {

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("text")
	private String text;

	@SerializedName("in_reply_to_status_id")
	private String inReplyToStatusId;

	@SerializedName("in_reply_to_user_id")
	private String inReplyToUserId;

	@SerializedName("in_reply_to_screen_name")
	private String inReplyToScreenName;

	@SerializedName("user")
	private TwitterUser twitterUser;

	
	// Getters & Setters -----------------------------------
	
	public String getCreatedAt() {
		return createdAt;
	}
	
	public int getTweetLenght() {
		return text.length();
	}
	
	public String getId() {
		return id;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public String getText() {
		return text;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}
	
	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}
	
	public void setInReplyToUserId(String inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setTwitterUser(TwitterUser twitterUser) {
		this.twitterUser = twitterUser;
	}

	public TwitterUser getTwitterUser() {
		return twitterUser;
	}

	@Override
	public String toString(){
		return getText();
	}
}
