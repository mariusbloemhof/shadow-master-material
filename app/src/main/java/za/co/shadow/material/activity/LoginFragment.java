package za.co.shadow.material.activity;

import android.app.Activity;
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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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

        LinearLayout navLayout = (LinearLayout) getActivity().findViewById(R.id.navigation_bar);
        navLayout.setVisibility(View.GONE);

        View view = inflater.inflate(R.layout.activity_login_selection, container, false);

//        parseUser = ParseUser.getCurrentUser();

        Button btnsignup = (Button) view.findViewById(R.id.btn_login);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });


        mBtnSignupFaceBook = (Button) view.findViewById(R.id.btnfacebook_login);
//        listen to register button click
        FacebookDetails facebookDetails = new FacebookDetails();
        View.OnClickListener clicklistener = facebookDetails.OnFacebookSignup(getActivity().findViewById(android.R.id.content), this);
        mBtnSignupFaceBook.setOnClickListener(clicklistener);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void signup() {

        MainActivity parentactivity = (MainActivity) getActivity();
        parentactivity.displayView(1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
