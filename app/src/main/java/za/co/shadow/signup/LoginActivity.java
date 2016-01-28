package za.co.shadow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import za.co.shadow.maps.MapsActivity;
import za.co.shadow.material.R;

/**
 * Created by Beast on 1/26/2016.
 */
public class LoginActivity extends AppCompatActivity {

    protected Button mBtnSignupFaceBook;
    protected ParseUser parseUser;
    protected String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "0cJSz075MogqVzprRn36GXO6m1ur547EN8fVhOF4", "nafYF4kwB41R4AyTGZY7j0oGPKMVh5DXNzxurWjt");
        ParseFacebookUtils.initialize(this);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Button btnsignup = (Button) findViewById(R.id.btn_login);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentSignup = new Intent(view.getContext(), SignupActivity.class);
                startActivityForResult(intentSignup, 0);
            }
        });
        InitializeEventListeners(findViewById(android.R.id.content));
    }

    private void InitializeEventListeners(View view) {
        mBtnSignupFaceBook = (Button) findViewById(R.id.btnfacebook_login);

        //listen to register button click
        FacebookDetails facebookDetails = new FacebookDetails();
        mBtnSignupFaceBook.setOnClickListener(facebookDetails.OnFacebookSignup(view, this));

    }


}
