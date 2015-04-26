package com.aspiration.pal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements LocationListener {

    public static final int CAMERA_PIC_REQUEST =0 ;
    ImageButton photo;
    EditText locationView, deatil;
    String mCurrentPhotoPath;
    String loc, det;
    ParseFile file;
    TextView lint,longt;
    StringBuilder result = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        photo = (ImageButton) findViewById(R.id.c_photo);
        locationView = (EditText) findViewById(R.id.loc);
        deatil = (EditText) findViewById(R.id.detail);
        lint=(TextView)findViewById(R.id.textView);
        longt=(TextView)findViewById(R.id.textView2);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        ParseObject test = new ParseObject("Test");
        test.put("name","kartik");
        test.put("Lastname","fumakiya");
        //test.saveInBackground();
        test.saveEventually();
        Toast.makeText(getApplicationContext(),"Your data saved on parse",Toast.LENGTH_SHORT).show();


    }

    public void photoclick(View v) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

        /*Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("File:", "Error while creating file" + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }*/
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View

        int targetW = photo.getWidth();
        int targetH = photo.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        //Bitmap to Bytes[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        file = new ParseFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png", byteArray);

        photo.setImageBitmap(bitmap);
        photo.setAdjustViewBounds(true);
        //photo.setFitToScreen(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path=null;
        try
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            thumbnail = Bitmap.createScaledBitmap(thumbnail,photo.getWidth(),photo.getHeight(), true);
            ImageView imageView = (ImageView) findViewById(R.id.c_photo);
            imageView.setImageBitmap(thumbnail);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray=stream.toByteArray();

            file = new ParseFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png", byteArray);
            imageView.destroyDrawingCache();
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }
    }

    public void savedata(View v) {


        if (photo.getDrawable() == null) {

            Toast.makeText(getApplicationContext(), "You must upload Image", Toast.LENGTH_SHORT).show();
        } else if (locationView.getText().toString().isEmpty()) {

            locationView.setError("You Must Enter Location");
        }
        else
        {
            loc = locationView.getText().toString();
            det = deatil.getText().toString();

            ParseObject insert = new ParseObject("Data");
            insert.put("Image", file);
            insert.put("Location", loc);
            insert.put("Detail", det);
          //  insert.saveInBackground();
            insert.saveEventually();
            Toast.makeText(getApplicationContext(), "Your data saved", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplication(), Capture.class);
            startActivity(i);
        }
    }

    @Override
    public void onLocationChanged(Location location) {


        lint.setText(String.valueOf(location.getLatitude()));
        longt.setText(String.valueOf(location.getLongitude()));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


   }

