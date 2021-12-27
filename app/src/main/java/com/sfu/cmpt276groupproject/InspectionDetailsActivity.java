package com.sfu.cmpt276groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.cmpt276groupproject.Model.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * UI for the Inspection Details screen
 */
public class InspectionDetailsActivity extends AppCompatActivity {

    private static final String RESTAURANT_INDEX = "Restaurant index";
    private static final String INSPECTION_INDEX = "inspection index";

    private String[] permitViolationId = {"101", "102", "103", "104", "311"};
    private String[] certificateViolationId = {"501", "502"};
    private String[] foodViolationId = {"201", "202", "203", "204", "205", "206", "208", "209", "210", "211", "212"};
    private String[] equipmentViolationId = {"301", "302", "303", "306", "307", "308", "310", "312", "315", "313"};
    private String[] employeeViolationId = {"401", "402", "403", "404", "314"};
    private String[] pestViolationId = {"304", "305"};
    private String[] cleanViolationId = {"309"};

    Restaurant restaurant;
    RestaurantManager manager;
    List<Inspection> inspections;
    Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "InspectDetails onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);

        handleIntent();
        setupInspectionInfo();
        populateListView();
        registerClickCallbacks();
    }

    private void handleIntent() {
        Intent intent = this.getIntent();
        int restaurant_index = intent.getIntExtra(RESTAURANT_INDEX, -1);
        int inspection_index = intent.getIntExtra(INSPECTION_INDEX, -1);
        try {
            manager = RestaurantManager.getInstance(this.getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        restaurant = manager.getRestaurant(restaurant_index);
        inspections = restaurant.getInspections();
        inspection = inspections.get(inspection_index);
    }

    public static Intent makeInspectionIntent(Context context, int InspectionIndex, int RestaurantIndex){
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        intent.putExtra(INSPECTION_INDEX, InspectionIndex);
        intent.putExtra(RESTAURANT_INDEX, RestaurantIndex);
        return intent;
    }

    private void registerClickCallbacks() {
        ListView list = findViewById(R.id.violation_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = view.findViewById(R.id.severity);
                Toast.makeText(getApplicationContext(), inspection.getViolations().get(position).getLongDescription(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new ListAdapter();
        ListView list = findViewById(R.id.violation_list);
        list.setAdapter(adapter);
    }

    @SuppressLint("StringFormatInvalid")
    private void setupInspectionInfo() {
        //Name
        TextView restaurantName = findViewById(R.id.Name);
        restaurantName.setText(String.format("%s", restaurant.getName()));

        //type
        TextView inspectionType = findViewById(R.id.Type);
        String type = inspection.getType();
        inspectionType.setText(String.format("%s%s", getString(R.string.inspection_type), type));

        //Date
        String date = null;
        try {
            date = DateCalculator.getFullDate(inspection.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView dateText = findViewById(R.id.Update);

        // Change the date to other languages
        assert date != null;
        if (!date.contains(getString(R.string.days_ago))) {
            if (date.contains("January")) {
                date = date.replace("January", getString(R.string.january));
            } else if (date.contains("February")) {
                date = date.replace("February", getString(R.string.february));
            } else if (date.contains("March")) {
                date = date.replace("March", getString(R.string.march));
            } else if (date.contains("April")) {
                date = date.replace("April", getString(R.string.april));
            } else if (date.contains("May")) {
                date = date.replace("May", getString(R.string.may));
            } else if (date.contains("June")) {
                date = date.replace("June", getString(R.string.june));
            } else if (date.contains("July")) {
                date = date.replace("July", getString(R.string.july));
            } else if (date.contains("August")) {
                date = date.replace("August", getString(R.string.august));
            } else if (date.contains("September")) {
                date = date.replace("September", getString(R.string.september));
            } else if (date.contains("October")) {
                date = date.replace("October", getString(R.string.october));
            } else if (date.contains("November")) {
                date = date.replace("November", getString(R.string.november));
            } else if (date.contains("December")) {
                date = date.replace("December", getString(R.string.december));
            }
        }

        dateText.setText(String.format(getString(R.string.date), date));

        //Set Hazard Icon & text
        ImageView hazardImageView = findViewById(R.id.Hazard_image);
        TextView levelView = findViewById(R.id.Hazard_level);
        Hazard currentHazard = inspection.getHazard();
        hazardImageView.setImageResource(currentHazard.getHazardImageResource());
        levelView.setText(currentHazard.getHazardString(InspectionDetailsActivity.this));
        levelView.setTextColor(currentHazard.getHazardColor(InspectionDetailsActivity.this));

        //number of issues
        TextView critical = findViewById(R.id.Critical_issue);
        TextView nonCritical = findViewById(R.id.Non_critical_issue);
        int criticalCount;
        int nonCriticalCount;
        criticalCount = inspection.getNumberOfCritical();
        nonCriticalCount = inspection.getNumberOfNonCritical();
        critical.setText(String.format(getString(R.string.critical_issues), criticalCount));
        critical.setTextColor(getResources().getColor(R.color.criticalColor));
        nonCritical.setText(String.format(getString(R.string.non_critical_issues), nonCriticalCount));
        nonCritical.setTextColor(getResources().getColor(R.color.nonCriticalColor));

    }

    private class ListAdapter extends ArrayAdapter<Violation> {
        public ListAdapter(){
            super(InspectionDetailsActivity.this, R.layout.violation_item, inspection.getViolations());
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(convertView == null){
                itemView = getLayoutInflater().inflate(R.layout.violation_item, parent, false);
            }

            List<Violation> ViolationList = inspection.getViolations();
            //If inspection has violations
            if(ViolationList.size() != 0){

                Violation currentViolation = ViolationList.get(position);

                //set violation icon
                ImageView violation_icon = itemView.findViewById(R.id.violation_icon);
                String violationId = currentViolation.getShortDescription();
                if (Arrays.asList(foodViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.food_icon);
                } else if (Arrays.asList(pestViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.pest_icon);
                } else if (Arrays.asList(equipmentViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.equipment_icon);
                } else if(Arrays.asList(permitViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.permit_icon);
                } else if(Arrays.asList(cleanViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.clean_icon);
                } else if(Arrays.asList(certificateViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.certificate_icon);
                } else if (Arrays.asList(employeeViolationId).contains(violationId)) {
                    violation_icon.setImageResource(R.drawable.employee_icon);
                }

                //set short description
                TextView shortDescription = itemView.findViewById(R.id.short_description);
                shortDescription.setText(String.format(getString(R.string.violation), currentViolation.getShortDescription()));

                //set severity icon and level
                ImageView severityIcon = itemView.findViewById(R.id.severity_icon);
                TextView severity = itemView.findViewById(R.id.severity);
                String currentSeverity = currentViolation.getSeverity();
                if (currentSeverity.equalsIgnoreCase("not critical")){
                    severityIcon.setImageResource(R.drawable.non_critical_icon);
                    severity.setText(currentViolation.getSeverity());
                    severity.setTextColor(getResources().getColor(R.color.nonCriticalColor));
                }
                else if (currentSeverity.equalsIgnoreCase("critical")){
                    severityIcon.setImageResource(R.drawable.critical_icon);
                    severity.setText(currentViolation.getSeverity());
                    severity.setTextColor(getResources().getColor(R.color.criticalColor));
                }
            } else {
                TextView issueText = itemView.findViewById(R.id.Update);
                issueText.setText(R.string.no_violation);
            }

            return itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}