package za.co.shadow.material.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.arulnadhan.AchievementUnlockedLib.AchievementUnlocked;
import me.arulnadhan.recyclerview.draggable.DraggableItemConstants;
import me.arulnadhan.recyclerview.draggable.ItemDraggableRange;
import me.arulnadhan.recyclerview.expandable.ExpandableDraggableItemAdapter;
import me.arulnadhan.recyclerview.expandable.ExpandableItemConstants;
import me.arulnadhan.recyclerview.expandable.ExpandableItemViewHolder;
import me.arulnadhan.recyclerview.expandable.ExpandableSwipeableItemAdapter;
import me.arulnadhan.recyclerview.expandable.RecyclerViewExpandableItemManager;
import me.arulnadhan.recyclerview.swipeable.SwipeableItemConstants;
import me.arulnadhan.recyclerview.swipeable.action.SwipeResultAction;
import me.arulnadhan.recyclerview.swipeable.action.SwipeResultActionDefault;
import me.arulnadhan.recyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import me.arulnadhan.recyclerview.swipeable.action.SwipeResultActionRemoveItem;
import me.arulnadhan.recyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import me.arulnadhan.recyclerview.utils.AbstractExpandableItemAdapter;
import me.arulnadhan.recyclerview.utils.RecyclerViewAdapterUtils;
import za.co.shadow.common.data.AbstractMessageDataProvider;
import za.co.shadow.common.utils.DrawableUtils;
import za.co.shadow.common.utils.ViewUtils;
import za.co.shadow.material.R;


