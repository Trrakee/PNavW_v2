package trrakee.pnavw.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;

import java.util.Objects;

/**
 * A RecycleView which binds Extra context menu information and overrides {@link
 * ContextMenuRecyclerView#getContextMenuInfo()} to provide the ContextMenuInfo object instead of
 * default null ContextMenuInfo in {@link android.view.View#getContextMenuInfo()}
 */
public class ContextMenuRecyclerView extends RecyclerView {

    private ContextMenu.ContextMenuInfo mContextMenuInfo;

    public ContextMenuRecyclerView(Context context) {
        super(context);
    }

    public ContextMenuRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContextMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    /**
     * Used to initialize before creating context menu and Bring up the context menu for this view.
     *
     * @param position for ContextMenuInfo
     */
    public void openContextMenu(int position) {
        if (position >= 0) {
            long childId = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                childId = Objects.requireNonNull(getAdapter()).getItemId(position);
            }
            mContextMenuInfo = createContextMenuInfo(position, childId);
        }
        showContextMenu();
    }


    ContextMenu.ContextMenuInfo createContextMenuInfo(int position, long id) {
        return new RecyclerContextMenuInfo(position, id);
    }


    /**
     * Extra menu information provided to the {@link android.view.View
     * .OnCreateContextMenuListener#onCreateContextMenu(android.view.ContextMenu, View,
     * ContextMenuInfo) } callback when a context menu is brought up for this RecycleView.
     */
    public static class RecyclerContextMenuInfo implements ContextMenu.ContextMenuInfo {

        /**
         * The position in the adapter for which the context menu is being displayed.
         */
        public int position;

        /**
         * The row id of the item for which the context menu is being displayed.
         */
        public long id;

        RecyclerContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }
    }

}