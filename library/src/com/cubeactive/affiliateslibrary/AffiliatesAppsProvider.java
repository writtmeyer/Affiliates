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
		
		final List<AffiliatesApp> _Result = new ArrayList<AffiliatesApp>();
        //Get affiliates xml resource id
      	final int _resID = _Res.getIdentifier(AFFILIATES_XML, "xml", _PackageName);		
    	final XmlResourceParser _xml = _Res.getXml(_resID);
    	try {
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
            	if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("app"))){
            		_Result.add(parseAppTag(_xml, _Res, _PackageName));      		
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

	public static List<AffiliatesApp> getASyncAffiliatesApps(final Context context) {
		//TODO: Add async function for loading a affiliates apps list.
		return null;
	}
}
