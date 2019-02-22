/* 1st : add this depenacny in build.gradle
--------------------------------------------------- */
// Google Maps Services (needed for directions)
    implementation 'com.google.maps:google-maps-services:0.2.9'
    implementation 'org.slf4j:slf4j-nop:1.7.25'
    
/* 2nd : in Map Fragment 
--------------------------------------------------- */
private GeoApiContext mGeoApiContext;
    
// put this in init map method
if(mGeoApiContext == null){
    mGeoApiContext = new GeoApiContext.Builder()
             .apiKey(getString(R.string.google_maps_api_key))
             .build();
}

//this method takes marker to get the destination GeoPoint
private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");
        
        //my destination
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(   
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        
        // to get more than on route 
        directions.alternatives(true);  
        
        // my location
        directions.origin(    
                new com.google.maps.model.LatLng(
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                //Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
               // Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
               //this method will be found in the 4th step
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }
    
    
/* 3rd : for On info click (inside the MapFragment)
--------------------------------------------------- */  
implements GoogleMap.OnInfoWindowClickListener
// this for marker on snippet click " remember that u made it say 'Determine route to ?'"
@Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            //this method will be created in the 7th step to show the other hidden marker 
                            resetSelectedMarker();
                            //this marker will be created in the 6th step to hide this marker later 
                            mSelectedMarker = marker;
                            // if he says yes pass the marker to calculateDirections method   
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }
    
/* 4th : create new model for polyline
--------------------------------------------------- */ 
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolylineData {

    private Polyline polyline;
    private DirectionsLeg leg;

    public PolylineData(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    @Override
    public String toString() {
        return "PolylineData{" +
                "polyline=" + polyline +
                ", leg=" + leg +
                '}';
    }
}
    
/* 5th : for adding Tirp line (poly line)
--------------------------------------------------- */  
private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();

private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                //to make sure that we don't add duplicate polyline
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    // list to store all the polyline
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                 //if the tempDuration is smaller than duration highlight it 
                 //for each loop the duration will change till it reach the lowest polyline
                 // once it reach it will highlight it as the smallest route        
                        duration = tempDuration;   
                        onPolylineClick(polyline);
                        //Zoom in on the selected route
                        //i think we should call this inside the onPolyline click
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }
// zoom in on the selected route
public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

/* 6th : for adding on poly line click
--------------------------------------------------- */  
implements GoogleMap.OnPolylineClickListener
//this Marker is made to hide the custom marker when the target marker is created
private Marker mSelectedMarker = null;
//to store the new EndPoint Marker 
private ArrayList<Marker> mTripMarkers = new ArrayList<>();

@Override
public void onMapReady(GoogleMap map) {
  mGoogleMap.setOnPolylineClickListener(this);
    }
    
 @Override
 public void onPolylineClick(Polyline polyline) {
        //indicate the trip number 
        int index = 0;
        // loop in the polyline Array we created and fill in the step 4 & 5
        for(PolylineData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            //if the polyline clicked equals to one of my arraylist's polylines
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                //change it's color to blue
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                //highlight it (make it higher than other polyline)
                polylineData.getPolyline().setZIndex(1);

                //get the endpoint latlng
                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );
                //make new marker for the endpoint
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));
                //store the endpoint marker
                mTripMarkers.add(marker);

                // to show the info window once the new marker is created
                marker.showInfoWindow(); 
                
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

/* 7th : remove endpoint Marker and set the custom marker to visiable 
------------------------------------------------------------------------- */ 
//this method will be called onInfoWindowClick when selecting new marker 
private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }
