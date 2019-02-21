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
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }
    
    
/* 3rd : in Map Fragment 
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
