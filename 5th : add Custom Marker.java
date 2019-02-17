/* 1st : at menifest add this :
--------------------------------*/
// Maps utils (required for custom markers)
implementation 'com.google.maps.android:android-maps-utils:0.5+'
  
/*-----------------------------------------------------------------------------
 2nd : create a ClusterMarker class :
-----------------------------------------*/  
// this is a simple model class   
public class ClusterMarker implements ClusterItem {

    private LatLng position; // required field for the marker location 
    private String title; // required field for the title on the marker 
    private String snippet; // required field for the text under title on the marker
    private String iconPicture;  // use int if the image is Drawable in the app
    private User user;

    public ClusterMarker(LatLng position, String title, String snippet, String iconPicture, User user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.user = user;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(String iconPicture) {
        this.iconPicture = iconPicture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}

/*-----------------------------------------------------------------------------
 3rd : create a ClusterManagerRenderer class :
-------------------------------------------------*/   
// this class is responsible for creating and showing the Marker on the map 
  public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;  
    private final ImageView imageView; 
    private final int markerWidth;  
    private final int markerHeight;
    private Context ctx;  // i used this to set the image in glide or picasso

    public MyClusterManagerRenderer(Context context, GoogleMap googleMap,
                                    ClusterManager<ClusterMarker> clusterManager) {

        super(context, googleMap, clusterManager);
        ctx = context; 
        // initialize cluster item icon generator
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);  // 50dp
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);  // 50dp
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);  // 2dp
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);   // set the imageview 

    }

    /**
     * Rendering of the individual ClusterItems
     *
     */
    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        //imageView.setImageResource(item.getIconPicture());  // this for drawable image
        Glide.with(ctx).load(item.getIconPicture()).into(imageView);  //this for online image 
        //picasso.with(context).load(item.getIconPicture()).fit().into(imageview 3)   //optional
        Bitmap icon = iconGenerator.makeIcon();  
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }


    // da 3lshan lw fe 2 icon gnb b3d ylz2 sorthom fe marker wa7d m3mlnhash fa 5laha false 
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }
}
  
/*-----------------------------------------------------------------------------
 4th : create a addMapMarkers() method inside the MapFragment :
---------------------------------------------------------------*/ 
//but first add this inside the fragment 
private ClusterManager<ClusterMarker> mClusterManager;
private MyClusterManagerRenderer mClusterManagerRenderer;
private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    private void addMapMarkers(){
    /* this method purpose is to loop inside the list of user and create the Custom marker for them
    */
        if(mGoogleMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            // looping through user location
            for(UserLocation userLocation: mUserLocations){

                Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
                try{
                    String snippet = "";
                    if(userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                        snippet = "This is you";
                    }
                    else{
                        snippet = "Determine route to " + userLocation.getUser().getUsername() + "?";
                    }

                    String avatar = "http// image url"; // set the default avatar
                    try{
                       // avatar = Integer.parseInt(userLocation.getUser().getAvatar());  // if the image is drwable
                      avatar = userLocation.getUser().getAvatar(); //the image url from the userlocation
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
                    }
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();

            setCameraView();
        }
    }
/*-----------------------------------------------------------------------------
 5th : how to use this method on map fragment :
---------------------------------------------------------------*/ 

 @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();
      
        mGoogleMap = map;
        addMapMarkers();
    }

