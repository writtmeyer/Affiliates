package com.cubeactive.affiliateslibrary;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cubeactive.affiliates.R;

public abstract class AffiliatesAppsAdapter extends ArrayAdapter<AffiliatesApp> 
{
	//private final String TAG = "AffiliatesAppsAdapter";
	protected int mSelectedIndex = -1;
	private int mDefaultResource = -1;
    private AppIconCache mCache;
	
	public AffiliatesAppsAdapter(final Context context, final int resource, final AffiliatesApp[] aArray) {
		super(context, resource, aArray);
		mDefaultResource = resource;
		createCache(context);
	}
		
	public AffiliatesAppsAdapter(final Context context, final int resource, final List<AffiliatesApp> aList) {
		super(context, resource, aList);
		mDefaultResource = resource;
		createCache(context);
    }

	private void createCache(final Context context) {
        // Pick cache size based on memory class of device
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
        mCache = new AppIconCache(memoryClassBytes / 2);		
		
	}
	
	public void setSelectedIndex(final int position)
	{
	    mSelectedIndex = position;
	    notifyDataSetChanged();
	}

	public long getSelectedId()
	{			
	    if (mSelectedIndex > -1)
	    	return getItemId(mSelectedIndex);
	    else
	    	return -1;
	}
	
	protected abstract LayoutInflater getLayoutInflater();

	private Boolean checkViewType(final View convertView, final int aItemType) {
		//TODO: If the adapter reuses a view it will create a bug causing some tasks for loading the icons
		//to be canceled or a view gets the wrong icon. To fix this return false here for now, this will
		//make the adapter create a new view every time but it might be better to take a look at the issue
		//later to see if we can get it to work correctly and let the adapter reuse it's views if possible.
		return false;
		
		//The convertView might not be the correct one (updates to the adapter does not update cached
		//views at position x. So when the item type from a position changes the adapter will recycle a
		//wrong kind of view.
//		switch (aItemType) {
//		case 1:				
//			return ((String) convertView.getTag()).equals("1");				
//		default :
//			return ((String) convertView.getTag()).equals("0");				
//		}		
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		//Reuse view if possible
		View _Result = convertView;		
		final int _ItemType = getItemViewType(position);  
		final AffiliatesApp _Item = getItem(position);
		if ((_Result == null) || !checkViewType(convertView, _ItemType))
		{
			final LayoutInflater inflater = getLayoutInflater();
			switch (_ItemType) {
			case 1:
				_Result = inflater.inflate(R.layout.affiliatesapp_header, parent, false);				
				//Set text of header label
				final TextView _TextViewHeader = (TextView) _Result.findViewById(R.id.affiliatesapps_header);
				_TextViewHeader.setText(_Item.getTitle());
				_Result.setTag("1");
				break;
			default :
				_Result = inflater.inflate(mDefaultResource, parent, false);				
				_Result.setTag("0");
				break;
			}			
		}
		
		if (_ItemType == 1)
			return _Result;
		
		//Set text of title label
		final TextView _TextViewTitle = (TextView) _Result.findViewById(R.id.affiliatesapp_title);
		_TextViewTitle.setText(_Item.getTitle());
		
		//Set text of description label
		final TextView _TextViewDescription = (TextView) _Result.findViewById(R.id.affiliatesapp_description);
		_TextViewDescription.setText(_Item.getDescription());
		
        final ImageView _ImageViewIcon = (ImageView) _Result.findViewById(R.id.affiliatesapp_icon);
		
        // Cancel any pending app icon task, since this view is now bound
        // to new app icon
        final AppIconAsyncTask oldTask = (AppIconAsyncTask) _ImageViewIcon.getTag();
        if (oldTask != null) {
            //Log.i(TAG, "Cancel old task");
            oldTask.cancel(false);
        }

        if (_Item.getIcon() != 0) {
	        // Cache enabled, try looking for cache hit, cache images are stored by their resource id.
	        final Bitmap cachedResult = mCache.get(String.valueOf(_Item.getIcon()));
	        if (cachedResult != null) {
	             //Log.i(TAG, "cachedResult found");
	        	_ImageViewIcon.setImageBitmap(cachedResult);
	        } else {
	            //Log.i(TAG, "starting async task");
	        	//Icon is not available from cache, load the icon from the resource id.
		        final AppIconAsyncTask task = new AppIconAsyncTask(_ImageViewIcon);
		        _ImageViewIcon.setImageBitmap(null);
		        _ImageViewIcon.setTag(task);
		        task.execute(_Item.getIcon());
	        }
        } else {
            //Log.i(TAG, "item has no icon");
        	_ImageViewIcon.setScaleType(ImageView.ScaleType.CENTER);	
        	_ImageViewIcon.setImageResource(R.drawable.ic_no_image);
        	_ImageViewIcon.setTag(null);        	
        }
        
		return _Result;
	}
	
