package za.co.shadow.material.activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import za.co.shadow.maps.LocationProvider;
import za.co.shadow.maps.MapsActivity;
import za.co.shadow.material.R;

public class LocationFragment extends Fragment implements
        LocationProvider.LocationCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAehORbYkgLVAC8tagNlCcdMvGJfgNEQIU";

    public static final String TAG = LocationFragment.class.getSimpleName();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationProvider mLocationProvider;
    private EditText edtAddress; //edtSignup_first_name
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView autoCompView;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_address, container, false);
        SetupMap();
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        AddMarkertoMap(latLng, "This is your current location");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void AddMarkertoMap(LatLng latlng, String markerText){
        MarkerOptions options = new MarkerOptions()
                .position(latlng)
                .title(markerText)
                .draggable(true);
        mMap.clear();
        mMap.addMarker(options);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            MapFragment  mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googleMap);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        //DO WHATEVER YOU WANT WITH GOOGLEMAP
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
//        map.setTrafficEnabled(true);
//        map.setIndoorEnabled(true);
//        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        mMap = map;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                AddMarkertoMap(mMap.getCameraPosition().target, "This is your current screen coordinates");
            }
        });

        mLocationProvider = new LocationProvider(getActivity(), this);
        mLocationProvider.connect();

        autoCompView = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchAddress("");
            }
        });

        Button btnFindMyLocation = (Button)getView().findViewById(R.id.btnLocationPin);
        btnFindMyLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getMyLocation();
            }
        });

    };

    protected void SetupMap() {

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        else
        {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            mLocationProvider.connect();
        }
        setUpMapIfNeeded();
        getMyLocation();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        // More about this in the 'Handle Connection Failures' section.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    private void getMyLocation()
    {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            AddMarkertoMap(latLng, "This is your current location");
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 5);
                if (addresses.size() > 0) {

                    String address = addresses.get(0).getAddressLine(0) + ", " +
                            addresses.get(0).getAddressLine(1) + ", " +
                            addresses.get(0).getCountryName();
                    autoCompView.setOnItemClickListener(null);
                    edtAddress = (EditText)getView().findViewById(R.id.autoCompleteTextView);
                    edtAddress.setText(address);
                    autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            searchAddress("");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    public void searchAddress(String input)
    {
        String address = input;
        /* get latitude and longitude from the adderress */
        if (address == "") {
            edtAddress = (EditText)getView().findViewById(R.id.autoCompleteTextView);
            address = edtAddress.getText().toString();
        }
        if (address != "") {
            Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocationName(address, 5);
                if (addresses.size() > 0) {

                    Double lat = (double) (addresses.get(0).getLatitude());
                    Double lon = (double) (addresses.get(0).getLongitude());

                    Log.d("lat-long", "" + lat + "......." + lon);
                    final LatLng FoundLocation = new LatLng(lat, lon);

                    AddMarkertoMap(FoundLocation, "This is the location that you searched");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(FoundLocation)      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:za");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new Filter.FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mLocationProvider.disconnect();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googleMap);
        if (mapFragment != null)
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
    }
}
