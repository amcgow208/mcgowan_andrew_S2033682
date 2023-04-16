//
// Name                 Andrew McGowan
// Student ID           S2033682
// Programme of Study   Computing
//

package org.me.gcu.mdcw1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;




public class EarthquakeListFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private ListView earthquakeListView;
    private Button startButton;
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private MainActivity mainActivity;

    private List<Earthquake> earthquakeList = new ArrayList<>();

    private EarthquakeListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        mainActivity = (MainActivity) getActivity();
        earthquakeListView = view.findViewById(R.id.earthquake_list);
        startButton = view.findViewById(R.id.startButton);

        startButton.setOnClickListener(this);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = (Earthquake) parent.getItemAtPosition(position);
                showEarthquakeDetails(earthquake);
            }
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        Spinner sortSpinner = view.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(mainActivity,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortOption = parent.getItemAtPosition(position).toString();
                sortEarthquakeList(selectedSortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button datePickerButton = view.findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });


        RadioGroup feedRadioGroup = view.findViewById(R.id.feed_radio_group);
        RadioButton britishFeedRadioButton = view.findViewById(R.id.british_feed_radio_button);
        RadioButton worldFeedRadioButton = view.findViewById(R.id.world_feed_radio_button);

        britishFeedRadioButton.setChecked(true); // Set the default value to British Isles feed
        feedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.british_feed_radio_button) {
                    urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
                } else if (checkedId == R.id.world_feed_radio_button) {
                    urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.startButton) {
            startProgress();
        }
    }

    public void startProgress() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Earthquake> earthquakes = fetchEarthquakes(urlSource);
                if (earthquakes != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateEarthquakeList(earthquakes);
                        }
                    });
                } else {
                    Log.e("FetchTask", "No earthquakes found");
                }
            }
        });
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Earthquake> fetchEarthquakes(String urlSource) {
        BufferedReader reader = null;
        List<Earthquake> earthquakes = null;

        try {
            URL url = new URL(urlSource);
            URLConnection conn = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder xmlData = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                xmlData.append(line);
            }

            EarthquakeXmlParser parser = new EarthquakeXmlParser();
            earthquakes = EarthquakeXmlParser.parseXmlData(xmlData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return earthquakes;
    }


    private void updateEarthquakeList(List<Earthquake> earthquakes) {
        earthquakeList = earthquakes;
        adapter = new EarthquakeListAdapter(getActivity(), earthquakes);
        earthquakeListView.setAdapter(adapter);
    }

    private void showEarthquakeDetails(Earthquake earthquake) {
        EarthquakeDetailsFragment detailsFragment = EarthquakeDetailsFragment.newInstance(earthquake);
        mainActivity.loadFragment(detailsFragment);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        filterList(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterList(newText);
        return true;
    }

    private void filterList(String query) {
        if (earthquakeListView.getAdapter() instanceof EarthquakeListAdapter) {
            EarthquakeListAdapter adapter = (EarthquakeListAdapter) earthquakeListView.getAdapter();
            adapter.getFilter().filter(query);
        }
    }
    private void sortEarthquakeList(String sortOptionString) {
        if (earthquakeList != null && adapter != null) {
            Log.d("EarthquakeListFragment", "Sorting earthquake list using option: " + sortOptionString);
            adapter.sortEarthquakes(sortOptionString);
            adapter.notifyDataSetChanged(); // Add this line to update the ListView after sorting
        } else {
            Log.e("EarthquakeListFragment", "Unable to sort: earthquakeList or adapter is null");
        }
    }

    private void showDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getChildFragmentManager(), "date_picker");

        picker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selected range to Date objects
            Date startDate = new Date(selection.first);
            Date endDate = new Date(selection.second);
            filterEarthquakeList(startDate, endDate);
        });
    }


    private void filterEarthquakeList(Date startDate, Date endDate) {
        List<Earthquake> filteredList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK);

        for (Earthquake earthquake : earthquakeList) {
            try {
                Date earthquakeDate = dateFormat.parse(earthquake.getPubDate());
                if (earthquakeDate != null && earthquakeDate.compareTo(startDate) >= 0 && earthquakeDate.compareTo(endDate) <= 0) {
                    filteredList.add(earthquake);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        adapter.updateEarthquakes(filteredList);
    }






}