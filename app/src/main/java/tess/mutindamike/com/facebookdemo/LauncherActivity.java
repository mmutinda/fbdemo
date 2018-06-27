package tess.mutindamike.com.facebookdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private boolean loggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sharedPreferences  = getSharedPreferences("fbdb", Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean("loggedIn",false);

//        this.getSupportActionBar().hide();


        Thread splashscreen = new Thread() {// the thread to run the halal
            // splash screen
            @Override
            public void run() {
                try {
                    sleep(2000); // the splash screen should run for 2 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intentDashboard;
                    if(loggedIn){
                        intentDashboard = new Intent(LauncherActivity.this,
                                SignInActivity.class);
                    }else {
                        intentDashboard = new Intent(LauncherActivity.this,
                                SignInActivity.class);
                    }


                    startActivity(intentDashboard);
                    finish();
                }

            }
        };
        splashscreen.start();
    }

}
