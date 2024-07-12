package com.example.to_do_list.ui.Acitivities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.example.to_do_list.MainActivity;
import com.example.to_do_list.R;
public class SplashScreenTodo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Delay for 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenTodo.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 1500); // 3000 milliseconds = 3 seconds
    }
}

