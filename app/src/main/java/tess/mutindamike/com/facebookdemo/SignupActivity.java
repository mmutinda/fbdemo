package tess.mutindamike.com.facebookdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tess.mutindamike.com.facebookdemo.global.Fonting;
import tess.mutindamike.com.facebookdemo.global.GlobalVars;
import tess.mutindamike.com.facebookdemo.AppController;

public class SignupActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    Context ctx;

    Button btLogin;
    EditText etUsername, etPassword,etEmail;

    private SharedPreferences sharedPreferences;


    public String strUsername, strPassword,strEmail,message;
    private boolean error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ctx = SignupActivity.this;

        sharedPreferences = getSharedPreferences("fbdb", Context.MODE_PRIVATE);


        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preValidate();
            }
        });

        builder = new MaterialDialog.Builder(ctx)
                .title("Processing")
                .content("Kindly wait as we register you .\nThank you.")
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .cancelable(false)
                .progress(true, 0);


        dialog = builder.build();
        Fonting.setTypeFaceForViewGroup((ViewGroup) etUsername.getRootView(), ctx,
                Fonting.KEY_LIGHT);

    }

    private void preValidate() {
        strUsername = etUsername.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();
        strEmail = etEmail.getText().toString().trim();

        if (strUsername.isEmpty()) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.missing_username), Toast.LENGTH_LONG).show();

        }else if (strEmail.isEmpty()) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.missing_email), Toast.LENGTH_LONG).show();
        } else if (strPassword.isEmpty()) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.missing_password), Toast.LENGTH_LONG).show();
        } else {

            performData(GlobalVars.BASE_URL + "/register");


        }
    }


        public void performData(String url) {
        dialog.show();
            Log.i("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                try {

                    JSONObject object = new JSONObject(s);
                    error = object.getBoolean("error");
                    message = object.getString("message");
                    if(error){
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }else{
                        String token = object.getString("token");
                        SharedPreferences sharedPreferences =  ctx.getSharedPreferences("fbdb", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username",strUsername);
                        editor.putString("email",strEmail);
                        editor.putString("token",token);
                        editor.putBoolean("loggedIn",true);

                        editor.apply();
                        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ctx
                                , DashboardActivity.class));
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();

                VolleyLog.e("Error: ", volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("name", strUsername);
                params.put("email", strEmail);
                params.put("password", strPassword);
                params.put("password_repeat", strPassword);
                return params;
            }
        };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);


    }



}
