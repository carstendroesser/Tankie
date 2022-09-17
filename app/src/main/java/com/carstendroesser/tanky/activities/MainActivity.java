package com.carstendroesser.tanky.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carstendroesser.tanky.R;
import com.carstendroesser.tanky.adapters.AddressesAdapter;
import com.carstendroesser.tanky.adapters.StationsAdapter;
import com.carstendroesser.tanky.database.Database;
import com.carstendroesser.tanky.dialogs.FiltersDialog;
import com.carstendroesser.tanky.models.Price;
import com.carstendroesser.tanky.models.SearchParams;
import com.carstendroesser.tanky.models.Station;
import com.carstendroesser.tanky.network.RestClient;
import com.carstendroesser.tanky.utils.AddressSearchThread;
import com.carstendroesser.tanky.views.RecyclerViewItemDivider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements Callback<List<Station>>, StationsAdapter.OnStationClickListener, AddressesAdapter.OnAddressClickListener, AddressSearchThread.OnAddressSearchListener, FiltersDialog.OnFiltersChangedListener, Database.DatabaseCallback {

    // VIEWS

    @Bind(R.id.searchContainer)
    LinearLayout mSearchContainer;
    @Bind(R.id.searchcontainerBackButton)
    ImageView mSearchContainerBackButton;
    @Bind(R.id.searchcontainerSearchEditText)
    EditText mSearchContainerEditText;
    @Bind(R.id.searchcontainerRecyclerView)
    RecyclerView mSearchContainerRecyclerView;
    @Bind(R.id.searchContainerEmptyContainer)
    View mSearchContainerEmptyContainer;
    @Bind(R.id.searchContainerErrorContainer)
    View mSearchContainerErrorContainer;
    @Bind(R.id.searchContainerLoadingContainer)
    View mSearchContainerLoadingContainer;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.loadingContainer)
    View mLoadingContainer;
    @Bind(R.id.emptyContainer)
    View mEmptyContainer;
    @Bind(R.id.errorContainer)
    View mErrorContainer;
    @Bind(R.id.errorRetryButton)
    Button mErrorRetryButton;
    @Bind(R.id.resultsRecyclerView)
    RecyclerView mResultsRecyclerView;
    @Bind(R.id.searchFloatingActionButton)
    FloatingActionButton mSearchFAB;

    // ENUMS

    /**
     * Used to show only one view, replacing each other view
     * in the main-container.
     */
    private enum ListPlaceHolder {
        LOADING,
        EMPTY,
        ERROR,
        LIST;
    }

    // MEMBERS

    private RestClient mRestClient;
    private StationsAdapter mStationsAdapter;
    private AddressesAdapter mAddressesAdapter;
    private boolean mIsSearchingForAddress;

    // CONSTRUCTOR

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.layout_activity_main);
        ButterKnife.bind(this);

        setup();

        checkForLicense();
    }

    // ACTIVITY

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_main, pMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pMenuItem) {
        if (pMenuItem.getItemId() == R.id.menuitem_search) {
            showSearchPanel();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // if searchpanel is visible, hide it
        if (isSearchPanelVisible()) {
            hideSearchPanel();
        } else {
            super.onBackPressed();
        }
    }

    // PRIVATE-API

    private void setup() {
        // we are currently not searching for an address
        mIsSearchingForAddress = false;

        // update toolbar
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        // setup address-search-container
        mSearchContainer.setVisibility(View.GONE);
        mSearchContainerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSearchPanel();
            }
        });

        // as soon as search on the keyboard is pressed, start a search
        mSearchContainerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textview, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForAddress();
                    return true;
                }
                return false;
            }
        });

        mSearchContainerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddressesAdapter = new AddressesAdapter();
        mAddressesAdapter.setOnAddressClickListener(this);
        mSearchContainerRecyclerView.setAdapter(mAddressesAdapter);

        // setup the main-results-list
        mResultsRecyclerView.addItemDecoration(new RecyclerViewItemDivider(this));
        mResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStationsAdapter = new StationsAdapter();
        mStationsAdapter.setOnStationClickListener(this);
        mResultsRecyclerView.setAdapter(mStationsAdapter);

        // show empty-state for both, stations and addresses list
        showResultsPlaceHolder(ListPlaceHolder.EMPTY);
        showAddressesPlaceHolder(ListPlaceHolder.EMPTY);

        // setup the network-client
        mRestClient = new RestClient(RestClient.Type.STATIONS);
    }

    /**
     * Checks if the user accepted the license the Tankerkönig-api is licensed with.
     */
    private void checkForLicense() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("acceptedlicense", false)) {
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage(getString(R.string.info_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface pDialog, int pWhich) {
                            pDialog.dismiss();
                            prefs.edit().putBoolean("acceptedlicense", true).apply();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface pDialog, int pWhich) {
                            finish();
                        }
                    })
                    .show();
        }

    }

    /**
     * Show the given placeholder and hides every other view in place of the
     * stationslist.
     *
     * @param pPlaceHolder the type of placeholder to show
     */
    private void showResultsPlaceHolder(ListPlaceHolder pPlaceHolder) {
        mLoadingContainer.setVisibility(pPlaceHolder == ListPlaceHolder.LOADING ? View.VISIBLE : View.GONE);
        mEmptyContainer.setVisibility(pPlaceHolder == ListPlaceHolder.EMPTY ? View.VISIBLE : View.GONE);
        mErrorContainer.setVisibility(pPlaceHolder == ListPlaceHolder.ERROR ? View.VISIBLE : View.GONE);
        mResultsRecyclerView.setVisibility(pPlaceHolder == ListPlaceHolder.LIST ? View.VISIBLE : View.GONE);
    }

    /**
     * Shows the given placeholder and hides every other view in place of the address
     * list.
     *
     * @param pPlaceHolder the type of placeholder to show
     */
    private void showAddressesPlaceHolder(ListPlaceHolder pPlaceHolder) {
        mSearchContainerLoadingContainer.setVisibility(pPlaceHolder == ListPlaceHolder.LOADING ? View.VISIBLE : View.GONE);
        mSearchContainerEmptyContainer.setVisibility(pPlaceHolder == ListPlaceHolder.EMPTY ? View.VISIBLE : View.GONE);
        mSearchContainerErrorContainer.setVisibility(pPlaceHolder == ListPlaceHolder.ERROR ? View.VISIBLE : View.GONE);
        mSearchContainerRecyclerView.setVisibility(pPlaceHolder == ListPlaceHolder.LIST ? View.VISIBLE : View.GONE);
    }

    /**
     * Shows the searchpanel for searching an address.
     */
    private void showSearchPanel() {
        if (!isSearchPanelVisible()) {
            // set focus to inputfield
            mSearchContainerEditText.requestFocus();

            // show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchContainerEditText, 0);

            // show the searchpanel
            mSearchContainer.setVisibility(View.VISIBLE);

            // hide the floatingactionbutton
            mSearchFAB.hide();
        }
    }

    /**
     * Hides the searchpanel for searching an address.
     */
    private void hideSearchPanel() {
        if (isSearchPanelVisible()) {
            // hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchContainerEditText.getWindowToken(), 0);

            // make address-inputfield lose focus
            mSearchContainerEditText.clearFocus();

            // hide the searchcontainer
            mSearchContainer.setVisibility(View.GONE);

            // show floatingactionbutton
            mSearchFAB.show();
        }
    }

    /**
     * Checks for the visibility of the address-searchpanel.
     *
     * @return true if the searchpanel is visible at the moment
     */
    private boolean isSearchPanelVisible() {
        return mSearchContainer.getVisibility() == View.VISIBLE;
    }

    /**
     * Helper-method to show a notification-snackbar.
     *
     * @param pMessage the message to notify
     */
    private void notify(String pMessage) {
        Snackbar.make(mResultsRecyclerView,
                pMessage,
                Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Helper-method to show a notification-snackbar with an ok-button.
     *
     * @param pMessage the message to show
     */
    private void notifyWithOk(String pMessage) {
        final Snackbar snackbar = Snackbar.make(mResultsRecyclerView, pMessage, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                snackbar.dismiss();
            }
        });
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    /**
     * Checks if a position is selected and then starts a network-request
     * to retreive stations.
     */
    private void loadStations() {

        // check if a location is set
        if (SearchParams.latitude == null || SearchParams.longitude == null) {
            notify(getString(R.string.no_address));
            return;
        }

        // we have a location, start loading!
        showResultsPlaceHolder(ListPlaceHolder.LOADING);
        mRestClient.getApiService().getGasStations(SearchParams.latitude,
                SearchParams.longitude,
                "" + SearchParams.radius,
                SearchParams.type,
                SearchParams.sortby,
                this);
    }

    /**
     * Starts a network-request to lookup for addresses.
     */
    private void searchForAddress() {
        // check if we are already searching for an address
        // to prevent multiple calls at the same time
        if (mIsSearchingForAddress) {
            notify(getString(R.string.already_searching));
        } else {
            mIsSearchingForAddress = true;
            showAddressesPlaceHolder(ListPlaceHolder.LOADING);
            AddressSearchThread.searchForAddress(this, mSearchContainerEditText.getText().toString(), this);
        }
    }

    // VIEW-ACTIONS

    @OnClick(R.id.searchFloatingActionButton)
    protected void onSearchFloatingActionButtonClicked() {
        // the user wants to change the filters, show them!
        FiltersDialog filtersDialog = new FiltersDialog(this, this);
        filtersDialog.show();
    }

    @OnClick(R.id.errorRetryButton)
    protected void onErrorRetryButtonClicked() {
        loadStations();
    }

    // LIST-ACTIONS

    @Override
    public void onStationClick(Station pStation) {
        // open the details for the clicked station
        Intent intent = new Intent(this, DetailsActivity.class);

        // set some extra values to pass the id and brand of the station
        intent.putExtra(DetailsActivity.ARGUMENT_STATION_ID, pStation.getId());
        intent.putExtra(DetailsActivity.ARGUMENT_STATION_BRAND, pStation.getBrand());

        // start the DetailsActivity
        startActivity(intent);
    }

    @Override
    public void onAddressClick(Address pAddress) {
        // update the searchparams
        SearchParams.updateByAddress(pAddress);

        // show the station-results-list
        hideSearchPanel();

        // reload stations with the updated address
        loadStations();
    }

    // NETWORK CALLBACKS

    @Override
    public void success(List<Station> pStations, Response pResponse) {

        // update data-source
        mStationsAdapter.updateList(pStations);

        // show the correct placeholder
        if (pStations.isEmpty()) {
            showResultsPlaceHolder(ListPlaceHolder.EMPTY);
        } else {
            showResultsPlaceHolder(ListPlaceHolder.LIST);
        }

        notify(pStations.size() + getString(R.string.amount_stations_found));

        // update the prices-database
        final List<Price> prices = new ArrayList<>();

        // get the current hour the price is for
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // get all the prices of the retreived stations...
        for (Station station : pStations) {
            Price price = new Price(SearchParams.type, station.getPrice(), hour);
            prices.add(price);
        }

        // ... and insert them into the database
        Database.getInstance(this).insertPrices(prices, this);
    }

    @Override
    public void failure(RetrofitError pError) {
        // something went wrong, may be an network-error
        showResultsPlaceHolder(ListPlaceHolder.ERROR);
        notify(getString(R.string.connection_error_message));
    }

    // ADDRESS-SEARCH CALLBACKS

    @Override
    public void onAddressesSearchFinished(List<Address> pAddresses) {
        mIsSearchingForAddress = false;

        if (pAddresses == null) {
            // no addresses found
            mAddressesAdapter.clear();
            showAddressesPlaceHolder(ListPlaceHolder.EMPTY);
        } else {

            // check all the addresses for being german and remove non-german addresses
            for (Iterator<Address> iterator = pAddresses.iterator(); iterator.hasNext(); ) {
                Address address = iterator.next();
                if (address.getCountryCode() == null || !address.getCountryCode().equals("DE")) {
                    iterator.remove();
                }
            }

            if (pAddresses.isEmpty()) {
                // no addresses found
                mAddressesAdapter.clear();
                showAddressesPlaceHolder(ListPlaceHolder.EMPTY);
            } else {
                // addresses found
                mAddressesAdapter.updateList(pAddresses);
                showAddressesPlaceHolder(ListPlaceHolder.LIST);
            }
        }
    }

    @Override
    public void onAddressesSearchError() {
        // something went wrong when searching for an address
        mIsSearchingForAddress = false;
        mAddressesAdapter.clear();
        showAddressesPlaceHolder(ListPlaceHolder.ERROR);
    }

    // FILTER-CALLBACKS

    @Override
    public void onFiltersChanged() {
        // the filters were changed
        loadStations();
    }

    // DATABASE-CALLBACKS

    @Override
    public void onPricesInserted() {
        // new price have been inserted into the database

        // recalculate the time to get the best average price
        Database.getInstance(this).getBestPriceFor(SearchParams.type, this);
    }

    @Override
    public void onBestPriceCalculated(final Price pPrice) {
        // the best-time-price has been calculated
        // show it after a delay of 1 second

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String priceValue = String.format("%.2f", pPrice.getPriceValue());
                notifyWithOk("Du tankst mit durchschnittlich "
                        + priceValue + "€ am besten zwischen "
                        + pPrice.getPriceTime()
                        + ":00 und "
                        + ((pPrice.getPriceTime() + 1) % 24)
                        + ":00 Uhr.");
            }
        }, 2500);

    }

}
