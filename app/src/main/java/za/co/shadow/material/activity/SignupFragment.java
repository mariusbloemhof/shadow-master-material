package za.co.shadow.material.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import za.co.shadow.maps.MapsActivity;
import za.co.shadow.material.R;

public class SignupFragment extends Fragment {

    private static final String[] ITEMS = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"};
    private ArrayAdapter<String> adapter;

    protected EditText edtFirstName; //edtSignup_first_name
    protected EditText edtLastName; //edtSignup_first_name
    protected EditText edtMobileNo; //edtSignup_first_name
    protected EditText edtSecurityPhoneNo;


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

        LinearLayout navLayout = (LinearLayout) getActivity().findViewById(R.id.navigation_bar);
        navLayout.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.activity_signup, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

//        MaterialSpinner spnMedicalprovider = (MaterialSpinner) view.findViewById(R.id.spnMedical_aid_provider);
//        adapter = new ArrayAdapter<>(getActivity(), R.layout.styles_spinner_layout_material, ITEMS);
//        adapter.setDropDownViewResource(R.layout.styles_spinner_dropdown);
//        spnMedicalprovider.setPaddingSafe(0, -10, 0, -10);
//        spnMedicalprovider.setAdapter(adapter);
//
//        MaterialSpinner spnSecurity = (MaterialSpinner) view.findViewById(R.id.spnSecurity_provider);
//        adapter = new ArrayAdapter<>(getActivity(), R.layout.styles_spinner_layout_material, ITEMS);
//        adapter.setDropDownViewResource(R.layout.styles_spinner_dropdown);
//        spnSecurity.setPaddingSafe(0, -10, 0, -10);
//        spnSecurity.setAdapter(adapter);

        InitializeSpinner(R.id.spnMedical_aid_provider, R.array.medical_aid_provider_arrays, "Medical Aid Provider");
        InitializeSpinner(R.id.spnSecurity_provider, R.array.security_provider_arrays, "Security Company");

        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            edtFirstName = (EditText)view.findViewById(R.id.edtSignup_first_name);
            edtFirstName.setText((String) parseUser.get("first_name"));
            edtLastName = (EditText)view.findViewById(R.id.edtSignup_last_name);
            edtLastName.setText((String) parseUser.get("last_name"));
        }
        edtMobileNo = (EditText)view.findViewById(R.id.edtSignup_mobile_no);
        edtMobileNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        edtMobileNo.setText((String)parseUser.get("mobile_no"));
        InitBirthDate();

        edtSecurityPhoneNo = (EditText)view.findViewById(R.id.edtSecurity_provider_contact_no);
        edtSecurityPhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void InitializeSpinner(@IdRes int spinnerid, @ArrayRes int arrayid, String defaultText)
    {
        View view = getView();

        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(getResources().getStringArray(arrayid)));
        list.add(defaultText);
        final int listsize = list.size() - 1;


        MaterialSpinner spinner = (MaterialSpinner) view.findViewById(spinnerid);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.styles_spinner_layout_material, list);
        adapter.setDropDownViewResource(R.layout.styles_spinner_dropdown);
        spinner.setPaddingSafe(0, -10, 0, -10);
        spinner.setAdapter(adapter);



//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.styles_spinner_layout_material, list) {
//            @Override
//            public int getCount() {
//                return(listsize); // Truncate the list
//            }
//        };
//
//        MaterialSpinner spinner = (MaterialSpinner)view.findViewById(spinnerid);
//
//        dataAdapter.setDropDownViewResource(R.layout.styles_spinner_dropdown);
//        spinner.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//        spinner.setAdapter(dataAdapter);
//        spinner.setSelection(listsize); // Hidden item to appear in the spinner
    }

    private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    private String getMy10DigitPhoneNumber(){
        String s = getMyPhoneNumber();
        return s != null && s.length() > 1 ? s.substring(1) : null;
    }

    private void InitBirthDate() {
        //get the reference of this edit text field
        View view = getView();
        final EditText  etNICNO_Sender=(EditText)view.findViewById(R.id.edtDate_of_birth);
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

    @Override
    public void onStop() {
        super.onStop();
        ParseUser parseUser = ParseUser.getCurrentUser();

        String mobileno = edtMobileNo.getText().toString();
        parseUser.put("mobile_no", mobileno);

        if (parseUser != null) {
            parseUser.saveInBackground();
        }
    }
}