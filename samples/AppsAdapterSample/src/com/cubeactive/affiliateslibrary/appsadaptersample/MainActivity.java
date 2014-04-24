package com.cubeactive.affiliateslibrary.appsadaptersample;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cubeactive.affiliateslibrary.AffiliatesApp;
import com.cubeactive.affiliateslibrary.AffiliatesAppsAdapter;
import com.cubeactive.affiliateslibrary.AffiliatesAppsProvider;

public class MainActivity extends Activity implements AffiliatesAppsProvider.Callbacks {

	private List<AffiliatesApp> mAffiliatesApps = null;
	private AffiliatesAppsAdapter mAffiliatesAdapter = null;
	private AffiliatesAppsProvider.GetAffiliatesAppsAsyncTask mAffiliatesAppsAsyncTask = null;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView _List = (ListView) findViewById(R.id.list);
		//Set on click listener for the list items.
		_List.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				if (mAffiliatesAdapter != null) {
					//Open Google Play page for the clicked item.
					final AffiliatesApp _AffiliatesApp = mAffiliatesAdapter.getItem(position);
					if (!_AffiliatesApp.isHeader())
						_AffiliatesApp.openInGooglePlay(MainActivity.this);
				}
			}			
		});					
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Get affiliates apps list if it is not yet available
		if (mAffiliatesApps == null)
			mAffiliatesAppsAsyncTask = AffiliatesAppsProvider.getASyncAffiliatesApps(this, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAffiliatesAppsLoadFinished(final List<AffiliatesApp> aAffiliatesApps) {
		//Check if task has not been canceled
		if (mAffiliatesAppsAsyncTask == null)
			return;
		mAffiliatesAppsAsyncTask = null;
		
		mAffiliatesApps = aAffiliatesApps;
		//Create adapter
		if (mAffiliatesApps != null) {
			mAffiliatesAdapter = new AffiliatesAppsAdapter(this, R.layout.affiliatesapp_listitem, mAffiliatesApps) {
				
				@Override
				protected LayoutInflater getLayoutInflater() {
					return MainActivity.this.getLayoutInflater();
				}
			};
		} else {
			mAffiliatesAdapter = null;
		}
		
		//Connect adapter to the list view
		final ListView _List = (ListView) findViewById(R.id.list);
		_List.setAdapter(mAffiliatesAdapter);
		
	}

	@Override
	protected void onPause() {
		//Cancel async task if there is one running in the background 
		if (mAffiliatesAppsAsyncTask != null) {
			mAffiliatesAppsAsyncTask.cancel(true);
			mAffiliatesAppsAsyncTask = null;			
		}
		super.onPause();
	}
}
