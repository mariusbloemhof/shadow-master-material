package za.co.shadow.material.activity;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import za.co.shadow.Parse.ShadowDevice;
import za.co.shadow.material.R;

public class LinkDeviceFragment extends Fragment {

    public LinkDeviceFragment() {
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

        View view = inflater.inflate(R.layout.fragment_linkdevice, container, false);

        Button btnScanDevice = (Button)  view.findViewById(R.id.btnScan_shadow);
        btnScanDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //scan for device
                if (ScanforDevice()) {
                    ShadowDevice shadowdevice;
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseQuery<ShadowDevice> query = ParseQuery.getQuery("ShadowDevice");
                    query.whereEqualTo("user", user);
                    try {
                        List<ShadowDevice> results = query.find();
                        if (results.size() > 0) {
                            shadowdevice = results.get(0);
                        }
                        else {
                            shadowdevice = new ShadowDevice();
                        }
                    }
                    catch (ParseException e) {shadowdevice = new ShadowDevice();}
                    shadowdevice.setBlueToothDeviceID(21154);
                    shadowdevice.SaveLater();

                }
            }
        });

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

    private Boolean ScanforDevice() {
        return true;
    }

}
