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
import android.widget.LinearLayout;

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

        LinearLayout navLayout = (LinearLayout) getActivity().findViewById(R.id.navigation_bar);
        navLayout.setVisibility(View.INVISIBLE);

        View view = inflater.inflate(R.layout.fragment_linkdevice, container, false);

        FloatingActionButton myFab = (FloatingActionButton)  view.findViewById(R.id.btn_add_shadow);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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

}
