package com.carstendroesser.tanky.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.carstendroesser.tanky.R;
import com.carstendroesser.tanky.models.SearchParams;

/**
 * Created by carstendrosser on 20.11.15.
 */
public class FiltersDialog extends AlertDialog {

    // CONSTANTS

    private final static int MIN_RADIUS = 5;

    // MEMBERS

    private TextView mRadiusTextView;
    private SeekBar mRadiusSeekBar;
    private RadioGroup mTypeRadioGroup;
    private CheckBox mFilterOpeningsCheckBox;
    private RadioGroup mSortByRadioGroup;


    /**
     * Private constructor, called by the other one.
     *
     * @param pContext we need that
     */
    private FiltersDialog(Context pContext) {
        super(pContext);

        // inflate the content-view
        View content = LayoutInflater.from(pContext).inflate(R.layout.layout_dialog_filters, null);
        setView(content);

        // setup the seekbar for radius
        mRadiusSeekBar = (SeekBar) content.findViewById(R.id.filtersRadiusSeekBar);
        mRadiusSeekBar.setProgress(SearchParams.radius - MIN_RADIUS);
        mRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar pSeekBar, int pProgress, boolean pFromUser) {
                if (pFromUser) {
                    mRadiusTextView.setText("" + (pProgress + MIN_RADIUS) + " km");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar pSeekBar) {
                // empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar pSeekBar) {
                // empty
            }
        });

        // setup the textview for the radius
        mRadiusTextView = (TextView) content.findViewById(R.id.filtersRadiusTextView);
        mRadiusTextView.setText("" + SearchParams.radius + " km");

        // setup the radiogroup for fuel-types
        mTypeRadioGroup = (RadioGroup) content.findViewById(R.id.filtersTypeRadioGroup);

        // set the correct radiobutton
        switch (SearchParams.type) {
            case "diesel":
                mTypeRadioGroup.check(R.id.filtersTypeDiesel);
                break;
            case "e5":
                mTypeRadioGroup.check(R.id.filtersTypeE5);
                break;
            case "e10":
                mTypeRadioGroup.check(R.id.filtersTypeE10);
                break;
        }

        // setup the checkbox for filtering closed stations
        mFilterOpeningsCheckBox = (CheckBox) content.findViewById(R.id.filtersOpeningsCheckBox);
        if (SearchParams.filterClosedStations) {
            mFilterOpeningsCheckBox.setChecked(true);
        }

        // setup the radiogroup for sorting
        mSortByRadioGroup = (RadioGroup) content.findViewById(R.id.filtersSortByRadioGroup);

        // set the correct radiobutton
        switch (SearchParams.sortby) {
            case "price":
                mSortByRadioGroup.check(R.id.filtersSortByPrice);
                break;
            case "dist":
                mSortByRadioGroup.check(R.id.filtersSortByDistance);
                break;
        }

        // add an negative button, doing nothing
        setButton(BUTTON_NEGATIVE, pContext.getResources().getString(R.string.button_cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                pDialog.dismiss();
            }
        });
    }

    /**
     * Creates a dialog showing filters, changing the SearchParams.
     *
     * @param pContext  we need that
     * @param pListener listener to get notified about filter-changes
     */
    public FiltersDialog(Context pContext, final OnFiltersChangedListener pListener) {
        this(pContext);

        setTitle(R.string.filters);

        // add a button to the right
        setButton(BUTTON_POSITIVE, pContext.getResources().getString(R.string.button_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {

                // update the SearchParams.type
                switch (mTypeRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.filtersTypeDiesel:
                        SearchParams.type = "diesel";
                        break;
                    case R.id.filtersTypeE5:
                        SearchParams.type = "e5";
                        break;
                    case R.id.filtersTypeE10:
                        SearchParams.type = "e10";
                        break;
                }

                // update SearchParams.radius & filterClosedStations
                SearchParams.radius = mRadiusSeekBar.getProgress() + MIN_RADIUS;
                SearchParams.filterClosedStations = mFilterOpeningsCheckBox.isChecked();

                // update SearchParams.sortby
                switch (mSortByRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.filtersSortByPrice:
                        SearchParams.sortby = "price";
                        break;
                    case R.id.filtersSortByDistance:
                        SearchParams.sortby = "dist";
                        break;
                }

                // notify the listener and dismiss the dialog
                pListener.onFiltersChanged();
                pDialog.dismiss();
            }
        });
    }

    /**
     * Listener used to get notified about changed regarding the filters.
     */
    public interface OnFiltersChangedListener {
        /**
         * The filters have changed.
         */
        void onFiltersChanged();
    }

}
