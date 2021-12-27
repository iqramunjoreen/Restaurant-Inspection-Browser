package com.sfu.cmpt276groupproject.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Cluster Item Helper
 */
public class RestaurantItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final float hazardColor;

    public RestaurantItem(LatLng pos, String title, float color) {
        this.position = pos;
        this.title = title;
        this.snippet = null;
        this.hazardColor = color;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public float getHazardColor(){return hazardColor;}
}
