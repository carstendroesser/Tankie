package com.carstendroesser.tanky.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carstendroesser.tanky.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by carstendrosser on 10.06.16.
 */
public class TagView extends LinearLayout {

    // MEMBERS

    @Bind(R.id.tagview_textview)
    protected TextView mTextView;

    // CONSTRUCTORS

    public TagView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public TagView(Context pContext, AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    /**
     * Constructor used for the layoutfiles.
     *
     * @param pContext we need that
     * @param pAttrs   attributes that were set within xml
     */
    public TagView(Context pContext, AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);
        setup();
    }

    // PRIVATE-API

    private void setup() {
        inflate(getContext(), R.layout.layout_tagview, this);
        ButterKnife.bind(this);
    }

    // PUBLIC-API

    /**
     * Sets the text of the TagView.
     *
     * @param pText the text to show
     */
    public void setTagText(CharSequence pText) {
        mTextView.setText(pText);
    }

}
