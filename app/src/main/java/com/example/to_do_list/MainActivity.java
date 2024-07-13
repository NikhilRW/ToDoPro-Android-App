package com.example.to_do_list;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.to_do_list.Backend.Adapters.CategoryAdapter;
import com.example.to_do_list.Backend.Adapters.CustomAdapter;
import com.example.to_do_list.Backend.Models.Task;
import com.example.to_do_list.Backend.ToDoDatabaseManager;
import com.example.to_do_list.ui.Acitivities.AddTaskActivity;
import com.example.to_do_list.ui.Acitivities.EditTaskActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_do_list.databinding.ActivityMainBinding;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Serializable {
    private AppBarConfiguration mAppBarConfiguration;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_DEFAULT_TASKS_ADDED = "defaultTasksAdded";
    private static final int REQUEST_CODE_EDIT_TASK = 1;
    public boolean isSearching = false;
    private static final String TAG = "nikh";
    private static final int REQUEST_CODE_ADD_TASK = 2;
    public String taskCategoryBeforeChill="Default";
    RecyclerView recyclerView,recyclerView3;
    private ClipboardManager clipboardManager;
    public MediaPlayer mediaPlayer;
    CategoryAdapter categoryAdapter;
    EditText quickTask;
    public boolean isShowOnlyNotCompleted=true;
    CustomAdapter customAdapter;
    LinearLayout nothingJustChill,noSearchResults;
    ImageButton addQuickTask;
    SearchView searchView;
    Toolbar toolbar;
    boolean isDarkMode;
    FloatingActionButton addDetailedTask;
    public  ToDoDatabaseManager toDoDatabaseManager;
    public ArrayList<Task> tasks = new ArrayList<Task>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.to_do_list.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        nothingJustChill=findViewById(R.id.nothingJustChill);
        toDoDatabaseManager =new ToDoDatabaseManager(this);
        addDetailedTask=findViewById(R.id.addDetailedTask);
        toDoDatabaseManager.open();
        mediaPlayer = MediaPlayer.create(this,R.raw.taskaccomplished2);
        isDarkMode=(getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        tasks = (ArrayList<Task>) toDoDatabaseManager.fetchAllTask();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean defaultTasksAdded = preferences.getBoolean(KEY_DEFAULT_TASKS_ADDED, false);
        if (!defaultTasksAdded) {
            toDoDatabaseManager.defaultTaskForExample();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DEFAULT_TASKS_ADDED, true);
            editor.apply();
        }
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapter = new CustomAdapter(this,toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,"Default"), this,toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,"Default"));
        recyclerView.setAdapter(customAdapter);
        recyclerView3=findViewById(R.id.recyclerView3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter= new CategoryAdapter(this,toDoDatabaseManager.getAllTaskCategory(), this);
        recyclerView3.setAdapter(categoryAdapter);
        DrawerLayout drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavigationView navigationView = binding.navView;
        setSupportActionBar(binding.appBarMain.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        toolbar=findViewById(R.id.toolbar);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        quickTask = findViewById(R.id.quickTask);
        addQuickTask = findViewById(R.id.addQuickTask);
        addQuickTask.setOnClickListener(v->{
            if(quickTask.getText().toString().isEmpty()){
                Toast.makeText(this, "Task Is Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(getSupportActionBar().getTitle().equals("Just Chill")){
                 getSupportActionBar().setTitle(taskCategoryBeforeChill);
                 nothingToDo(false);
                 toDoDatabaseManager.addTask(quickTask.getText().toString(),0,taskCategoryBeforeChill);
                     refreshAllTaskOf(taskCategoryBeforeChill);
            }
            else{
                String taskCategory = getSupportActionBar().getTitle().toString();
                toDoDatabaseManager.addTask(quickTask.getText().toString(),0,taskCategory);
                    refreshAllTaskOf(taskCategory);
            }
            quickTask.setText("");
        });

        quickTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                if(!s.toString().isEmpty()){
                    addQuickTask.setVisibility(View.VISIBLE);
                }
                else{
                    addQuickTask.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        addDetailedTask=findViewById(R.id.addDetailedTask);
        addDetailedTask.setOnClickListener(v->{
            createNewDetailedTask();
        });
        if(getIntent().getStringExtra("dueTaskCategory")!=null){
            String dueTaskCategory = getIntent().getStringExtra("dueTaskCategory");
            recyclerView.setAdapter(new CustomAdapter(this,toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,dueTaskCategory),this,toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,dueTaskCategory)));
        }
    }
    private final ClipboardManager.OnPrimaryClipChangedListener clipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        CharSequence copiedText = clipData.getItemAt(0).getText();
                        if (copiedText != null) {
                            showSnackbar(copiedText.toString());
                        }
                    }
                }
            };
    private void showSnackbar(String copiedText) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), copiedText, Snackbar.LENGTH_LONG)
                .setAction("Add", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                        intent.putExtra("taskName",copiedText);
                        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
                    }
                });
        snackbar.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager ;
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnSearchClickListener(v->{
            changeSearchIconColor();});
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {return true;}
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Handle the search view collapse here
                isSearching = false;
                noSearchResults(false);
                refreshAllTask();
                return true; // Return true to allow collapse
            }
        });
        searchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isSearching = false;
            }
        });
        searchView.setQueryHint(getResources().getString(R.string.search));
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ImageView goButton = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (closeButton != null) {
            closeButton.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        if (goButton != null) {
            goButton.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        // Customize SearchView text color
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchView.setBackgroundColor(Color.TRANSPARENT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterCustomAdapter(newText);
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
        return true;
    }
    private void changeSearchIconColor(){
        Drawable navigationIcon = toolbar.getCollapseIcon();
        isSearching=true;
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.isShowOnlyNotCompleted) {
            isShowOnlyNotCompleted=!isShowOnlyNotCompleted;
            if(!isShowOnlyNotCompleted){
                item.setTitle("Show Uncompleted Task");
            }
            else{
                item.setTitle("Show All Task");
            }
            if(isShowOnlyNotCompleted){
                        Toast.makeText(MainActivity.this, "Showing Uncompleted Task", Toast.LENGTH_SHORT).show();
           }
            else{
                        Toast.makeText(MainActivity.this, "Showing All Task", Toast.LENGTH_SHORT).show();
            }
                    refreshAllTaskOf(taskCategoryBeforeChill);
                    return true;
        }
        if(id==R.id.deleteAllTask){
            new AlertDialog.Builder(this,R.style.CustomAlertDialog)
                    .setTitle("Are You Sure ?")
                    .setMessage("Delete All Task")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            toDoDatabaseManager.deleteAllTask();
                            refreshAllCategory();
                            refreshAllTask();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle("Just Chill");
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.delete)
                    .show();
            refreshAllTask();
            refreshAllCategory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg); // Use your video path
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true); // Loop the video
            videoView.start();
        });
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    void createNewDetailedTask(){
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
    }
    public void swtichToEditTaskActivity(int id){
        Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
        intent.putExtra("id",id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_TASK);
    }
    public void refreshAllTask(){
        recyclerView.setAdapter(new CustomAdapter(this,toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,"Default"),this,toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,"Default")));
    }
    public void refreshAllTaskOf(String taskCategory){
        recyclerView.setAdapter(new CustomAdapter(this,toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,taskCategory),this,toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,taskCategory)));
    }
    public void refreshAllCategory(){
        categoryAdapter= new CategoryAdapter(this,toDoDatabaseManager.getAllTaskCategory(), this);
        recyclerView3.setAdapter(categoryAdapter);
    }
    public  void changeTaskCategory(String taskCategory){
        taskCategoryBeforeChill=taskCategory;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(taskCategory);
        }
        recyclerView.setAdapter(new CustomAdapter(this,toDoDatabaseManager.fetchAllTaskNameOf(taskCategory,isShowOnlyNotCompleted),this,toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,taskCategory)));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_TASK ||requestCode==REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
                String taskCategory =data.getStringExtra("taskCategory");
                getSupportActionBar().setTitle(taskCategory);
                refreshAllTaskOf(taskCategory);
                refreshAllCategory();
        }
    }
    public  void filterCustomAdapter(String newText){
        ArrayList<Integer> arrOfIds = new ArrayList<>();
        ArrayList<String> arrOfTaskName = new ArrayList<>();
        if (newText.isEmpty()) {
            arrOfIds = new ArrayList<>(toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,taskCategoryBeforeChill));
            arrOfTaskName=new ArrayList<>(toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,taskCategoryBeforeChill));
        } else {
            ArrayList<String> arrOfAllTaskName=new ArrayList<>(toDoDatabaseManager.getAllTaskNamesOf(isShowOnlyNotCompleted,taskCategoryBeforeChill));
            ArrayList<Integer> arrOfAllTaskIds=new ArrayList<>(toDoDatabaseManager.getAllTaskIds(isShowOnlyNotCompleted,taskCategoryBeforeChill));
            for (int i = 0; i < arrOfAllTaskName.size(); i++) {
                String task = arrOfAllTaskName.get(i);
                if (task.toLowerCase().contains(newText.toLowerCase())) {
                    arrOfTaskName.add(task);
                    arrOfIds.add(arrOfAllTaskIds.get(i));
                }
            }
        }
        if(arrOfTaskName.isEmpty()&&arrOfIds.isEmpty()){

        }
        recyclerView.setAdapter(new CustomAdapter(this,arrOfTaskName,this,arrOfIds));
    }
    public void nothingToDo(boolean isNothingToDo){
        nothingJustChill=findViewById(R.id.nothingJustChill);
        if(isNothingToDo){
            nothingJustChill.setVisibility(View.VISIBLE);
        }
        else{
            nothingJustChill.setVisibility(View.GONE);
        }
    }
    public void noSearchResults(boolean isNoSearchResults){
        noSearchResults=findViewById(R.id.noSearchResultFound);
        if(isNoSearchResults){
            noSearchResults.setVisibility(View.VISIBLE);
        }
        else{
            noSearchResults.setVisibility(View.GONE);
        }
    }
    public void setSupportActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        toDoDatabaseManager.close();
    }
}