package com.cubeactive.affiliateslibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.util.Log;

public class AffiliatesAppsProvider {
	private static String TAG = "AffiliatesAppsProvider";
    static final private String AFFILIATES_XML = "affiliates"; 
    
	public AffiliatesAppsProvider() {
	}
		
	private static AffiliatesApp parseAppTag(final XmlResourceParser aXml, final Resources aResources, final String aPackagename) throws XmlPullParserException, IOException {
		final AffiliatesApp _Result = new AffiliatesApp();
		
		//Parse attributes
		if (aXml.getAttributeValue(null, "package") != null)
			_Result.setPackageName(aXml.getAttributeValue(null, "package"));
		if (aXml.getAttributeValue(null, "developer") != null)
			_Result.setDeveloper(aXml.getAttributeValue(null, "developer"));
		if (aXml.getAttributeValue(null, "category") != null)
			_Result.setCategory(Integer.parseInt(aXml.getAttributeValue(null, "category")));
		if (aXml.getAttributeValue(null, "type") != null)
			_Result.setType(Integer.parseInt(aXml.getAttributeValue(null, "type")));
		if (aXml.getAttributeValue(null, "paid") != null)
			_Result.setIsPaid(aXml.getAttributeValue(null, "paid").equals("1"));

		final String _FormattedPackageName = _Result.getPackageName().replace(".", "_");
		//Get title
		final String _TitleResourceName = "affiliate_app_title_" + _FormattedPackageName;
		int _ResId = aResources.getIdentifier( _TitleResourceName , "string", aPackagename);
		_Result.setTitle(aResources.getString(_ResId));
		
		//Get description
		final String _DescriptionResourceName = "affiliate_app_description_" + _FormattedPackageName;
		_ResId = aResources.getIdentifier( _DescriptionResourceName , "string", aPackagename);
		_Result.setDescription(aResources.getString(_ResId));
		
		//Find app icon resource id
		final String _IconResourceName = "ic_app_" + _FormattedPackageName;
		_ResId = aResources.getIdentifier( _IconResourceName , "drawable", aPackagename);
		_Result.setIcon(_ResId);
		
		//Move to the next tag
        int eventType = aXml.getEventType();
        while (!((eventType == XmlPullParser.END_TAG) && (aXml.getName().equals("app")))) {
        	eventType = aXml.next();
        }		
        return _Result;
	}
	
	//Function for loading a affiliates apps list.
	//Use GetAffiliatesAppsAsyncTask() to load the list in a background thread, using this call
	//might block the UI thread.
	public static List<AffiliatesApp> getAffiliatesApps(final Context context) {
    	//Get resources
    	final String _PackageName = context.getPackageName();
    	Resources _Res;
		try {
			_Res = context.getPackageManager().getResourcesForApplication(_PackageName);
		} catch (final NameNotFoundException e) {
			e.printStackTrace();
			return null;
		} 

		return getAffiliatesApps(_Res, _PackageName);		
	}

	protected static List<AffiliatesApp> getAffiliatesApps(final Resources aResources, final String aPackagename) {
		final List<AffiliatesApp> _Result = new ArrayList<AffiliatesApp>();
        //Get affiliates xml resource id
      	final int _resID = aResources.getIdentifier(AFFILIATES_XML, "xml", aPackagename);		
    	final XmlResourceParser _xml = aResources.getXml(_resID);
    	try {
    		AffiliatesApp _Item;
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("app"))){
            		_Item = parseAppTag(_xml, aResources, aPackagename);
            		//Check if item matches current packagename, if so skip item to avoid 
            		//displaying the current app in the list.
            		if (!_Item.getPackageName().equals(aPackagename))
            			_Result.add(_Item);      		
	            }
            	eventType = _xml.next();
            }
    	} catch (final XmlPullParserException e) {
    		Log.e(TAG, e.getMessage(), e);
    	} catch (final IOException e) {
    		Log.e(TAG, e.getMessage(), e);
    		
    	} finally {        	
    		_xml.close();
    	}
    	
    	//Randomize the order of apps in the list
    	Collections.shuffle(_Result, new Random());
		return _Result;		
	}
	
	//Async function for loading a affiliates apps list.
	public static GetAffiliatesAppsAsyncTask getASyncAffiliatesApps(final Context context, final Callbacks aCallback) {
		final GetAffiliatesAppsAsyncTask task = new GetAffiliatesAppsAsyncTask(context, aCallback);
        task.execute("");
        return task;
	}
	
    public static class GetAffiliatesAppsAsyncTask extends AsyncTask<String, Void, List<AffiliatesApp>> {
    	private final String TAG = "GetAffiliatesAppsAsyncTask";
    	private Resources mResources;
    	private final String mPackagename;
    	private final Callbacks mCallback;
    	
        public GetAffiliatesAppsAsyncTask(final Context context, final Callbacks aCallback) {
        	mCallback = aCallback;
        	//Get resources
        	mPackagename = context.getPackageName();
    		try {
    			mResources = context.getPackageManager().getResourcesForApplication(mPackagename);
    		} catch (final NameNotFoundException e) {
    			e.printStackTrace();
    		} 
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<AffiliatesApp> doInBackground(final String... params) {
        	if (mResources == null)
        		return null;
            return getAffiliatesApps(mResources, mPackagename);
        }

        @Override
        protected void onPostExecute(final List<AffiliatesApp> result) {
        	if ((!isCancelled()) && (mCallback != null))
        			mCallback.onAffiliatesAppsLoadFinished(result);
        }

    }
	
    /**
	 * A callback interface for the async method getASyncAffiliatesApps
	 */
	public interface Callbacks {
		/**
		 * Callback for when an note has been selected.
		 */
		public void onAffiliatesAppsLoadFinished(List<AffiliatesApp> aAffiliatesApps);
	}        
}
