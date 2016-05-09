package za.co.shadow.common.data;


import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.arulnadhan.recyclerview.expandable.RecyclerViewExpandableItemManager;

public class MessageDataProvider extends AbstractMessageDataProvider {
    private List<Pair<messageData, List<contactData>>> mData;

    // for undo group item
    private Pair<messageData, List<contactData>> mLastRemovedGroup;
    private int mLastRemovedGroupPosition = -1;
    private long messageId = 1;
    private String defaultMessage = "Emergency Message";
    private ConcreteGroupData group = null;

    // for undo child item
    private contactData mLastRemovedChild;
    private long mLastRemovedChildParentGroupId = -1;
    private int mLastRemovedChildPosition = -1;

    public MessageDataProvider() {
        mData = new LinkedList<>();
        final List<contactData> children = new ArrayList<>();
        group = new ConcreteGroupData(messageId, defaultMessage);
        mData.add(new Pair<messageData, List<contactData>>(group, children));
    }

    ;

    @Override
    public contactData addContactItem(String name, String phoneNo) {
        final long contactId = group.generateNewChildId();
        contactData newItem = new ConcreteContactData(contactId, name, phoneNo);
        final Pair<messageData, List<contactData>> toGroup = mData.get(0);

        toGroup.second.add(newItem);
        return newItem;
    }

    ;

    @Override
    public int getMessageCount() {
        return mData.size();
    }

    @Override
    public int getContactCount(int groupPosition) {
        return mData.get(groupPosition).second.size();
    }

    @Override
    public messageData getMessageItem(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getMessageCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        return mData.get(groupPosition).first;
    }

    @Override
    public contactData getContactItem(int groupPosition, int contactPosition) {
        if (groupPosition < 0 || groupPosition >= getMessageCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        final List<contactData> contacts = mData.get(groupPosition).second;

        if (contactPosition < 0 || contactPosition >= contacts.size()) {
            throw new IndexOutOfBoundsException("contactPosition = " + contactPosition);
        }

        return contacts.get(contactPosition);
    }

    @Override
    public void moveMessageItem(int fromMessagePosition, int toMessagePosition) {
        if (fromMessagePosition == toMessagePosition) {
            return;
        }

        final Pair<messageData, List<contactData>> item = mData.remove(fromMessagePosition);
        mData.add(toMessagePosition, item);
    }

    @Override
    public void moveContactItem(int fromMessagePosition, int fromContactPosition, int toMessagePosition, int toContactPosition) {
        if ((fromMessagePosition == toMessagePosition) && (fromContactPosition == toContactPosition)) {
            return;
        }

        final Pair<messageData, List<contactData>> fromGroup = mData.get(fromMessagePosition);
        final Pair<messageData, List<contactData>> toGroup = mData.get(toMessagePosition);

        final ConcreteContactData item = (ConcreteContactData) fromGroup.second.remove(fromContactPosition);

        if (toMessagePosition != fromMessagePosition) {
            // assign a new ID
            final long newId = ((ConcreteGroupData) toGroup.first).generateNewChildId();
            item.setChildId(newId);
        }

        toGroup.second.add(toContactPosition, item);
    }

    @Override
    public void removeMessageItem(int groupPosition) {
        mLastRemovedGroup = mData.remove(groupPosition);
        mLastRemovedGroupPosition = groupPosition;

        mLastRemovedChild = null;
        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
    }

    @Override
    public void removeContactItem(int groupPosition, int childPosition) {
        mLastRemovedChild = mData.get(groupPosition).second.remove(childPosition);
        mLastRemovedChildParentGroupId = mData.get(groupPosition).first.getMessageId();
        mLastRemovedChildPosition = childPosition;

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;
    }


    @Override
    public long undoLastRemoval() {
        if (mLastRemovedGroup != null) {
            return undoGroupRemoval();
        } else if (mLastRemovedChild != null) {
            return undoChildRemoval();
        } else {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }
    }

    private long undoGroupRemoval() {
        int insertedPosition;
        if (mLastRemovedGroupPosition >= 0 && mLastRemovedGroupPosition < mData.size()) {
            insertedPosition = mLastRemovedGroupPosition;
        } else {
            insertedPosition = mData.size();
        }

        mData.add(insertedPosition, mLastRemovedGroup);

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition);
    }

    private long undoChildRemoval() {
        Pair<messageData, List<contactData>> group = null;
        int groupPosition = -1;

        // find the group
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).first.getMessageId() == mLastRemovedChildParentGroupId) {
                group = mData.get(i);
                groupPosition = i;
                break;
            }
        }

        if (group == null) {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }

        int insertedPosition;
        if (mLastRemovedChildPosition >= 0 && mLastRemovedChildPosition < group.second.size()) {
            insertedPosition = mLastRemovedChildPosition;
        } else {
            insertedPosition = group.second.size();
        }

        group.second.add(insertedPosition, mLastRemovedChild);

        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
        mLastRemovedChild = null;

        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition);
    }

    public static final class ConcreteGroupData extends messageData {

        private final long mId;
        private String mMessage;
        private boolean mPinned;
        private long mNextChildId;

        ConcreteGroupData(long id, String name) {
            mId = id;
            mMessage = name;
            mNextChildId = 0;
        }

        @Override
        public long getMessageId() {
            return mId;
        }

        @Override
        public String getMessage() {
            return mMessage;
        }

        @Override
        public void setMessage(String message) {
            mMessage = message;
        }

        @Override
        public void setPinned(boolean pinnedToSwipeLeft) {
            mPinned = pinnedToSwipeLeft;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        public long generateNewChildId() {
            final long id = mNextChildId;
            mNextChildId += 1;
            return id;
        }
    }

    public static final class ConcreteContactData extends contactData {

        private long mId;
        private final String mContactName;
        private final String mPhoneNo;
        private boolean mPinned;

        ConcreteContactData(long id, String contactname, String phoneno) {
            mId = id;
            mContactName = contactname;
            mPhoneNo = phoneno;
        }

        @Override
        public long getContactId() {
            return mId;
        }

        @Override
        public String getContactName() {
            return mContactName;
        }

        public String getPhoneNo() {
            return mPhoneNo;
        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        public void setChildId(long id) {
            this.mId = id;
        }
    }
}
