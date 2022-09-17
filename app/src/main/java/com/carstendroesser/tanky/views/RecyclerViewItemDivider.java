package com.carstendroesser.tanky.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.carstendroesser.tanky.R;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class RecyclerViewItemDivider extends RecyclerView.ItemDecoration {

    // MEMBERS

    private Drawable mDivider;

    // CONSTRUCTOR

    public RecyclerViewItemDivider(Context pContext) {
        mDivider = pContext.getResources().getDrawable(R.drawable.stations_list_divider);
    }

    // PUBLIC-API

    /**
     * Draws lines above the RecyclerView between each listitem.
     *
     * @param pCanvas the canvas of the recyclerview
     * @param pParent the recyclerview
     * @param pState  i don't know what this is for
     */
    public void onDrawOver(Canvas pCanvas, RecyclerView pParent, RecyclerView.State pState) {
        int left = pParent.getPaddingLeft();
        int right = pParent.getWidth() - pParent.getPaddingRight();

        int childCount = pParent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = pParent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(pCanvas);
        }
    }

}
