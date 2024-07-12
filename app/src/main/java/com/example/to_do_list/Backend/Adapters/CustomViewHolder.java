package com.example.to_do_list.Backend.Adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_do_list.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    CheckBox checkBox;
    TextView textView;
    LinearLayout linearLayout;
    public CustomViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checkBox);
        textView = itemView.findViewById(R.id.textView);
        linearLayout = itemView.findViewById(R.id.linear);
    }
}
