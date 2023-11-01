package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.cache.DiskLruCache;

public class Set_Location extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    //Google Map

    private GoogleMap myMap;

    //Search Location
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private PlacesAdapter adapter;
    private EditText edtSearch;

    Button button_location;
    Button pop_manual_location;

    Button Create_account, Create_account2;
    TextView textView_location;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Member Account Information");

    Dialog myDialog;
    LocationManager locationManager;

    LatLng current_location;

    FirebaseAuth auth;
    FirebaseUser user;

    //
    @Override
    public void onBackPressed() {
        // Do nothing to prevent going back
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        current_location = new LatLng(3.1319, 101.6841);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDialog = new Dialog(Set_Location.this);
        pop_manual_location = findViewById(R.id.btnSearchLocation);

        pop_manual_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.setContentView(R.layout.manual_location_popup);
                myDialog.show();

                Create_account2 = myDialog.findViewById(R.id.createacct2);
                Create_account2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notebookRef.whereEqualTo("email", user.getEmail())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        auth = FirebaseAuth.getInstance();
                                        user = auth.getCurrentUser();
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Note_member note = documentSnapshot.toObject(Note_member.class);
                                            if(note.getLocation() == null){
                                                Toast.makeText(Set_Location.this, "Please Select a Location to proceed.", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(Set_Location.this, "Location Set", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Set_Location.this, member_home_pg.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                    }
                });

                String apikey = "AIzaSyD7vNMkwErfevruq1TPtFDxIGMAQOSgptc";
                if (!Places.isInitialized()) {
                    Places.initialize(myDialog.getContext(),apikey);
                }

                placesClient = Places.createClient(myDialog.getContext());
                sessionToken = AutocompleteSessionToken.newInstance();

                edtSearch = myDialog.findViewById(R.id.Searchbar);
                ListView listPlaces = myDialog.findViewById(R.id.ListPlaces);

                adapter = new PlacesAdapter(myDialog.getContext());
                listPlaces.setAdapter(adapter);
                listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (adapter.getCount() > 0) {
                            detailPlace(adapter.predictions.get(position).getPlaceId());
                        }
                    }
                });
                edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if(actionId == EditorInfo.IME_ACTION_SEARCH){
                            if(edtSearch.length() > 0) {
                                searchPlaces();
                            }
                        }
                        return false;
                    }
                });
            }
        });

        //Prevent Swipe Back
        View rootLayout = findViewById(R.id.RootLayout);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // Consume the touch events
            }
        });

        //Current Location
        button_location = findViewById(R.id.btncurrentLocation);
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(Set_Location.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Set_Location.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        Create_account = findViewById(R.id.createacct);
        Create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notebookRef.whereEqualTo("email", user.getEmail())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                auth = FirebaseAuth.getInstance();
                                user = auth.getCurrentUser();
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Note_member note = documentSnapshot.toObject(Note_member.class);
                                    if(note.getLocation() == null){
                                        Toast.makeText(Set_Location.this, "Please Select a Location to proceed.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(Set_Location.this, "Location Set", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Set_Location.this, member_home_pg.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });




    }

    //Search Location Functions
    private void searchPlaces(){
        final LocationBias bias = RectangularBounds.newInstance(
                new LatLng(22.458744, 88.208162),
                new LatLng(22.730671, 88.524896)
        );

        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(sessionToken)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(edtSearch.getText().toString())
                .setLocationBias(bias)
                .setCountries("MY")
                .build();

        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                List<AutocompletePrediction> predictions = findAutocompletePredictionsResponse.getAutocompletePredictions();
                adapter.setPredictions(predictions);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ApiException){
                    ApiException apiException = (ApiException) e;
                    Log.e("Set_Location","Place Not Found" + apiException.getStatusCode());
                }
            }
        });
    }

    private void detailPlace(String placeId){
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                LatLng latlng = place.getLatLng();
                if (latlng != null){
                    GeoPoint geoPoint = new GeoPoint(latlng.latitude, latlng.longitude);
                    notebookRef.whereEqualTo("email", user.getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Note_member note = documentSnapshot.toObject(Note_member.class);
                                        note.setLocation(geoPoint);
                                        String documentId = documentSnapshot.getId(); // Get the document ID
                                        notebookRef.document(documentId).set(note)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(myDialog.getContext(), "Failed to set Location", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                    Toast.makeText(myDialog.getContext(), "Location Set", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException){
                    final ApiException apiException = (ApiException) e;
                    Log.e("Set_Location", "Place Not Found:" + e.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.addMarker(new MarkerOptions().position(current_location).title("Current Location"));
        // Set the camera position to the desired location and zoom level
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current_location)
                .zoom(15) // Adjust the zoom level as needed (higher values for closer zoom)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void updateMapLocation(LatLng newLocation) {
        if (myMap != null) {
            myMap.clear(); // Clear existing markers

            // Add new marker
            myMap.addMarker(new MarkerOptions().position(newLocation).title("Current Location"));

            // Move camera to new location
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(current_location)
                    .zoom(15) // Adjust the zoom level as needed (higher values for closer zoom)
                    .build();

            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private static class PlacesAdapter extends BaseAdapter{
        private final List<AutocompletePrediction> predictions = new ArrayList<>();
        private final Context context;

        public PlacesAdapter(Context context) {
            this.context = context;
        }

        public void setPredictions(List <AutocompletePrediction> predictions){
            this.predictions.clear();
            this.predictions.addAll(predictions);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return predictions.size();
        }

        @Override
        public Object getItem(int i) {
            return predictions.get(i);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false);
            TextView txtShortAddress = v.findViewById(R.id.txtShortAddress);
            TextView txtLongAddress =  v.findViewById(R.id.txtLongAddress);

            txtShortAddress.setText(predictions.get(position).getPrimaryText(null));
            txtLongAddress.setText(predictions.get(position).getSecondaryText(null));
            return v;
        }
    }

    //Current Location Methods
    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,Set_Location.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onLocationChanged(Location location) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();

            current_location = new LatLng(latitude, longitude);
            updateMapLocation(current_location);
            notebookRef.whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member note = documentSnapshot.toObject(Note_member.class);
                                note.setLocation(geoPoint);
                                String documentId = documentSnapshot.getId(); // Get the document ID
                                notebookRef.document(documentId).set(note)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(myDialog.getContext(), "Location Set, please click Create Account", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(myDialog.getContext(), "Failed to set Location", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
            Geocoder geocoder = new Geocoder(Set_Location.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            textView_location = findViewById(R.id.locationText);
            textView_location.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}