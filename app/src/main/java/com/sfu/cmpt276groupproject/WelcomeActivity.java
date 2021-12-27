package com.sfu.cmpt276groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sfu.cmpt276groupproject.Model.FavoriteDB;
import com.sfu.cmpt276groupproject.Model.Restaurant;
import com.sfu.cmpt276groupproject.Model.RestaurantManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * UI for checking data updated screen
 */
public class WelcomeActivity extends AppCompatActivity {
    public static RequestQueue queue;

    public static String LAST_UPDATED_DATE = "LastUpdatedDate";
    public static String LAST_MODIFIED_DATE = "LastModifiedDate";

    public volatile boolean exit = false;

    public static int counter = 0;

    public static boolean flag = true;

    public String restaurantCSVUrl;
    public Date restaurantLastModifiedDate;

    public String inspectionCSVUrl;
    public Date inspectionLastModifiedDate;

    public ProgressDialog progressDialog;

    public static final String URL_RESTAURANT = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    public static final String URL_INSPECTION = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));

        queue = Volley.newRequestQueue(WelcomeActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATED_DATE,0);
        int tempCounter = sharedPreferences.getInt("UpdatedCounter", 0);

        if (tempCounter == 0 && flag) {
            updateData();
        } else if (!flag) {
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("isUpdated", false);
            editor1.commit();
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            String temp = sharedPreferences.getString("GetTime","0");
            long time = Long.parseLong(temp);
            Log.i("UpdatedTime", "last updated time: " + temp);


//            // test case about more than 20 hours
//            String testString = "2021-01-01 00:00:00";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date testDate = null;
//            try {
//                 testDate = simpleDateFormat.parse(testString);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            long timeDifference = (testDate.getTime() - time) / (60 * 60 * 1000);


            long timeDifference = (new Date().getTime() - time) / (60 * 60 * 1000);
            Log.i("TimeDiff", "Time difference:" + timeDifference);
            if (timeDifference >= 20.0) {
                updateData();
            } else {
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean("isUpdated", false);
                editor1.commit();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    private void updateData() {
        // Code reference: [https://www.youtube.com/watch?v=y2xtLqP8dSQ&t=467s].
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, URL_RESTAURANT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject temp = response.getJSONObject("result");
                    JSONArray jsonArray = temp.getJSONArray("resources");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject resource = jsonArray.getJSONObject(i);
                        if (resource.getString("format").equals("CSV")) {
                            restaurantCSVUrl = resource.getString("url");
                            String lastModified = resource.getString("last_modified");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            restaurantLastModifiedDate = simpleDateFormat.parse(lastModified);
                            Log.i("URLRestaurant", "URL: " + restaurantCSVUrl);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request1);

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, URL_INSPECTION, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject temp = response.getJSONObject("result");
                    JSONArray jsonArray = temp.getJSONArray("resources");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject resource = jsonArray.getJSONObject(i);
                        if (resource.getString("format").equals("CSV")) {
                            inspectionCSVUrl = resource.getString("url");
                            String lastModified = resource.getString("last_modified");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            inspectionLastModifiedDate = simpleDateFormat.parse(lastModified);
                            Log.i("URLInspection", "URL: " + inspectionCSVUrl);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request2);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // If it's been 20 hours or more since data was last updated, need to check the data of the server is modified or not
                SharedPreferences sharedPreferences1 = getSharedPreferences(LAST_MODIFIED_DATE, 0);

//                // test case about the modified date on the server is updated
//                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                try {
//                    Date testRestaurantDate = simpleDateFormat.parse("2021-07-07T21:54:05.241280");
//                    Date testInspectionDate = simpleDateFormat.parse("2021-07-07T21:54:05.241280");
//                    boolean check1 = String.valueOf(testRestaurantDate).equals(
//                            sharedPreferences1.getString("Restaurant", ""));
//                    boolean check2 = String.valueOf(testInspectionDate).equals(
//                            sharedPreferences1.getString("Inspection", ""));
//                    if (check1 && check2) {
//                        startActivity(new Intent(WelcomeActivity.this, MapsActivity.class));
//                    } else {
//                        dialog();
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

                boolean check1 = String.valueOf(restaurantLastModifiedDate).equals(
                        sharedPreferences1.getString("Restaurant", ""));
                boolean check2 = String.valueOf(inspectionLastModifiedDate).equals(
                        sharedPreferences1.getString("Inspection", ""));
                if (check1 && check2) {
                    SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATED_DATE, 0);
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean("isUpdated", false);
                    editor1.commit();
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                } else {
                    dialog();
                }
            }
        }, 1000);

    }

    private void dialog() {
        Builder builder = new Builder(WelcomeActivity.this);
        builder.setTitle(R.string.updated_alert_title);
        builder.setMessage(R.string.updated_alert_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.updated_alert_ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog = new ProgressDialog(WelcomeActivity.this);
                progressDialog.setMessage(getString(R.string.progress_dialog_msg));

                progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        exit = true;
                    }
                });

                progressDialog.show();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            downloadData();
                            if (exit) {
                                flag = false;
                                SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATED_DATE, 0);
                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.putBoolean("isUpdated", false);
                                editor1.commit();
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                finish();
                            } else {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                final Thread thread = new Thread(runnable);
                thread.start();
            }
        });


        builder.setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag = false;
                dialog.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATED_DATE, 0);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean("isUpdated", false);
                editor1.commit();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.create().show();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {// Update the shared preferences about user's updated time
                SharedPreferences sharedPreferences1 = getSharedPreferences(LAST_UPDATED_DATE, 0);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("GetTime", String.valueOf(new Date().getTime()));
                int counter = sharedPreferences1.getInt("UpdatedCounter", 0);
                counter++;
                editor1.putInt("UpdatedCounter", counter);
                editor1.putBoolean("isUpdated", true);
                editor1.commit();

                // Update the shared preferences about last modified date of the server data
                SharedPreferences sharedPreferences2 = getSharedPreferences(LAST_MODIFIED_DATE, 0);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString("Restaurant", String.valueOf(restaurantLastModifiedDate));
                editor2.putString("Inspection", String.valueOf(inspectionLastModifiedDate));
                editor2.commit();
                backupData();
                progressDialog.dismiss();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
            super.handleMessage(msg);
        }

        private void backupData() {
            try {
                String str;
                String fileName = "used_restaurants.csv";
                FileInputStream inputStream = openFileInput("updated_restaurants.csv");
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                    writer.flush();
                }
                writer.close();
                outputStream.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String str;
                String fileName = "used_inspections.csv";
                FileInputStream inputStream = openFileInput("updated_inspections.csv");
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                    writer.flush();
                }
                writer.close();
                outputStream.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void downloadData() {
        try {
            URL url = new URL(restaurantCSVUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            String fileName = "updated_restaurants.csv";
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            while ((str = in.readLine()) != null) {
                writer.write(str + "\n");
                writer.flush();
            }
            writer.close();
            outputStream.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(inspectionCSVUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            String fileName = "updated_inspections.csv";
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            while ((str = in.readLine()) != null) {
                writer.write(str + "\n");
                writer.flush();
            }
            writer.close();
            outputStream.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }



}