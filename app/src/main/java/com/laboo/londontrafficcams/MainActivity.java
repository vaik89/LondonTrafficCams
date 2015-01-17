package com.laboo.londontrafficcams;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SignUpCallback;
import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    public TextView txtView;
    ImageView image;
    public int radius=1;
    public String strPlace;
    //   public    double[] lat = {38.051084,38.026092,38.049647};
    //   public    double[] lng = {23.804034,23.798087,23.817958};

    //VARIABLES FROM THE PREVIOUS VERSION OF CODE (MIGHT NEED CLEARING)
    // declare the ArrayLists for the different types of data
    private ArrayList<String> locations;
    private ArrayList<String> file;
    private ArrayList<String> lat;
    private ArrayList<String> lng;
    private ArrayList<String> avail;
    // private ArrayList<Bitmap> images;
    private Client TcpClient;
    private PopupWindow popupWindow; // menu
    private Marker searchMarker;
    private PopupWindow imageWindow; // live image
    private static final LatLng LONDON = new LatLng(51.511213, -0.119821);
    LocationManager locationManager;
    Location location;
    double latitude; // latitude
    double longitude;
    public Marker searchm;
    public Bitmap temp;
    Bitmap bitmapResult1;
    URL url1;
    ProgressDialog progress;
    double templat;
    double templng;
    boolean finished = false;
    boolean online=true;
    boolean execute=true;
    boolean firsttime=false;
    boolean background=false;
    boolean created=false;
    long startTime;
    long estimatedTime;
    boolean begin=false;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    // check if client has Internet connectivity
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }



    //test with handler
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        public void run() {
            System.out.println("INSIDE HANDLER");

            finished = false;
            locations.clear();
            file.clear();
            lat.clear();
            lng.clear();
            avail.clear();
//map.clear();

            if (execute == false && isOnline()) {


                new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                execute = true;

            }


            if (isOnline()) {
                startTime=System.currentTimeMillis();
              //  System.out.println(startTime);
                TcpClient.sendMessage("locations");
                TcpClient.sendMessage("file");
                TcpClient.sendMessage("lat");
                TcpClient.sendMessage("lng");
                TcpClient.sendMessage("availability");

            }









            mHandler.postDelayed(this, 120000);
        }
    };


    //test with handler




    @Override
    protected void onCreate(Bundle savedInstanceState) {






        if (!isOnline()) {
            // shows the message for connectivity if client is offline
            online=false;

        }

        // Parse.initialize(this, "9aaGVYwJUsYGMgfD7VbRfzwhwUcNMsgfxMfudg9l", "eiYzfXxpdLh8c4ILhf0Ry2GiptFXBvNXbaAoQcqa");


        if (isOnline()) {
            new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        }
        else
        {
            execute =false;
        }
//System.out.println(TcpClient.isRunning());

        // prepares the message to be displayed if the client has no Internet
        // connectivity
        Context context = getApplicationContext();
        CharSequence text = "No internet connectivity, turn on your Wi-Fi or mobile data!";
        int duration = Toast.LENGTH_LONG;
        final Toast toast = Toast.makeText(context, text, duration);

        if (!isOnline()) {
            // shows the message for connectivity if client is offline
            online=false;
            toast.show();
        }

        // initialise the arraylists
        locations = new ArrayList<String>();
        file = new ArrayList<String>();
        lat = new ArrayList<String>();
        lng = new ArrayList<String>();
        avail = new ArrayList<String>();
        // images = new ArrayList<Bitmap>();





        super.onCreate(savedInstanceState);



        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        System.out.println(location);
        if (location==null){

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        if (location != null) {
            // System.out.println("entered locationmanager");
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            Log.d("old","lat :  "+latitude);
            Log.d("old","long :  "+longitude);

        }
        else {
            Context context1 = getApplicationContext();
            CharSequence text1 = "Open your network location services or GPS!";
            int duration1 = Toast.LENGTH_LONG;
            final Toast toast1 = Toast.makeText(context1, text1, duration1);
            toast1.show();
            latitude=51.5286416;
            longitude=-0.1015987;


        }




        // FlurryAgent.onStartSession(this, "SFPMD6YM9W54PN2FDZSC");



        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        setContentView(R.layout.activity_main);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        ParseObject testObject = new ParseObject("UserBase");
        testObject.put("Mobile", Build.MODEL);
        testObject.put("Manufacturer", Build.MANUFACTURER);
        testObject.saveInBackground();
        FlurryAgent.logEvent("App Started");

        // set
// sends message to the server for the requested data
        if (isOnline()) {
            locations.clear();
            file.clear();
            lat.clear();
            lng.clear();
            avail.clear();
            startTime=System.currentTimeMillis();
           // System.out.println(startTime);
            begin=true;
            TcpClient.sendMessage("locations");
            TcpClient.sendMessage("file");
            TcpClient.sendMessage("lat");
            TcpClient.sendMessage("lng");
            TcpClient.sendMessage("availability");

        }




        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setMyLocationEnabled(true);

        map.setTrafficEnabled(true);
map.getUiSettings().setZoomControlsEnabled(true);


        // creates a schedule to be executed every 2 minutes for the refresh of
        // the data from the server
        Timer timer = new Timer();
        class refresh extends TimerTask {

            public void run() {
                //   System.out.println("entered timer");
                //    if (background=false) {

                finished = false;
                locations.clear();
                file.clear();
                lat.clear();
                lng.clear();
                avail.clear();
//map.clear();

                if (execute == false && isOnline()) {


                    new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    execute = true;

                }


                if (isOnline()) {
                    TcpClient.sendMessage("locations");
                    TcpClient.sendMessage("file");
                    TcpClient.sendMessage("lat");
                    TcpClient.sendMessage("lng");
                    TcpClient.sendMessage("availability");
                }

                // }


            }
        }
        TimerTask refreshmsg = new refresh();
        // timer.scheduleAtFixedRate(refreshmsg, 120000,120000);










//120000,120000

        float[] results= new float[1];

        Location.distanceBetween(latitude,longitude,51.511213, -0.119821,results);
        //   System.out.println(results[0]);

        if (results[0]>40000){
            firsttime=true;
            latitude=51.511213;
            longitude=-0.119821;


            //   System.out.println("INSIDE IF");
            LatLng current = new LatLng(51.511213, -0.119821);
            //  System.out.println(current);
            searchm= map.addMarker(new MarkerOptions()
                    .position(new LatLng(51.511213, -0.119821))
                    .title("LONDON"));
            searchm.setVisible(false);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,13));





            firsttime=false;


        }
        else {

//System.out.println("INSIDE ELSE");
            LatLng current = new LatLng(latitude, longitude);
            //  System.out.println(current);
            searchm = map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("LONDON"));
            searchm.setVisible(false);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));








        }



        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng arg0) {
                if (searchm != null) {
                    searchm.remove();
                }
                map.clear();
                searchm = map.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(arg0.latitude,
                                        arg0.longitude))
                        .draggable(true).visible(true).title("Your place"));
                templat=arg0.latitude;
                templng=arg0.longitude;
                created=true;
                CameraUpdate yourloc2 = CameraUpdateFactory.newLatLngZoom(arg0, 14);
                map.animateCamera(yourloc2);
                float[] resu= new float[1];

                for (int i = 0; i < locations.size(); i++) {





                    Location.distanceBetween(arg0.latitude,
                            arg0.longitude, Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)), resu);

                    resu[0] = resu[0] * 0.000621371192f;
                    //   System.out.println(resu[0]);
                    if (resu[0] < radius) {


                        double lati = Double.parseDouble(lat.get(i));
                        double lngt = Double.parseDouble(lng.get(i));
                        String location = locations.get(i);
                        if (avail.get(i).equals("true")) {
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(lati, lngt))
                                    .title(location)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }


                    }




                }









            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                searchm.setVisible(false);
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public  void onCameraChange(CameraPosition position) {
                if (finished==true) {
                    //   System.out.println("entered on camera change");
                    map.clear();
                    if (searchm != null && created==true ) {
                        //  System.out.println("mpikna sto on camera change searchm not null");
                        searchm = map.addMarker(new MarkerOptions()
                                .position(new LatLng(templat, templng))
                                .title(strPlace));
                        //searchm.setVisible(false);
                    }
                    LatLng center = map.getCameraPosition().target;
                    if (firsttime==false) {
                        latitude = center.latitude;
                        longitude = center.longitude;
                    }
                    float[] resu = new float[1];

                    for (int i = 0; i < locations.size(); i++) {


                        Location.distanceBetween(center.latitude,
                                center.longitude, Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i)), resu);

                        resu[0] = resu[0] * 0.000621371192f;
                        //   System.out.println(resu[0]);
                        if (resu[0] < radius) {

                            //   System.out.println("mpikna sto on camera change ");
                            double lati = Double.parseDouble(lat.get(i));
                            double lngt = Double.parseDouble(lng.get(i));
                            String location = locations.get(i);
                            if (avail.get(i).equals("true")) {
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lati, lngt))
                                        .title(location)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            }


                        }


                    }
                }

            }
        });
































        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                if ((locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)==null)) {

                    Context context1 = getApplicationContext();
                    CharSequence text1 = "Open your network location services or GPS!";
                    int duration1 = Toast.LENGTH_LONG;
                    final Toast toast1 = Toast.makeText(context1, text1, duration1);
                    toast1.show();
                    return true;
                }else{
                    map.clear();
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng current = new LatLng(latitude, longitude);

                    CameraUpdate yourloc1 = CameraUpdateFactory.newLatLngZoom(current, 14);
                    map.animateCamera(yourloc1);

                    float[] resu = new float[1];

                    for (int i = 0; i < locations.size(); i++) {


                        Location.distanceBetween(latitude, longitude, Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i)), resu);

                        resu[0] = resu[0] * 0.000621371192f;
                        //   System.out.println(resu[0]);
                        if (resu[0] < radius) {


                            double lati = Double.parseDouble(lat.get(i));
                            double lngt = Double.parseDouble(lng.get(i));
                            String location = locations.get(i);
                            if (avail.get(i).equals("true")) {
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lati, lngt))
                                        .title(location)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            }


                        }


                    }

                }
                //Toast.makeText(MainActivity.this, "MYLOC", Toast.LENGTH_LONG).show();// display toast
                return true;
            }

        });




        /*map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
AYTO NA ALLAKSO AN DEN PAIZEI TIPOTA!!!!!!!!!!!!!!!!!!!!
            @Override
            public boolean onMarkerClick(Marker arg0) {



                if (arg0.equals(searchm)){
                    // arg0.showInfoWindow();
                    return false;

                }

                else{

                    int temp4 = locations.indexOf(arg0.getTitle());
                    //  System.out.println(file.get(temp2));
                 //   new ImageDownloadTask().executeOnExecutor(
                   //         AsyncTask.THREAD_POOL_EXECUTOR, file.get(temp4));

//Bitmap eikona=temp;
                    // Intent intent = new Intent(MainActivity.this, showimg.class);
                    //  intent.putExtra("bitmap",eikona);
                    //   startActivity(intent);

                    // setContentView(R.layout.image_view);
                    //    image = (ImageView) findViewById(R.id.imageView1);
                    //   image.setImageResource(R.drawable.successstories);

                    //  Toast.makeText(MainActivity.this, arg0.getTitle(), Toast.LENGTH_LONG).show();// display toast
                    return true;}
            }

        });*/
        //  System.out.println(android.os.Build.MODEL);

        radius=1;
     /*   map.addMarker(new MarkerOptions()
                .position(new LatLng(38.051084, 23.804034))
                .title("Hello world"));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(38.026092, 23.798087))
                .title("Hello world"));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(38.049647, 23.817958))
                .title("Hello world"));*/

        //    System.out.println(map.getCameraPosition().target);

        map.setOnMarkerClickListener(this);

    }





    @Override
    public boolean onMarkerClick(Marker arg0) {

        if (arg0.getPosition().equals(searchm.getPosition())) {
            //   System.out.println(arg0.getTitle());
//System.out.println("mpika sto onmarker click when the position is the same of the marker we selected");
            return false;
        } else {
            if (finished == true) {

                //  System.out.println(arg0.getTitle());
//System.out.println(arg0.getPosition());
                //  System.out.println(searchm.getPosition());
                progress = ProgressDialog.show(this, "Please wait",
                        "Loading", true);


                int temp4 = locations.indexOf(arg0.getTitle());
                //  System.out.println(avail.get(temp4));
//System.out.println(locations.get(605));
                new ImageDownloadTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, file.get(temp4));

//Bitmap eikona=temp;
                //  Intent intent1 = new Intent(MainActivity.this, example.class);
                //  startActivity(intent1);
                // Intent intent = new Intent(MainActivity.this, showimg.class);
                //  intent.putExtra("bitmap",eikona);
                //   startActivity(intent);

                // setContentView(R.layout.image_view);
                //    image = (ImageView) findViewById(R.id.imageView1);
                //   image.setImageResource(R.drawable.successstories);

                //  Toast.makeText(MainActivity.this, arg0.getTitle(), Toast.LENGTH_LONG).show();// display toast
                return true;
            } else {

                Context context1 = getApplicationContext();
                CharSequence text1 = "Updating map points!";
                int duration1 = Toast.LENGTH_LONG;
                final Toast toast1 = Toast.makeText(context1, text1, duration1);
                toast1.show();
                return true;
            }
        }

    }























    // asynctask to connect to the server and download data in the background
    public class ConnectTask extends
            AsyncTask<String, ArrayList<String>, ArrayList<String>> {
        int x = 0;

        @Override
        protected ArrayList<String> doInBackground(String... message) {

            // we create a TCPClient object
            TcpClient = new Client(new Client.OnMessageReceived() {
                @Override
                public void messageReceived(ArrayList<String> message) {
                    // this method calls the onProgressUpdate to portray the map
                    // markers
 begin=true;
                    x++;
                    System.out.println(x);

                    if (x == 1) {
                        locations.addAll(message);
                        // System.out.println(locations);

                    }
                    if (x == 2) {
                        file.addAll(message);

                        //  System.out.println(file.get(19));

                    }

                    if (x == 3) {
                        lat.addAll(message);

                        // System.out.println();
                    }
                    if (x == 4) {
                        lng.addAll(message);

                        // System.out.println();
                    }
                    if (x == 5) {
                        avail.addAll(message);
                        //  System.out.println(avail);
                        x = 0;

                        publishProgress(locations);

                    }

                }

            });


            TcpClient.run();
            // if the server is down a message is displayed
            if (!TcpClient.isRunning()) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // creates and shows the message when the server is down
                        execute=false;
                        Context context = getApplicationContext();
                        CharSequence text = "Server connectivity issues - Check your internet connection!";
                        int duration = Toast.LENGTH_LONG;

                        final Toast toast = Toast.makeText(context, text,
                                duration);
                        toast.show();

                    }
                });

            }

            return null;

        }

        protected void onProgressUpdate(ArrayList<String>... values) {
            System.out.println(locations.size());
            //  System.out.println(file.size());
            //  System.out.println(lat.size());
            //  System.out.println(lng.size());
            //  System.out.println(avail.size());
            // mTcpClient.stopClient();
            //  double  currentlat=51.5286416;
            //  double   currentlng = -0.1015987;
            float[] resu= new float[1];
            // finished=true;
            // creates the map markers and put them on the map as the results
            // are published
            //   System.out.println(latitude);
            //  System.out.println(longitude);
            //   System.out.println("the size of the avail file is "+ lat.size());
            //   System.out.println("the size of the loc file is "+ locations.size());
            //  System.out.println("in the debug "+avail.get(7));
            for (int i = 0; i < locations.size(); i++) {



                //  System.out.println("entered inside the progress");

                Location.distanceBetween(latitude, longitude, Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)), resu);
                //System.out.println(latitude);
                // System.out.println(longitude);
                resu[0] = resu[0] * 0.000621371192f;
                //  System.out.println(resu[0]);
                if (resu[0] < radius) {

                    //  System.out.println("mpika sto radius " );
                    double lati = Double.parseDouble(lat.get(i));
                    double lngt = Double.parseDouble(lng.get(i));
                    String location = locations.get(i);
                    if (avail.get(i).equals("true")) {
                        //  System.out.println("mpika gia " + i);

                        //  System.out.println("in the debug "+avail.get(i));
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(lati, lngt))
                                .title(location)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }


                }




            }
            finished=true;

        }

        protected void onPostExecute(ArrayList<String> test) {
            finished=true;
            // System.out.println("debug");

        }

    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        // getSupportMenuInflater().inflate(R.menu.activity_main_actions, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.requestFocus();
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                //  System.out.println(query);
                strPlace=query;
                map.clear();

                Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> adrs = null;
                try{
                    adrs = gc.getFromLocationName(strPlace,5);
                }catch(IOException e){

                }finally{

                    if (adrs != null) {
                        if (adrs.size() > 0) {

                            LatLng loc = new LatLng(adrs.get(0).getLatitude(), adrs.get(0).getLongitude());

                            //    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                            //    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                            // Zoom in, animating the camera.
                            //    map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                            latitude=adrs.get(0).getLatitude();
                            longitude=adrs.get(0).getLongitude();
                            //  System.out.println("before search");
                            //  System.out.println(latitude + longitude);
                            CameraUpdate yourloc=CameraUpdateFactory.newLatLngZoom(loc,14);
                            map.animateCamera(yourloc);
                            searchm= map.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(strPlace));
                            created=true;
                            templat=latitude;
                            templng=longitude;
                            searchm.showInfoWindow();

                            float[] resu= new float[1];

                            for (int i = 0; i < locations.size(); i++) {





                                Location.distanceBetween(latitude, longitude, Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)), resu);

                                resu[0] = resu[0] * 0.000621371192f;
                                //   System.out.println(resu[0]);
                                if (resu[0] < radius) {


                                    double lati = Double.parseDouble(lat.get(i));
                                    double lngt = Double.parseDouble(lng.get(i));
                                    String location = locations.get(i);
                                    if (avail.get(i).equals("true")) {
                                        // System.out.println("in the debug "+avail.get(i));
                                        map.addMarker(new MarkerOptions()
                                                .position(new LatLng(lati, lngt))
                                                .title(location)
                                                .icon(BitmapDescriptorFactory
                                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    }


                                }




                            }








                        }

                    }}

                searchView.clearFocus();

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);

        //return super.onCreateOptionsMenu(menu);
    }




    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                FlurryAgent.logEvent("Search Button");
                //   String strPlace = etSearch.getText().toString();
                //String strPlace="marousi";


                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null){
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }




                return true;
            case R.id.action_location_found:
                // radius
                FlurryAgent.logEvent("Radius Button");
                ShowDialog();
                return true;
            case R.id.action_refresh:
                // refresh
                FlurryAgent.logEvent("Refresh Button");

