package com.alleit.alleinfo;

import android.content.Context;
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
	public String color = "#000000";
	public String uniqueIdentifier = "";
	public ContentType contentType = ContentType.NoNews;

	public Drawable Image(Context c)
	{
		return Webber.getImg(c, "",  rawHandler);
	}
}