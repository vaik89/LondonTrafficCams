package com.laboo.londontrafficcams;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by VaiosK on 13/12/2014.
 */
public class showimg extends Activity  {
    ImageView image;
    String temp;
    Bitmap data1;
    File file;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FlurryAgent.logEvent("Image opened");


        setContentView(R.layout.image_view);


        Intent intent = getIntent();
        Bitmap bitmap= (Bitmap) intent.getParcelableExtra("bitmap");
        data1=bitmap;
        //  temp=(String) intent.getStringExtra("url");
        // System.out.println("called showimg");
        //  System.out.println(temp);
        image = (ImageView) findViewById(R.id.imageView1);
        //  image.setImageResource(R.drawable.successstories);
        image.setImageBitmap(bitmap);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                FlurryAgent.logEvent("Share");
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                data1.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "ltc.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }



                // sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getFilesDir(), "temp.png")));


                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/ltc.jpg"));
                // sharingIntent.setType("image/png");
                // sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //  String shareBody = " - Captured via London Traffic Cams";
                //     sharingIntent.putExtra(Intent.EXTRA_STREAM, data);
                //   sharingIntent.putExtra(Intent.EXTRA_TEXT, temp);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, " - Captured via London Traffic Cams "+"http://goo.gl/WlZyox");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));




            }
        });





    }







}