// when the refresh button is cleared the arraylists are cleared
                // first and then the request for new data from the server is
                // done
                //finished=false;
                locations.clear();
                file.clear();
                lat.clear();
                lng.clear();
                avail.clear();
                //  map.clear();
                if (!isOnline()) {

                    final Toast toast = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "No internet connectivity, turn on your Wi-Fi or mobile data!",
                                    Toast.LENGTH_LONG);
                    toast.show();
                }

                if (TcpClient != null) {
                    // sends the proper messages to the server to receive the
                    // data needed
                    begin=true;
                    TcpClient.sendMessage("locations");
                    TcpClient.sendMessage("file");
                    TcpClient.sendMessage("lat");
                    TcpClient.sendMessage("lng");
                    TcpClient.sendMessage("availability");

                }

                if (execute==false && isOnline()) {


                    new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    execute=true;
                    if (TcpClient != null) {
                        // sends the proper messages to the server to receive the
                        // data needed


                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        finished=false;
                                        locations.clear();
                                        file.clear();
                                        lat.clear();
                                        lng.clear();
                                        avail.clear();
                                        // map.clear();
                                        if(isOnline()) {
                                            begin=true;
                                            TcpClient.sendMessage("locations");
                                            TcpClient.sendMessage("file");
                                            TcpClient.sendMessage("lat");
                                            TcpClient.sendMessage("lng");
                                            TcpClient.sendMessage("availability");
                                        }
                                    }
                                },
                                2000
                        );


                    }

                }














                return true;
            case R.id.action_help:
                // help action
                FlurryAgent.logEvent("Help Button");

                Intent intent1 = new Intent(MainActivity.this, example.class);
                startActivity(intent1);

                return true;


          /*  case R.id.action_share_img:
                // help action
                FlurryAgent.logEvent("Share");

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */



    public void ShowDialog()
    {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
                (ViewGroup) findViewById(R.id.layout_dialog));

        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1



        // popDialog.setTitle("Please Select Radius ");

        popDialog.setView(Viewlayout);

        //  seekBar1
        SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setMax(20);

        seek1.setProgress(radius);
        item1.setText("Radius in miles :  " + radius );
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                item1.setText("Radius in miles : " + progress);
                radius=progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });




        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        map.clear();

                        if (latitude!=location.getLatitude()) {
                            // searchm = map.addMarker(new MarkerOptions()
                            //        .position(new LatLng(latitude, longitude))
                            //        .title(strPlace));
                            // searchm.setVisible(false);
                            templat=latitude;
                            templng=longitude;
                        }

                        //  double  currentlat=51.5286416;
                        //  double   currentlng = -0.1015987;
                        float[] resu= new float[1];

                        for (int i=0; i < locations.size(); i++) {


                            Location.distanceBetween(latitude, longitude, Double.parseDouble(lat.get(i)),Double.parseDouble(lng.get(i)), resu);

                            resu[0] = resu[0] * 0.000621371192f;
                            //   System.out.println(resu[0]);
                            if (resu[0] < radius) {


                                double lati = Double.parseDouble(lat.get(i));
                                double lngt = Double.parseDouble(lng.get(i));
                                String location = locations.get(i);
                                if (avail.get(i).equals("true")) {
                                    map.addMarker(new MarkerOptions()
                                            .position(new LatLng(lati, lngt))
                                            .title(location)
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                }


                            }

                        }

                    }

                });


        popDialog.create();
        popDialog.show();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FlurryAgent.onStartSession(this, "GRPVSRP4NKVSN2RSVZK9");
        // System.out.println("start");

    }


    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
        // System.out.println("end");
    }




    // asynctask to download image in the background and store it in a variable
    public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url1 = new URL(urls[0]);
                HttpURLConnection ucon = (HttpURLConnection) url1.openConnection();
                ucon.setInstanceFollowRedirects(false);
                //URL secondURL = new URL(ucon.getHeaderField("Location"));

                //   URL test= new URL("https://s3-eu-west-1.amazonaws.com/tfl.pub/Jamcams/0000107382.jpg");

                // bitmapResult1 = BitmapFactory
                //     .decodeStream(test.openConnection().getInputStream());

                bitmapResult1 = BitmapFactory
                        .decodeStream((InputStream) new URL(ucon.getHeaderField("Location"))
                                .getContent());



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // System.out.println("inback");
            // System.out.println(bitmapResult1);
//System.out.println(urls[0]);
            Intent intent = new Intent(MainActivity.this, showimg.class);
            intent.putExtra("bitmap",bitmapResult1);
            // intent.putExtra("url","aek");
            progress.dismiss();
            startActivity(intent);

            return bitmapResult1;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // shows the picture on the UI thread
            temp = bitmap;
            //System.out.println("onpost");
//System.out.println(temp);










            // Intent intent = new Intent(MainActivity.this, showimg.class);
            // intent.putExtra("bitmap",temp);
            // startActivity(intent);
            cancel(true);
            // image.setImageResource(R.drawable.ic_launcher);
        }

    }










    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mRunnable);
        super.onPause();
        background=true;
        System.out.println(background);
    }




    @Override
    public void onResume() {
        if (begin==false){mHandler.postDelayed(mRunnable, 5000);}
        mHandler.postDelayed(mRunnable, 120000);
        super.onResume();  // Always call the superclass method first

        FlurryAgent.logEvent("App resumed");
        estimatedTime=System.currentTimeMillis()-startTime;
       // System.out.println(estimatedTime);
        if (estimatedTime>120000){
            mHandler.postDelayed(mRunnable, 0);

        }

        background=false;
        System.out.println(background);
    }













}
