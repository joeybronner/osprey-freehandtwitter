package fr.joeybronner.freehandtwitter.api;

import com.google.gson.annotations.SerializedName;

public class TwitterUser {

	@SerializedName("screen_name")
	private String screenName;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("profile_image_url")
	private String profileImageUrl;
	
	@SerializedName("profile_background_color")
	private String profileBackgroundColor;

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getScreenName() {
		return screenName;
	}
	
	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setProfileBackgroundColor(String color) {
		this.profileBackgroundColor = color;
	}
}