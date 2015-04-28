package com.aspiration.pal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


public class MainActivity extends ActionBarActivity implements LocationListener {

    public static final int CAMERA_PIC_REQUEST = 0;
    ImageButton photo;
    EditText locationView, deatil;
    String mCurrentPhotoPath;
    String loc, det;
    ParseFile File;

    StringBuilder result = new StringBuilder();
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        photo = (ImageButton) findViewById(R.id.c_photo);
        locationView = (EditText) findViewById(R.id.loc);
        deatil = (EditText) findViewById(R.id.detail);

        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


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

        /*Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if
        (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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

    /*private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }*/

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

        //  file = new ParseFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png", byteArray);

        photo.setImageBitmap(bitmap);
        photo.setAdjustViewBounds(true);
        //photo.setFitToScreen(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path = null;
        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            thumbnail = Bitmap.createScaledBitmap(thumbnail, photo.getWidth(), photo.getHeight(), true);
            ImageView imageView = (ImageView) findViewById(R.id.c_photo);
            imageView.setImageBitmap(thumbnail);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //file = new ParseFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png", byteArray);

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

            insert.saveEventually();
            //insert.saveInBackground();
            Toast.makeText(getApplicationContext(), "Your data Saved", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplication(), Capture.class);
            startActivity(i);
        }
    }

    @Override
    public void onLocationChanged(Location location) {


   /*     lint.setText(String.valueOf(location.getLatitude()));
        longt.setText(String.valueOf(location.getLongitude()));
*/
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
        } else {
            Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
        }
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


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            locationView.setText(locationAddress);
        }
    }
}

