package com.sfu.cmpt276groupproject.UI;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.sfu.cmpt276groupproject.Model.RestaurantItem;

/**
 * Cluster Class
 */
// Code based off of: https://stackoverflow.com/questions/32909260/disable-clustering-at-max-zoom-level-with-googles-android-maps-utils
public class ClusterRenderer extends DefaultClusterRenderer<RestaurantItem> implements GoogleMap.OnCameraMoveListener {

    private final GoogleMap mMap;
    private float currentZoom, maxZoom;
    private boolean showMarker = false;
    private RestaurantItem item = null;

    // this is used for showing the marker after you click on the coordinates in Restaurant Details
    // After entering the Map Fragment, it zooms into the restaurant, and then when the marker is
    // finished rendering (in onClusterItemRendered), the info window is displayed
    public void showMarker(RestaurantItem item) {
        this.showMarker = true;
        this.item = item;
    }

    @Override
    protected void onClusterItemRendered(@NonNull RestaurantItem clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        if(showMarker && getMarker(item) != null && marker.equals(getMarker(item))) {
            marker.showInfoWindow();
            showMarker = false;
        }
        //Log.d("TAG", "marker added");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBeforeClusterItemRendered(@NonNull RestaurantItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        //Log.d("TAG", "before render");
        float hazardColor = item.getHazardColor();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hazardColor));
    }

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<RestaurantItem> clusterManager, float currentZoom, float maxZoom) {
        super(context, map, clusterManager);

        this.mMap = map;
        this.currentZoom = currentZoom;
        this.maxZoom = maxZoom;
    }

    @Override
    public void onCameraMove() {
        currentZoom = mMap.getCameraPosition().zoom;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<RestaurantItem> cluster) {
        // determine if superclass would cluster first, based on cluster size
        boolean superWouldCluster = super.shouldRenderAsCluster(cluster);

        // smaller clusters show their markers at a lower zoom level (so you don't have to zoom all the way in to just see 5 restaurants)
        if (superWouldCluster) {
            if (cluster.getSize() > 10) {
                superWouldCluster = currentZoom < maxZoom;
            } else {
                superWouldCluster = currentZoom < (maxZoom - 2);
            }
        }

        return superWouldCluster;
    }

}