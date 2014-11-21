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


import android.content.SharedPreferences;  
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView.OnEditorActionListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.plaxto.common.gps.GpsMain;
import com.plaxto.common.http.HttpSynch;
import com.plaxto.common.qrcode.ZBarConstants;
import com.plaxto.common.qrcode.ZBarScannerActivity;
import com.plaxto.hino.mobile.park.global.GlobalModel;

public class LoginActivity extends Activity{
	static String IP_SERVER = "http://unit-locator.com/parkir/api/";
	private static final String URL_API_ = IP_SERVER+"petugasAuth.php";
	private static final int ZBAR_SCANNER_REQUEST 	 = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private static final String LOGIN_CACHE = "loginhinopark"; // 
	  Button load_img;
	  ImageView img;
	  Bitmap bitmap;
	  ProgressDialog pDialog;
	public ImageButton btnScanQr;
	@SuppressWarnings("unused")
	private TextView txtDate;
	private EditText txtResultQR;
	private EditText txtEmail;
	private EditText txtPassword;
	private EditText txtLatitude;
	private EditText txtLongitude;
	static String namaParkirArea;
	private ProgressDialog dialog;
	private SimpleDateFormat formatter;
	
	private TableLayout tableview;
	private TableLayout tableimageview;
	private String statusMessage;
	private String qrData;
	private String messageLoading;
	private String currentDateandTime;
	private double mLatitude;
	private double mLongitude;
	private String mLatString;
	private String mLongString;
	private String mVinString;
	private String password;
	private String usernameLama;
	private String passwordLama;
	static int id;
	static String email;
	static String idParkirPetugas;
	static String noGcm;
	private GpsMain gpsMain;
	private HttpSynch httpSync;
	private GlobalModel globalModel;
	private ArrayList<NameValuePair> params;
	TableRow tr1, tr2, tr3, tr4, tr5, tr6, tr7;
	TextView t1, t2, t3, t4, t5, t6, t7, dummy, tvinNumber, tAreaPakir, tAreaParkir , tTimeIn, tStatus, tBrand, tType;
	EditText et1, et2;
	CheckBox rememberPassword;
	Button btnShowMap;
	private NotificationManager mNotificationManager;
	   private int notificationID = 100;
	   private int numMessages = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		setupViews();
		// displayNotification();
		
		// existing height is ok as is, no need to edit it
		
