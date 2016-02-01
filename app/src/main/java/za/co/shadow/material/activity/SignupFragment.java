package za.co.shadow.material.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.co.shadow.maps.MapsActivity;
import za.co.shadow.material.R;

public class SignupFragment extends Fragment {

    protected EditText edtFirstName; //edtSignup_first_name
    protected EditText edtLastName; //edtSignup_first_name
    protected EditText edtMobileNo; //edtSignup_first_name
    protected EditText edtSecurityPhoneNo;
    protected View rootView;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_signup, container, false);


        InitializeSpinner(R.id.spnMedical_aid_provider, R.array.medical_aid_provider_arrays, "Medical Aid Provider");
        InitializeSpinner(R.id.spnSecurity_provider, R.array.security_provider_arrays, "Security Company");

        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            edtFirstName = (EditText)rootView.findViewById(R.id.edtSignup_first_name);
            edtFirstName.setText((String) parseUser.get("first_name"));
            edtLastName = (EditText)rootView.findViewById(R.id.edtSignup_last_name);
            edtLastName.setText((String) parseUser.get("last_name"));
        }
        edtMobileNo = (EditText)rootView.findViewById(R.id.edtSignup_mobile_no);
        edtMobileNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        edtMobileNo.setText(getMy10DigitPhoneNumber());

        InitBirthDate();

        edtSecurityPhoneNo = (EditText)rootView.findViewById(R.id.edtSecurity_provider_contact_no);
        edtSecurityPhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Button btnsignupBack = (Button)rootView.findViewById(R.id.btn_back_signup);
        btnsignupBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(getFragmentManager().getBackStackEntryCount() >= 0)
                {
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });

        Button btnsignupNext = (Button)rootView.findViewById(R.id.btn_next_signup);
        btnsignupNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intentMap = new Intent(view.getContext(), MapsActivity.class);
                startActivityForResult(intentMap, 0);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void InitializeSpinner(@IdRes int spinnerid, @ArrayRes int arrayid, String defaultText){
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(getResources().getStringArray(arrayid)));
        list.add(defaultText);
        final int listsize = list.size() - 1;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.styles_spinner_layout, list) {
            @Override
            public int getCount() {
                return(listsize); // Truncate the list
            }
        };

        Spinner spinner = (Spinner)rootView.findViewById(spinnerid);

        dataAdapter.setDropDownViewResource(R.layout.styles_spinner_dropdown);
        spinner.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(listsize); // Hidden item to appear in the spinner
    }

    private String getMyPhoneNumber(){
//        TelephonyManager mTelephonyMgr;
//        mTelephonyMgr = (TelephonyManager)
//                getSystemService(Context.TELEPHONY_SERVICE);
//        return mTelephonyMgr.getLine1Number();
        return "";
    }

    private String getMy10DigitPhoneNumber(){
        String s = getMyPhoneNumber();
        return s != null && s.length() > 1 ? s.substring(1) : null;
    }

    private void InitBirthDate() {
//get the reference of this edit text field
        final EditText  etNICNO_Sender=(EditText)rootView.findViewById(R.id.edtDate_of_birth);
  /*add textChangeListner with TextWatcher argument
         by adding text change listner with text watcher we can get three methods of
         Edit Text 1) onTextChanged 2) beforeTextChanged 3) afterTextChanged
         these methods work when user types in text feild.
   */
        etNICNO_Sender.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etNICNO_Sender.getText().toString();

                if ((str.length() == 4 && len < str.length()) || (str.length() == 7 && len < str.length())) {
                    //checking length  for backspace.
                    etNICNO_Sender.append("-");
                    //Toast.makeText(getBaseContext(), "add minus", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                String str = etNICNO_Sender.getText().toString();
                len = str.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}