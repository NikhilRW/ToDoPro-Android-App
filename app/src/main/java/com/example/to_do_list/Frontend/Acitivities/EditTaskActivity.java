package com.example.to_do_list.Frontend.Acitivities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.core.content.FileProvider;

import com.example.to_do_list.Backend.Models.Task;
import com.example.to_do_list.Backend.ToDoDatabaseManager;
import com.example.to_do_list.Frontend.attachments.Attachment;
import com.example.to_do_list.R;
import com.example.to_do_list.Frontend.NotificationChannel.NotificationReciever;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {
    Toolbar toolbar;
    ToDoDatabaseManager toDoDatabaseManager;
    EditText taskName,selectedDate,selectedTime;
    public TextView currentAttachment;
    ImageButton removeDate,removeTime,setTime,setDate,updateTask,uploadBtn;
    Spinner setCategory;
    boolean isUpdatedTask=true;
    public Task currentTask;
    ArrayAdapter<String> dataAdapter;
    CheckBox isCompletedCheckBox;
    ImageButton addCategory;
    int id;
    Toast toast;
    Attachment attachment;
    public String attachmentFilePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_activity);
        taskName=findViewById(R.id.taskName);
        isCompletedCheckBox=findViewById(R.id.isCompletedCheckBox);
        setCategory=findViewById(R.id.setCategory);
        selectedDate=findViewById(R.id.selectedDate);
        selectedDate.setInputType(InputType.TYPE_NULL); // Disable keyboard input
        selectedDate.setFocusable(false);
        selectedTime=findViewById(R.id.selectedTime);
        selectedTime.setInputType(InputType.TYPE_NULL); // Disable keyboard input
        selectedTime.setFocusable(false);
        addCategory=findViewById(R.id.addCategory);
        removeTime=findViewById(R.id.removeTime);
        updateTask=findViewById(R.id.updateTask);
        currentAttachment = findViewById(R.id.currentAttachment);
        setTime=findViewById(R.id.setTime);
        setDate=findViewById(R.id.setDate);
        uploadBtn = findViewById(R.id.upload_btn);
        removeDate=findViewById(R.id.removeDate);
        toDoDatabaseManager = new ToDoDatabaseManager(this);
        toDoDatabaseManager.open();
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        attachment = new Attachment(this,false);
        currentTask=toDoDatabaseManager.getTaskDetails(id);
        if(currentTask.getAttachment() != null){
            currentAttachment.setText(Uri.parse(currentTask.getAttachment()).getLastPathSegment());
            currentAttachment.setVisibility(View.VISIBLE);
            uploadBtn.setOnClickListener(v -> {
                showUploadAttachmentAlert();
            });
        }
        else{
            attachment.setupButton();
        }
        if(currentTask.getDueDate()!=0){
            String[] arr = getDateTimeFromUnix(currentTask.getDueDate());
            selectedDate.setText(arr[0]);
            selectedTime.setText(arr[1]);
        }
        String message = "Task Updated";
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        if(toast.getView()!=null){
            View toastView = toast.getView();
            toastView.getBackground().setColorFilter(Color.parseColor("#90EE90"), PorterDuff.Mode.SRC_IN); // Light green background
        }
        isCompletedCheckBox.setChecked(    currentTask.getIsCompleted() == 1);
        taskName.setText(    currentTask.getName());
        // Hide the default ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setCategory.getBackground().setColorFilter(getResources().getColor(R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        addOrRefreshCategoryInSpinner();
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCategory.setAdapter(dataAdapter);
        setCategory.setSelection(toDoDatabaseManager.getAllTaskCategory().indexOf(currentTask.getTaskCategory()));
        // Set up the custom Toolbar as the ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setNavigationIcon(navigationIcon);
        // Set up the delete button
        ImageButton deleteButton = findViewById(R.id.delete_button);
        ImageButton previousActivity = findViewById(R.id.perviousActivity);
        previousActivity.setOnClickListener(v->{
            setResult(RESULT_OK);
            if(!isUpdatedTask){
                showExitConfirmationDialog();
            }
            else{
                setResult(RESULT_OK,getReturnIntent());
                superBackPressed();
            }
        });
        deleteButton.setOnClickListener(v->{
            showDeleteConfirmationDialog();
        });
        // Enable the back button in the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        isCompletedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTask.getIsCompleted()==1!=isCompletedCheckBox.isChecked()){
                    isUpdatedTask=false;
                }
            }
        });
        selectedDate.setOnClickListener(v->{
            showDatePickerDialog();
            isUpdatedTask=false;
        });
        setDate.setOnClickListener(v->{
            showDatePickerDialog();
        });
        removeDate.setOnClickListener(v->{
            selectedDate.setText("");
        });
        removeTime.setOnClickListener(v->{
            selectedTime.setText("");
        });

        selectedTime.setOnClickListener(v->{
            showTimePickerDialog();
            isUpdatedTask=false;
        });
        setTime.setOnClickListener(v -> {
            showTimePickerDialog();
        });
        updateTask.setOnClickListener(v->{
            updateTheTask();
        });
        removeDate.setOnClickListener(v->{
            selectedDate.setText("");
        });
        removeTime.setOnClickListener(v->{
            selectedTime.setText("");
        });
        taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!taskName.getText().toString().equals(currentTask.getName())) {isUpdatedTask = false;}
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        setCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    isUpdatedTask=false;
                }
            }
        });
        addCategory.setOnClickListener(v->{
            showInputDialog();
        });
    }
    private Intent getReturnIntent(){
        Intent returnIntent=new Intent();
        returnIntent.putExtra("taskCategory",toDoDatabaseManager.getTaskDetails(id).getTaskCategory());
        return  returnIntent;
    }
    private  Intent getDeleteReturnIntent(){
        Intent returnIntent=new Intent();
        returnIntent.putExtra("taskCategory", setCategory.getSelectedItem().toString());
        return  returnIntent;
    }
    private void addOrRefreshCategoryInSpinner(){
        dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_layout, toDoDatabaseManager.getAllTaskCategory()) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                // Customize the dropdown item view here
                if (position == setCategory.getSelectedItemPosition()) {
                    view.setBackgroundColor(Color.parseColor("#016BAB")); // Selected item color
                    textView.setTextColor(Color.WHITE);
                } else {
                    textView.setTextColor(Color.BLACK);
                    view.setBackgroundColor(Color.WHITE); // Default item color
                }
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setCategory.setAdapter(dataAdapter);
    }
    // Method to show the delete confirmation dialog
    private void showDeleteConfirmationDialog() {

        new AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform the delete action
                          toDoDatabaseManager.deleteTask(id);
                          setResult(RESULT_OK,getDeleteReturnIntent());
                         superBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.delete)
                .show();
    }
    private void showUploadAttachmentAlert() {

        new AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .setTitle("Attachment")
                .setMessage("What do you want to do with this Attachment")
                .setPositiveButton("Upload new Attachment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attachment.launchUploadActivity();
                    }
                })
                .setNegativeButton("View Current Attachment",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    openCurrentAttachment();
                    }
                })
                .setIcon(R.drawable.delete)
                .show();
    }
    private void superBackPressed(){
        super.onBackPressed();
    }
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .setTitle("Are You Sure ?")
                .setMessage("Quit Without Saving")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK,getReturnIntent());
                        superBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.delete)
                .show();
    }

    private void openCurrentAttachment(){
        File file = new File(currentTask.getAttachment());
        Uri sharedUri = FileProvider.getUriForFile(this, getPackageName()+".provider",
                file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setDataAndType(sharedUri, "*/*");
        try {
            startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException ignored){};
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditTaskActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Convert hourOfDay from 24-hour format to 12-hour format and determine AM/PM
                boolean isPM = hourOfDay >= 12;
                int hourIn12HourFormat = hourOfDay % 12;
                if (hourIn12HourFormat == 0) {
                    hourIn12HourFormat = 12;
                }
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12HourFormat, minute, isPM ? "PM" : "AM");
                selectedTime.setText(formattedTime);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    // Method to update the TextView with the selected time
    private void updateSelectedTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String timeString = String.format("%02d:%02d", hour, minute);
        selectedTime.setText(timeString);
    }
    private long getUnixTime(String dateString, String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString + " " + timeString);
            return date != null ? date.getTime() / 1000 : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String[] getDateTimeFromUnix(long unix) {
        Date date = new Date(unix * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return new String[]{dateFormat.format(date), timeFormat.format(date)};
    }

    private void updateTheTask(){
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
            if(myDueDate*1000<new Date().getTime()){
                Toast.makeText(this, "Invalid Due Date", Toast.LENGTH_SHORT).show();
                return;
            }
            toDoDatabaseManager.updateTask(id,newTaskName,isCompleted,myDueDate,
                    selectedTaskCategory,attachmentFilePath);
            scheduleNotification(EditTaskActivity.this,myDueDate,id,newTaskName);
        }
        else{
            toDoDatabaseManager.updateTask(id,newTaskName,isCompleted,0,selectedTaskCategory,attachmentFilePath);
        }
        toast.show();
        setResult(RESULT_OK,getReturnIntent());
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
    public void scheduleNotification(Context context, long dueDateUnix, int taskId, String taskName) {
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra("task_id", taskId);
        intent.putExtra("task_name", taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
}
