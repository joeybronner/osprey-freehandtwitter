package fr.joeybronner.freehandtwitter.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TwitterStatuses {

	@SerializedName("statuses")
	public List<TwitterStatus> statuses;


}
