package tess.mutindamike.com.facebookdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import tess.mutindamike.com.facebookdemo.adapters.PostsAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tess.mutindamike.com.facebookdemo.adapters.PostsAdapter;
import tess.mutindamike.com.facebookdemo.global.GlobalVars;
import tess.mutindamike.com.facebookdemo.models.Posts;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsAdapter PostsAdapter;
    private Context context;
    private ArrayList<Posts> PostssArray;
    MaterialDialog.Builder builder,builderpost;
    MaterialDialog dialog,dialogpost;
    private Button btnPost;
    private EditText editTextPost;
    private boolean error;
    private Button uploadBtn;
    private String message,post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = DashboardActivity.this;

        btnPost = (Button) findViewById(R.id.btnPost);
        editTextPost = (EditText) findViewById(R.id.tvPostvalue);
        uploadBtn = (Button) findViewById(R.id.btnUpload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context
                        , ImageUpload.class));
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           post = editTextPost.getText().toString().trim();
                                           if (post.isEmpty()) {
                                               Toast.makeText(context, context.getResources().getString(R.string.empty_post), Toast.LENGTH_LONG).show();
                                           } else{
                                               posting();


                                           }

                                       }
                                   }
        );





        builder = new MaterialDialog.Builder(context)
                .title("Processing")
                .content("Fetching posts...")
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .cancelable(false)
                .progress(true, 0);


        dialog = builder.build();

        builderpost = new MaterialDialog.Builder(context)
                .title("Processing")
                .content("Posting status...")
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .cancelable(false)
                .progress(true, 0);


        dialogpost = builderpost.build();

        fetchPostData(GlobalVars.BASE_URL+"/postdata");


        PostssArray = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.recylerViewHome);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



    }

    public void fetchPostData(String url){
        dialog.show();
        Log.i("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                try {

                    JSONArray jsonArrayPosts = new JSONArray(s);
                    Log.i("NUMBER",Integer.toString(jsonArrayPosts.length()));
                    for (int x = 0;x < jsonArrayPosts.length();x++){
                        JSONObject jsonObjectsinglepost = jsonArrayPosts.getJSONObject(x);
                        Posts posts = new Posts();
                        posts.setCreated_by("mike");
                        posts.setLikes(20);
                        posts.setPost_title(jsonObjectsinglepost.getString("post_title"));
                        posts.setPost_text(jsonObjectsinglepost.getString("post_text"));
                        posts.setPost_image(jsonObjectsinglepost.getString("post_image"));

                        PostssArray.add(posts);

                    }
                    PostsAdapter = new PostsAdapter(context, PostssArray);
                    recyclerView.setAdapter(PostsAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();

                VolleyLog.e("Error: ", volleyError.getMessage());
            }
        })
        ;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void submitPost2(String url){
        dialogpost.show();

    }

    public void submitPost(String url){
//        dialogpost.show();
        Log.i("URLR",url.toString());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                dialogpost.dismiss();
                try {

                    JSONObject object = new JSONObject(s);
                    error = object.getBoolean("error");
                    message = object.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    if(!error){
                        Posts posts = new Posts();
                        posts.setCreated_by("mike");
                        posts.setLikes(20);
                        posts.setPost_title(post);
                        posts.setPost_text(post);
                        posts.setPost_image("http://lorempixel.com/400/200");

                        PostssArray.add(0,posts);
                        PostsAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                dialogpost.dismiss();
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                VolleyLog.e("Error: ", volleyError.getMessage());
                System.out.println(volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences sharedPreferences = getSharedPreferences("fbdb", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
                Map<String, String> params = new HashMap<>();
                params.put("post", post);
                params.put("username", username);
                Log.i("usernmae",username);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

    }


    public void performData(String url) {
//        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    JSONObject object = new JSONObject(s);
                    error = object.getBoolean("error");
                    message = object.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    if(!error){
                        Posts posts = new Posts();
                        posts.setCreated_by("mike");
                        posts.setLikes(20);
                        posts.setPost_title(post);
                        posts.setPost_text(post);
                        posts.setPost_image("http://lorempixel.com/400/200");

                        PostssArray.add(0,posts);
                        PostsAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();

                VolleyLog.e("Error: ", volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences sharedPreferences = getSharedPreferences("fbdb", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
                Map<String, String> params = new HashMap<>();
                params.put("post", post);
                params.put("username", username);
                Log.i("usernmae",username);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);


    }


    public void posting() {

        RequestParams params = new RequestParams();
        try {
            params.put("username", GlobalVars.username);
            params.put("post", post);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(GlobalVars.BASE_URL + "/post", params, new AsyncHttpResponseHandler() {

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, getResources().getString(R.string.success), Toast.LENGTH_LONG).show();

                    Posts posts = new Posts();
                    posts.setCreated_by("mike");
                    posts.setLikes(20);
                    posts.setPost_title(post);
                    posts.setPost_text(post);
                    posts.setPost_image("http://lorempixel.com/400/200");

                    PostssArray.add(0,posts);
                    PostsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, getResources().getString(R.string.failed) + statusCode + " " + error.getMessage(), Toast.LENGTH_LONG).show();
                   //updateUISuccess(image_type_passed);
                    error.printStackTrace();
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

        }


    }

}
