package com.sfu.cmpt276groupproject.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sfu.cmpt276groupproject.Model.DateCalculator;
import com.sfu.cmpt276groupproject.Model.Hazard;
import com.sfu.cmpt276groupproject.Model.Inspection;
import com.sfu.cmpt276groupproject.Model.Restaurant;
import com.sfu.cmpt276groupproject.R;

import java.text.ParseException;
import java.util.List;

/**
 * Dialog of favourite list when finishing updating
 */
// Code reference: [https://blog.csdn.net/sakurakider/article/details/80735400]
public class ListDialog extends Dialog implements View.OnClickListener {
    private Context context;
    ArrayAdapter<Restaurant> adapter;
    List<Restaurant> list;

    public ListDialog(Context context, List<Restaurant> list) {
        super(context);
        this.context = context;
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window dialogWindow = getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER);
        setContentView(R.layout.favourite_popup_dialog);

        populateListView();
        setupButton();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 9 / 10;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    private void setupButton() {
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void populateListView() {
        adapter = new ListDialog.ListAdapter();
        ListView list = findViewById(R.id.restaurantList);
        list.setAdapter(adapter);
    }

    private OnCenterItemClickListener listener;
    public interface OnCenterItemClickListener {
        void OnCenterItemClick(ListDialog dialog, View view);
    }
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    private class ListAdapter extends ArrayAdapter<Restaurant> {
        ListAdapter() {
            super(context, R.layout.favourite_popup_dialog, list);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.favourite_list_item, parent, false);
            }

            Restaurant currentRestaurant = list.get(position);

            // Name:
            TextView nameText = itemView.findViewById(R.id.restaurant_name);
            nameText.setText(currentRestaurant.getName());

            // Get the most recent inspection
            if (list.get(position).getInspections().size() != 0) {
                Inspection inspection = list.get(position).getInspections().get(0);

                // Hazard icon and level:
                ImageView hazardImageView = itemView.findViewById(R.id.hazard_image);
                TextView levelView = itemView.findViewById(R.id.hazard_level);
                Hazard currentHazard = inspection.getHazard();
                hazardImageView.setImageResource(currentHazard.getHazardImageResource());
                levelView.setText(currentHazard.getHazardString(getContext()));
                levelView.setTextColor(currentHazard.getHazardColor(getContext()));

                // Date:
                String date = null;
                try {
                    date = DateCalculator.changeDateToString(getContext(), inspection);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TextView dateText = itemView.findViewById(R.id.restaurant_date);

                // Change the date to other languages
                assert date != null;
                if (!date.contains(context.getString(R.string.days_ago))) {
                    if (date.contains("January")) {
                        date = date.replace("January", context.getString(R.string.january));
                    } else if (date.contains("February")) {
                        date = date.replace("February", context.getString(R.string.february));
                    } else if (date.contains("March")) {
                        date = date.replace("March", context.getString(R.string.march));
                    } else if (date.contains("April")) {
                        date = date.replace("April", context.getString(R.string.april));
                    } else if (date.contains("May")) {
                        date = date.replace("May", context.getString(R.string.may));
                    } else if (date.contains("June")) {
                        date = date.replace("June", context.getString(R.string.june));
                    } else if (date.contains("July")) {
                        date = date.replace("July", context.getString(R.string.july));
                    } else if (date.contains("August")) {
                        date = date.replace("August", context.getString(R.string.august));
                    } else if (date.contains("September")) {
                        date = date.replace("September", context.getString(R.string.september));
                    } else if (date.contains("October")) {
                        date = date.replace("October", context.getString(R.string.october));
                    } else if (date.contains("November")) {
                        date = date.replace("November", context.getString(R.string.november));
                    } else if (date.contains("December")) {
                        date = date.replace("December", context.getString(R.string.december));
                    }
                }

                dateText.setText(String.format(context.getString(R.string.updated_date), date));
            } else {
                // Hazard icon and level:
                ImageView hazardImageView = itemView.findViewById(R.id.hazard_image);
                TextView levelView = itemView.findViewById(R.id.hazard_level);
                TextView dateText = itemView.findViewById(R.id.restaurant_date);
                hazardImageView.setImageResource(0);
                levelView.setText("");
                dateText.setText("");
            }

            return itemView;
        }

    }
    @Override
    public void onClick(View v) {
        dismiss();
        listener.OnCenterItemClick(this, v);
    }
}