/* 1st : in mapfragment.xml
---------------------------------------*/
<RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="50"
            android:id="@+id/map_container">
            
            //add this btn to map_container
            <ImageButton
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:src="@drawable/ic_refresh_black_24dp"
                android:background="@color/White"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:layout_marginTop="10dp"
                android:layout_marginLeft="10dp"
                android:layout_marginStart="10dp"
                android:id="@+id/btn_reset_map"/>
 </RelativeLayout>
 
 // ic_refresh_black_24dp.xml   -----> the image used in the reset 
 
 <vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="24dp"
        android:height="24dp"
        android:viewportWidth="24.0"
        android:viewportHeight="24.0">
    <path
        android:fillColor="#FF000000"
        android:pathData="M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z"/>
</vector>

/* 2nd: in mapFragment.java
-------------------------------------------*/
oncreateview(){
view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
}

private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

// put it in this method
private void addMapMarkers(){

        if(mGoogleMap != null){
          // call this method before creating the custom markers
            resetMap();
            }
}


    @Override
    public void onClick(View v) {
        switch (v.getId()){
          case R.id.btn_reset_map:{
                addMapMarkers();
                startUserLocationsRunnable();
                break;
            }
        }
    }
