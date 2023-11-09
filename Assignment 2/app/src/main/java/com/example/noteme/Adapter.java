package com.example.noteme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Display a list of notes
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    // Member variables
    private LayoutInflater inflater;
    private List<Coordinate> coordinates;
    private OnDeleteListener removeListener;
    private OnEditListener changeListener;

    Adapter(Context context, List<Coordinate> coordinates, OnDeleteListener removeListener, OnEditListener changeListener) {
        this.inflater = LayoutInflater.from(context);
        this.coordinates = coordinates;
        this.removeListener = removeListener;
        this.changeListener=changeListener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.custom_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //Get note at current position
        holder.address.setText(coordinates.get(position).getAddress());
        holder.latitude.setText(coordinates.get(position).getLatitude()+"");
        holder.longitude.setText(coordinates.get(position).getLongitude()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coordinate coordinateToEdit = coordinates.get(position);
                changeListener.onEdit(coordinateToEdit);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Coordinate coordinateToDelete = coordinates.get(position);
                removeListener.onDelete(coordinateToDelete);
            }
        });


//        // Debugging
//        Log.d("Color", "Color -> " + colorOfNote);
    }

    @Override
    public int getItemCount() {
        // Return the total number of notes
        return coordinates.size();
    }

    //Each row of the note item
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView latitude, longitude, address;
        ImageButton delete;
        ConstraintLayout nColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind the views from the layout to the ViewHolder's member variables
            latitude = itemView.findViewById(R.id.lat);
            longitude = itemView.findViewById(R.id.lon);
            address = itemView.findViewById(R.id.address);
            delete = itemView.findViewById(R.id.deleteNote);
        }
    }

    public void updateNotes(List<Coordinate> newCoordinates) {
        coordinates = newCoordinates;
        notifyDataSetChanged();
    }

    public interface OnDeleteListener {
        void onDelete(Coordinate coordinate);
    }
    public interface OnEditListener {
        void onEdit(Coordinate coordinate);
    }
}

