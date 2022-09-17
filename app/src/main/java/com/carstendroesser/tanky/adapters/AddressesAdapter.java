package com.carstendroesser.tanky.adapters;

import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carstendroesser.tanky.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carstendrosser on 11.06.16.
 */
public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    // MEMBERS

    private List<Address> mAdresses;
    private OnAddressClickListener mOnAdressClickListener;

    // CONSTRUCTORS

    public AddressesAdapter() {
        mAdresses = new ArrayList<>();
    }

    // ADAPTER

    @Override
    public AddressesAdapter.ViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        // inflate a view for a list-element
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_listitem_address, pParent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressesAdapter.ViewHolder pHolder, final int pPosition) {
        // update the viewholder with the currently-to-be-shown address
        pHolder.setAddress(mAdresses.get(pPosition));

        // set an clicklistener to get notified when this listelement has been clicked
        pHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (mOnAdressClickListener != null) {
                    mOnAdressClickListener.onAddressClick(mAdresses.get(pPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAdresses.size();
    }

    // PUBLIC-API

    /**
     * Updates the datasource for this adapter and will update the
     * list this adapter is attached to.
     *
     * @param pList the new datasource
     */
    public void updateList(List<Address> pList) {
        // replace the current datasource
        mAdresses.clear();
        mAdresses.addAll(pList);

        // update the list
        notifyDataSetChanged();
    }

    /**
     * Clears the datasource and updates the list.
     */
    public void clear() {
        mAdresses.clear();
        notifyDataSetChanged();
    }

    /**
     * Set a listener to get notified about clicks on listelements.
     *
     * @param pListener the listener to notify
     */
    public void setOnAddressClickListener(OnAddressClickListener pListener) {
        mOnAdressClickListener = pListener;
    }

    // INTERFACES

    /**
     * Listener used to retreive callbacks for clicks on an address.
     */
    public interface OnAddressClickListener {
        /**
         * An listelement has been clicked.
         *
         * @param pAddress the clicked address
         */
        void onAddressClick(Address pAddress);
    }

    // VIEWHOLDERS

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // MEMBERS

        private TextView nameTextView;
        private TextView zipTextView;

        // CONSTRUCTOR

        public ViewHolder(View pView) {
            super(pView);
            nameTextView = (TextView) pView.findViewById(R.id.listitemAddressName);
            zipTextView = (TextView) pView.findViewById(R.id.listitemAddressZip);
        }

        // PUBLIC-API

        /**
         * Updates the viewholder's views with the values of the given address.
         *
         * @param pAddress the address to show in this viewholder
         */
        public void setAddress(Address pAddress) {
            nameTextView.setText(pAddress.getLocality());
            zipTextView.setText(pAddress.getCountryCode() + ", " + pAddress.getPostalCode());
        }

    }

}
