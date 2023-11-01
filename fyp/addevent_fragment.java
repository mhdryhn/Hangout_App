package com.example.fyp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addevent_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addevent_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private addevent_fragment.PlacesAdapter adapter;
    private EditText edtSearch;

    Dialog myDialog;
    LocationManager locationManager;
    Button pop_manual_location,Create_account2;
    GeoPoint location;


    //Variables for Date Picker
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    //Variables for TimePicker
    private Button timeButton;
    int hour, minute;
    int dayOfMonth, month, year;

    //DropBox Variables
    String category;

    ArrayAdapter <String> adapterItems;

    long selectedTimeInMillis;

    //To get user email
    FirebaseUser user;
    FirebaseAuth auth;

    //Image Variables
    private Uri imageUri;
    private StorageReference storageRef;
    TextView label;

    String imageurl;


    //Variables for returning data to database
    private Button createEventbutton;
    private static final String TAG = "add_eventfragment ";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Event Information");
    private DocumentReference noteRef = db.document("Event Information/New Event");
    EditText evn_text, mxm_text, addinfo_text;
    public addevent_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addevent_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static addevent_fragment newInstance(String param1, String param2) {
        addevent_fragment fragment = new addevent_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addevent_fragment, container, false);
        initDatePicker();

        myDialog = new Dialog(view.getContext());
        pop_manual_location = view.findViewById(R.id.btnSearchLocation);

        pop_manual_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.setContentView(R.layout.event_location_set);
                myDialog.show();

                String apikey = "AIzaSyD7vNMkwErfevruq1TPtFDxIGMAQOSgptc";
                if (!Places.isInitialized()) {
                    Places.initialize(myDialog.getContext(),apikey);
                }

                placesClient = Places.createClient(myDialog.getContext());
                sessionToken = AutocompleteSessionToken.newInstance();

                edtSearch = myDialog.findViewById(R.id.Searchbar);
                ListView listPlaces = myDialog.findViewById(R.id.ListPlaces);

                adapter = new addevent_fragment.PlacesAdapter(myDialog.getContext());
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

        //Initializing Storage Reference for Image
        storageRef = FirebaseStorage.getInstance().getReference().child("Images");

        String[] languages = getResources().getStringArray(R.array.Categories);
        adapterItems = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item, languages);
        AutoCompleteTextView dropDownBox = view.findViewById(R.id.DropDownBox);
        dropDownBox.setAdapter(adapterItems);


        //Time picker On click Listener
        timeButton = view.findViewById(R.id.Event_Time_tb);
        getTodaysTime();
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(view);
            }
        });

        //Date picker On click Listener
        dateButton = view.findViewById(R.id.Event_Date_tb);
        dateButton.setText(getTodaysdate());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(view);
            }
        });



        //DropDown
        dropDownBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }
        });

        //ImageView
        ImageView previewimage = view.findViewById(R.id.ImagePreview);
        label = view.findViewById(R.id.selectimage);
        final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        label.setVisibility(view.INVISIBLE);
                        imageUri = result;
                        previewimage.setImageURI(result);
                    }
                }
        );

        previewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch("image/*");
            }
        });

        //Saving Info Onclick listener
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        createEventbutton = view.findViewById(R.id.btncreateevent);
        createEventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Returning Data to Firebase
                evn_text = view.findViewById(R.id.Event_Name_tb);
                mxm_text = view.findViewById(R.id.Max_members_tb);
                addinfo_text = view.findViewById(R.id.Additional_Info_tb);
                String eventname, additional_info, date, time, email;
                long time_val;

                int max_members;
                eventname = String.valueOf(evn_text.getText());
                additional_info = String.valueOf(addinfo_text.getText());
                date = String.valueOf(dateButton.getText());
                time = String.valueOf(timeButton.getText());
                email = String.valueOf(user.getEmail());


                if (mxm_text.length() == 0){
                    mxm_text.setText("0");
                }
                max_members = Integer.parseInt(mxm_text.getText().toString());

                //Check if Fields are Empty
                if (TextUtils.isEmpty(eventname)) {
                    Toast.makeText(getActivity(), "Please Enter your Event Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(additional_info)) {
                    Toast.makeText(getActivity(), "Please add additional information for member", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(getActivity(), "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (max_members == 0) {
                    Toast.makeText(getActivity(), "Please provide the maximum number of members (within 100)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(time)) {
                    Toast.makeText(getActivity(), "Please select a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if maximum numbers of member is exceeded.
                if(max_members > 100){
                    Toast.makeText(getActivity(), "The maximum number of members is 100.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (location == null) {
                    Toast.makeText(getActivity(), "Please select a location by clicking the button Below", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(imageUri == null) {
                    Toast.makeText(getActivity(), "Please select a banner for your event.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert the selected time into milliseconds
                Calendar calendar = Calendar.getInstance();

                Log.d("Selected Time1", "Hour: " + hour + ", Minute: " + minute);
                calendar.set(year, month, dayOfMonth, hour, minute);
                selectedTimeInMillis = calendar.getTimeInMillis();


                //Getting the Image URL and Saving the Information
                storageRef = storageRef.child(System.currentTimeMillis() + "");
                if (imageUri != null) {
                    storageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageurl = uri.toString();
                                        Note_Event note = new Note_Event(eventname, max_members, additional_info, time, date, email, selectedTimeInMillis, category, imageurl, location);
                                        notebookRef.add(note)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(getActivity(), "Event Created", Toast.LENGTH_SHORT).show();
                                                        Intent intent= new Intent(getActivity(), organizer_home_pg.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(), "Error! Event not created.", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, e.toString());
                                                    }
                                                });
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }

        });
        return view;
    }


    //Date Picker Methods
    private String getTodaysdate() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        dayOfMonth = cal.get(Calendar.DATE);
        return makeDateString(dayOfMonth, month + 1, year);
    }
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = makeDateString(dayOfMonth, month + 1, year);
                dateButton.setText(date);

                addevent_fragment.this.year = year;
                addevent_fragment.this.month = month;
                addevent_fragment.this.dayOfMonth = dayOfMonth;

            }
        };

        //Default Day Selected is Current Day
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        Log.d("Time in Milis2", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 10000);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }


    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    //Time Picker Methods

    private void getTodaysTime() {
        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }
    public void popTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute, false);

        timePickerDialog.setTitle("Select Event Time");
        timePickerDialog.show();
    }

    //Image methods
    private void uploadImage(){

    }

    //Location Methods
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
                    location = geoPoint;
                    myDialog.dismiss();
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

    private static class PlacesAdapter extends BaseAdapter {
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

}