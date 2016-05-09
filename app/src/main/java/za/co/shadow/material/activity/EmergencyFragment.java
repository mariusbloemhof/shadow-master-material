package za.co.shadow.material.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import me.arulnadhan.recyclerview.expandable.RecyclerViewExpandableItemManager;
import za.co.shadow.blelib.Constants;
import za.co.shadow.blelib.ble.BluetoothLeService;
import za.co.shadow.common.data.AbstractMessageDataProvider;
import za.co.shadow.common.fragment.ExpandableItemPinnedMessageDialogFragment;
import za.co.shadow.common.fragment.MessageDataProviderFragment;
import za.co.shadow.constants;
import za.co.shadow.material.R;

public class EmergencyFragment extends Fragment implements ExpandableItemPinnedMessageDialogFragment.EventListener {

    private FloatingActionButton mBtnAddContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        final View view = inflater.inflate(R.layout.rv_activity_demo, container, false);

        if (savedInstanceState == null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(new MessageDataProviderFragment(), constants.FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecyclerListViewFragment(), constants.FRAGMENT_LIST_VIEW)
                    .commit();
        }

        mBtnAddContact = (FloatingActionButton) view.findViewById(R.id.btnAddContact);
        mBtnAddContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                getActivity().startActivityForResult(intent, constants.PICK_CONTACT);
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

    private void showMessage(String str){
        Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public void onGroupItemRemoved(int groupPosition) {
        Snackbar snackbar = Snackbar.make(
                getActivity().findViewById(R.id.container),
                R.string.snack_bar_text_group_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a child item is removed
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemRemoved(int groupPosition, int childPosition) {
        Snackbar snackbar = Snackbar.make(
                getActivity().findViewById(R.id.container),
                R.string.snack_bar_text_child_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a group item is pinned
     *
     * @param groupPosition The position of the group item within data set
     */
    public void onGroupItemPinned(int groupPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, RecyclerView.NO_POSITION);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, constants.FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    /**
     * This method will be called when a child item is pinned
     *
     * @param groupPosition The group position of the child item within data set
     * @param childPosition The position of the child item within the group
     */
    public void onChildItemPinned(int groupPosition, int childPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, childPosition);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, constants.FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    public void onGroupItemClicked(int groupPosition) {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_LIST_VIEW);
        AbstractMessageDataProvider.messageData data = getDataProvider().getMessageItem(groupPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((RecyclerListViewFragment) fragment).notifyGroupItemChanged(groupPosition);
        }
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_LIST_VIEW);
        AbstractMessageDataProvider.contactData data = getDataProvider().getContactItem(groupPosition, childPosition);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((RecyclerListViewFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    private void onItemUndoActionClicked() {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_LIST_VIEW);
        final long result = getDataProvider().undoLastRemoval();

        if (result == RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION) {
            return;
        }

        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(result);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(result);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            ((RecyclerListViewFragment) fragment).notifyGroupItemRestored(groupPosition);
        } else {
            // child item
            ((RecyclerListViewFragment) fragment).notifyChildItemRestored(groupPosition, childPosition);
        }
    }

    // implements ExpandableItemPinnedMessageDialogFragment.EventListener
    @Override
    public void onNotifyExpandableItemPinnedDialogDismissed(int groupPosition, int childPosition, boolean ok) {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_LIST_VIEW);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            getDataProvider().getMessageItem(groupPosition).setPinned(ok);
            ((RecyclerListViewFragment) fragment).notifyGroupItemChanged(groupPosition);
        } else {
            // child item
            getDataProvider().getContactItem(groupPosition, childPosition).setPinned(ok);
            ((RecyclerListViewFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    public AbstractMessageDataProvider getDataProvider() {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(constants.FRAGMENT_TAG_DATA_PROVIDER);
        return ((MessageDataProviderFragment) fragment).getDataProvider();
    }

}
