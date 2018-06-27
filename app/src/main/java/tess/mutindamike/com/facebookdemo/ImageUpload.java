package tess.mutindamike.com.facebookdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import tess.mutindamike.com.facebookdemo.global.GlobalVars;

public class ImageUpload extends AppCompatActivity {
    public Button btCapture, btUpload;
    public ImageView imgPhoto;
    public Context ctx;
    public String post="default text";
    public ProgressBar progressBar;

    public static File f;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static String IMAGE_TAG = "PF.jpg";
    String image_type_passed = "";

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = ImageUpload.this;

        Bundle bundle = getIntent().getExtras();

//Extract the data…
        image_type_passed = "image";

        if (shouldAskPermissions()) {
            askPermissions();
        }

        btCapture = (Button) findViewById(R.id.btCapture);
        btUpload = (Button) findViewById(R.id.btUpload);
        btUpload.setEnabled(true);

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
                // Toast.makeText(ctx, getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                // GlobalVars.status_customer_photo = getResources().getString(R.string.uploaded);
                // onBackPressed();//checkcheck
            }
        });
        btCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

//        getSupportActionBar().setTitle(getResources().getString(R.string.title_capture_image));
//        getSupportActionBar().setSubtitle(getResources().getString(R.string.title_capture_image_cust));


    }




    public void upload() {
        btUpload.setEnabled(false);

        RequestParams params = new RequestParams();
        try {
            params.put("photo", f);
            params.put("username", GlobalVars.username);
            params.put("post", post);
            AsyncHttpClient client = new AsyncHttpClient();
            progressBar.setVisibility(View.VISIBLE);
            client.post(GlobalVars.BASE_URL + "/uploadimages", params, new AsyncHttpResponseHandler() {

               @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    progressBar.setMax(Integer.parseInt(totalSize + ""));
                    progressBar.setProgress(Integer.parseInt(bytesWritten + ""));

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(ctx, getResources().getString(R.string.success), Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);

                    //  updateUISuccess(image_type_passed);
                    onBackPressed();//checkcheck
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(ctx, getResources().getString(R.string.failed) + statusCode + " " + error.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    //updateUISuccess(image_type_passed);
                    error.printStackTrace();
                }

            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "No photo", Toast.LENGTH_LONG).show();

        }


    }


    //new photo stuff
    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.choose_photo),
                getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(getResources().getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.take_picture))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), IMAGE_TAG);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getResources().getString(R.string.choose_photo))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getResources().getString(R.string.select_photo)),
                            SELECT_FILE);
                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        File destination = new File(Environment.getExternalStorageDirectory(), IMAGE_TAG);
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(destination.getAbsolutePath(), options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(destination.getAbsolutePath(), options);

        imgPhoto.setImageBitmap(bm);
        bitmapToFile(GlobalVars.username + IMAGE_TAG, bm);
//        bitmapToFile("267726128484"+ IMAGE_TAG, bm);

    }

    //    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 500;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);


        imgPhoto.setImageBitmap(bm);
        bitmapToFile(GlobalVars.username + IMAGE_TAG, bm);
    }

    public void bitmapToFile(String filename, Bitmap your_bitmap) {
        try {
            f = new File(ctx.getCacheDir(), filename);
            f.createNewFile();

            Bitmap bitmap = your_bitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = null;

            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


