package za.co.shadow.common.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import za.co.shadow.common.data.AbstractMessageDataProvider;
import za.co.shadow.common.data.MessageDataProvider;


public class MessageDataProviderFragment extends Fragment {
    private MessageDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new MessageDataProvider();
    }

    public AbstractMessageDataProvider getDataProvider() {
        return mDataProvider;
    }
}
