package com.sfu.cmpt276groupproject.Model;

import android.content.Context;

import com.sfu.cmpt276groupproject.R;

/**
 * This enum is used to represent the Hazard levels
 */
public enum Hazard {
    LOW(R.string.hazard_low_text, R.drawable.low_hazard_icon, R.color.lowHazardColor),
    MODERATE(R.string.hazard_moderate_text, R.drawable.moderate_hazard_icon, R.color.moderateHazardColor),
    HIGH(R.string.hazard_high_text, R.drawable.high_hazard_icon, R.color.highHazardColor),
    NO_HAZARD(R.string.hazard_no_text, 0, R.color.testColor);

    int hazardStringResource;
    int hazardImageResource;
    int hazardColorResource;
    Hazard(int hazardStringResource, int hazardImageResource, int hazardColorResource) {
        this.hazardStringResource = hazardStringResource;
        this.hazardImageResource = hazardImageResource;
        this.hazardColorResource = hazardColorResource;
    }

    /* Returns the Hazard string from a Hazard */
    public String getHazardString(Context context) {
        return context.getString(hazardStringResource);
    }
    /* Returns the Hazard image resource from a Hazard */
    public int getHazardImageResource() {
        return hazardImageResource;
    }
    /* Returns the Hazard color from a Hazard */
    public int getHazardColor(Context context) {
        return context.getResources().getColor(hazardColorResource);
    }

    public static Hazard toHazard(String hazardString) {
        if (hazardString.equalsIgnoreCase("low")) {
            return LOW;
        } else if (hazardString.equalsIgnoreCase("moderate")) {
            return MODERATE;
        } else if (hazardString.equalsIgnoreCase("high")) {
            return HIGH;
        }
        return NO_HAZARD;
    }
}
