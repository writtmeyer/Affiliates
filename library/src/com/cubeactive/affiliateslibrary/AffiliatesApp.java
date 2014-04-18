package com.cubeactive.affiliateslibrary;

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
}
