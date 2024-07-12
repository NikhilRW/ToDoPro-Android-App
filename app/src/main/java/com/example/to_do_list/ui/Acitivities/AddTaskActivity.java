package com.example.to_do_list.ui.Acitivities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.to_do_list.Backend.Models.Task;
import com.example.to_do_list.Backend.ToDoDatabaseManager;
import com.example.to_do_list.R;
import com.example.to_do_list.ui.NotificationChannel.NotificationReciever;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    Toolbar toolbar;
    ToDoDatabaseManager toDoDatabaseManager;
    EditText taskName,selectedDate,selectedTime;
    ImageButton removeDate,removeTime,setTime,setDate,addTask,previousActivity;
    Spinner setCategory;
    boolean isUpdatedTask=true;
    CheckBox isCompletedCheckBox;
    ImageButton addCategory;
    ArrayAdapter<String> dataAdapter;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_activity);
        ImageButton deleteButton = findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);
        taskName=findViewById(R.id.taskName);
        if(getIntent().getStringExtra("taskName")!=null){
            taskName.setText(getIntent().getStringExtra("taskName"));
        }
        isCompletedCheckBox=findViewById(R.id.isCompletedCheckBox);
        isCompletedCheckBox.setVisibility(View.GONE);
        setCategory=findViewById(R.id.setCategory);
        selectedDate=findViewById(R.id.selectedDate);
        selectedTime=findViewById(R.id.selectedTime);
        addCategory=findViewById(R.id.addCategory);
        removeTime=findViewById(R.id.removeTime);
        addTask=findViewById(R.id.updateTask);
        setTime=findViewById(R.id.setTime);
        setDate=findViewById(R.id.setDate);
        removeDate=findViewById(R.id.removeDate);
        previousActivity = findViewById(R.id.perviousActivity);
        previousActivity.setOnClickListener(v->{
            showExitConfirmationDialog();
        });
        toDoDatabaseManager = new ToDoDatabaseManager(this);
        toDoDatabaseManager.open();
        Intent intent = getIntent();
        String message = "Task Added";
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        if(toast.getView()!=null){
            View toastView = toast.getView();
            toastView.getBackground().setColorFilter(Color.parseColor("#90EE90"), PorterDuff.Mode.SRC_IN); // Light green background
        }
        // Hide the default ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setCategory.getBackground().setColorFilter(getResources().getColor(R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        addOrRefreshCategoryInSpinner();
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCategory.setAdapter(dataAdapter);
        // Set up the custom Toolbar as the ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setNavigationIcon(navigationIcon);
        // Set up the delete button
        // Enable the back button in the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setDate.setOnClickListener(v->{
            showDatePickerDialog();
        });
        removeDate.setOnClickListener(v->{
            selectedDate.setText("");
        });
        removeTime.setOnClickListener(v->{
            selectedTime.setText("");
        });

        setTime.setOnClickListener(v -> {
            showTimePickerDialog();
        });
        addTask.setOnClickListener(v->{
            addTask();
        });
        removeDate.setOnClickListener(v->{
            selectedDate.setText("");
        });
        removeTime.setOnClickListener(v->{
            selectedTime.setText("");
        });
        addCategory.setOnClickListener(v->{
            showInputDialog();
        });
    }

    // Method to show the delete confirmation dialog
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .setTitle("Are You Sure ?")
                .setMessage("Quit Without Adding Task")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        superBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.delete)
                .show();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        long unixTime = calendar.getTimeInMillis() / 1000;
                        updateSelectedDate(calendar);
                        // Handle the Unix timestamp (e.g., store it, display it, etc.)
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    private void updateSelectedDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based in Calendar
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = day + "/" + month + "/" + year;
        selectedDate.setText(dateString);
    }
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        updateSelectedTime(calendar);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addOrRefreshCategoryInSpinner(){
        dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_layout, toDoDatabaseManager.getAllTaskCategory()) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // Customize the dropdown item view here
                if (position == setCategory.getSelectedItemPosition()) {
                    view.setBackgroundColor(Color.parseColor("#016BAB")); // Selected item color
                    textView.setTextColor(Color.WHITE);
                } else {
                    view.setBackgroundColor(Color.WHITE); // Default item color
                }
                return view;
            }
        };

    }
    // Method to update the TextView with the selected time
    private void updateSelectedTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String timeString = String.format("%02d:%02d", hour, minute);
        selectedTime.setText(timeString);
    }
    private long getUnixTime(String dateString, String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString + " " + timeString);
            return date != null ? date.getTime() / 1000 : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private void addTask(){
        if(taskName.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Fill The Task Name And Category", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!selectedDate.getText().toString().isEmpty()&&selectedTime.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Select The Time For Due", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedDate.getText().toString().isEmpty()&&!selectedTime.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Select The Date For Due", Toast.LENGTH_SHORT).show();
            return;
        }
        String newTaskName = taskName.getText().toString();
        int isCompleted = isCompletedCheckBox.isChecked() ? 1:0;
        String selectedTaskCategory = setCategory.getSelectedItem().toString();
        long myDueDate;
        if(!selectedDate.getText().toString().isEmpty()&&!selectedTime.getText().toString().isEmpty()){
            myDueDate = getUnixTime(selectedDate.getText().toString(),selectedTime.getText().toString());
            if(myDueDate<new Date().getTime()){
                Toast.makeText(this, "Invalid Due Date", Toast.LENGTH_SHORT).show();
                return;
            }
            toDoDatabaseManager.addTask(newTaskName,isCompleted,myDueDate,selectedTaskCategory);
            scheduleNotification(AddTaskActivity.this,myDueDate,newTaskName);
        }
        else{
            toDoDatabaseManager.addTask(newTaskName,isCompleted,selectedTaskCategory);
        }
        toast.show();
        setResult(RESULT_OK,getReturnIntent(selectedTaskCategory));
        superBackPressed();
    }
    private void superBackPressed(){
        super.onBackPressed();
    }
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        toDoDatabaseManager.close();
    }
    public void scheduleNotification(Context context, long dueDateUnix, String taskName) {
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra("task_name", taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dueDateUnix * 1000, pendingIntent);
    }
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Task Category To Add");
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editTextInput = customLayout.findViewById(R.id.editTextInput);
                String input = editTextInput.getText().toString();
                if (!input.isEmpty()) {
                    toDoDatabaseManager.addTask(input+" Task",0,input);
                    addOrRefreshCategoryInSpinner();
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    setCategory.setAdapter(dataAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private Intent getReturnIntent(String taskCategory){
        Intent returnIntent=new Intent();
        returnIntent.putExtra("taskCategory",taskCategory);
        return  returnIntent;
    }
}
