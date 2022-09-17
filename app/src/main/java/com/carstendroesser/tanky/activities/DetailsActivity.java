package com.carstendroesser.tanky.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carstendroesser.tanky.R;
import com.carstendroesser.tanky.models.Station;
import com.carstendroesser.tanky.network.RestClient;
import com.carstendroesser.tanky.utils.TextUtils;
import com.carstendroesser.tanky.views.TagView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, Callback<Station> {

    // CONSTANTS

    public static final String ARGUMENT_STATION_BRAND = "stationName";
    public static final String ARGUMENT_STATION_ID = "stationId";

    // MEMBERS

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.loadingContainer)
    protected View mLoadingView;
    @Bind(R.id.errorContainer)
    protected View mErrorView;
    @Bind(R.id.errorRetryButton)
    protected Button mErrorRetryButton;
    @Bind(R.id.mainContent)
    protected View mMainContent;
    @Bind(R.id.detailsNameTextView)
    protected TextView mNameTextView;
    @Bind(R.id.detailsIsOpenTextView)
    protected TextView mIsOpenTextView;
    @Bind(R.id.detailsPricesLinearLayout)
    protected LinearLayout mPricesLinearLayout;
    @Bind(R.id.dieselPriceTagView)
    protected TagView mDieselTagView;
    @Bind(R.id.e5PriceTagView)
    protected TagView mE5TagView;
    @Bind(R.id.e10PriceTagView)
    protected TagView mE10TagView;
    @Bind(R.id.detailsAddressStreetTextView)
    protected TextView mAddressStreetTextView;
    @Bind(R.id.detailsAddressPlaceTextView)
    protected TextView mAddressPlaceTextView;
    @Bind(R.id.detailsOpeningsLinearLayout)
    protected LinearLayout mOpeningsLinearLayout;

    private RestClient mRestClient;
    private String mStationId;
    private GoogleMap mGoogleMap;
    private Station mStation;

    // ENUMS

    /**
     * States used to show only one of the views, replacing each other.
     */
    private enum DetailsPlaceHolder {
        LOADING,
        ERROR,
        DETAILS;
    }

    // LIFECYCLE

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.layout_activity_details);
        ButterKnife.bind(this);
        setup();
    }

    // PRIVATE-API

    private void setup() {

        // retrieve passed station-id
        mStationId = getIntent().getStringExtra(ARGUMENT_STATION_ID);

        // setup network-client
        mRestClient = new RestClient(RestClient.Type.STATION);

        // setup toolbar
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.setTitle(getIntent().getStringExtra(ARGUMENT_STATION_BRAND));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                onBackPressed();
            }
        });

        // load details for this station
        loadDetails();

        // setup mapfragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    /**
     * Starts a network-call to retrieve the details for the specific station.
     * Will replace the maincontent by a loading-placeholder.
     */
    private void loadDetails() {
        showPlaceHolder(DetailsPlaceHolder.LOADING);
        mRestClient.getApiService().getStationForId(mStationId, this);
    }

    /**
     * Show exactly one content-view and makes any other state-view disappear.
     *
     * @param pPlaceHolder the type of view to show
     */
    private void showPlaceHolder(DetailsPlaceHolder pPlaceHolder) {
        mLoadingView.setVisibility(pPlaceHolder == DetailsPlaceHolder.LOADING ? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(pPlaceHolder == DetailsPlaceHolder.ERROR ? View.VISIBLE : View.GONE);
        mMainContent.setVisibility(pPlaceHolder == DetailsPlaceHolder.DETAILS ? View.VISIBLE : View.GONE);
    }

    /**
     * Should be called as soon as either the map or the details have been retrieved.
     * If the map has been setup and the details are retrieved already, the content
     * is updated and will be shown.
     */
    private void updateContent() {
        // check if the map and the details have been loaded
        if (mGoogleMap == null || mStation == null) {
            // if not both, do nothing yet
            return;
        } else {
            // else we are sure that everything is setup to show
            // the details properly
            showPlaceHolder(DetailsPlaceHolder.DETAILS);

            // place a marker on the map
            LatLng ltdlng = new LatLng(mStation.getLatitude(), mStation.getLongitude());
            CameraUpdate center = CameraUpdateFactory.newLatLng(ltdlng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mGoogleMap.addMarker(new MarkerOptions().position(ltdlng).title(mStation.getBrand()));
            mGoogleMap.moveCamera(center);
            mGoogleMap.animateCamera(zoom);

            // update or hide the prices if the station is closed
            if (mStation.getIsOpen()) {
                mDieselTagView.setTagText(TextUtils.downsize(4, 5, "" + mStation.getPriceDiesel() + " €"));
                mE5TagView.setTagText(TextUtils.downsize(4, 5, "" + mStation.getPriceE5() + " €"));
                mE10TagView.setTagText(TextUtils.downsize(4, 5, "" + mStation.getPriceE10() + " €"));
            } else {
                mPricesLinearLayout.setVisibility(View.GONE);
            }

            // update name
            mNameTextView.setText(mStation.getName());

            // show if the station is open atm
            if (mStation.getIsOpen()) {
                mIsOpenTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                mIsOpenTextView.setText(R.string.is_open);
            } else {
                mIsOpenTextView.setTextColor(getResources().getColor(R.color.red));
                mIsOpenTextView.setText(R.string.is_closed);
            }

            // setup address
            mAddressStreetTextView.setText(mStation.getStreet() + " " + mStation.getHouseNumber());
            mAddressPlaceTextView.setText(mStation.getPostCode() + " " + mStation.getPlace());

            // show list of openings
            for (Station.OpeningTime openingTime : mStation.getOpenings()) {
                View openingEntry = LayoutInflater.from(this).inflate(R.layout.layout_listitem_opening, null);
                TextView daysTextView = (TextView) openingEntry.findViewById(R.id.listitemOpeningDaysTextView);
                TextView timesTextView = (TextView) openingEntry.findViewById(R.id.listitemOpeningTimeTextView);
                daysTextView.setText(openingTime.getText());
                timesTextView.setText(openingTime.getStart() + getString(R.string.openingtime_conjunction) + openingTime.getEnd());
                mOpeningsLinearLayout.addView(openingEntry);
            }

        }
    }

    // MAP-CALLBACKS

    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        // the map is displayed now!
        mGoogleMap = pGoogleMap;

        // update content, if the station-details are retreived already
        updateContent();
    }

    // VIEW-CALLBACKS

    @OnClick(R.id.errorRetryButton)
    protected void onErrorRetryButtonClicked() {
        loadDetails();
    }

    // NETWORK-CALLBACKS

    @Override
    public void success(Station pStation, Response pResponse) {
        mStation = pStation;
        updateContent();
    }

    @Override
    public void failure(RetrofitError pError) {
        showPlaceHolder(DetailsPlaceHolder.ERROR);
    }

}
