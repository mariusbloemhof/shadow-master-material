package za.co.shadow.material.activity;


import android.bluetooth.BluetoothDevice;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import za.co.shadow.Parse.ShadowDevice;
import za.co.shadow.blelib.ble.BLEDeviceListAdapter;
import za.co.shadow.blelib.ble.BluetoothHandler;
import za.co.shadow.material.R;

public class LinkDeviceFragment extends Fragment {


    private Button scanButton;
    private ListView bleDeviceListView;
    private BLEDeviceListAdapter listViewAdapter;
    private BluetoothHandler bluetoothHandler;
    private boolean isConnected;

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

        final View view = inflater.inflate(R.layout.fragment_linkdevice, container, false);

        scanButton = (Button) view.findViewById(R.id.btnScan_shadow);

        scanButton.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View v) {scanOnClick(v);
                                             }
        });


        bleDeviceListView = (ListView) view.findViewById(R.id.bleDeviceListView);
        listViewAdapter = new BLEDeviceListAdapter(this.getActivity());

        bluetoothHandler = new BluetoothHandler(this.getActivity());
        bluetoothHandler.setOnConnectedListener(new BluetoothHandler.OnConnectedListener() {

            @Override
            public void onConnected(boolean isConnected) {
                // TODO Auto-generated method stub
                setConnectStatus(isConnected);
            }
        });
        return view;
    }

    public void setConnectStatus(boolean isConnected){
        this.isConnected = isConnected;
        if(isConnected){
            showMessage("Connection successful");
            scanButton.setText("break");
        }else{
            bluetoothHandler.onPause();
            bluetoothHandler.onDestroy();
            scanButton.setText("Scan for Shadow remote");
        }
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

    private void showMessage(String str){
        Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public void scanOnClick(final View v){
        if(!isConnected){
            bleDeviceListView.setAdapter(bluetoothHandler.getDeviceListAdapter());
            bleDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String buttonText = (String) ((Button)v).getText();
                    if(buttonText.equals("scanning")){
                        showMessage("scanning...");
                        return ;
                    }
                    BluetoothDevice device = bluetoothHandler.getDeviceListAdapter().getItem(position).device;
                    // connect
                    bluetoothHandler.connect(device.getAddress());
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
                    String deviceID = device.getAddress().toString();
                    shadowdevice.setBlueToothDeviceID(deviceID);
                    shadowdevice.SaveLater();


                }
            });
            bluetoothHandler.setOnScanListener(new BluetoothHandler.OnScanListener() {
                @Override
                public void onScanFinished() {
                    // TODO Auto-generated method stub
                    ((Button)v).setText("scan");
                    ((Button)v).setEnabled(true);
                }
                @Override
                public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {}
            });
            ((Button)v).setText("scanning");
            ((Button)v).setEnabled(false);
            bluetoothHandler.scanLeDevice(true);
        }else{
            setConnectStatus(false);
        }
    }

}