public class MyExpandableDraggableSwipeableItemAdapter
        extends AbstractExpandableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.messageViewHolder, MyExpandableDraggableSwipeableItemAdapter.contactViewHolder>
        implements ExpandableDraggableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.messageViewHolder, MyExpandableDraggableSwipeableItemAdapter.contactViewHolder>,
        ExpandableSwipeableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.messageViewHolder, MyExpandableDraggableSwipeableItemAdapter.contactViewHolder> {
    private static final String TAG = "MyEDSItemAdapter";

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    private interface Draggable extends DraggableItemConstants {
    }

    private interface Swipeable extends SwipeableItemConstants {
    }

    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private AbstractMessageDataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;
    private View.OnFocusChangeListener mMessageFocusChangeListener;

    public interface EventListener {
        void onGroupItemRemoved(int groupPosition);

        void onChildItemRemoved(int groupPosition, int childPosition);

        void onGroupItemPinned(int groupPosition);

        void onChildItemPinned(int groupPosition, int childPosition);

        void onItemViewClicked(View v, boolean pinned);

        void onMessageFocusChanged(View v);
    }

    public static abstract class baseViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        private int mExpandStateFlags;

        public baseViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mContainer.setClickable(false);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mDragHandle.setClickable(false);
            mDragHandle.setEnabled(false);

        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

        public void shwet(View v) {
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(v.getContext());
            builder.setTitle("Emergency message");
            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setView(inflater.inflate(R.layout.dialog_et, null));
            builder.setPositiveButton("Done", null);
            builder.setNegativeButton("Dismiss", null);
            builder.show();
        }

    }

    public static class messageViewHolder extends baseViewHolder {

        public EditText mMessage;
        public Button btnPreview;
        private Activity activity;

        private Activity getActivity(View v) {
            Context context = v.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
            return null;
        }

        public messageViewHolder(View v) {
            super(v);
            activity = getActivity(v);
            mMessage = (EditText) v.findViewById(R.id.textMessage);
            btnPreview = (Button) v.findViewById(R.id.btnPreview);
            mMessage.addTextChangedListener(new MyTextWatcher(mMessage));
            btnPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Showpreview(v);
                }
            });
        }

        public void Showpreview(View v) {
            AchievementUnlocked notification = new AchievementUnlocked(v.getContext());
            notification.setTitle("Joe Soap").
                    setSubTitle(mMessage.getText().toString() + ": location: www.google.co.za/maps/@-25.7646938,28.2709898,19z?").
                    setSubtitleColor(0x80000000).
                    setHeight(90).
                    setIcon(getDrawableFromRes(R.drawable.chat)).
                    isRounded(false).
                    setDuration(4500).isLarge(true).build();


            notification.show();

        }

        private Drawable getDrawableFromRes(int ResID) {
            if (Build.VERSION.SDK_INT >= 21) activity.getDrawable(ResID);
            return activity.getResources().getDrawable((ResID));
        }

        private boolean validateName(View v) {
            if (mMessage.getText().toString().trim().isEmpty()) {
                mMessage.setError("Message cannot be empty");
                return false;
            } else if (mMessage.getText().toString().length() > 50) {
                mMessage.setError("Message cannot be more than 50 characters");
                return false;
            } else {
                mMessage.setError(null);
            }

            return true;
        }

        private class MyTextWatcher implements TextWatcher {

            private View view;

            private MyTextWatcher(View view) {
                this.view = view;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateName(view);
            }
        }
    }

    public static class contactViewHolder extends baseViewHolder {
        public TextView mContactName;
        public TextView mPhoneno;


        public contactViewHolder(View v) {
            super(v);
            mContactName = (TextView) v.findViewById(android.R.id.text1);
            mPhoneno = (TextView) v.findViewById(android.R.id.text2);
        }
    }

    public MyExpandableDraggableSwipeableItemAdapter(
            RecyclerViewExpandableItemManager expandableItemManager,
            AbstractMessageDataProvider dataProvider) {
        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mMessageFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMessageFocusChanged(v);
            }
        };

        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        // ExpandableItemAdapter, ExpandableDraggableItemAdapter and ExpandableSwipeableItemAdapter
        // require stable ID, and also have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true);  // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
        }
    }

    private void onMessageFocusChanged(View v) {
        if (mEventListener != null) {
            mEventListener.onMessageFocusChanged(v);  // true --- pinned
        }
    }

    @Override
    public int getGroupCount() {
        return mProvider.getMessageCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getContactCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getMessageItem(groupPosition).getMessageId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getContactItem(groupPosition, childPosition).getContactId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public messageViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.rv_list_group_item_draggable, parent, false);
        return new messageViewHolder(v);
    }

    @Override
    public contactViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.rv_list_item_draggable, parent, false);
        return new contactViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(messageViewHolder holder, int groupPosition, int viewType) {
        // group item
        String message = holder.mMessage.getText().toString();
        final AbstractMessageDataProvider.messageData item = mProvider.getMessageItem(groupPosition);

        // set listeners
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        holder.mMessage.setOnFocusChangeListener(mMessageFocusChangeListener);

        if (!message.equals("")) {
            if (!message.equals(item.getMessage())) {
                item.setMessage(message);
            } else {
                message = item.getMessage();
            }
        } else
            message = item.getMessage();
        holder.mMessage.setText(message);

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int expandState = holder.getExpandStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        holder.mContainer.setBackgroundResource(R.drawable.bg_group_item_expanded_state);
        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }

    @Override
    public void onBindChildViewHolder(contactViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // child item
        final AbstractMessageDataProvider.contactData item = mProvider.getContactItem(groupPosition, childPosition);

        // set listeners
        // (if the item is *not pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        holder.itemView.setOnFocusChangeListener(mMessageFocusChangeListener);
        // (if the item is *pinned*, click event comes to the mContainer)
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

        // set text
        holder.mContactName.setText(item.getContactName());
        holder.mPhoneno.setText(item.getPhoneNo());

        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(messageViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getMessageItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckGroupCanStartDrag(messageViewHolder holder, int groupPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckChildCanStartDrag(contactViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(messageViewHolder holder, int groupPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(contactViewHolder holder, int groupPosition, int childPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        mProvider.moveMessageItem(fromGroupPosition, toGroupPosition);
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mProvider.moveContactItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    @Override
    public int onGetGroupItemSwipeReactionType(messageViewHolder holder, int groupPosition, int x, int y) {
        return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
//        if (onCheckGroupCanStartDrag(holder, groupPosition, x, y)) {
//            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
//        }
//
//        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(contactViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        if (onCheckChildCanStartDrag(holder, groupPosition, childPosition, x, y)) {
            return Swipeable.REACTION_CAN_SWIPE_RIGHT;
        }

        return Swipeable.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public void onSetGroupItemSwipeBackground(messageViewHolder holder, int groupPosition, int type) {
        int bgResId = 0;
//        switch (type) {
//            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
//                bgResId = R.drawable.bg_swipe_item_neutral;
//                break;
//            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
//                bgResId = R.drawable.bg_swipe_group_item_left;
//                break;
//            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
//                bgResId = R.drawable.bg_swipe_group_item_right;
//                break;
//        }
        bgResId = R.drawable.bg_group_item_expanded_state;
        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public void onSetChildItemSwipeBackground(contactViewHolder holder, int groupPosition, int childPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(messageViewHolder holder, int groupPosition, int result) {
        Log.d(TAG, "onSwipeGroupItem(groupPosition = " + groupPosition + ", result = " + result + ")");
        result = Swipeable.RESULT_CANCELED;
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                if (mProvider.getMessageItem(groupPosition).isPinned()) {
                    // pinned --- back to default position
                    return new GroupUnpinResultAction(this, groupPosition);
                } else {
                    // not pinned --- remove
                    return new GroupSwipeRightResultAction(this, groupPosition);
                }
                // swipe left -- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new GroupSwipeLeftResultAction(this, groupPosition);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                if (groupPosition != RecyclerView.NO_POSITION) {
                    return new GroupUnpinResultAction(this, groupPosition);
                } else {
                    return null;
                }
        }
    }

    @Override
    public SwipeResultAction onSwipeChildItem(contactViewHolder holder, int groupPosition, int childPosition, int result) {
        Log.d(TAG, "onSwipeChildItem(groupPosition = " + groupPosition + ", childPosition = " + childPosition + ", result = " + result + ")");

        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                if (mProvider.getContactItem(groupPosition, childPosition).isPinned()) {
                    // pinned --- back to default position
                    return new ChildUnpinResultAction(this, groupPosition, childPosition);
                } else {
                    // not pinned --- remove
                    return new ChildSwipeRightResultAction(this, groupPosition, childPosition);
                }
                // swipe left -- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new ChildSwipeLeftResultAction(this, groupPosition, childPosition);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                if (groupPosition != RecyclerView.NO_POSITION) {
                    return new ChildUnpinResultAction(this, groupPosition, childPosition);
                } else {
                    return null;
                }
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private static class GroupSwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private boolean mSetPinned;

        GroupSwipeLeftResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractMessageDataProvider.messageData item =
                    mAdapter.mProvider.getMessageItem(mGroupPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.mExpandableItemManager.notifyGroupItemChanged(mGroupPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onGroupItemPinned(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class GroupSwipeRightResultAction extends SwipeResultActionRemoveItem {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;

        GroupSwipeRightResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeMessageItem(mGroupPosition);
            mAdapter.mExpandableItemManager.notifyGroupItemRemoved(mGroupPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onGroupItemRemoved(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class GroupUnpinResultAction extends SwipeResultActionDefault {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;

        GroupUnpinResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractMessageDataProvider.messageData item = mAdapter.mProvider.getMessageItem(mGroupPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.mExpandableItemManager.notifyGroupItemChanged(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


    private static class ChildSwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;
        private boolean mSetPinned;

        ChildSwipeLeftResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractMessageDataProvider.contactData item =
                    mAdapter.mProvider.getContactItem(mGroupPosition, mChildPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.mExpandableItemManager.notifyChildItemChanged(mGroupPosition, mChildPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onChildItemPinned(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildSwipeRightResultAction extends SwipeResultActionRemoveItem {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;

        ChildSwipeRightResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeContactItem(mGroupPosition, mChildPosition);
            mAdapter.mExpandableItemManager.notifyChildItemRemoved(mGroupPosition, mChildPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onChildItemRemoved(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildUnpinResultAction extends SwipeResultActionDefault {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;

        ChildUnpinResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractMessageDataProvider.contactData item = mAdapter.mProvider.getContactItem(mGroupPosition, mChildPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.mExpandableItemManager.notifyChildItemChanged(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


}
