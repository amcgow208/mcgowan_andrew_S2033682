//
// Name                 Andrew McGowan
// Student ID           S2033682
// Programme of Study   Computing
//

package org.me.gcu.mdcw1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EarthquakeDetailsFragment extends Fragment implements OnMapReadyCallback {

    private Earthquake earthquake;
    private MapView mapView;
    private GoogleMap googleMap;

    public EarthquakeDetailsFragment() {
        // Required empty public constructor
    }

    public static EarthquakeDetailsFragment newInstance(Earthquake earthquake) {
        EarthquakeDetailsFragment fragment = new EarthquakeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("earthquake", earthquake);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            earthquake = (Earthquake) getArguments().getSerializable("earthquake");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_details, container, false);

        // Initialize back button and set onClickListener
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        displayEarthquakeDetails(view);
        return view;
    }

    private void displayEarthquakeDetails(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvLink = view.findViewById(R.id.tv_link);
        TextView tvPubDate = view.findViewById(R.id.tv_pubDate);

        tvTitle.setText(earthquake.getTitle());
        tvDescription.setText(earthquake.getDescription());
        tvLink.setText(earthquake.getLink());
        tvPubDate.setText(earthquake.getPubDate());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng earthquakeLocation = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(earthquakeLocation).title(earthquake.getTitle()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(earthquakeLocation, 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
