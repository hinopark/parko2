package com.plaxto.hino.mobile.park;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class map_activity2 extends Activity  implements LocationListener, android.location.LocationListener, SensorEventListener{
	GoogleMap map;
	Circle MyCircle;
	private boolean started = false;
	double latitude = -6.4727133; 
	double longitude = 106.8660923;
	static double slatitude;
	static double slongitude;
	public static int viewMapSetting;
	private String provider;
	private LocationManager locationManager;
	Marker now;
	Marker youarehere;
	 static  LatLng POSISI_MOBIL;
	 static  LatLng POSISI_USER = new LatLng(FormSearchActivity.mLatitude, FormSearchActivity.mLongitude);
	 static  LatLng POSISI_AREA = new LatLng(listParking.Clatitude,listParking.Clongitude);
	 static  LatLng MOUNTAIN_VIEW = new LatLng(-6.4727133, 106.8660923);
	 @SuppressLint("NewApi")
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        slatitude=0;
        slongitude=0;
        slatitude = listParking.Slatitude;
        slongitude = listParking.Slongitude;
        POSISI_MOBIL = new LatLng(slatitude,slongitude);
        POSISI_USER = new LatLng(FormSearchActivity.mLatitude, FormSearchActivity.mLongitude);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
       // Toast.makeText(getApplicationContext(), 
  		//"ID MAP SETTING"+slatitude+"", 
  		//Toast.LENGTH_LONG).show();
        switch (viewMapSetting) {
		case 1:
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);	
			break;
		
		case 2:
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);	
			break;	
		
		}
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        started = true;
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));

     // Zoom in, animating the camera.
     //map.animateCamera(CameraUpdateFactory.zoomIn());

     // Zoom out to zoom level 10, animating with a duration of 2 seconds.
     //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

     // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
     if(slatitude == 0 && slongitude == 0){
    	 CameraPosition cameraPosition = new CameraPosition.Builder()
         .target(POSISI_USER)      // Sets the center of the map to Mountain View
         .zoom(18)                   // Sets the zoom
         .bearing(90)                // Sets the orientation of the camera to east
         .tilt(30)                   // Sets the tilt of the camera to 30 degrees
         .build();                   // Creates a CameraPosition from the builder
     map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
     youarehere = map.addMarker(new MarkerOptions().position(POSISI_USER).title("You are here...").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
     youarehere.showInfoWindow();
     youarehere.setPosition(POSISI_USER);
     }else{
      CameraPosition cameraPosition = new CameraPosition.Builder()
         .target(POSISI_MOBIL)      // Sets the center of the map to Mountain View
         .zoom(18)                   // Sets the zoom
         .bearing(90)                // Sets the orientation of the camera to east
         .tilt(30)                   // Sets the tilt of the camera to 30 degrees
         .build();                   // Creates a CameraPosition from the builder
     map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
     //map.getMapCenter();   
        LatLng latLng = new LatLng(-6.4727133, 106.8660923);
        Marker mobilnya = map.addMarker(new MarkerOptions().position(POSISI_MOBIL).title("Vin Number : "+listParking.vinNya));
        mobilnya.showInfoWindow();
        mobilnya.setPosition(POSISI_MOBIL);
        
        CircleOptions circleOptions = new CircleOptions()
        .center(POSISI_MOBIL)   //set center
        .radius(listParking.maccuracy)   //set radius in meters
        .fillColor(0x40ff0000)  //semi-transparent
        .strokeColor(Color.BLUE)
        .strokeWidth(5);
        Polygon polygon = map.addPolygon(new PolygonOptions()
        .add(new LatLng(listParking.lat1, listParking.long1), new LatLng(listParking.lat2, listParking.long2), new LatLng(listParking.lat3, listParking.long3), new LatLng(listParking.lat4, listParking.long4))
        .strokeColor(Color.RED)
        );
        MyCircle = map.addCircle(circleOptions);
     
        youarehere = map.addMarker(new MarkerOptions().position(POSISI_USER).title("You are here...").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        //youarehere.showInfoWindow();
        
     }
        //request location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager
                .getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);
	 	
        
      //Toast.makeText(getApplicationContext(), 
		//"Latitude"+slatitude+"", 
		//Toast.LENGTH_LONG).show();
        
        
	}
    
	 public void onSensorChanged(SensorEvent event) {

	        if (started) {

	            double x = event.values[0];
	            double y = event.values[1];
	            double z = event.values[2];

	            long timestamp = System.currentTimeMillis();

	            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	            Criteria criteria = new Criteria();
	            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
	            criteria.setAccuracy(Criteria.ACCURACY_FINE);

	            provider = locManager.getBestProvider(criteria, true);
	            Location location = locManager.getLastKnownLocation(provider);

	            double latitude = 0;
	            double longitude = 0;
	            if (location != null) {
	                latitude = location.getLatitude();
	                longitude = location.getLongitude();
	            }
	           

	        }
	    }
	 
	 
    @Override
    public void onLocationChanged(Location location) {

        if(youarehere != null){
                    youarehere.remove();

                }

        //TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        
        youarehere = map.addMarker(new MarkerOptions().position(latLng).title("You Are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        youarehere.showInfoWindow();
        // Showing the current location in Google Map
        //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(latLng)      // Sets the center of the map to Mountain View
        .zoom(18)                   // Sets the zoom
        .bearing(90)                // Sets the orientation of the camera to east
        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
        .build();                   // Creates a CameraPosition from the builder
        //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        
        //map.animateCamera(CameraUpdateFactory.zoomTo(15));






    }
    
    
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

     @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }
    
	public void onBackPressed() {
		locationManager.removeUpdates(this);
		locationManager = null;
		//Intent i = new Intent(map_activity.this, FormSearchActivity.class);
		//startActivity(i);
		slatitude = 0;
		slongitude = 0;
		//POSISI_MOBIL = New LatLng(0,0);
		
		finish();
    }
	
	

}

