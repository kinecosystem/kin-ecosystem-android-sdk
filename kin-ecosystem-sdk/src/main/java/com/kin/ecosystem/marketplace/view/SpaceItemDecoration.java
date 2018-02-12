package com.kin.ecosystem.marketplace.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int lastIndex = state.getItemCount() - 1;
        final int index = parent.getChildLayoutPosition(view);

        if (index == 0) {
            outRect.left = space / 2;
        }
        else if (index == lastIndex) {
            outRect.right = space / 2;
            outRect.left = space;
        }
        else {
            outRect.left = space;
        }

    }
}
