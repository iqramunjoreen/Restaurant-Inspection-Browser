package com.sfu.cmpt276groupproject.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.sfu.cmpt276groupproject.Model.Hazard;
import com.sfu.cmpt276groupproject.Model.Inspection;
import com.sfu.cmpt276groupproject.Model.RestaurantItem;
import com.sfu.cmpt276groupproject.Model.Restaurant;
import com.sfu.cmpt276groupproject.Model.RestaurantManager;
import com.sfu.cmpt276groupproject.R;
import com.sfu.cmpt276groupproject.RestaurantDetailsActivity;
import com.sfu.cmpt276groupproject.SearchActivity;
import com.sfu.cmpt276groupproject.WelcomeActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

/**
 * UI Fragment for Map Screen
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<RestaurantItem> mClusterManager;
    private ClusterRenderer mRenderer;

    private int selectIndex;
    private boolean justEntered = true;
    public RestaurantManager manager;
    List<ClusterItem> items = new ArrayList<>();

    private boolean needsInit = false;

    public MapFragment() {
        this.selectIndex = selectIndex;
    }

    public MapFragment(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment(-1);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            manager = RestaurantManager.getInstance(getActivity().getApplicationContext());
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WelcomeActivity.LAST_UPDATED_DATE, 0);
            boolean checker = sharedPreferences.getBoolean("isUpdated", false);
            List<Restaurant> favourites = new ArrayList<>();
            if (checker) {
                for (int i = 0; i < manager.getRestaurants().size(); i++) {
                    Restaurant temp = manager.getRestaurants().get(i);
                    if (temp.isFavorite()) {
                        favourites.add(temp);
                    }
                }
            }
            if (favourites.size() != 0) {
                for (int i = 0; i < favourites.size(); i++) {
                    Log.i("Favourite: ", favourites.get(i).getName());
                }
                ListDialog dialog;
                dialog = new ListDialog(getActivity(), favourites);
                dialog.show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            needsInit = true;
        }
        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpClusterManager();
        selectMarker();

        if (hasLocationPermission()) {
            setUserLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @SuppressLint("MissingPermission")
    public void setUserLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("TAG", "justEntered = " + justEntered + " ----- selectIndex = " + selectIndex);
                if (justEntered && selectIndex == -1) {
                    justEntered = false;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15), 3000, null);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "eeee");
        if (requestCode == 1) {
            Log.d("TAG", "dddd");
            if (hasLocationPermission()) {
                Log.d("TAG", "cccc");
                setUserLocation();
            }
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private float getPegColor(Restaurant restaurant) {
        if(manager.getInspections(restaurant).size() == 0) {
            return HUE_RED;
        }
        else{
            Inspection latestInspection = manager.getInspections(restaurant).get(0);
            Hazard hazardLv = latestInspection.getHazard();
            float[] hsv = new float[3];
            Color.colorToHSV(hazardLv.getHazardColor(getActivity()), hsv);
            return hsv[0];
        }
    }

    public void selectMarker() {
        Log.d("TAG", "select index = " + selectIndex);
        if (selectIndex != -1) {
            final RestaurantItem item = (RestaurantItem) items.get(selectIndex);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item.getPosition(), 15), 1500, null);
            mRenderer.showMarker(item);
        }
    }

    private void setUpClusterManager() {
        mClusterManager = new ClusterManager<RestaurantItem>(getActivity(), mMap);

        mMap.setMinZoomPreference(10);
        LatLng restPosition = null;
        for (final Restaurant restaurant : manager.getSearchRestaurants()) {
            restPosition = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            float[] hsv = new float[3];
            hsv[0] = HUE_RED;
            if(manager.getInspections(restaurant).size() != 0) {
                int color = manager.getInspections(restaurant).get(0).getHazard().getHazardColor(getActivity());
                Color.colorToHSV(color, hsv);
            }

            RestaurantItem item = new RestaurantItem(restPosition, restaurant.getName() + restaurant.getAddress(), hsv[0]);
            mClusterManager.addItem((RestaurantItem) item);
            items.add(item);
        }
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(restPosition));


        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(mClusterManager.getMarkerManager());
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<RestaurantItem>() {
            @Override
            public void onClusterItemInfoWindowClick(RestaurantItem item) {
                for (int i = 0; i < manager.getSearchRestaurants().size(); i++) {
                    Restaurant temp = manager.getSearchRestaurant(i);
                    if (item.getTitle().equals(temp.getName() + temp.getAddress())) {
                        Intent intent = RestaurantDetailsActivity.makeRestaurantIntent(getActivity(), i);
                        startActivity(intent);
                    }
                }
            }
        });
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View itemView = getLayoutInflater().inflate(R.layout.map_restaurant_item, null);

                Restaurant currentRestaurant = null;
                for (int i = 0; i < manager.getSearchRestaurants().size(); i++) {
                    Restaurant temp = manager.getSearchRestaurant(i);
                    String title = marker.getTitle();
                    if (title == null) {
                        Log.d("TAG", "NULL");
                    }
                    Log.d("TAG", title);
                    if (marker.getTitle().equals(temp.getName() + temp.getAddress())) {
                        currentRestaurant = manager.getSearchRestaurant(i);

                        // Name:
                        TextView nameText = itemView.findViewById(R.id.restaurant_name);
                        nameText.setText(currentRestaurant.getName());

                        // Address:
                        TextView addressText = itemView.findViewById(R.id.restaurant_address);
                        addressText.setText(currentRestaurant.getAddress());

                        // Hazard icon and level:
                        ImageView hazardImageView = itemView.findViewById(R.id.hazard_image);
                        TextView levelView = itemView.findViewById(R.id.hazard_level);
                        if (currentRestaurant.getInspections().size() != 0) {
                            Hazard currentHazard = currentRestaurant.getInspections().get(0).getHazard();
                            hazardImageView.setImageResource(currentHazard.getHazardImageResource());
                            levelView.setText(currentHazard.getHazardString(getActivity()));
                            levelView.setTextColor(currentHazard.getHazardColor(getActivity()));
                        } else {
                            hazardImageView.setImageResource(0);
                            levelView.setText("");
                        }
                    }
                }

                return itemView;
            }
        });

        mRenderer = new ClusterRenderer(getActivity(), mMap, mClusterManager, mMap.getCameraPosition().zoom, 16);
        mClusterManager.setRenderer(mRenderer);
        mMap.setOnCameraMoveListener(mRenderer);
        mClusterManager.cluster();
    }
}

