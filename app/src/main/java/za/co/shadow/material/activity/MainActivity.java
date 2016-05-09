package za.co.shadow.material.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

import za.co.shadow.Parse.ShadowDevice;
import za.co.shadow.blelib.Constants;
import za.co.shadow.blelib.ble.BluetoothHandler;
import za.co.shadow.blelib.ble.BluetoothLeService;
import za.co.shadow.constants;
import za.co.shadow.material.R;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();


    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Fragment loginfragment;
    private Fragment signupfragment;
    private Fragment locationfragment;
    private Fragment linkDevicefragment;
    private Fragment emergencyfragment;


    private int nextfragment;
    private int previousfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(ShadowDevice.class);
        Parse.initialize(this, "0cJSz075MogqVzprRn36GXO6m1ur547EN8fVhOF4", "nafYF4kwB41R4AyTGZY7j0oGPKMVh5DXNzxurWjt");
        ParseFacebookUtils.initialize(this);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        Button btnsignupBack = (Button) findViewById(R.id.btn_back_signup);
        btnsignupBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button btnsignupNext = (Button) findViewById(R.id.btn_next_signup);
        btnsignupNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onNextPressed();
            }
        });

//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT);
//        ConnectExistingDevice();

        // display the first navigation drawer view on app launch
        displayView(0);
    }

    private void ConnectExistingDevice() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            BluetoothHandler bluetoothHandler = new BluetoothHandler(this);

            ShadowDevice shadowdevice = null;
            ParseQuery<ShadowDevice> query = ParseQuery.getQuery("ShadowDevice");
            query.whereEqualTo("user", parseUser);
            try {
                List<ShadowDevice> results = query.find();
                if (results.size() > 0) {
                    shadowdevice = results.get(0);
                } else {
                    shadowdevice = new ShadowDevice();
                }
            } catch (ParseException e) {
            }

            if (shadowdevice != null) {
//                Intent startIntent = new Intent(MainActivity.this, BluetoothLeService.class);
//                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//                startService(startIntent);

                bluetoothHandler.connect(shadowdevice.getBlueToothDeviceID());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        this.getSupportActionBar().show();
        Fragment fragment = null;
        String tag = "";
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                this.getSupportActionBar().hide();
                if (loginfragment == null) {
                    loginfragment = new LoginFragment();
                    tag = constants.TAG_LOGIN_FRAGMENT;
                }
                fragment = loginfragment;

                nextfragment = 1;
                previousfragment = -1;

                title = getString(R.string.title_login);
                break;
            case 1:
                if (signupfragment == null) {
                    signupfragment = new SignupFragment();
                    tag = constants.TAG_SIGNUP_FRAGMENT;
                }
                fragment = signupfragment;
                nextfragment = 2;
                previousfragment = 0;
                title = getString(R.string.title_signup);
                break;
            case 2:
                if (locationfragment == null) {
                    locationfragment = new LocationFragment();
                    tag = constants.TAG_LOCATION_FRAGMENT;
                }
                fragment = locationfragment;
                nextfragment = 3;
                previousfragment = 1;

                title = getString(R.string.title_location);
                break;
            case 3:
                if (linkDevicefragment == null) {
                    linkDevicefragment = new LinkDeviceFragment();
                    tag = constants.TAG_LINK_DEVICE_FRAGMENT;
                }
                fragment = linkDevicefragment;
                title = getString(R.string.title_linkdevice);
                nextfragment = 4;
                previousfragment = 2;
                break;
            case 4:
                if (emergencyfragment == null) {
                    emergencyfragment = new EmergencyFragment();
                    tag = constants.TAG_EMERGENCY_FRAGMENT;
                }
                fragment = emergencyfragment;
                title = getString(R.string.title_emergency);
                nextfragment = -1;
                previousfragment = 3;
                break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, tag);
//            fragmentTransaction.add(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public void onSignupPressed() {
        displayView(1);
    }

    public void onNextPressed() {
        if (nextfragment != -1) {
            displayView(nextfragment);
        }
    }


    @Override
    public void onBackPressed() {

        if (previousfragment != -1) {
            displayView(previousfragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (constants.PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    cursor.moveToFirst();

                    String name;
                    String phoneNo;

                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phoneNo = cursor.getString(column);
                    column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    name = cursor.getString(column);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(constants.TAG_EMERGENCY_FRAGMENT);
                    Log.d("phone number", cursor.getString(column));
                    ((EmergencyFragment) fragment).getDataProvider().addContactItem(name, phoneNo);

                    fragment = getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_LIST_VIEW);
                    ((RecyclerListViewFragment) fragment).notifyDataSetChanged(0);



                }

        }


    }

}