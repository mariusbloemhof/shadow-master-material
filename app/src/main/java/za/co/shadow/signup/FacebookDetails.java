package za.co.shadow.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.shadow.Callable;
import za.co.shadow.material.activity.MainActivity;

/**
 * Created by Beast on 11/3/2015.
 */

public class FacebookDetails{

    private String email;
    private String lastname;
    private String firstname;
    private String name;
    private Callable saveUser;
    private ArrayList<String> valuelist;
    private Activity activity;
    private Fragment fragment;
//    private View view;

    public FacebookDetails(ArrayList<String> values) {
        saveUser = new SaveUser();
        valuelist = values;
        GetFaceBookData(AccessToken.getCurrentAccessToken());
    }

    public FacebookDetails() {
        saveUser = new SaveUser();
        valuelist = new ArrayList<String>();
        valuelist.add("name");
        valuelist.add("last_name");
        valuelist.add("first_name");
        valuelist.add("email");
//        GetFaceBookData(accessToken);
    }

    public View.OnClickListener OnFacebookSignup (final View view, final Activity activity) {

        this.activity = activity;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<String> permissions = new ArrayList<String>();
                permissions.add("public_profile");
                permissions.add("user_status");
                permissions.add("user_friends");
                permissions.add("email");
                permissions.add("user_birthday");

                Toast.makeText(v.getContext(), "User trying to sign up through Facebook!", Toast.LENGTH_LONG).show();

                ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Toast.makeText(view.getContext(), "Error: " + err, Toast.LENGTH_LONG).show();
                            Log.d("MyApp", "Error: " + err);
                        } else {
                            if (user.isNew()) {
                                GetFaceBookData(AccessToken.getCurrentAccessToken());
                                Toast.makeText(view.getContext(), "User signed up with Facebook!", Toast.LENGTH_LONG).show();
                            } else {
                                GetFaceBookData(AccessToken.getCurrentAccessToken());
                                Toast.makeText(view.getContext(), "User signed in with Facebook!", Toast.LENGTH_LONG).show();
                            }
//                            MainActivity parentactivity = (MainActivity) this.activity;
//                            parentactivity.displayView(1);
                        }
                    }
                });


            }
        };
    }

    public View.OnClickListener OnFacebookSignup (final View view, final Fragment afragment) {

        this.activity = afragment.getActivity();
//        this.view = view;
        this.fragment = afragment;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<String> permissions = new ArrayList<String>();
                permissions.add("public_profile");
                permissions.add("user_status");
                permissions.add("user_friends");
                permissions.add("email");
                permissions.add("user_birthday");

                Toast.makeText(v.getContext(), "User trying to sign up through Facebook!", Toast.LENGTH_LONG).show();

                ParseFacebookUtils.logInWithReadPermissionsInBackground(fragment, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Toast.makeText(view.getContext(), "Error: " + err, Toast.LENGTH_LONG).show();
                            Log.d("MyApp", "Error: " + err);
                        } else {
                            if (user.isNew()) {
                                GetFaceBookData(AccessToken.getCurrentAccessToken());
                                Toast.makeText(view.getContext(), "User signed up with Facebook!", Toast.LENGTH_LONG).show();
                            } else {
                                GetFaceBookData(AccessToken.getCurrentAccessToken());
                                Toast.makeText(view.getContext(), "User signed in with Facebook!", Toast.LENGTH_LONG).show();
                            }
                            MainActivity parentactivity = (MainActivity) fragment.getActivity();
                            parentactivity.displayView(1);
                        }
                    }
                });


            }
        };
    }


    private void GetFaceBookData(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            List<Pair> pairs = new ArrayList<Pair>();
                            for (String value : valuelist) {
                                pairs.add(new Pair(value, object.getString(value)));
                            }
                            saveUser.call(pairs);
//                            Intent intentSignup = new Intent(view.getContext(), SignupActivity.class);
//                            activity.startActivityForResult(intentSignup, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,last_name,link,email,first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public String getEmail() {
        return this.email;
    }
    public String getLastname() {
        return this.lastname;
    }
    public String getFirstname() {
        return this.firstname;
    }
    public String getName() {
        return this.name;
    }

}
