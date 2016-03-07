package za.co.shadow.blelib.ble;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import za.co.shadow.blelib.R;

public class BLEDeviceListAdapter extends BaseAdapter{
	private TextView devNameTextView, devAddressTextView;
	private TextView devUUIDTextView, devMajorTextView, devMinorTextView;
	private TextView devTxPowerTextView, devDistanceTextView;

	private ArrayList<BluetoothHandler.BluetoothScanInfo> bleArrayList;
	private BluetoothHandler.BluetoothScanInfo findResult;
	private Context context;

	public BLEDeviceListAdapter(Context context) {
		this.context = context;
		bleArrayList = new ArrayList<BluetoothHandler.BluetoothScanInfo>();
	}

	public void addDevice(BluetoothHandler.BluetoothScanInfo device) {
		if (!isContains(device)) {
			bleArrayList.add(device);
		}else{
			if(findResult != null){
				int index = bleArrayList.indexOf(findResult);
				bleArrayList.set(index, device);
			}
		}
	}
	
	public boolean isContains(BluetoothHandler.BluetoothScanInfo dstDevice){
		boolean val = false;
		findResult = null;
		for(BluetoothHandler.BluetoothScanInfo d:bleArrayList){
			if(d.device.getAddress().equals(dstDevice.device.getAddress())){
				val = true;
				findResult = d;
				break;
			}
		}
		return val;
	}
	
	public void clearDevice(){
		bleArrayList.clear();
	}
	
	public void removeDevice(BluetoothHandler.BluetoothScanInfo dev){
		bleArrayList.remove(dev);
	}

	@Override
	public int getCount() {
		return bleArrayList.size();
	}

	@Override
	public BluetoothHandler.BluetoothScanInfo getItem(int position) {
		return bleArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		
		if (convertView == null) {
			layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_list_material, null);
			devNameTextView = (TextView) layout.findViewById(R.id.textViewDevName);
			devAddressTextView = (TextView) layout.findViewById(R.id.textViewDevAddress);
			devUUIDTextView = new TextView(context);
			devUUIDTextView.setTextSize(12);
			devMajorTextView = new TextView(context);
			devMajorTextView.setTextSize(12);
			devMinorTextView = new TextView(context);
			devMinorTextView.setTextSize(12);
			devTxPowerTextView = new TextView(context);
			devTxPowerTextView.setTextSize(12);
			devDistanceTextView = new TextView(context);
			devDistanceTextView.setTextSize(12);
			convertView = layout;
		}

		// add-Parameters
		BluetoothDevice device = bleArrayList.get(position).device;
		int rssi = bleArrayList.get(position).rssi;
		byte[] scanRecord = bleArrayList.get(position).scanRecord;
		String devName = device.getName();
		devName = "Shadow remote";
		if (devName != null && devName.length() > 0) {
			devNameTextView.setText(devName+"   rssi:"+String.valueOf(rssi));
		} else {
			devNameTextView.setText("unknow-device"+"   rssi:"+String.valueOf(rssi));
		}
		devAddressTextView.setText(device.getAddress());
		
		if(scanRecord[7] == 0x02 && scanRecord[8] == 0x15){
			String uuid = String.format("uuid:%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X", 
					scanRecord[9], scanRecord[10], scanRecord[11], scanRecord[12], 
					scanRecord[13], scanRecord[14], 
					scanRecord[15], scanRecord[16], 
					scanRecord[17], scanRecord[18], 
					scanRecord[19], scanRecord[20], scanRecord[21], scanRecord[22], scanRecord[23], scanRecord[24]);
			String major = String.format("major:%02X%02X", scanRecord[25], scanRecord[26]);
			String minor = String.format("minor:%02X%02X", scanRecord[27], scanRecord[28]);
			String txPower = String.format("txPower:%02X", scanRecord[29]);
			String distance = String.format("distance:%f m", BluetoothHandler.calculateAccuracy(-59, rssi));
			devUUIDTextView.setText(uuid);
			devMajorTextView.setText(major);
			devMinorTextView.setText(minor);
			devTxPowerTextView.setText(txPower);
			devDistanceTextView.setText(distance);
			if(layout != null){
				layout.addView(devUUIDTextView);
				layout.addView(devMajorTextView);
				layout.addView(devMinorTextView);
				layout.addView(devTxPowerTextView);
				layout.addView(devDistanceTextView);
			}
		}else{
			devUUIDTextView.setText("");
			devMajorTextView.setText("");
			devMinorTextView.setText("");
		}
		
		return convertView;
	}
}
