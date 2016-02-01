package za.co.shadow.material.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import za.co.shadow.material.R;
import za.co.shadow.signup.FacebookDetails;
import za.co.shadow.signup.SignupActivity;

public class LoginFragment extends Fragment {

    protected Button mBtnSignupFaceBook;
    protected ParseUser parseUser;
    protected String name;
    protected View rootView;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = null;
        rootView = inflater.inflate(R.layout.activity_login_selection, container, false);

        parseUser = ParseUser.getCurrentUser();
//        Parse.enableLocalDatastore((Context)rootView.getContext());
//        Parse.initialize(rootView.getContext(), "0cJSz075MogqVzprRn36GXO6m1ur547EN8fVhOF4", "nafYF4kwB41R4AyTGZY7j0oGPKMVh5DXNzxurWjt");
//        ParseFacebookUtils.initialize(rootView.getContext());
//        ParseAnalytics.trackAppOpenedInBackground(this.getContext());

        Button btnsignup = (Button) rootView.findViewById(R.id.btn_login);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });
        InitializeEventListeners(rootView.findViewById(android.R.id.content));

        return rootView;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void InitializeEventListeners(View view) {
        mBtnSignupFaceBook = (Button) rootView.findViewById(R.id.btnfacebook_login);

        //listen to register button click
        FacebookDetails facebookDetails = new FacebookDetails();
        mBtnSignupFaceBook.setOnClickListener(facebookDetails.OnFacebookSignup(view, getActivity()));

    }

    private void signup() {
        String title  = getString(R.string.title_signup);
        Fragment fragment = new SignupFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // set the toolbar title
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

}
