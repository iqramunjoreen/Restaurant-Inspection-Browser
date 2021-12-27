package com.sfu.cmpt276groupproject.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sfu.cmpt276groupproject.Model.DateCalculator;
import com.sfu.cmpt276groupproject.Model.FavoriteDB;
import com.sfu.cmpt276groupproject.Model.Hazard;
import com.sfu.cmpt276groupproject.Model.Inspection;
import com.sfu.cmpt276groupproject.Model.Restaurant;
import com.sfu.cmpt276groupproject.Model.RestaurantManager;
import com.sfu.cmpt276groupproject.R;
import com.sfu.cmpt276groupproject.RestaurantDetailsActivity;
import com.sfu.cmpt276groupproject.SearchActivity;
import com.sfu.cmpt276groupproject.WelcomeActivity;

import java.io.FileNotFoundException;
import java.text.ParseException;

/**
 * UI Fragment for Restaurant List Screen
 */
public class RestaurantListFragment extends Fragment {

    public RestaurantManager manager;
    ArrayAdapter<Restaurant> adapter;
    ListView list;

    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance() {
        RestaurantListFragment fragment = new RestaurantListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WelcomeActivity.LAST_UPDATED_DATE, 0);
            WelcomeActivity.counter = sharedPreferences.getInt("UpdatedCounter", 0);
            manager = RestaurantManager.getInstance(getActivity().getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateListView();
        registerClickCallbacks();
    }

    // Create a list view
    private void populateListView() {
        adapter = new ListAdapter();
        ListView list = getActivity().findViewById(R.id.restaurantList);
        list.setAdapter(adapter);
    }

    // Register clicker for every restaurant
    private void registerClickCallbacks() {
        list = getActivity().findViewById(R.id.restaurantList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = RestaurantDetailsActivity.makeRestaurantIntent(getActivity(), position);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        populateListView();
        registerClickCallbacks();
    }

    // an inner class for self-defining arrayAdapter
    private class ListAdapter extends ArrayAdapter<Restaurant> {
        ListAdapter() {
            super(getActivity(), R.layout.restaurant_item, manager.getSearchRestaurants());
        }

        /*public void updateList(ArrayAdapter<Restaurant> ){
            notifyDataSetChanged();
        }*/

        @SuppressLint("StringFormatInvalid")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_item, parent, false);
            }

            Restaurant currentRestaurant = manager.getSearchRestaurant(position);

