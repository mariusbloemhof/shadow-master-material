package za.co.shadow.Parse;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Beast on 2/23/2016.
 */

@ParseClassName("ShadowDevice")
public class ShadowDevice extends ParseObject {

    private String BlueToothDeviceID;
    private ParseUser user;
    private ParseUser parent;

    public void setParent(ParseUser parent) {
        this.parent = parent;
        this.put("parent", parent);
    }
    public ParseUser getParent() {
        if (parent == null){
            parent = (ParseUser) this.get("parent");
        }
        return parent;
    }
    public ParseUser getUser() {
        if (user == null){
            user = ParseUser.getCurrentUser();
        }
        return user;

    }
    public void setUser(ParseUser user) {
        this.user = user;
        this.put("user", user);
    }

    public ShadowDevice() {

    }

    public final void SaveLater() {
        setUser(this.getUser());
        setParent(this.getUser());
        saveEventually();
    }

    public String getBlueToothDeviceID() {
        if ((BlueToothDeviceID == null) && (this.get("BLEDeviceID") != null ))
        {
            BlueToothDeviceID = (String)this.get("BLEDeviceID") ;
        }
        else {BlueToothDeviceID = "";}
        return BlueToothDeviceID;
    }

    public void setBlueToothDeviceID(String blueToothDeviceID) {
        BlueToothDeviceID = blueToothDeviceID;
        this.put("BLEDeviceID", blueToothDeviceID);
    }


}

