package za.co.shadow.material.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;

import za.co.shadow.material.R;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Fragment loginfragment;
    private Fragment signupfragment;
    private Fragment locationfragment;
    private int nextfragment;
    private int previousfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
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


        Button btnsignupBack = (Button)findViewById(R.id.btn_back_signup);
        btnsignupBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button btnsignupNext = (Button)findViewById(R.id.btn_next_signup);
        btnsignupNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onNextPressed();
            }
        });


        // display the first navigation drawer view on app launch
        displayView(0);
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

        if(id == R.id.action_search){
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
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                this.getSupportActionBar().hide();
                if (loginfragment == null) {
                    loginfragment = new LoginFragment();
                }
                fragment = loginfragment;

                nextfragment = 1;
                previousfragment = -1;

                title = getString(R.string.title_login);
                break;
            case 1:
                if (signupfragment == null) {
                    signupfragment = new SignupFragment();
                }
                fragment = signupfragment;
                nextfragment = 2;
                previousfragment = 0;
                title = getString(R.string.title_signup);
                break;
            case 2:
                if (locationfragment == null) {
                    locationfragment = new LocationFragment();
                }
                fragment = locationfragment;
                nextfragment = -1;
                previousfragment = 1;

                title = getString(R.string.title_location);
                break;
            case 3:
                fragment = new MessagesFragment();
                title = getString(R.string.title_messages);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
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

}