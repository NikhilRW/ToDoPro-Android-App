package com.example.to_do_list.Backend.Adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_do_list.Backend.Models.Task;
import com.example.to_do_list.MainActivity;
import com.example.to_do_list.R;
import java.util.ArrayList;
public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>  {
    private final Context context;
    private ArrayList<String> arr,arrFiltered;
    private final ArrayList<Integer> arr2;
    private boolean isCheckBoxEnabled = true;
    int isCompleted;
    private final MainActivity mainActivity;
    public CustomAdapter(Context context, ArrayList<String> arr, MainActivity mainActivity,ArrayList<Integer> arr2) {
        this.context = context;
        this.arr = arr;
        this.mainActivity = mainActivity;
        arrFiltered= new ArrayList<>(arr);
        mainActivity.nothingToDo(arr.isEmpty());
        this.arr2=arr2;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position){
        Log.d("nik", "onBindViewHolder: "+position);
        try {
            holder.checkBox.setChecked(mainActivity.toDoDatabaseManager.getTaskDetails(arr2.get(position)).getIsCompleted() == 1);
            if(arrFiltered.get(position).length()>25){
                String longLengthTask = arrFiltered.get(position).substring(0,35)+"...";
                holder.textView.setText(longLengthTask);
            } else if (arrFiltered.get(position).contains("\n")) {
                int indexOfNewLineCharacter= arrFiltered.get(position).indexOf("\n");
                String longLengthTask = arrFiltered.get(position).substring(0,indexOfNewLineCharacter)+"...";
                holder.textView.setText(longLengthTask);
            } else{
                holder.textView.setText(arrFiltered.get(position));
            }
            holder.checkBox.setEnabled(isCheckBoxEnabled);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isCompleted=isChecked?1:0;
                mainActivity.toDoDatabaseManager.setIsCompleted(arr2.get(position),isCompleted);
                if(isChecked){mainActivity.mediaPlayer.start();};
                if (isChecked&&mainActivity.isShowOnlyNotCompleted) {
                    removeItemWithAnimation(position);
                }
            });
            holder.linearLayout.setOnClickListener(v -> {
                mainActivity.swtichToEditTaskActivity(arr2.get(position));
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void removeItemWithAnimation(int position) {
        setCheckBoxesEnabled(false);
        new Handler().postDelayed(() -> {
            arr.remove(position);
            arr2.remove(position);
            arrFiltered.remove(position);
            if(arr.isEmpty()){
                mainActivity.nothingToDo(true);
                mainActivity.setSupportActionBarTitle("Just Chill");
            }
            else{
                mainActivity.nothingToDo(false);
            }
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arr.size());
            setCheckBoxesEnabled(true);
        }, 8);
    }
    @Override
    public int getItemCount() {
        return arrFiltered.size();
    }
    public void setCheckBoxesEnabled(boolean enabled) {
        this.isCheckBoxEnabled = enabled;
          notifyDataSetChanged();
    }

}
