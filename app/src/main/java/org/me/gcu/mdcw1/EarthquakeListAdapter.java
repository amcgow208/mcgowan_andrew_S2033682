//
// Name                 Andrew McGowan
// Student ID           S2033682
// Programme of Study   Computing
//

package org.me.gcu.mdcw1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {

    private List<Earthquake> earthquakes;
    public EarthquakeListAdapter(@NonNull Context context, @NonNull List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
        this.earthquakes = earthquakes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_earthquake, parent, false);
        }

        Earthquake earthquake = getItem(position);

        TextView earthquakeTitle = convertView.findViewById(R.id.earthquake_title);
        TextView earthquakePubDate = convertView.findViewById(R.id.earthquake_pub_date);
        View magnitudeColor = convertView.findViewById(R.id.color_square);

        earthquakeTitle.setText(earthquake.getTitle());
        earthquakePubDate.setText(earthquake.getPubDate());

        float magnitude = earthquake.getMagnitude();
        if (magnitude < 1) {
            magnitudeColor.setBackgroundResource(R.drawable.border_green);
        } else if (magnitude >= 1 && magnitude <= 2) {
            magnitudeColor.setBackgroundResource(R.drawable.border_orange);
        } else {
            magnitudeColor.setBackgroundResource(R.drawable.border_red);
        }


        return convertView;
    }

    public void sortEarthquakes(String sortOptionString) {
        Comparator<Earthquake> comparator = null;

        switch (sortOptionString) {
            case "Magnitude (Highest to Lowest)":
                comparator = (e1, e2) -> Double.compare(e2.getMagnitude(), e1.getMagnitude());
                break;
            case "Magnitude (Lowest to Highest)":
                comparator = (e1, e2) -> Double.compare(e1.getMagnitude(), e2.getMagnitude());
                break;
            case "Date (Newest to Oldest)":
                comparator = (e1, e2) -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK);
                    try {
                        Date date1 = dateFormat.parse(e1.getPubDate());
                        Date date2 = dateFormat.parse(e2.getPubDate());
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            case "Date (Oldest to Newest)":
                comparator = (e1, e2) -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK);
                    try {
                        Date date1 = dateFormat.parse(e1.getPubDate());
                        Date date2 = dateFormat.parse(e2.getPubDate());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            default:
                break;
        }

        if (comparator != null) {
            Collections.sort(earthquakes, comparator);
        }
    }

    public void updateEarthquakes(List<Earthquake> earthquakes) {
        clear();
        addAll(earthquakes);
        notifyDataSetChanged();
    }


}
