package com.plaxto.hino.mobile.park;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.sourceforge.zbar.Symbol;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera.Area;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.plaxto.common.gps.GpsMain;
import com.plaxto.common.http.HttpSynch;
import com.plaxto.common.qrcode.ZBarConstants;
import com.plaxto.common.qrcode.ZBarScannerActivity;
import com.plaxto.hino.mobile.park.global.GlobalModel;

public class FormSearchActivity extends Activity{
	
	private static final String URL_API_ = "http://unit-locator.com/parkir/api/cariMobil.php";
	private static final int ZBAR_SCANNER_REQUEST 	 = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	
	  Button load_img;
	  ImageView img;
	  Bitmap bitmap;
	  ProgressDialog pDialog;
	public ImageButton btnScanQr;
	@SuppressWarnings("unused")
	private TextView txtDate;
	private EditText txtResultQR;
	private EditText txtLatitude;
	private EditText txtLongitude;
	
	private ProgressDialog dialog;
	private SimpleDateFormat formatter;
	
	private TableLayout tableview;
	private TableLayout tableimageview;
	private String statusMessage;
	private String qrData;
	private String messageLoading;
	private String currentDateandTime;
	static double mLatitude;
	static double mLongitude;
	static double maccuracy;

	static String vinnya;
	private String mLatString;
	private String mLongString;
	private String mVinString;

	LocationManager lm;
	private GpsMain gpsMain;
	private HttpSynch httpSync;
	private GlobalModel globalModel;
	private ArrayList<NameValuePair> params;
	TableRow tr1, tr2, tr3, tr4, tr5, tr6, tr7;
	TextView t1, t2, t3, t4, t5, t6, t7, dummy, tvinNumber, tAreaPakir, tAreaParkir , tTimeIn, tStatus, tBrand, tType;
	EditText et1, et2;
	Button btnShowMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_form_search);
		setupViews();
		
		
		 
		 Button btnSearch=(Button) findViewById(R.id.btnSearch);
		 btnSearch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtResultQR.setError(null);

					// Store values at the time of the login attempt.
					

					boolean cancel = false;
					View focusView = null;

					// Check for a valid password.
					mVinString = txtResultQR.getText().toString();
					
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
						initializeServer(null, null, 0.0, 0.0);
					}
				}
			});
		 
		
	}
	
	
	
	public void onPause(){
		super.onPause();
		gpsMain.stopGps();
	}
	
	public void onResume(){
		super.onResume();
		formatter = new SimpleDateFormat("dd MMMM yyyy");
		currentDateandTime = formatter.format(new Date());
		
		Log.e("DATE", currentDateandTime);
		//txtDate.setText(currentDateandTime);
		
		getActionBar().setSubtitle(currentDateandTime);
	}

	
	private void setupViews() {
		gpsMain 	= new  GpsMain(FormSearchActivity.this);
		globalModel	= new GlobalModel();
		txtDate		= (TextView)findViewById(R.id.date);
		txtResultQR	= (EditText)findViewById(R.id.vin_id);
		
		btnScanQr	= (ImageButton)findViewById(R.id.scan_qr);
	}

	//@Override
	
	
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	//    int itemId = item.getItemId();
	  //  switch (itemId) {
	   // case R.id.action_settings:
	    //	Toast.makeText(this, 
          //  		"Pilihan 1", 
            //		Toast.LENGTH_SHORT).show();
		//	break;
	    //case R.id.action_search:
	    //	Toast.makeText(this, 
          //  		"Pilihan 2", 
            //		Toast.LENGTH_SHORT).show();
	    	//initialize();
	    	//initializeServer(null, null, 0.0, 0.0);
	    	
	    //	break;
	   // }
	//   return true;
