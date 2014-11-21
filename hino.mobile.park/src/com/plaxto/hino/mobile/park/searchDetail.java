package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
public class searchDetail extends Activity {

	EditText txtVin;
	EditText txtBrand;
	EditText txtType;
	EditText txtAreaParkir;
	EditText txtJamMasuk;
	EditText txtPrice;
	EditText txtDesc;
	EditText txtCreatedAt;
	static EditText txtLong,txtLat;
	Button btnShowMap;
	Button btnSave;
	Button btnDelete;
    private String vin_number;
	String pid;
	String ambilVin;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_product_detials = LoginActivity.IP_SERVER+"searchDetail.php";

	// url to update product
	private static final String url_update_product = "http://192.168.1.132/android_connect/update_product.php";
	
	// url to delete product
	private static final String url_delete_product = "http://192.168.1.132/android_connect/delete_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "products";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "price";
	private static final String TAG_DESCRIPTION = "description";
	//Slatitude = product.getDouble("lat");
	//Slongitude = product.getDouble("long");
	//Clatitude = product.getDouble("clat");
	//Clongitude = product.getDouble("clong");
	//maccuracy = product.getInt("accuracy");
	static double Slatitude;
	static double Slongitude;
	static double Clatitude;
	static double Clongitude;
	static int maccuracy;
	static double lat1;
	static double lat2;
	static double lat3;
	static double lat4;
	static double long1;
	static double long2;
	static double long3;
	static double long4;
	static String vinNya;
	  private NotificationManager mNotificationManager;
	   private int notificationID = 100;
	   private int numMessages = 0;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_product);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		
		//displayNotification();
		
		// save button
		btnShowMap = (Button) findViewById(R.id.btnShowMap2);
		//btnDelete = (Button) findViewById(R.id.btnDelete);
		
		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		ambilVin = i.getStringExtra("vinnya");
		txtLat = (EditText) findViewById(R.id.txtLat);
		txtLong = (EditText) findViewById(R.id.txtLong);
		
		
		// Getting complete product details in background thread
		
		new GetProductDetails().execute();

		// save button click event
		btnShowMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				 //new SaveProductDetails().execute();
				Intent i = new Intent(searchDetail.this, map_activity.class);
            	
            	startActivity(i);
            	
            	//finish();
				//Toast.makeText(getApplicationContext(), 
	        			//Slatitude+"", 
	        			//Toast.LENGTH_LONG).show();
			}
		});

		// Delete button click event
		
	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	
	protected void displayNotification() {
	      Log.i("Start", "notification");

	      /* Invoking the default notification service */
	      NotificationCompat.Builder  mBuilder = 
	      new NotificationCompat.Builder(this);	

	      mBuilder.setContentTitle("New Message");
	      mBuilder.setContentText("You've received new message.");
	      mBuilder.setTicker("New Message Alert!");
	      mBuilder.setSmallIcon(R.drawable.gps_icon);

	      /* Increase notification number every time a new notification arrives */
	      mBuilder.setNumber(++numMessages);
	      
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
	      mBuilder.setAutoCancel(true); // On Click Close
	      mNotificationManager =
	      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	      /* notificationID allows you to update the notification later on. */
	      mNotificationManager.notify(notificationID, mBuilder.build());
	   }
	
	class GetProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(searchDetail.this);
			pDialog.setMessage("Loading product details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("vin", ambilVin));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						Log.e("JSON kirim aa : ", params.toString());
						
						JSONObject json = jsonParser.makeHttpRequest(
								url_product_detials, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);
							
							// product with this pid found
							// Edit Text
							txtVin = (EditText) findViewById(R.id.showVin);
							txtBrand = (EditText) findViewById(R.id.showBrand);
							txtType = (EditText) findViewById(R.id.showType);
							txtAreaParkir = (EditText) findViewById(R.id.areaParkir);
							txtJamMasuk = (EditText) findViewById(R.id.jamMasuk);
							
							
							txtVin.setText(product.getString("vinNumber"));
							vinNya = product.getString("vinNumber");
							txtBrand.setText(product.getString("brand"));
							txtType.setText(product.getString("type"));
							txtAreaParkir.setText(product.getString("namaParkir"));
							txtJamMasuk.setText(product.getString("jamMasuk"));
							txtLat.setText(product.getString("lat"));
							txtLong.setText(product.getString("long"));
							
							Slatitude = product.getDouble("lat");
							Slongitude = product.getDouble("long");
							Clatitude = product.getDouble("clat");
							Clongitude = product.getDouble("clong");
							maccuracy = product.getInt("accuracy");
							lat1 = product.getDouble("lat1");
					    	lat2 = product.getDouble("lat2");
					    	lat3 = product.getDouble("lat3");
					    	lat4 = product.getDouble("lat4");
					    	long1 = product.getDouble("long1");
					    	long2 = product.getDouble("long2");
					    	long3 = product.getDouble("long3");
					    	long4 = product.getDouble("long4");
							
							
							///// display product data in EditText
							//txtName.setText(product.getString(TAG_NAME));
							//txtPrice.setText(product.getString(TAG_PRICE));
							//txtDesc.setText(product.getString(TAG_DESCRIPTION));

						}else{
							// product with pid not found
							Log.e("SQL NOT FOUND", json.toString());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(searchDetail.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}

	}
}
