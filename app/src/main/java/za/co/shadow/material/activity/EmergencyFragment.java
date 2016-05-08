package za.co.shadow.material.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import za.co.shadow.blelib.Constants;
import za.co.shadow.blelib.ble.BluetoothLeService;
import za.co.shadow.material.R;

public class EmergencyFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static Button btnaddContact;

    public EmergencyFragment() {
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

        final View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        btnaddContact = (Button) view.findViewById(R.id.btnAddcontact);

        btnaddContact.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, MainActivity.PICK_CONTACT);

        }});

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
        String[] mDataset = {
                "Abundance",
                "Anxiety",
                "Bruxism",
                "Discipline",
                "Drug Addiction"
        };

        mAdapter = new EmergencyContactAdapter(mDataset);

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

    private void showMessage(String str){
        Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
    }


}