		EditText inputEmail = (EditText) findViewById(R.id.txtEmail);
		inputEmail.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		            //sendMessage();
		        	txtPassword.requestFocus();
		            handled = true;
		        }
		        return handled;
		    }
		});
		 
		txtPassword.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            //sendMessage();
		        	txtEmail.setError(null);
					txtPassword.setError(null);
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
				   imm.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
					// Store values at the time of the login attempt.
					

					boolean cancel = false;
					View focusView = null;

					// Check for a valid password.
					//mVinString = txtResultQR.getText().toString();
					
					// Check for a valid email address.
					if (txtEmail.getText().toString().equals("")) {
						txtEmail.setError(getString(R.string.error_field_required));
						focusView = txtEmail;
						cancel = true;
					} 
					
					if (txtPassword.getText().toString().equals("")) {
						txtPassword.setError(getString(R.string.error_field_required));
						focusView = txtPassword;
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
		            handled = true;
		        }
		        return handled;
		    }
		});
		 
		 Button btnLogin=(Button) findViewById(R.id.btnLogin);
		 btnLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtEmail.setError(null);
					txtPassword.setError(null);
					
					// Store values at the time of the login attempt.
					

					boolean cancel = false;
					View focusView = null;

					// Check for a valid password.
					//mVinString = txtResultQR.getText().toString();
					
					// Check for a valid email address.
					if (txtEmail.getText().toString().equals("")) {
						txtEmail.setError(getString(R.string.error_field_required));
						focusView = txtEmail;
						cancel = true;
					} 
					
					if (txtPassword.getText().toString().equals("")) {
						txtPassword.setError(getString(R.string.error_field_required));
						focusView = txtPassword;
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
	
	
	
	
	
	
	
	
	
	private void setupViews() {
		gpsMain 	= new  GpsMain(LoginActivity.this);
		globalModel	= new GlobalModel();
		//txtDate		= (TextView)findViewById(R.id.date);
		//txtResultQR	= (EditText)findViewById(R.id.vin_id);
		txtEmail = (EditText)findViewById(R.id.txtEmail);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		rememberPassword = (CheckBox)findViewById(R.id.remember); 
		//btnScanQr	= (ImageButton)findViewById(R.id.scan_qr);
		SharedPreferences loginPreferences = getSharedPreferences(LOGIN_CACHE,
	            Context.MODE_PRIVATE);
		
		usernameLama = loginPreferences.getString("email", "");
		passwordLama = loginPreferences.getString("pwd", "");
		
		if(usernameLama.toString().equals("") && passwordLama.toString().equals("")){
			
		}else{
			txtEmail.setText(loginPreferences.getString("email", ""));
			txtPassword.setText(loginPreferences.getString("pwd", ""));
			rememberPassword.setChecked(true);
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//	    int itemId = item.getItemId();
//	    switch (itemId) {
//	    case R.id.action_settings:
//	    	Toast.makeText(this, 
 //           		"Pilihan 1", 
   //         		Toast.LENGTH_SHORT).show();
	//		break;
	  //  case R.id.action_search:
	    //	Toast.makeText(this, 
          //  		"Pilihan 2", 
            //		Toast.LENGTH_SHORT).show();
	    	//initialize();
	    	//initializeServer(null, null, 0.0, 0.0);
	    	
	    //	break;
	   // }
	   // return true;
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
	    			
	    			gpsMain.stopGps();
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
			Integer res    = jsonResponse.getInt("code");
			
		   
			if(res==1){
				Toast.makeText(getApplicationContext(), 
	        			"Selamat Datang di Aplikasi e-Parking Hino", 
	        			Toast.LENGTH_LONG).show();
				email = txtEmail.getText().toString();
				password = txtPassword.getText().toString();
				id = (jsonResponse.getInt("id"));
				idParkirPetugas = (jsonResponse.getString("idParkir"));
				namaParkirArea = (jsonResponse.getString("namaParkir"));
				noGcm = (jsonResponse.getString("idParkir"));
				txtEmail.setText("");
				txtPassword.setText("");
				Log.i("idPetugas", "Ini adalah "+id);
				MainActivity.checkType = "I";
				map_activity.viewMapSetting = 1;
				
				showPopupRemember();
				
		    	
			}else{
				Toast.makeText(getApplicationContext(), 
	        			"Salah Password.", 
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
	            pDialog = new ProgressDialog(LoginActivity.this);
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
	           img = new ImageView(LoginActivity.this);
	           img.setImageBitmap(image);
	            tableimageview = new TableLayout(LoginActivity.this);
	            tr1 = new TableRow(LoginActivity.this);
	            tr1.addView(img);
	            tableimageview.addView(tr1);
	            setContentView(tableimageview);
	           pDialog.dismiss();
	         }else{
	           pDialog.dismiss();
	           Toast.makeText(LoginActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
	         }
	       }
	   }
	
	
	public void createJSONObjectRequest(){
		
		String emailnya	= txtEmail.getText().toString();
		String passwordnya = txtPassword.getText().toString();
		params = new ArrayList<NameValuePair>();
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("email", emailnya);
			jsonData.put("password", passwordnya);

			
			
			params.add(new BasicNameValuePair("email",emailnya));
			params.add(new BasicNameValuePair("password",passwordnya));
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
	
	private void showPopupRemember(){
		if(rememberPassword.isChecked()){
					dialog.dismiss();
                	   SharedPreferences settings = getSharedPreferences(LOGIN_CACHE, 0);
                	   
       				    settings.edit().putString("email", email).putString("pwd", password).putString("idPetugas", id+"").putString("idParkir", idParkirPetugas).commit();
       				 Intent i = new Intent(LoginActivity.this, AndroidDashboardDesignActivity.class);
     				 startActivity(i);
     				
     				 finish();
		}else{
						dialog.dismiss();
							SharedPreferences settings = getSharedPreferences(
   							LOGIN_CACHE, Context.MODE_PRIVATE);
   							settings.edit().clear().commit();
   							Intent i = new Intent(LoginActivity.this, AndroidDashboardDesignActivity.class);
   							startActivity(i);
   							
   							finish();
   		
		}         
		
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
	
	private void showProgressDialog(String messageLoading){
		dialog = new ProgressDialog(this);
		this.dialog.setMessage(messageLoading);
		this.dialog.setIndeterminate(false);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	
	protected void displayNotification() {
	      Log.i("Start", "notification");

	      /* Invoking the default notification service */
	      NotificationCompat.Builder  mBuilder = 
	      new NotificationCompat.Builder(this);	

	      mBuilder.setContentTitle("Detecting Location...");
	      mBuilder.setContentText("");
	      mBuilder.setTicker("Detecting Your Location");
	      mBuilder.setSmallIcon(R.drawable.gps_icon);
	      mBuilder.setOngoing(true);
	      /* Increase notification number every time a new notification arrives */
	      //mBuilder.setNumber(++numMessages);
	      
	      /* Creates an explicit intent for an Activity in your app */
	      Intent resultIntent = new Intent(this, MainActivity.class);

	      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	      stackBuilder.addParentStack(MainActivity.class);

	      /* Adds the Intent that starts the Activity to the top of the stack */
	      stackBuilder.addNextIntent(resultIntent);
	      PendingIntent resultPendingIntent =
	         stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_UPDATE_CURRENT
	         );

	      mBuilder.setContentIntent(resultPendingIntent);
	      //mBuilder.setAutoCancel(true); // On Click Close
	      
	      mNotificationManager =
	      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	      /* notificationID allows you to update the notification later on. */
	      mNotificationManager.notify(notificationID, mBuilder.build());
	   }
	
	
}
