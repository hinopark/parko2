package com.plaxto.hino.mobile.park;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.zbar.Symbol;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.R.integer;
import android.app.ListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import com.plaxto.common.gps.GpsMain;
import com.plaxto.common.http.HttpSynch;
import com.plaxto.common.qrcode.ZBarConstants;
import com.plaxto.common.qrcode.ZBarScannerActivity;
import com.plaxto.hino.mobile.park.global.GlobalModel;
import com.plaxto.hino.mobile.park.listSearch.LoadAllProducts;

public class MainActivity  extends ListActivity {
	
	
	private static final String URL_API_ = LoginActivity.IP_SERVER+"MainGateSave.php";
	private static final String LOGIN_CACHE = "loginhinopark";
	private static final int ZBAR_SCANNER_REQUEST 	 = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "vinNumber";
	private static final String TAG_NAME = "vinNumber";
	public ImageButton btnScanQr;
	JSONArray products = null;
	@SuppressWarnings("unused")
	private TextView txtDate;
	private EditText txtResultQR;
	private EditText txtLatitude;
	private EditText txtLongitude;
	Spinner spinJenis;
	private Button btnCek;
	private static String url_all_products = LoginActivity.IP_SERVER+"cariTop.php";
	private ProgressDialog dialog;
	private SimpleDateFormat formatter;
	public static String checkType;
	private String statusMessage;
	private String qrData;
	private String messageLoading;
	private String currentDateandTime;
	private double mLatitude;
	private double mLongitude;
	private double maccuracy;
	private String mLatString;
	private String mLongString;
	private String mVinString;
	private ProgressDialog pDialog;
	private GpsMain gpsMain;
	private HttpSynch httpSync;
	private GlobalModel globalModel;
	private String messageKirim;
	View view2;
	private String registerIdJenis;
	private ArrayList<NameValuePair> params;
	JSONParser jParser = new JSONParser();
	AlertDialog.Builder builderRegister;
	ArrayList<HashMap<String, String>> productsList;
	ArrayList<HashMap<String, String>> masterJenisList;
	List<String> spinnerItem = new ArrayList<String>();
	List<String> spinnerIndex = new ArrayList<String>();
	List<String> spinnerList = new ArrayList<String>();
	LocationManager locationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupViews();
		productsList = new ArrayList<HashMap<String, String>>();
		getActionBar().setTitle("Gate In : "+ LoginActivity.namaParkirArea);
		new LoadHistory().execute();
		ListView lv = getListView();
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		currentDateandTime = formatter.format(new Date());
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		
		 
		 
		 
		 Button btnSearch=(Button) findViewById(R.id.btnSearch);
		 if(checkType=="I"){
			 btnSearch.setText("Masuk");
		 }else{
			 btnSearch.setText("Keluar");
		 }
		 btnSearch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					txtResultQR.setError(null);
                    
					// Store values at the time of the login attempt.
					
					mVinString	= txtResultQR.getText().toString();

					boolean cancel = false;
					View focusView = null;

					// Check for a valid password.
					

