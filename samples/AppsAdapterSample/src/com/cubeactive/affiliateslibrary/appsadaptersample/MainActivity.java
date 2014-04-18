package com.cubeactive.affiliateslibrary.appsadaptersample;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import com.cubeactive.affiliateslibrary.AffiliatesApp;
import com.cubeactive.affiliateslibrary.AffiliatesAppsAdapter;
import com.cubeactive.affiliateslibrary.AffiliatesAppsProvider;

public class MainActivity extends Activity {

	private List<AffiliatesApp> _AffiliatesApps;
	private AffiliatesAppsAdapter _AffiliatesAdapter;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_AffiliatesApps = AffiliatesAppsProvider.getAffiliatesApps(this);
		_AffiliatesAdapter = new AffiliatesAppsAdapter(this, R.layout.affiliatesapp_listitem, _AffiliatesApps) {
			
			@Override
			protected LayoutInflater getLayoutInflater() {
				return MainActivity.this.getLayoutInflater();
			}
		};
		//TODO: Connect adapter to a listview
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
