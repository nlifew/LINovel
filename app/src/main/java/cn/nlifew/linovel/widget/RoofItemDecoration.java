package cn.nlifew.linovel.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.utils.DisplayUtils;


public class RoofItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "RoofItemDecoration";

    public interface ItemHelper {
        int getItemType(int position);
        void draw(Canvas c, int l, int t, int r, int b, int type);
    }

    public RoofItemDecoration(ItemHelper helper) {
        mHelper = helper;
    }

    private final ItemHelper mHelper;
    private final int DP30 = DisplayUtils.dp2px(30);

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.set(0, 0, 0, 0);

        final int position = parent.getChildAdapterPosition(view);
        final int type = mHelper.getItemType(position);
        if (position == 0 || type != mHelper.getItemType(position - 1)) {
            outRect.top = DP30;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int itemCount = state.getItemCount();
        int lastItemType, curItemType = -1;
        for (int i = 0, n = parent.getChildCount(); i < n; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            lastItemType = curItemType;
            curItemType = mHelper.getItemType(position);

            if (position != 0 && lastItemType == curItemType) {
                continue;
            }

            int bottom = Math.max(DP30, child.getTop());
            if (position + 1 < itemCount) {
                int b;
                int nextItemType = mHelper.getItemType(position + 1);
                if (nextItemType != curItemType && (b = child.getBottom()) <= DP30) {
                    bottom = b;
                }
            }
            int top = bottom - DP30;
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            mHelper.draw(c, left, top, right, bottom, curItemType);
        }
    }
}
