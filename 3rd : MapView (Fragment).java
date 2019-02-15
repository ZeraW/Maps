1st : in fragment xml 
----------------------------------
<RelativeLayout
            android:id="@+id/map_container"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="50">

            <com.google.android.gms.maps.MapView xmlns:android="http://schemas.android.com/apk/res/android"
                android:id="@+id/user_list_map"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        </RelativeLayout>
 ------------------------------------------------------------------------------------------------------
 2nd : in fragment 
 ----------------------------------
public class fragmentnamegoeshere extends Fragment implements OnMapReadyCallback {
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    //widgets
    private RecyclerView mUserListRecyclerView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition;
    private LatLngBounds mMapBoundary;
    
    @Override
    public View onCreateView(){
    mMapView = view.findViewById(R.id.user_list_map);
    initGoogleMap(savedInstanceState);
}
private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
}

@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //show my location
        map.setMyLocationEnabled(true);
        mGoogleMap = map;
        setCameraView();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    
    
     /**
     * Determines the view boundary then sets the camera
     * Sets the view
     * To zoom in the user location
     */
    private void setCameraView() {

        // Set a boundary to start  
        // the main user location 
        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - .1;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - .1;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + .1;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }
    
    

}
