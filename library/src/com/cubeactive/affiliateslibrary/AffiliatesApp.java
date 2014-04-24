package com.cubeactive.affiliateslibrary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.cubeactive.affiliates.R;

public class AffiliatesApp {
	private String mTitle = "";
	private String mDescription = "";
	private int mIcon = 0;
	private int mCategory = 0;
	private int mType = 0;
	private boolean mIsPaid = false;
	private boolean mIsHeader = false;
	private long mId = 0;
	private String mPackageName = "";
	private String mDeveloper = "";	
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(final String aTitle) {
		this.mTitle = aTitle;
	}

	public int getIcon() {
		return mIcon;
	}

	public void setIcon(final int aIcon) {
		this.mIcon = aIcon;
	}

	public boolean isHeader() {
		return mIsHeader;
	}

	public void setIsHeader(final boolean aIsHeader) {
		this.mIsHeader = aIsHeader;
	}

	public long getId() {
		return mId;
	}

	public void setId(final long aId) {
		this.mId = aId;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(final String aDescription) {
		this.mDescription = aDescription;
	}

	public int getCategory() {
		return mCategory;
	}

	public void setCategory(final int aCategory) {
		this.mCategory = aCategory;
	}

	public int getType() {
		return mType;
	}

	public void setType(final int aType) {
		this.mType = aType;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(final String aPackageName) {
		this.mPackageName = aPackageName;
	}

	public String getDeveloper() {
		return mDeveloper;
	}

	public void setDeveloper(final String aDeveloper) {
		this.mDeveloper = aDeveloper;
	}

	public boolean isPaid() {
		return mIsPaid;
	}

	public void setIsPaid(final boolean aIsPaid) {
		this.mIsPaid = aIsPaid;
	}

	private static boolean startActivity(final Context context, final Intent aIntent) {
        try {
        	context.startActivity(aIntent);
        	return true;
        } catch (final ActivityNotFoundException e) {        	
        	return false;
        }    	
		
	}	
	
	public void openInGooglePlay(final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        //Try to open the app page in the Google Play app
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        if (startActivity(context, intent) == false) {
        	//Google Play app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            if (startActivity(context, intent) == false) {
            	//Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(context, context.getString(R.string.message_could_not_open_app_page), Toast.LENGTH_SHORT).show();            	
            }
        }
	}
	
}
