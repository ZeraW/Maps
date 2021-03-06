/* 1st : create new class ViewWeightAnimationWrapper 
-----------------------------------------------------*/
public class ViewWeightAnimationWrapper {
    private View view;

    public ViewWeightAnimationWrapper(View view) {
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.view = view;
        } else {
            throw new IllegalArgumentException("The view should have LinearLayout as parent");
        }
    }

    public void setWeight(float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.getParent().requestLayout();
    }

    public float getWeight() {
        return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    }
}

/*------------------------------------------------------------------------------------------------
2nd : add this to mapFragment xml file 
-----------------------------------------------------*/
<RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="50"
            android:id="@+id/map_container">

            <com.google.android.gms.maps.MapView
                xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:id="@+id/user_list_map" />
      // add this image btn
            <ImageButton
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:src="@drawable/ic_full_screen_black_24dp"
                android:background="@color/White"
                android:layout_alignParentRight="true"
                android:layout_alignParentEnd="true"
                android:layout_marginTop="10dp"
                android:layout_marginRight="10dp"
                android:layout_marginEnd="10dp"
                android:id="@+id/btn_full_screen_map"/>

        </RelativeLayout>
        
// ic_full_screen_black_24dp.xml    the image used
<vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="24dp"
        android:height="24dp"
        android:viewportWidth="24.0"
        android:viewportHeight="24.0">
    <path
        android:fillColor="#FF000000"
        android:pathData="M5,15L3,15v4c0,1.1 0.9,2 2,2h4v-2L5,19v-4zM5,5h4L9,3L5,3c-1.1,0 -2,0.9 -2,2v4h2L5,5zM19,3h-4v2h4v4h2L21,5c0,-1.1 -0.9,-2 -2,-2zM19,19h-4v2h4c1.1,0 2,-0.9 2,-2v-4h-2v4zM12,8c-2.21,0 -4,1.79 -4,4s1.79,4 4,4 4,-1.79 4,-4 -1.79,-4 -4,-4zM12,14c-1.1,0 -2,-0.9 -2,-2s0.9,-2 2,-2 2,0.9 2,2 -0.9,2 -2,2z"/>
</vector>
             
/*------------------------------------------------------------------------------------------------
3rd : add this to mapFragment
-----------------------------------------------------*/
// make the class implement  View.OnClickListener

private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
private RelativeLayout mMapContainer;


onCreateView(){
view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
mMapContainer = view.findViewById(R.id.map_container);
}


private void expandMapAnimation(){
                                                                            // the map container
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,    // start from weight 50
                100); // end at weight 100
        mapAnimation.setDuration(800);
                                                                             //the other view that will disappear due to map expansion 
        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50, // start from weight 50
                0); // end at weight 0
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }
        }
    }
