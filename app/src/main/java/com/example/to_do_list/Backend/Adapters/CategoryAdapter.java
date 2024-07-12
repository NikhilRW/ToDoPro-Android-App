package com.example.to_do_list.Backend.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_do_list.MainActivity;
import com.example.to_do_list.R;
import java.util.ArrayList;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context context;
    private ArrayList<String> arr;
    ArrayList<String> allCategories;
    private MainActivity mainActivity;

    public CategoryAdapter(Context context, ArrayList<String> arr, MainActivity mainActivity) {
        this.context = context;
        this.arr = arr;
        this.mainActivity = mainActivity;
        allCategories= mainActivity.toDoDatabaseManager.getAllTaskCategory();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.textView.setText(allCategories.get(position));
        holder.theLayout.setOnClickListener(v->{
            mainActivity.changeTaskCategory(allCategories.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
