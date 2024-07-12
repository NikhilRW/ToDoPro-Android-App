package com.example.to_do_list.Backend.Adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_do_list.R;

public class CategoryViewHolder  extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView listIcons;
    LinearLayout theLayout;
    public CategoryViewHolder(View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.textView5);
        listIcons=itemView.findViewById(R.id.imageView);
        theLayout=itemView.findViewById(R.id.theLayout);
    }
}
