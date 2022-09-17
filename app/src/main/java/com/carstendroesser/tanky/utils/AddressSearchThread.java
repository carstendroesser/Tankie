package com.carstendroesser.tanky.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.List;

/**
 * Created by carstendrosser on 13.06.16.
 */
public class AddressSearchThread {

    /**
     * Starts an address-search for a given string. Will call the listener
     * with a list of detected addresses.
     *
     * @param pContext
     * @param pInput
     * @param pListener
     */
    public static void searchForAddress(final Context pContext, final String pInput, final OnAddressSearchListener pListener) {
        // do it asynchron..
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // get all the addresses by the given input
                    final List<Address> addresses = new Geocoder(pContext).getFromLocationName(pInput, 10);

                    // notify the listener in the main-ui-thread that the search is finished
                    postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pListener != null) {
                                pListener.onAddressesSearchFinished(addresses);
                            }
                        }
                    });
                } catch (IOException pException) {
                    // something went wrong
                    pException.printStackTrace();
                    // notify the listener in the main-ui-thread that there was an error
                    postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pListener != null) {
                                pListener.onAddressesSearchError();
                            }
                        }
                    });
                }

            }
        }).start();
    }

    // PRIVATE-API

    /**
     * Calls the runnable within the main-ui-thread.
     *
     * @param pRunnable the code to call within the main-ui-thread
     */
    private static void postToMainThread(Runnable pRunnable) {
        new Handler(Looper.getMainLooper()).post(pRunnable);
    }

    // INTERFACES

    /**
     * Listener used to get notified about address-search actions.
     */
    public interface OnAddressSearchListener {

        /**
         * Called as soon as addresses have been retreived.
         *
         * @param addresses a list of addresses, that match the given input
         */
        void onAddressesSearchFinished(List<Address> addresses);

        /**
         * Called as soon as an error occured.
         */
        void onAddressesSearchError();
    }

}
