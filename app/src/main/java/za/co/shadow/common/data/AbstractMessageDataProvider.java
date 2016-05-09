package za.co.shadow.common.data;

public abstract class AbstractMessageDataProvider {
    public static abstract class BaseData {

        public abstract void setPinned(boolean pinned);

        public abstract boolean isPinned();
    }

    public static abstract class messageData extends BaseData {

        public abstract String getMessage();
        public abstract void setMessage(String message);

        public abstract long getMessageId();
    }

    public static abstract class contactData extends BaseData {
        public abstract long getContactId();

        public abstract String getContactName();

        public abstract String getPhoneNo();
    }

    public abstract int getMessageCount();

    public abstract int getContactCount(int groupPosition);

    public abstract messageData getMessageItem(int groupPosition);

    public abstract contactData getContactItem(int groupPosition, int childPosition);

    public abstract void moveMessageItem(int fromGroupPosition, int toGroupPosition);

    public abstract void moveContactItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition);

    public abstract void removeMessageItem(int groupPosition);

    public abstract void removeContactItem(int groupPosition, int childPosition);

    public abstract contactData addContactItem(String name, String phoneNo);

    public abstract long undoLastRemoval();
}
