package com.aspiration.pal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Provider;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements LocationListener {

    public static final int CAMERA_PIC_REQUEST = 0;
    ImageButton photo;
    EditText locationView, deatil;
    String mCurrentPhotoPath;
    String loc, det;
    double lat,log;
    ParseFile File;

    LocationManager locmanger;
    String Provider;
    StringBuilder result = new StringBuilder();
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        photo = (ImageButton) findViewById(R.id.c_photo);
        locationView = (EditText) findViewById(R.id.loc);
        deatil = (EditText) findViewById(R.id.detail);


        //FETACHING lONGTITUDE AND LATITIDUTE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Criteria c = new Criteria();
        String provider;
        provider = locationManager.getBestProvider(c, false);

        Location loc = locationManager.getLastKnownLocation(provider);
        if (loc != null)
        {
             lat = loc.getLatitude();
             log = loc.getLongitude();
           // deatil.setText(String.valueOf(lat) + "/" + (String.valueOf(log)));

            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            String mylocation;
            if (Geocoder.isPresent()) {
                try
                {
                    List<Address> address = geo.getFromLocation(lat, log, 1);

                    if (address != null && address.size() > 0) {
                        Address add = address.get(0);
                        String addressText = String.format("%s, %s, %s",
                                // If there's a street address, add it
                                add.getMaxAddressLineIndex() > 0 ? add.getAddressLine(0) : "",
                                // Locality is usually a city
                                add.getLocality(),
                                // The country of the address
                                add.getCountryName());
                        locationView.setText(addressText);
                        if (locationView == null) {
                            Toast.makeText(getApplicationContext(), "No provider", Toast
                                    .LENGTH_LONG).show();
                        }
                    }


                } catch (Exception e) {

                }

            }
        }
    }

     public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void photoclick(View v) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path = null;
        try
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            thumbnail = Bitmap.createScaledBitmap(thumbnail, photo.getWidth(), photo.getHeight(), true);
            ImageView imageView = (ImageView) findViewById(R.id.c_photo);
            imageView.setImageBitmap(thumbnail);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            File = new ParseFile("image.png", byteArray);



            File.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    ParseObject img = new ParseObject("Image");
                    img.put("Image", File);
                    img.saveEventually();
                    Toast.makeText(getApplicationContext(), "Now your image has been set", Toast.LENGTH_SHORT).show();
                }
            });



        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public void savedata(View v) {


        if (photo.getDrawable() == null) {

            Toast.makeText(getApplicationContext(), "You must upload Image", Toast.LENGTH_SHORT).show();
        } else if (locationView.getText().toString().isEmpty()) {

            locationView.setError("You Must Enter Location");
        } else {
            loc = locationView.getText().toString();
            det = deatil.getText().toString();


            ParseObject insert = new ParseObject("Data");
            insert.put("Image", File);
            insert.put("Location", loc);
            insert.put("Detail", det);
            insert.put("Longitude",String.valueOf(lat));
            insert.put("Latitude",String.valueOf(log));
            insert.saveEventually();
            //insert.saveInBackground();
            Toast.makeText(getApplicationContext(), "Your data Saved", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplication(), Capture.class);
            startActivity(i);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {

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



