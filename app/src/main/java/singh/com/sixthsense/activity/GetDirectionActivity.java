package singh.com.sixthsense.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import singh.com.sixthsense.R;
import singh.com.sixthsense.manager.SettingsManager;
import singh.com.sixthsense.manager.APIManager;
import singh.com.sixthsense.model.EstimoteBeacon;
import singh.com.sixthsense.model.DestinationResponse;

public class GetDirectionActivity extends AppCompatActivity {

    ArrayList<EstimoteBeacon> mDestinations = new ArrayList<EstimoteBeacon>();
    ArrayAdapter<String> mDestinationAdapter;
    AutoCompleteTextView actv;
    private static final String API_URL = "http://54.69.39.220:8082/";
    Context mContext = null;
    private BeaconManager beaconManager;
    private com.estimote.sdk.Region region;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_direction);
        mContext = this;
        beaconManager = new BeaconManager(this);

        mDestinationAdapter = new ArrayAdapter<String>
                (this, R.layout.destination_item, new ArrayList<String>());
        mDestinationAdapter.setNotifyOnChange(true);
        //Getting the instance of AutoCompleteTextView
        actv = (AutoCompleteTextView) findViewById(R.id.college_name);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(mDestinationAdapter);//setting the adapter data into the AutoCompleteTextView

        //final TextView noDestination = (TextView) findViewById(R.id.collegename_suggestion);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    int i = 0;
                    //    for (i = 0; i < mColleges.size(); i++) {
                    //        if (mColleges.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                    //            break;
                    //        }
                    //    }
                    //if (i == mColleges.size()) {
                    //    collegeNameSuggestion.setVisibility(View.VISIBLE);
                    //} else {
                    //    collegeNameSuggestion.setVisibility(View.GONE);
                    //}
                } else {
                    //    collegeNameSuggestion.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        FloatingActionButton submit = (FloatingActionButton) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String destination = actv.getText().toString();
                String finalId = "";
                for (int i = 0; i < mDestinations.size(); i++) {
                    if (mDestinations.get(i).getLocation().equals(destination)) {
                        finalId = mDestinations.get(i).getId();
                        SettingsManager.getInstance(mContext).setFinalLocation(finalId);
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        finalId = "";
                    }
                }
            }
        });



        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    Toast.makeText(getApplicationContext(), "Beacon in Range", Toast.LENGTH_SHORT);
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(API_URL)
                                    //.client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    APIManager service = retrofit.create(APIManager.class);
                    Call<DestinationResponse> call = service.getLocations("B9407F30-F5F8-466E-AFF9-25556B57FE6D", nearestBeacon.getMinor(), 1);
                    call.enqueue(new Callback<DestinationResponse>() {
                        @Override
                        public void onResponse(Call<DestinationResponse> call, Response<DestinationResponse> response) {
                            // handle success

                            ArrayList<String> locationNames = new ArrayList<String>();
                            mDestinations = response.body().getDestinations();
                            //Log.e("kamal", "" + mDestinations.size());
                            for (int i = 0; i < mDestinations.size(); i++) {
                                locationNames.add(mDestinations.get(i).getLocation());
                            }

                            mDestinationAdapter = new ArrayAdapter<String>
                                    (mContext, R.layout.destination_item, locationNames);
                            mDestinationAdapter.notifyDataSetChanged();
                            mDestinationAdapter.setNotifyOnChange(true);
                            actv.setAdapter(mDestinationAdapter);

                            //String nextMessage = response.body().getMessage();
                            //String str = "Currently at   " + currentLocation + "   next location " + nextMessage;
                            //   textView.setText(str);
                            //  t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                        }

                        @Override
                        public void onFailure(Call<DestinationResponse> call, Throwable t) {
                            // handle failure
                            //   textView.setText("Failed to retrieve Final Location");
                            Log.e("Failed", "abcde");

                        }
                    });
                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    }
    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        //beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    protected void onStop() {
        //beaconManager.stopRanging(region);
        super.onStop();
    }

}
