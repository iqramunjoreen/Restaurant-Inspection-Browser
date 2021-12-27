package com.sfu.cmpt276groupproject.Model;

import android.content.Context;
import android.util.Log;


import com.sfu.cmpt276groupproject.R;
import com.sfu.cmpt276groupproject.WelcomeActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Restaurant Manager class: interface between the UI (activities) and the Model (Restaurant data)
 */
public class RestaurantManager {
    Context context;
    private FavoriteDB favoriteList;
    private List<Restaurant> restaurants = new ArrayList<>();
    private List<Restaurant> searchList = new ArrayList<>();
    boolean hasSearched;

    private RestaurantManager(Context context) {
        this.context = context;
        this.favoriteList = new FavoriteDB(context);
        this.hasSearched = false;
        initializeData();
        searchList.addAll(restaurants);
    }

    /* Singleton code */
    private static RestaurantManager instance;
    public static RestaurantManager getInstance(Context context) throws FileNotFoundException {
        if (instance == null) {
            instance = new RestaurantManager(context);
        }
        return instance;
    }

    /* Regular class code */
    // returns the List of Restaurants sorted alphabetically by Restaurant name
    public List<Restaurant> getRestaurants() {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getName().compareTo(r2.getName());
            }
        });
        return restaurants;
    }
    public List<Restaurant> getSearchRestaurants() {
        Collections.sort(searchList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getName().compareTo(r2.getName());
            }
        });
        return searchList;
    }

    public void setSearchList(List<Restaurant> searchList) {
        this.searchList.clear();
        this.searchList.addAll(searchList);
    }

    // returns the Restaurant at specified index of Restaurant list
    public Restaurant getRestaurant(int index) {
        return getRestaurants().get(index);
    }

    public Restaurant getSearchRestaurant(int index) {
        return getSearchRestaurants().get(index);
    }

    // returns the list of Inspections sorted by date (most recent->least recent)
    public List<Inspection> getInspections(String trackingNumber) {
        for (Restaurant restaurant : searchList) {
            if (trackingNumber.equals(restaurant.getTrackingNumber())) {
                List<Inspection> inspections = restaurant.getInspections();
                Collections.sort(inspections, new Comparator<Inspection>() {
                    @Override
                    public int compare(Inspection i1, Inspection i2) {
                        return i2.getDate().compareTo(i1.getDate());
                    }
                });
                return inspections;
            }
        }
        return null;
    }

    // returns the list of Inspections sorted by date (most recent->least recent)
    public List<Inspection> getInspections(Restaurant restaurant) {
        List<Inspection> inspections = restaurant.getInspections();
        Collections.sort(inspections, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection i1, Inspection i2) {
                return i2.getDate().compareTo(i1.getDate());
            }
        });
        return inspections;
    }

    public Restaurant findRestaurant(String trackingNumber) {
        for (Restaurant restaurant : restaurants) {
            if (trackingNumber.equals(restaurant.getTrackingNumber())) {
                return restaurant;
            }
        }
        return null;
    }
    public Restaurant findSearchRestaurant(String trackingNumber) {
        for (Restaurant restaurant : searchList) {
            if (trackingNumber.equals(restaurant.getTrackingNumber())) {
                return restaurant;
            }
        }
        return null;
    }

    /* Initializes RestaurantManager with Restaurant and Inspection data from the .csv files */
    private void initializeData() {
        // read all Restaurants from .csv file and store them

        // if-case: initially install with the small data set (from iteration 1)
        if (WelcomeActivity.counter == 0) {
            InputStream is = context.getResources().openRawResource(R.raw.restaurants_iteration1);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            readRestaurant(reader);

            // read all Inspections from .csv file and store them in their respective Restaurant
            is = context.getResources().openRawResource(R.raw.inspections_iteration1);
            reader = new BufferedReader(new InputStreamReader(is));
            readInspection(reader, true);

            // else-case: newly install with the data set from server
        } else {
            try {
                FileInputStream is = context.openFileInput("used_restaurants.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                readRestaurant(reader);
                is.close();

                is = context.openFileInput("used_inspections.csv");
                reader = new BufferedReader(new InputStreamReader(is));
                readInspection(reader, false);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readInspection(BufferedReader reader, boolean flag) {
        String line;
        line = "";
        try {
            // don't use the first line, which is just column titles
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // get violations from the line into a String
                String violations = null;
                int startViolationsIndex = line.indexOf("\"");  // the double quote begins violations
                if (startViolationsIndex != -1) {
                    if (flag) {
                        violations = line.substring(startViolationsIndex + 1, line.length() - 1); // do not include the endIndex
                        line = line.substring(0, startViolationsIndex); // remove violations from line since it's in a separate String variable
                    } else {
                        violations = line.substring(startViolationsIndex + 1);
                        String[] temp = violations.split(",");
                        line = line.substring(0, startViolationsIndex);
                        line = line + temp[temp.length - 1].trim();
                        violations = temp[0];
                        for (int i = 1; i < temp.length - 1; i++) {
                            violations = violations + "," + temp[i];
                        }
                        violations = violations.substring(0, violations.length() - 1);
                    }
                }
                String[] columns = line.split(",");

                if (columns.length != 0) {
                    String trackingNumber = columns[0].trim();
                    String inspectionDate = columns[1].trim();
                    String inspectionType = columns[2].trim();
                    int numCritical = Integer.parseInt(columns[3].trim());
                    int numNonCritical = Integer.parseInt(columns[4].trim());
                    Hazard hazard = null;
                    if (flag) {
                        hazard = Hazard.toHazard(columns[5].trim());
                    } else {
                        hazard = Hazard.toHazard(columns[columns.length - 1].trim());
                    }
                    String[] allViolations;
                    List<Violation> listOfViolations = new ArrayList<>();
                    String shortDescription;
                    String longDescription;
                    String severity;
                    String[] partsViolation;
                    String remainder;
                    if (violations != null) {
                        allViolations = violations.split("[|]");
                        for (String allViolation : allViolations) {
                            partsViolation = allViolation.split(",");
                            shortDescription = partsViolation[0];
                            severity = partsViolation[1];
                            remainder = allViolation.substring(allViolation.indexOf(",") + 1, allViolation.length());
                            remainder = remainder.substring(remainder.indexOf(",") + 1, remainder.length());
                            longDescription = remainder;
                            Violation violation = new Violation(shortDescription, longDescription, severity);
                            listOfViolations.add(violation);
                        }
                    }
                    Inspection inspection = new Inspection(trackingNumber, inspectionDate,
                            inspectionType, numCritical, numNonCritical, hazard, listOfViolations);

                    Restaurant restaurant = findRestaurant(trackingNumber);
                    if (restaurant != null) {
                        restaurant.addInspection(inspection);
//                        Log.d("Tag","Added: " + inspection);
                    } else {
                        Log.d("TAG", "Restaurant not found");
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            Log.wtf("TAG", "Error reading inspection .csv file on line: " + line);
            e.printStackTrace();
        }
    }



    private void readRestaurant(BufferedReader reader) {
        String line = "";
        try {
            // don't use the first line, which is just column titles
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");

                String trackingNumber = null;
                String restaurantName = "";
                String restaurantAddress = null;
                String restaurantCity = null;
                String factoryType = null;
                double latitude = (double) 0;
                double longitude = (double) 0;

                if (columns.length > 7) {
                    trackingNumber = columns[0].trim();
                    longitude = Double.parseDouble(columns[columns.length - 1]);
                    latitude = Double.parseDouble(columns[columns.length - 2]);
                    factoryType = columns[columns.length - 3];
                    restaurantCity = columns[columns.length - 4];
                    restaurantAddress = columns[columns.length - 5];

                    for (int i = 1; i < columns.length - 5; i++) {
                        if (i == 1) {
                            restaurantName = restaurantName + columns[i];
                        } else {
                            restaurantName = restaurantName + "," + columns[i];
                        }
                    }
                    restaurantName = restaurantName.substring(1, restaurantName.length() - 1);
                } else {
                    trackingNumber = columns[0].trim();
                    restaurantName = columns[1];
                    restaurantAddress = columns[2];
                    restaurantCity = columns[3];
                    factoryType = columns[4];
                    latitude = Double.parseDouble(columns[5]);
                    longitude = Double.parseDouble(columns[6]);
                }

                boolean isFavorite = false;
                if(favoriteList.isFound(trackingNumber)){
                    isFavorite = true;
                }

                // create the restaurant and add it to the manager
                Restaurant restaurant = new Restaurant(trackingNumber, restaurantName,
                        restaurantAddress, restaurantCity, factoryType, latitude, longitude, isFavorite);
                restaurants.add(restaurant);
            }
            reader.close();

        } catch(IOException e) {
            Log.wtf("TAG", "Error reading restaurant .csv file on line: " + line);
            e.printStackTrace();
        }
    }
}
