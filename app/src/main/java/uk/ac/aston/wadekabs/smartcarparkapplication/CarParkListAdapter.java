package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

/**
 * Recycler.Adapter for holding car parks.
 */

class CarParkListAdapter extends RecyclerView.Adapter<CarParkListAdapter.CarParkViewHolder> {

    private List<CarPark> mCarParkList;

    CarParkListAdapter(List<CarPark> carParkList) {
        mCarParkList = carParkList;
    }

    @Override
    public CarParkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_park, parent, false);
        return new CarParkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarParkViewHolder holder, int position) {
        CarPark carPark = mCarParkList.get(position);
        holder.mName.setText(carPark.getTitle());
        holder.mOccupancyTrend.setText(carPark.getOccupancyTrend());
    }

    @Override
    public int getItemCount() {
        return mCarParkList.size();
    }

    class CarParkViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mOccupancyTrend;

        CarParkViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.name);
            mOccupancyTrend = (TextView) itemView.findViewById(R.id.occupancy_trend);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    CarPark carPark = mCarParkList.get(position);
                    System.out.println(carPark);
                }
            });
        }
    }
}