            // Restaurant icon:
            ImageView restaurantImageView = itemView.findViewById(R.id.list_image);
            if (currentRestaurant.getName().startsWith("7-Eleven")) {
                restaurantImageView.setImageResource(R.drawable.seven_eleven_icon);
            } else if (currentRestaurant.getName().startsWith("A&W") ||
                    currentRestaurant.getName().startsWith("A & W")) {
                restaurantImageView.setImageResource(R.drawable.a_w_icon);
            } else if (currentRestaurant.getName().startsWith("Bento")) {
                restaurantImageView.setImageResource(R.drawable.bento_icon);
            } else if (currentRestaurant.getName().startsWith("Blenz")) {
                restaurantImageView.setImageResource(R.drawable.blenz_icon);
            } else if (currentRestaurant.getName().startsWith("Booster Juice")) {
                restaurantImageView.setImageResource(R.drawable.booster_juice_icon);
            } else if (currentRestaurant.getName().startsWith("Boston Pizza")) {
                restaurantImageView.setImageResource(R.drawable.boston_pizza_icon);
            } else if (currentRestaurant.getName().startsWith("Browns Socialhouse")) {
                restaurantImageView.setImageResource(R.drawable.browns_socialhouse_icon);
            } else if (currentRestaurant.getName().startsWith("Burger King")) {
                restaurantImageView.setImageResource(R.drawable.burger_king_icon);
            } else if (currentRestaurant.getName().startsWith("Church's Chicken")) {
                restaurantImageView.setImageResource(R.drawable.churchs_chicken_icon);
            } else if (currentRestaurant.getName().startsWith("Circle K")) {
                restaurantImageView.setImageResource(R.drawable.circle_k_icon);
            } else if (currentRestaurant.getName().startsWith("COBS Bread")) {
                restaurantImageView.setImageResource(R.drawable.cobs_bread_icon);
            } else if (currentRestaurant.getName().startsWith("D-Plus Pizza")) {
                restaurantImageView.setImageResource(R.drawable.d_plus_pizza_icon);
            } else if (currentRestaurant.getName().startsWith("De Duthc")) {
                restaurantImageView.setImageResource(R.drawable.de_duthc_icon);
            } else if (currentRestaurant.getName().startsWith("Domino's Pizza")) {
                restaurantImageView.setImageResource(R.drawable.dominos_pizza_icon);
            } else if (currentRestaurant.getName().startsWith("Elements Casino")) {
                restaurantImageView.setImageResource(R.drawable.elements_casino_icon);
            } else if (currentRestaurant.getName().startsWith("Fraserview Meats")) {
                restaurantImageView.setImageResource(R.drawable.fraserview_meats_icon);
            } else if (currentRestaurant.getName().startsWith("Freshii")) {
                restaurantImageView.setImageResource(R.drawable.freshii_icon);
            } else if (currentRestaurant.getName().startsWith("Freshlice")) {
                restaurantImageView.setImageResource(R.drawable.freshlice_icon);
            } else if (currentRestaurant.getName().startsWith("KFC")) {
                restaurantImageView.setImageResource(R.drawable.kfc_icon);
            } else if (currentRestaurant.getName().startsWith("Kids & Company")) {
                restaurantImageView.setImageResource(R.drawable.kids_company_icon);
            } else if (currentRestaurant.getName().startsWith("Little Caesars Pizza")) {
                restaurantImageView.setImageResource(R.drawable.little_caesars_pizza_icon);
            } else if (currentRestaurant.getName().startsWith("Mac's Convenience Store")) {
                restaurantImageView.setImageResource(R.drawable.macs_convenience_store_icon);
            } else if (currentRestaurant.getName().startsWith("McDonald's")) {
                restaurantImageView.setImageResource(R.drawable.mcdonalds_icon);
            } else if (currentRestaurant.getName().startsWith("Nando's Chicken")) {
                restaurantImageView.setImageResource(R.drawable.nandos_chicken_icon);
            } else if (currentRestaurant.getName().startsWith("Panago")) {
                restaurantImageView.setImageResource(R.drawable.panago_icon);
            } else if (currentRestaurant.getName().startsWith("Pizza Hut")) {
                restaurantImageView.setImageResource(R.drawable.pizza_hut_icon);
            } else if (currentRestaurant.getName().startsWith("Quesada")) {
                restaurantImageView.setImageResource(R.drawable.quesada_icon);
            } else if (currentRestaurant.getName().startsWith("Royal Canadian Legion Branch")) {
                restaurantImageView.setImageResource(R.drawable.royal_canadian_legion_branch_icon);
            } else if (currentRestaurant.getName().startsWith("Safeway")) {
                restaurantImageView.setImageResource(R.drawable.safeway_icon);
            } else if (currentRestaurant.getName().startsWith("Save On Foods")) {
                restaurantImageView.setImageResource(R.drawable.save_on_foods_icon);
            } else if (currentRestaurant.getName().startsWith("Starbucks Coffee")) {
                restaurantImageView.setImageResource(R.drawable.starbucks_icon);
            } else if (currentRestaurant.getName().startsWith("Subway")) {
                restaurantImageView.setImageResource(R.drawable.subway_icon);
            } else if (currentRestaurant.getName().startsWith("Tim Horton's") ||
                    currentRestaurant.getName().startsWith("Tim Hortons")) {
                restaurantImageView.setImageResource(R.drawable.tim_hortons_icon);
            } else if (currentRestaurant.getName().startsWith("T&T")) {
                restaurantImageView.setImageResource(R.drawable.tnt_icon);
            } else if (currentRestaurant.getName().startsWith("Wendy's")) {
                restaurantImageView.setImageResource(R.drawable.wendys_icon);
            } else if (currentRestaurant.getName().startsWith("White Spot")) {
                restaurantImageView.setImageResource(R.drawable.white_spot_icon);
            } else {
                restaurantImageView.setImageResource(R.drawable.restaurant_store_icon);
            }

            //Favorite Mark
            ImageView favoriteIcon = itemView.findViewById(R.id.isfavorite_icon);
            if(currentRestaurant.isFavorite()){
                favoriteIcon.setImageResource(R.drawable.favorite_icon);
            }
            else{
                favoriteIcon.setImageResource(R.drawable.add_favorite);
            }


            // Name:
            TextView nameText = itemView.findViewById(R.id.restaurant_name);
            nameText.setText(currentRestaurant.getName());

            // Get the most recent inspection
            if (manager.getInspections(currentRestaurant).size() != 0) {
                Inspection inspection = manager.getInspections(currentRestaurant).get(0);

                // Number of issues:
                TextView issueText = itemView.findViewById(R.id.issue_num);
                int counter = 0;
                counter = inspection.getNumberOfCritical() + inspection.getNumberOfNonCritical();
                issueText.setText(String.format(getString(R.string.last_inspection_issues), counter));

                // Hazard icon and level:
                ImageView hazardImageView = itemView.findViewById(R.id.hazard_image);
                TextView levelView = itemView.findViewById(R.id.hazard_level);
                Hazard currentHazard = inspection.getHazard();
                hazardImageView.setImageResource(currentHazard.getHazardImageResource());
                levelView.setText(currentHazard.getHazardString(getActivity()));
                levelView.setTextColor(currentHazard.getHazardColor(getActivity()));

                // Date:
                String date = null;
                try {
                    date = DateCalculator.changeDateToString(getActivity(), inspection);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TextView dateText = itemView.findViewById(R.id.date);

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

                dateText.setText(String.format(getString(R.string.updated_date), date));
            } else {
                TextView issueText = itemView.findViewById(R.id.issue_num);
                issueText.setText(R.string.no_issues);
                // Hazard icon and level:
                ImageView hazardImageView = itemView.findViewById(R.id.hazard_image);
                TextView levelView = itemView.findViewById(R.id.hazard_level);
                TextView dateText = itemView.findViewById(R.id.date);
                hazardImageView.setImageResource(0);
                levelView.setText("");
                dateText.setText("");
            }

            return itemView;
        }

    }

}