//	}
	
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

            		formatter = new SimpleDateFormat("yyyy-MM-dd");
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
		

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		mVinString = txtResultQR.getText().toString();
		
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
		//messageLoading = "Getting Your Current Location. Please Wait...";
		//showProgressDialog(messageLoading);
		new Handler().postDelayed(new Runnable() {
	        public void run() {  
	        	try {
	        		String kamu = "Aku";
	        		//gpsMain.settingan = "kaka";
	        		
	        		//GpsMain aku = new GpsMain(aku);
	        		//gpsMain.stopGps2();
	        		
	    			gpsMain.startGps();
	    			if(isOnlineGps()){
	    			Location location = gpsMain.getLocation();
	    			
	    			mLatitude 	= location.getLatitude();
	    			mLongitude 	= location.getLongitude();
	    			//maccuracy = location.getAccuracy();
	    			
	    			//txtLatitude.setText(mLatitude + "");
	    			//txtLongitude.setText(mLongitude + "");
	    			
	    			//gpsMain.stopGps();
	    			
	    		//	dialog.dismiss();
	    			}else{
	    		//		dialog.dismiss();
	    				showSettingAlert();
	    			}
	    			//Intent i = new Intent(FormSearchActivity.this, map_activity.class);
	            	
	            	//startActivity(i);
	            	
	            	//finish();
	    		} catch (NullPointerException e) {
	    			e.printStackTrace();
	    			dialog.dismiss();
	    			Toast.makeText(getApplicationContext(), 
	            			"Gagal mendapatkan lokasi.", 
	            			Toast.LENGTH_SHORT).show();
	    			showSettingAlert();
	    		}
	        }
	    }, 3500); 
	}
	
	private boolean isOnlineGps() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if(isGPSEnabled){
	        return true;
	    	}
	    }
	    return false;
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
			String res    = jsonResponse.getString("statusQuery");
			
		   
			if(res.toString().equals("Yes")){
				// Starting new intent
				initializeLoc();
				Intent in = new Intent(getApplicationContext(),
						listSearch.class);
				// sending pid to next activity
				vinnya = txtResultQR.getText().toString();
				in.putExtra("VIN_NUMBER", vinnya);
				dialog.dismiss();
				dialog.dismiss();
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
				
				//finish();
			}else{
				Toast.makeText(getApplicationContext(), 
	        			"Tidak Dapat Menemukan Mobil dengan Vin Number tersebut.", 
	        			Toast.LENGTH_LONG).show();
		    	dialog.dismiss();
			}
	        	
		    	
		    
		} catch (NumberFormatException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Number. Please try again later.", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (JSONException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Koneksi internet anda lambat, silahkan coba beberapa saat lagi.", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
	    @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(FormSearchActivity.this);
	            pDialog.setMessage("Loading Image ....");
	            pDialog.show();
	    }
	       protected Bitmap doInBackground(String... args) {
	         try {
	               bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
	        } catch (Exception e) {
	              e.printStackTrace();
	        }
	      return bitmap;
	       }
	       protected void onPostExecute(Bitmap image) {
	         if(image != null){
	           img = new ImageView(FormSearchActivity.this);
	           img.setImageBitmap(image);
	            tableimageview = new TableLayout(FormSearchActivity.this);
	            tr1 = new TableRow(FormSearchActivity.this);
	            tr1.addView(img);
	            tableimageview.addView(tr1);
	            setContentView(tableimageview);
	           pDialog.dismiss();
	         }else{
	           pDialog.dismiss();
	           Toast.makeText(FormSearchActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
	         }
	       }
	   }
	
	
	public void createJSONObjectRequest(){
		
		String vinId	= txtResultQR.getText().toString();
		params = new ArrayList<NameValuePair>();
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("vin", vinId);
			jsonData.put("idParkir",LoginActivity.idParkirPetugas);
			
			params.add(new BasicNameValuePair("vin",vinId));
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
	
	private void showSettingAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda harus mengaktifkan fitur GPS terlebih dahulu. Ingin mengaktifkan ? ")
               .setCancelable(false)
               .setPositiveButton("Ya, Aktifkan GPS", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        startActivity(newintent);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       finish();
                   }
               });
        builder.show();
		Toast.makeText(this, 
				"Data connection or GPS unavailable. " +
				"Please check your network connection and GPS or try again later.", 
				Toast.LENGTH_LONG).show();
	}

	private void showProgressDialog(String messageLoading){
		dialog = new ProgressDialog(this);
		this.dialog.setMessage(messageLoading);
		this.dialog.setIndeterminate(false);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}
	
	public void onBackPressed() {
		gpsMain.stopGps();
		//Intent i = new Intent(FormSearchActivity.this, MainActivity.class);
		//startActivity(i);
		finish();
	}
	

}