					// Check for a valid email address.
					if(TextUtils.isEmpty(checkType)){
						Toast.makeText(getApplicationContext(), 
			        			"Please setting Gate Type", 
			        			Toast.LENGTH_LONG).show();
					}else{
					
					// Check for a valid email address.
					if (TextUtils.isEmpty(mVinString)) {
						txtResultQR.setError(getString(R.string.error_field_required));
						focusView = txtResultQR;
						cancel = true;
					} 

					if (cancel) {
						// There was an error; don't attempt login and focus the first
						// form field with an error.
						focusView.requestFocus();
					} else {
						// Show a progress spinner, and kick off a background task to
						// perform the user login attempt.
						// showProgressDialog("Berhasil Masuk Sini ...");
						initializeServer(null, null, 0.0, 0.0);
					}
					}
				}
			});
		 
		 		
	}
	
	
	

	
	public void menuKlik(View View){
		Toast.makeText(this, 
        		"Rear Facing Camera Unavailable", 
        		Toast.LENGTH_SHORT).show();
		
	}
	
	
	public void onPause(){
		super.onPause();
		gpsMain.stopGps();
		//System.exit(0);
	}
	
	public void onResume(){
		super.onResume();
		formatter = new SimpleDateFormat("dd MMMM yyyy");
		currentDateandTime = formatter.format(new Date());
		
		Log.e("DATE", currentDateandTime);
		//txtDate.setText(currentDateandTime);
		getActionBar().setTitle("Gate In : "+ LoginActivity.namaParkirArea);
		getActionBar().setSubtitle(currentDateandTime);
		Button btnSearch=(Button) findViewById(R.id.btnSearch);
		 if(checkType=="I"){
			 btnSearch.setText("Masuk");
		 }else{
			 btnSearch.setText("Keluar");
		 }
		 //new LoadHistory().execute();
	}

	
	public void onBackPressed() {
		gpsMain.stopGps();
		//showLogOut();
		Intent i = new Intent(MainActivity.this, AndroidDashboardDesignActivity.class);
		startActivity(i);
		finish();
	}
	
	private void setupViews() {
		gpsMain 	= new  GpsMain(MainActivity.this);
		globalModel	= new GlobalModel();
		txtDate		= (TextView)findViewById(R.id.date);
		txtResultQR	= (EditText)findViewById(R.id.vin_id);
		
		btnScanQr	= (ImageButton)findViewById(R.id.scan_qr);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    int itemId = item.getItemId();
	    switch (itemId) {
	    case R.id.action_settings:
	    	  AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        	  alert.setTitle("Konfirmasi Password");
        	  alert.setMessage("Masukkan Password Anda");

        	  // Set an EditText view to get user input 
        	  final EditText input = new EditText(MainActivity.this);
        	  input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        	  alert.setView(input);
        	 
        	  alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	  public void onClick(DialogInterface dialog, int whichButton) {
        	    String kiko = input.getText().toString();
        	    if(kiko.toString().equals("1234")){
        	    	Intent i = new Intent(MainActivity.this, FormSetting.class);
        	    	startActivity(i);
        	    	finish();
        	    }else{
        	    	Toast.makeText(MainActivity.this, 
                    		"Invalid Password, please try again.", 
                    		Toast.LENGTH_SHORT).show();	
        	    }
        	    }
        	  });

        	  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int whichButton) {
        	      // Canceled.
        	    }
        	  });

        	  alert.show();
	    	
			
			//finish();
			
			break;
	    case R.id.action_search:
	    	Intent b = new Intent(MainActivity.this, FormSearchActivity.class);
			startActivity(b);
	    	//initialize();
	    	//initializeServer(null, null, 0.0, 0.0);
	    	
	    	break;
	    
	    case R.id.action_listParkir:
	    	Intent c = new Intent(MainActivity.this, listParking.class);
			startActivity(c);
			finish();
	    	//initialize();
	    	//initializeServer(null, null, 0.0, 0.0);
	    	
	    break;
	    case R.id.about_version:
	    dialoAbout();
	    break;
	    }
	    return true;
	}
	
	public void launchQRScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, 
            		"Rear Facing Camera Unavailable", 
            		Toast.LENGTH_SHORT).show();
        }
    }
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
            	if (resultCode == RESULT_OK) {
            		qrData = data.getStringExtra(ZBarConstants.SCAN_RESULT);
            		
            		txtResultQR.setText(qrData);

            		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            		currentDateandTime = formatter.format(new Date());
            		
            		// INPUT STICK = 
            		//initializeServer(qrData, currentDateandTime, 0.0, 0.0);
                }
            	
            	break;
        }
    }
	
	private void attemptSend() {
		// Reset errors.
		
		txtResultQR.setError(null);

		// Store values at the time of the login attempt.
		
		mVinString	= txtResultQR.getText().toString();
        
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		

		// Check for a valid email address.
		if(checkType==""){
			Toast.makeText(getApplicationContext(), 
        			"Please setting Gate Type", 
        			Toast.LENGTH_SHORT).show();
		}
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mVinString)) {
			txtResultQR.setError(getString(R.string.error_field_required));
			focusView = txtResultQR;
			cancel = true;
		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//showProgressDialog("Berhasil Masuk Sini ...");
			initializeServer(null, null, 0.0, 0.0);
		}
	}
	
	private void initializeServer(String qrCode, String date, double latitude, double longitude){
		if(isOnline() ==  true){
			
			createJSONObjectRequest();
			showProgressDialog("Loading...");
			
			httpSync = new HttpSynch(){
				@Override
				protected void onPostExecute(Object result) {
					String data 	= (String)result.toString();
					JSONObject json	= globalModel.ConvertStringToJSON(data);
					Log.e("DATA JSON", data);
					Log.e("JSON RESPONSE", json.toString());
					
					parseJSON(json);
					
				}
			};
			
			httpSync.execute(URL_API_, params);
			
		}else{
			showSettingAlert();
		}
	}
	
	private void initializeLoc(){
		messageLoading = "Getting Location...";
		showProgressDialog(messageLoading);
		new Handler().postDelayed(new Runnable() {
	        public void run() {  
	        	try {
	    			gpsMain.startGps();
	    			Location location = gpsMain.getLocation();
	    			
	    			mLatitude 	= location.getLatitude();
	    			mLongitude 	= location.getLongitude();
	    			
	    			
	    			txtLatitude.setText(mLatitude + "");
	    			txtLongitude.setText(mLongitude + "");
	    			
	    			//gpsMain.stopGps();
	    			dialog.dismiss();
	    		} catch (NullPointerException e) {
	    			e.printStackTrace();
	    			dialog.dismiss();
	    			Toast.makeText(getApplicationContext(), 
	            			"Gagal mendapatkan lokasi.", 
	            			Toast.LENGTH_SHORT).show();
	    		}
	        }
	    }, 3500); 
	}
	
	@SuppressWarnings("unused")
	private void initialize() {
		messageLoading = "Sending data...";
		showProgressDialog(messageLoading);
	    new Handler().postDelayed(new Runnable() {
	        public void run() {  
	        	Toast.makeText(getApplicationContext(), 
	        			"Data lokasi berhasil disimpan ke server.", 
	        			Toast.LENGTH_SHORT).show();
	        	txtLatitude.setText("");
	        	txtLongitude.setText("");
	        	dialog.dismiss();
	        }
	    }, 3500); 
	}
	
	public void parseJSON(JSONObject jsonResponse){
		try {
			String res    = jsonResponse.getString("statusId");
			statusMessage = jsonResponse.getString("statusMessage");
			String statusSimpan = jsonResponse.getString("statusSimpan");
			String statusPesan = jsonResponse.getString("statusPesan");
			
		    if(res.toString().equals("001")){
		    	if(statusPesan.toString().equals("002")){
		    		dialog.dismiss();
		    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
			    	 
	                // Setting Dialog Title
	                alertDialog.setTitle("Info :: ");
	                
	                // Setting Dialog Message
	                alertDialog.setMessage(statusMessage);
	  
	                // Setting Icon to Dialog
	                alertDialog.setIcon(R.drawable.save);
	 
	                // Setting Positive "Yes" Button
	                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                  public void onClick(DialogInterface dialog, int which) {
	                    // User pressed YES button. Write Logic Here
	                    //Toast.makeText(getApplicationContext(), "Data Updated", 
	                      //                  Toast.LENGTH_SHORT).show();
	                    new LoadHistory().execute();
	                    Log.d("Masuk ke row ", "B01");
	                    messageKirim = "Masuk ke sini 01. VIN : "+txtResultQR.getText().toString();
	                    new showPush().execute();
	                    }
	                });
	 
	 
	 
	                // Showing Alert Message
	                alertDialog.show();
	                if(statusSimpan.toString().equals("001")){
	    	        	txtResultQR.setText("");
	    		    	Toast.makeText(getApplicationContext(), 
	    	        			statusMessage, 
	    	        			Toast.LENGTH_SHORT).show();
	    		    	dialog.dismiss();
	    		    	new LoadHistory().execute();
	    		    	}else{
	    		    		txtResultQR.setText("");
	    			    	Toast.makeText(getApplicationContext(), 
	    		        			statusMessage, 
	    		        			Toast.LENGTH_SHORT).show();
	    			    	dialog.dismiss();
	    			    	new LoadHistory().execute();
	    			    	Log.d("Masuk ke row ", "B02");
	    			    	messageKirim = "Masuk ke sini 02. VIN : "+txtResultQR.getText().toString();
		                    new showPush().execute();
	    		    	}	
		    		
		    	}else{
		    	if(statusSimpan.toString().equals("001")){
	        	
		    	Toast.makeText(getApplicationContext(), 
	        			statusMessage, 
	        			Toast.LENGTH_SHORT).show();
		    	dialog.dismiss();
		    	new LoadHistory().execute();
		    	if(checkType.toString().equals("I")){
		    		messageKirim = "Mobil Masuk Baru. VIN : "+txtResultQR.getText().toString();
		    		new showPush().execute();
		    	}else{
		    		messageKirim = "Mobil Keluar Baru. VIN : "+txtResultQR.getText().toString();
		    	}
		    	
		    	
		    	Log.d("Masuk ke row ", "B03");
		    	txtResultQR.setText("");
		    	}else{
		    		
			    	Toast.makeText(getApplicationContext(), 
		        			statusMessage, 
		        			Toast.LENGTH_SHORT).show();
			    	dialog.dismiss();
			    	new LoadHistory().execute();
			    	messageKirim = "Masuk ke sini 03. VIN : "+txtResultQR.getText().toString();
			    	Log.d("Masuk ke row ", "B04");
                    new showPush().execute();
                    txtResultQR.setText("");
		    	}
		    	}
		    	//AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		    	 
                // Setting Dialog Title
                //alertDialog.setTitle("Pilih Area Parkir");
 
                // Setting Dialog Message
                //alertDialog.setMessage("Berikut ini area parkir yang masih kosong, silahkan pilih.");
  
                // Setting Icon to Dialog
                //alertDialog.setIcon(R.drawable.save);
 
                // Setting Positive "Yes" Button
                //alertDialog.setPositiveButton("Area 1", new DialogInterface.OnClickListener() {
                  //  public void onClick(DialogInterface dialog, int which) {
                    // User pressed YES button. Write Logic Here
                    //Toast.makeText(getApplicationContext(), "Area 1", 
                      //                  Toast.LENGTH_SHORT).show();
                   // }
                // });
 
                // Setting Negative "NO" Button
                // alertDialog.setNegativeButton("Area 2", new DialogInterface.OnClickListener() {
                   // public void onClick(DialogInterface dialog, int which) {
                    // User pressed No button. Write Logic Here
                   // Toast.makeText(getApplicationContext(), "Area 2", Toast.LENGTH_SHORT).show();
                   // }
               // });
 
                // Setting Netural "Cancel" Button
               // alertDialog.setNeutralButton("Area 3", new DialogInterface.OnClickListener() {
                //    public void onClick(DialogInterface dialog, int which) {
                    // User pressed Cancel button. Write Logic Here
                  //  Toast.makeText(getApplicationContext(), "Area 3",
                    //                    Toast.LENGTH_SHORT).show();
                   // }
//                });
 
                // Showing Alert Message
  //              alertDialog.show();
		    	
		    }else if(res.toString().equals("002")){	
		    	
		    	dialog.dismiss();
		    	//dialog.dismiss();
	    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		    	 
                // Setting Dialog Title
                alertDialog.setTitle("Info :: ");
 
                // Setting Dialog Message
                alertDialog.setMessage(statusMessage);
  
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.save);
 
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                	  new LoadHistory().execute();
                	  messageKirim = "Masuk ke sini 04. VIN : "+txtResultQR.getText().toString();
	                    new showPush().execute();
	                    Log.d("Masuk ke row ", "B05");
                    // User pressed YES button. Write Logic Here
                    //Toast.makeText(getApplicationContext(), "Area 1", 
                      //                  Toast.LENGTH_SHORT).show();
                    }
                });
 
 
 
                // Showing Alert Message
                alertDialog.show();
	    		
		    }else if(res.toString().equals("003")){
		    	dialog.dismiss();
		    	if(checkType.toString().equals("I")){
		    		askRegister();
		    		
		    	}else{
		    	//Toast.makeText(getApplicationContext(), "Masuk Ke sini GAN", 
                   //     Toast.LENGTH_SHORT).show();
		    	//dialog.dismiss();
	    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		    	 
                // Setting Dialog Title
                alertDialog.setTitle("Info :: ");
 
                // Setting Dialog Message
                alertDialog.setMessage(statusMessage);
  
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.save);
 
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                	  new LoadHistory().execute();
                	  messageKirim = "Masuk ke sini 01. VIN : "+txtResultQR.getText().toString();
	                    if(checkType.toString().equals("I")){
                	  	new showPush().execute();
	                    }
	                  Log.d("Masuk ke row ", "B06");
                    // User pressed YES button. Write Logic Here
                   //Toast.makeText(getApplicationContext(), "Area 1", 
                                        //Toast.LENGTH_SHORT).show();
                    }
                });
 
                
 
                // Showing Alert Message
                alertDialog.show();
                
		    }
                
		  }else if(res.toString().equals("007")){
			  dialog.dismiss();
			  AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		    	 
              // Setting Dialog Title
              alertDialog.setTitle("Info :: ");

              // Setting Dialog Message
              alertDialog.setMessage("Mobil ini tidak untuk area parkir ini");

              // Setting Icon to Dialog
              alertDialog.setIcon(R.drawable.save);

              // Setting Positive "Yes" Button
              alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
              	 
                  }
              });

              

              // Showing Alert Message
              alertDialog.show();
			  
		  }else if(res.toString().equals("008")){
			  dialog.dismiss();
			  AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		    	 
              // Setting Dialog Title
              alertDialog.setTitle("Info :: ");

              // Setting Dialog Message
              alertDialog.setMessage("Mobil ini belum bisa diambil");

              // Setting Icon to Dialog
              alertDialog.setIcon(R.drawable.save);

              // Setting Positive "Yes" Button
              alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
              	 
                  }
              });

              

              // Showing Alert Message
              alertDialog.show();
		  }
		    
		    
		} catch (NumberFormatException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Number Unable to connect server. Please try again later.", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (JSONException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Koneksi internet anda lambat, silahkan coba beberapa saat lagi. ", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public void createJSONObjectRequest(){
		//String email = LoginActivity.email;
		StringBuilder sb = new StringBuilder();
		sb.append(LoginActivity.id);
		String idPetugas = sb.toString();
		
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		currentDateandTime = formatter.format(new Date());
		String vinId	= txtResultQR.getText().toString();
		String email = LoginActivity.email;
		params = new ArrayList<NameValuePair>();
		JSONObject jsonData = new JSONObject();
		
		try {
			
			jsonData.put("vin", txtResultQR.getText().toString());
			jsonData.put("email", email);
			jsonData.put("waktu", currentDateandTime);
			jsonData.put("id", idPetugas);	
			jsonData.put("idParkir", LoginActivity.idParkirPetugas);
			params.add(new BasicNameValuePair("vin",vinId));
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("waktu",currentDateandTime));
			params.add(new BasicNameValuePair("id",idPetugas));
			params.add(new BasicNameValuePair("type",checkType));
			params.add(new BasicNameValuePair("idParkir",LoginActivity.idParkirPetugas));
			Log.e("JSON : ", params.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	private boolean isOnlineGps() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if(!isGPSEnabled || !isNetworkEnabled){
	        return true;
	    	}
	    }
	    return false;
	}
	
	private void showSettingAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to enable mobile data in order to use this application:")
               .setCancelable(false)
               
               .setPositiveButton("Turn on Data", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        startActivity(newintent);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();	
                   }
               });
        builder.show();
		Toast.makeText(this, 
				"Data connection unavailable. " +
				"Please check your network connection or try again later.", 
				Toast.LENGTH_LONG).show();
	}
	
	private void showLogOut(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to log out or exit application ?")
               .setCancelable(false)
               .setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        //Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        //startActivity(newintent);
                	   SharedPreferences settings = getSharedPreferences(
     							LOGIN_CACHE, Context.MODE_PRIVATE);
     							settings.edit().clear().commit();
                	   Intent i = new Intent(MainActivity.this, LoginActivity.class);
							startActivity(i);
                	   
                	   finish();
                   }
               })
               
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();	
                   }
               })
               .setNeutralButton("Just Exit", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Intent i = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(i);
                       finish();
                   }
               })
               
               ;
        builder.show();
		//Toast.makeText(this, 
			//	"Data connection unavailable. " +
			//	"Please check your network connection or try again later.", 
			//	Toast.LENGTH_LONG).show();
	}
	
	private void askRegister(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("VIN Number : "+txtResultQR.getText().toString()+" is Unregistered.")
               .setCancelable(false)
               .setPositiveButton("Register Now and Check In", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialogRegister();
                        //Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        //startActivity(newintent);
                	   
                	   //Intent i = new Intent(MainActivity.this, LoginActivity.class);
							//startActivity(i);
                	   
                	   //finish();
                   }
               })
               
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();	
                   }
               })
               
               
               ;
        builder.show();
		//Toast.makeText(this, 
			//	"Data connection unavailable. " +
			//	"Please check your network connection or try again later.", 
			//	Toast.LENGTH_LONG).show();
	}
	
	private void dialoAbout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("e-Parking HINO versi 1.0")
               .setCancelable(false)
               
               
               .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();	
                   }
               })
               
               
               ;
        builder.show();
		//Toast.makeText(this, 
			//	"Data connection unavailable. " +
			//	"Please check your network connection or try again later.", 
			//	Toast.LENGTH_LONG).show();
	}
	
	private void dialogRegister(){
		builderRegister = new AlertDialog.Builder(this);
		LayoutInflater inflater = MainActivity.this.getLayoutInflater();

	    view2 = inflater.inflate(R.layout.register_vin, null);
	    
	    
	    
	    new LoadMasterJenis().execute();
	    
        
		//Toast.makeText(this, 
			//	"Data connection unavailable. " +
			//	"Please check your network connection or try again later.", 
			//	Toast.LENGTH_LONG).show();
	}
	
	
	
	

	private void showProgressDialog(String messageLoading){
		dialog = new ProgressDialog(this);
		this.dialog.setMessage(messageLoading);
		this.dialog.setIndeterminate(false);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}
	
	class LoadHistory extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading data history. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("checkType", checkType));
			params2.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			// getting JSON string from URL
			JSONObject json2 = jParser.makeHttpRequest(url_all_products, "GET", params2);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json2.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json2.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json2.getJSONArray(TAG_PRODUCTS);
					Log.d("Debug Disini","A1");
					// looping through All Products
					productsList.clear();
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString("vinNumber");
						//String name = c.getString(TAG_NAME);
						//String brand = c.getString("brand");
						//String typenya = c.getString("type");
						//String areaParkir = c.getString("namaParkir");
						String waktuCheck = c.getString("WaktuCheck");
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						//map.put(TAG_NAME, name);
                        //map.put("brand", brand + " - " + typenya);
                       map.put("waktuCheck", waktuCheck);
                       
						map.put("vin", c.getString("vinNumber"));
						//map.put("areaParkir", areaParkir);
						//map.put("waktuMasuk", waktuMasuk);
                        // adding HashList to ArrayList
						productsList.add(map);
						Log.d("Debug Disini","A2");
					}
				} else {
					Log.d("Debug Disini","No Products");
					//Toast.makeText(MainActivity.this, 
						//	"ERROR SINI Data connection unavailable. " +
						//	"Please check your network connection or try again later.", 
						//	Toast.LENGTH_LONG).show();
					// no products found
					// Launch Add New product Activity
					//Intent i = new Intent(getApplicationContext(),
							//NewProductActivity.class);
					// Closing all previous activities
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							MainActivity.this, productsList,
							R.layout.list_top, new String[] { TAG_PID, "vin","waktuCheck"},
							new int[] { R.id.pid, R.id.VinNumberList, R.id.Waktunyah}){
						
							@Override
					        public View getView (int position, View convertView, ViewGroup parent)
					        {
								View v = super.getView(position, convertView, parent);
								v.setBackgroundColor(position % 2 == 0 ? Color.LTGRAY : Color.GRAY);
								
								return v;
								
					        }
					};
					// updating listview
					Log.d("Debug Disini","A3");
					//setListAdapter(null);
					setListAdapter(adapter);
				}
			});

		}

		

	}
	
	class LoadMasterJenis extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("checkType", checkType));
			params2.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			// getting JSON string from URL
			JSONObject json2 = jParser.makeHttpRequest(LoginActivity.IP_SERVER+"listMasterJenis.php", "GET", params2);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json2.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json2.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json2.getJSONArray("products");
					Log.d("Debug Disini","A1");
					// looping through All Products
					//masterJenisList.clear();
					spinnerItem.clear();
					spinnerIndex.clear();
					spinnerList.clear();
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						spinnerItem.add(c.getString("brand")+" - "+c.getString("type"));
						spinnerIndex.add(c.getString("idJenis"));
						// Storing each json item in variable
						//String id = c.getString("vinNumber");
						//String name = c.getString(TAG_NAME);
						//String brand = c.getString("brand");
						//String typenya = c.getString("type");
						//String areaParkir = c.getString("namaParkir");
						//String waktuCheck = c.getString("WaktuCheck");
						// creating new HashMap
						//HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						//map.put(TAG_PID, id);
						//map.put(TAG_NAME, name);
                        //map.put("brand", brand + " - " + typenya);
						//map.put("waktuCheck", waktuCheck);
                       
						//map.put("vin", c.getString("vinNumber"));
						//map.put("areaParkir", areaParkir);
						//map.put("waktuMasuk", waktuMasuk);
                        // adding HashList to ArrayList
						//productsList.add(map);
						Log.d("Debug Disini","A2A");
					}
				} else {
					Log.d("Debug Disini","No Products");
					//Toast.makeText(MainActivity.this, 
						//	"ERROR SINI Data connection unavailable. " +
						//	"Please check your network connection or try again later.", 
						//	Toast.LENGTH_LONG).show();
					// no products found
					// Launch Add New product Activity
					//Intent i = new Intent(getApplicationContext(),
							//NewProductActivity.class);
					// Closing all previous activities
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("Masuk ke PostExecute","Ya");
			// dismiss the dialog after getting all products
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					spinJenis = (Spinner)view2.findViewById(R.id.spinnerMasterJenis);
					
					spinnerList.addAll(spinnerItem);
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
							android.R.layout.simple_spinner_item, spinnerList); 
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
					
					spinJenis.setAdapter(dataAdapter);
					builderRegister.setMessage("VIN Number : "+txtResultQR.getText().toString()+" is Unregistered.")
		               .setCancelable(false)
		               .setView(view2)
		               .setPositiveButton("Register Now and Check In", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   int posisik = spinJenis.getSelectedItemPosition();
		                	   registerIdJenis = spinnerIndex.get(posisik).toString();
		                	   
		                	   new registerVin().execute();
		                	   //Toast.makeText(MainActivity.this,
		                         //      "On Button Click : " + 
		                           //    "\n" + spinnerIndex.get(posisik).toString(),
		                             //  Toast.LENGTH_LONG).show();
		                        //Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		                        //startActivity(newintent);
		                	   
		                	   //Intent i = new Intent(MainActivity.this, LoginActivity.class);
									//startActivity(i);
		                	   
		                	   //finish();
		                   }
		               })
		               
		               .setNegativeButton("No", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       dialog.cancel();	
		                   }
		               })
		               
		               
		               ;
		        builderRegister.show();
					
					
				}
			});
			pDialog.dismiss();

		}

		

	}
	
	
	class registerVin extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Registering, Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			StringBuilder sb = new StringBuilder();
			sb.append(LoginActivity.id);
			String idPetugas = sb.toString();
			
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentDateandTime = formatter.format(new Date());
			
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("vin",txtResultQR.getText().toString()));
			
			params2.add(new BasicNameValuePair("waktu",currentDateandTime));
			params2.add(new BasicNameValuePair("id",idPetugas));
			params2.add(new BasicNameValuePair("registerIdJenis",registerIdJenis));
			params2.add(new BasicNameValuePair("idParkir",LoginActivity.idParkirPetugas));
			
			// getting JSON string from URL
			JSONObject json2 = jParser.makeHttpRequest(LoginActivity.IP_SERVER+"registerVin.php", "GET", params2);
			
			// Check your log cat for JSON reponse
			Log.d("JSON RESPON REGISTER: ", json2.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json2.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					Log.d("STATUS: ", "OK");
				} else {
					Log.d("Debug Disini","No Products");
					//Toast.makeText(MainActivity.this, 
						//	"ERROR SINI Data connection unavailable. " +
						//	"Please check your network connection or try again later.", 
						//	Toast.LENGTH_LONG).show();
					// no products found
					// Launch Add New product Activity
					//Intent i = new Intent(getApplicationContext(),
							//NewProductActivity.class);
					// Closing all previous activities
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("Masuk ke PostExecute","Ya");
			pDialog.dismiss();
			// dismiss the dialog after getting all products
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					new LoadHistory().execute();
					messageKirim = "Mobil Masuk Baru. VIN : "+txtResultQR.getText().toString();
                    new showPush().execute();
					
				}
			});
			

		}

		

	}
	
	class showPush extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pDialog = new ProgressDialog(MainActivity.this);
			//pDialog.setMessage("Registering, Please Wait...");
			//pDialog.setIndeterminate(false);
			//pDialog.setCancelable(false);
			//pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			StringBuilder sb = new StringBuilder();
			sb.append(LoginActivity.id);
			String idPetugas = sb.toString();
			
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentDateandTime = formatter.format(new Date());
			
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("regId",LoginActivity.noGcm));
			params2.add(new BasicNameValuePair("idParkir",LoginActivity.idParkirPetugas));
			params2.add(new BasicNameValuePair("message",messageKirim));
			
			// getting JSON string from URL
			JSONObject json2 = jParser.makeHttpRequest("http://sistracksolusindo.com/gcm/send_message.php", "GET", params2);
			
			// Check your log cat for JSON reponse
			Log.d("JSON RESPON REGISTER: ", json2.toString());

			

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("Masuk ke PostExecute","Ya");
			//pDialog.dismiss();
			// dismiss the dialog after getting all products
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					//new LoadHistory().execute();
					Log.d("Aku lah","Ya");
					//dialog.dismiss();
					//pDialog.dismiss();
				}
			});
			

		}

		

	}
	
	

}
