package com.alleit.alleinfo;

import android.graphics.drawable.Drawable;

// information about the news
public class NewsData {
	public String headline = "";
	public String shortInfo = "";
	public String description = "";
	public String butURL = "";
	public String type = "";
	public String handler = "";
	public String rawHandler = "";
	public String color = "";
	public String uniqeIdentifier = "";
	public Drawable image;
	public ContentType contentType = ContentType.NoNews;
}