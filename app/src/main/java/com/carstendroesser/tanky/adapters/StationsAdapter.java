package com.carstendroesser.tanky.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carstendroesser.tanky.R;
import com.carstendroesser.tanky.models.Station;
import com.carstendroesser.tanky.utils.TextUtils;
import com.carstendroesser.tanky.views.TagView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.ViewHolder> {

    // MEMBERS

    private List<Station> mStations;
    private OnStationClickListener mOnStationClickListener;

    // CONSTRUCTORS

    public StationsAdapter() {
        mStations = new ArrayList<>();
    }

    // ADAPTER

    @Override
    public StationsAdapter.ViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        // inflate a new view for the listitems
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_listitem_station, pParent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StationsAdapter.ViewHolder pHolder, final int pPosition) {
        // fill the currently to-be-shown viewholder with the appropriate station
        pHolder.setStation(mStations.get(pPosition));

        // set a clicklistener on the listitem
        pHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (mOnStationClickListener != null) {
                    mOnStationClickListener.onStationClick(mStations.get(pPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStations.size();
    }

    // PUBLIC-API

    /**
     * Updates the datasource.
     *
     * @param pList the new datasource
     */
    public void updateList(List<Station> pList) {
        // replace the datasource
        mStations.clear();
        mStations.addAll(pList);

        // update the listview
        notifyDataSetChanged();
    }

    /**
     * Set a listener to get notified about clicks on stations.
     *
     * @param pListener the listener to notify
     */
    public void setOnStationClickListener(OnStationClickListener pListener) {
        mOnStationClickListener = pListener;
    }

    // INTERFACES

    public interface OnStationClickListener {
        void onStationClick(Station pStation);
    }

    // VIEWHOLDERS

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // MEMBERS

        private ImageView iconImageView;
        private TextView brandTextView;
        private TextView distanceTextView;
        private TagView priceTagView;
        private TextView adressTextView;
        private TextView isOpenTextView;

        // CONSTRUCTOR

        public ViewHolder(View pView) {
            super(pView);

            iconImageView = (ImageView) pView.findViewById(R.id.listitemIconImageView);
            brandTextView = (TextView) pView.findViewById(R.id.listitemBrandTextView);
            distanceTextView = (TextView) pView.findViewById(R.id.listitemDistanceTextView);
            priceTagView = (TagView) pView.findViewById(R.id.listitemPriceTagView);
            adressTextView = (TextView) pView.findViewById(R.id.listitemStreetTextView);
            isOpenTextView = (TextView) pView.findViewById(R.id.listitemIsOpenTextView);
        }

        // PUBLIC-API

        /**
         * Sets a station to this viewholder and updates all views.
         *
         * @param pStation the station to show within this viewholder
         */
        public void setStation(Station pStation) {
            brandTextView.setText(pStation.getBrand());
            distanceTextView.setText("" + pStation.getDistance() + itemView.getContext().getString(R.string.distance));

            // show price only if the station is open!
            priceTagView.setTagText(pStation.getIsOpen() ?
                    TextUtils.downsize(4, 5, "" + pStation.getPrice() + " €")
                    : itemView.getContext().getString(R.string.not_available));

            adressTextView.setText(pStation.getStreet() + " " + pStation.getHouseNumber());

            // show if open or closed
            if (pStation.getIsOpen()) {
                isOpenTextView.setText(R.string.is_open);
                isOpenTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.colorAccent));
            } else {
                isOpenTextView.setText(R.string.is_closed);
                isOpenTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            }
        }

    }

}
