package singh.com.sixthsense;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> ROUTES_BY_BEACONS;
    TextView textView;

    static {
        Map<String, List<String>> routesByBeacons = new HashMap<>();

        //hardcoded values for temporary purpose
        routesByBeacons.put("1:1001", new ArrayList<String>() {{
            add("Go to 2");
        }});
        routesByBeacons.put("1:1002", new ArrayList<String>() {{
            add("Go to 3");
            add("Warning");
        }});
        routesByBeacons.put("1:1003", new ArrayList<String>() {{
            add("Go to 4");
        }});
        ROUTES_BY_BEACONS = Collections.unmodifiableMap(routesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (ROUTES_BY_BEACONS.containsKey(beaconKey)) {
            return ROUTES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private com.estimote.sdk.Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    places.add("End");

                    String str = "Currently at   " + nearestBeacon.getMinor() + "   next location " + places.get(0);
                    textView.setText(str);
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
