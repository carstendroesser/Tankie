package com.carstendroesser.tanky.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

/**
 * Created by carstendrosser on 11.06.16.
 */
public class TextUtils {

    // PUBLIC-API

    /**
     * Downsizes a part of a string.
     *
     * @param pStart the startposition of the downsized part
     * @param pEnd   the endposition of the downsized part
     * @param pText   the text
     * @return the partly downsized charsequence
     */
    public static CharSequence downsize(int pStart, int pEnd, String pText) {
        SpannableString spannableString = new SpannableString(pText);
        spannableString.setSpan(new RelativeSizeSpan(0.5f), pStart, pEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