	@Override
	public int getItemViewType(final int position) {
		if (getItem(position).isHeader())
			return 1;
		else
			return super.getItemViewType(position);
	}
	
//	@Override
//	public long getItemId(int position) {			
//		return getItem(position).getId();
//	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	//TrimMemory can be called by the owner (activity) onTrimMemory, this will trim the
	//cache used memory.
    public void TrimMemory(final int level) {
        // Memory we can release here will help overall system performance, and
        // make us a smaller target as the system looks for memory

        if (level >= Activity.TRIM_MEMORY_MODERATE) { // 60
            // Nearing middle of list of cached background apps; evict our
            // entire thumbnail cache
            mCache.evictAll();

        } else if (level >= Activity.TRIM_MEMORY_BACKGROUND) { // 40
            // Entering list of cached background apps; evict oldest half of our
            // thumbnail cache
            mCache.trimToSize(mCache.size() / 2);
        }
    }
    
    private class AppIconAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
    	private final String TAG = "AppIconAsyncTask";
        private final ImageView mTarget;

        public AppIconAsyncTask(final ImageView target) {
            mTarget = target;
        }

        @Override
        protected void onPreExecute() {
            mTarget.setTag(this);
            mTarget.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        }

        @Override
        protected Bitmap doInBackground(final Integer... params) {
			Bitmap result = null;
        	final Resources _Res = getContext().getResources();
			try {
	        	result = BitmapFactory.decodeResource(_Res, params[0]);
	        } catch (final Exception e) {
	            Log.e(TAG, "Error on fetching icon: " + e.getMessage());
	        }        		

			//Keep reference to this bitmap
			if (result != null)
	            mCache.put(String.valueOf(params[0]), result);

            return result;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if (mTarget.getTag() == this) {
            	if (result != null) {
    	            //Log.i(TAG, "postExecute result != null");
            		mTarget.setScaleType(ImageView.ScaleType.CENTER);	
            		mTarget.setImageBitmap(result);
            	} else {
    	            //Log.i(TAG, "postExecute result is null");
            		mTarget.setScaleType(ImageView.ScaleType.CENTER);	
            		mTarget.setImageResource(R.drawable.ic_no_image);
            	}
            	animate(mTarget);
                mTarget.setTag(null);
            }
        }

        //Fade in an app icon.
        private void animate(final ImageView imageView) {
    	    final int fadeInDuration = 550; // Configure time values here

    	    imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends

    	    final Animation fadeIn = new AlphaAnimation(0, 1);
    	    fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
    	    fadeIn.setDuration(fadeInDuration);

    	    final AnimationSet animation = new AnimationSet(false); // change to false
    	    animation.addAnimation(fadeIn);
    	    imageView.setAnimation(animation);

    	    animation.setAnimationListener(new AnimationListener() {
    	        @Override
				public void onAnimationEnd(final Animation animation) {    	        	
    	            //Log.i(TAG, "Animation ended set imageview visible");
    	        	imageView.setVisibility(View.VISIBLE);
    	        }
    	        @Override
				public void onAnimationRepeat(final Animation animation) {
    	        }
    	        @Override
				public void onAnimationStart(final Animation animation) {
    	        }
    	    });
        }        
    }

    
    /**
     * Simple extension that uses Bitmap instances as keys, using their
     * memory footprint in bytes for sizing.
     */
    private class AppIconCache extends LruCache<String, Bitmap> {
        public AppIconCache(final int maxSizeBytes) {
            super(maxSizeBytes);
        }
        
        @SuppressLint("NewApi")
		@Override
        protected int sizeOf(final String key, final Bitmap aBitmap) {
            if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB_MR1) {
                return aBitmap.getByteCount();
            } else {
                return (aBitmap.getRowBytes() * aBitmap.getHeight()) / 1000;
            }
        }
    }        
}   

