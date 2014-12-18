package com.plaxto.hino.mobile.park;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.zbar.Symbol;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.plaxto.hino.mobile.park.JSONParser;
import com.plaxto.hino.mobile.park.LoginActivity;
import com.plaxto.hino.mobile.park.MainStockControl;
import com.google.android.gms.location.LocationListener;
import com.plaxto.common.gps.GpsMain;
import com.plaxto.common.http.HttpSynch;
import com.plaxto.common.qrcode.ZBarConstants;
import com.plaxto.common.qrcode.ZBarScannerActivity;
import com.plaxto.hino.mobile.park.map_activity;
import com.plaxto.hino.mobile.park.R;
import com.plaxto.hino.mobile.park.global.GlobalModel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.annotation.SuppressLint;

public class MainStockControl extends Activity implements android.location.GpsStatus.Listener, LocationListener,
		android.location.LocationListener, SensorEventListener
{
	private long mLastLocationMillis;
	TextView lblMessage;
	private ProgressDialog pDialog;
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	JSONParser jParser = new JSONParser();

	// Alert dialog manager

	public static String idPetugas;
	private static final String LOGIN_CACHE = "loginhinoparkcheck";
	private static final String URL_API_ = LoginActivity.IP_SERVER + "SaveStockControl.php";
	private static final String URL_CHECK = LoginActivity.IP_SERVER + "checkPosition.php";
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	protected static final long GPS_UPDATE_TIME_INTERVAL = 3000; // millis
	protected static final float GPS_UPDATE_DISTANCE_INTERVAL = 0; // meters
	private Location lastLocation;
	public ImageButton btnScanQr;
	@SuppressWarnings("unused")
	private TextView txtDate;
	private TextView mTextViewNoteSockControl;
	private EditText mEditTextNoteStockControl;
	private EditText mEditTextResultQR;
	private EditText mEdittextLatitude;
	private EditText EditTextLongitude;
	private EditText mEditTextAccuracy;
	private ProgressDialog dialog;
	private SimpleDateFormat formatter;
	private LocationManager loccheck;
	private String statusMessage;
	private String qrData;
	private String messageLoading;
	private String currentDateandTime;
	private double mLatitude;
	private double mLongitude;
	private double maccuracy;
	private String maccuracyString;
	private String mLatString;
	private String mLongString;
	private String mVinString;
	private String idEvent, nameEvent;
	private LocationManager locationManager;
	private GpsMain gpsMain;
	private HttpSynch httpSync;
	private GlobalModel globalModel;
	private ArrayList<NameValuePair> params;
	private String provider;
	Spinner sp;
	int isAllow;
	private boolean started = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_control);
		// fromNotif();

		setupViews();
		if (isOnlineGps() == false)
		{
			showSettingAlert();
		}

		Intent intent = getIntent();
		idEvent = intent.getStringExtra("idEvent");
		nameEvent = intent.getStringExtra("nameEvent");

		// Getting name, email from intent
		// Intent i = getIntent();
		StringBuilder sb = new StringBuilder();
		sb.append(LoginActivity.id);
		String idPetugas2 = sb.toString();
		idPetugas = idPetugas2;

		// RECEIVER GAN

	}

	public void onPause()
	{
		super.onPause();
		if (isOnlineGps())
		{
			locationManager.removeUpdates(this);
			locationManager = null;
		}
		// finish();

	}

	public void onBackPressed()
	{
		// onPause();
		gpsMain.stopGps();
		// showLogOut();
		Intent i = new Intent(MainStockControl.this, listStockEvent.class);
		startActivity(i);
		finish();

	}

	private void dialoAbout()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("e-Parking HINO versi 1.0").setCancelable(false)

		.setNegativeButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		})

		;
		builder.show();
		// Toast.makeText(this,
		// "Data connection unavailable. " +
		// "Please check your network connection or try again later.",
		// Toast.LENGTH_LONG).show();
	}

	public void onResume()
	{
		super.onResume();

		 initializeLoc();

		formatter = new SimpleDateFormat("dd MMMM yyyy");
		currentDateandTime = formatter.format(new Date());

		Log.e("DATE", currentDateandTime);
		// txtDate.setText(currentDateandTime);
		getActionBar().setTitle("Stock Event : " + nameEvent);
		getActionBar().setSubtitle(currentDateandTime);
	}

	class checkTransfer extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(MainStockControl.this);
			pDialog.setMessage("Validasi. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args)
		{
			// Building Parameters

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("idpetugas", LoginActivity.id + ""));
			params.add(new BasicNameValuePair("vin", mVinString));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(URL_CHECK, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt("statusId");

				if (success == 1)
				{
					isAllow = json.getInt("allow");

				}
				else
				{
					Toast.makeText(MainStockControl.this, "Invalid Vin Number", Toast.LENGTH_SHORT).show();
					// no products found
					// Launch Add New product Activity
					// Intent i = new Intent(getApplicationContext(),
					// NewProductActivity.class);
					// Closing all previous activities
					// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// startActivity(i);
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
				Toast.makeText(MainStockControl.this, "Unknown Error", Toast.LENGTH_SHORT).show();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url)
		{
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable()
			{
				public void run()
				{

					if (isAllow == 1)
					{
						initializeServer(null, null, 0.0, 0.0);
					}
					else
					{
						dialogConfirm();
					}
				}
			});

		}

	}

	private void setupViews()
	{
		gpsMain = new GpsMain(MainStockControl.this);
		globalModel = new GlobalModel();
		txtDate = (TextView) findViewById(R.id.date);
		mEditTextResultQR = (EditText) findViewById(R.id.vin_id);
		mEditTextResultQR.setKeyListener(null);
		mEdittextLatitude = (EditText) findViewById(R.id.latitude);
		mEdittextLatitude.setEnabled(false);
		EditTextLongitude = (EditText) findViewById(R.id.longitude);
		EditTextLongitude.setEnabled(false);
		btnScanQr = (ImageButton) findViewById(R.id.scan_qr);
		mEditTextAccuracy = (EditText) findViewById(R.id.edit_text_acuracy);
		mEditTextAccuracy.setEnabled(false);
		mTextViewNoteSockControl = (TextView) findViewById(R.id.text_view_note_stock_control);
		mEditTextNoteStockControl = (EditText) findViewById(R.id.edit_teks_note_stcok_control);
		mEditTextNoteStockControl.setFocusable(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_stock, menu);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.action_get_loc:
				initializeLoc();
				break;
			case R.id.action_park_here:
				// initialize();
				// initializeServer(null, null, 0.0, 0.0);
				attemptSend();
				break;
			case R.id.search_menu:
				// initialize();
				// initializeServer(null, null, 0.0, 0.0);
				Intent b = new Intent(MainStockControl.this, FormSearchActivity.class);
				startActivity(b);
				break;
			case R.id.about_version:
				dialoAbout();
				break;
		// case R.id.setting_map:
		// setContentView(R.layout.mapsetting);
		// break;
		}
		return true;
	}

	public void launchQRScanner(View v)
	{
		if (isCameraAvailable())
		{
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			intent.putExtra(ZBarConstants.SCAN_MODES, new int[] { Symbol.QRCODE });
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		}
		else
		{
			Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
		}
	}

	private void dialogConfirm()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Anda ingin transfer lokasi parkir mobil ini ? ").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						initializeServer(null, null, 0.0, 0.0);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});
		builder.show();

	}

	public void klikMapSetting(View View)
	{
		sp = (Spinner) findViewById(R.id.spinner1);
		if (sp.getSelectedItem().toString().equals("Standar"))
		{
			map_activity.viewMapSetting = 1;
			Toast.makeText(this, "Berhasil Setting Map to STANDAR ", Toast.LENGTH_SHORT).show();
			// Intent i = new Intent(FormSetting.this, FormSetting.class);
			// startActivity(i);

			setContentView(R.layout.activity_main);
			;
		}
		else
		{
			map_activity.viewMapSetting = 2;

			Toast.makeText(this, "Berhasil Setting Map to SATELITE", Toast.LENGTH_SHORT).show();
			// Intent i = new Intent(FormSetting.this, FormSetting.class);
			// startActivity(i);

			setContentView(R.layout.activity_main);
		}
	}

	public boolean isCameraAvailable()
	{
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case ZBAR_SCANNER_REQUEST:
			case ZBAR_QR_SCANNER_REQUEST:
				if (resultCode == RESULT_OK)
				{
					qrData = data.getStringExtra(ZBarConstants.SCAN_RESULT);

					mEditTextResultQR.setText(qrData);

					formatter = new SimpleDateFormat("yyyy-MM-dd");
					currentDateandTime = formatter.format(new Date());

					// INPUT STICK =
					// initializeServer(qrData, currentDateandTime, 0.0, 0.0);
				}

				break;
		}
	}

	private boolean isOnlineGps()
	{
		loccheck = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean isGPSEnabled = loccheck.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = loccheck.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			if (isGPSEnabled)
			{
				return true;
			}
		}
		return false;
	}

	private void attemptSend()
	{
		// Reset errors.
		mEdittextLatitude.setError(null);
		EditTextLongitude.setError(null);
		mEditTextResultQR.setError(null);

		// Store values at the time of the login attempt.
		mLatString = mEdittextLatitude.getText().toString();
		mLongString = EditTextLongitude.getText().toString();
		mVinString = mEditTextResultQR.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mLatString))
		{
			mEdittextLatitude.setError(getString(R.string.error_field_required));
			focusView = mEdittextLatitude;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mLongString))
		{
			EditTextLongitude.setError(getString(R.string.error_field_required));
			focusView = EditTextLongitude;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mVinString))
		{
			mEditTextResultQR.setError(getString(R.string.error_field_required));
			focusView = mEditTextResultQR;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.

			initializeServer(null, null, 0.0, 0.0);
		}
	}

	private void initializeServer(String qrCode, String date, double latitude, double longitude)
	{
		if (isOnline() == true)
		{

			createJSONObjectRequest();
			showProgressDialog("Loading...");

			httpSync = new HttpSynch()
			{
				@Override
				protected void onPostExecute(Object result)
				{
					String data = (String) result.toString();
					JSONObject json = globalModel.ConvertStringToJSON(data);
					Log.e("DATA JSON", data);
					Log.e("JSON RESPONSE", json.toString());

					parseJSON(json);

				}
			};

			httpSync.execute(URL_API_, params);

		}
		else
		{
			showSettingAlert();
		}
	}

	private void initializeLoc()
	{
		messageLoading = "Getting Location...";
		showProgressDialog(messageLoading);
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				try
				{
					mLatitude = 0;
					mLongitude = 0;
					maccuracy = 0;

					if (isOnlineGps())
					{
						// gpsMain.startGps();
						long timestamp = System.currentTimeMillis();
						started = true;
						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						Criteria criteria = new Criteria();
						criteria.setPowerRequirement(Criteria.POWER_HIGH);
						criteria.setAccuracy(Criteria.ACCURACY_FINE);

						provider = locationManager.getBestProvider(criteria, true);
						locationManager.requestLocationUpdates(provider, 0, 0, MainStockControl.this);
						Location location;
						// location=null;
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

						onLocationChanged(location);
						lastLocation = location;
						// Location location = gpsMain.getLocation();
						mLatitude = location.getLatitude();
						mLongitude = location.getLongitude();
						maccuracy = location.getAccuracy();
						mEdittextLatitude.setText(mLatitude + "");
						EditTextLongitude.setText(mLongitude + "");
						mEditTextAccuracy.setText(maccuracy + "");
						maccuracyString = maccuracy + "";
						mLastLocationMillis = SystemClock.elapsedRealtime();

						// gpsMain.stopGps();

						dialog.dismiss();
					}
					else
					{
						dialog.dismiss();
						showSettingAlert();
					}
				}
				catch (NullPointerException e)
				{
					e.printStackTrace();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "Gagal mendapatkan lokasi.", Toast.LENGTH_SHORT).show();
				}
			}
		}, 3500);
	}

	@SuppressWarnings("unused")
	private void initialize()
	{
		messageLoading = "Sending data...";
		showProgressDialog(messageLoading);
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				Toast.makeText(getApplicationContext(), "Data lokasi berhasil disimpan ke server.", Toast.LENGTH_SHORT)
						.show();
				mEdittextLatitude.setText("");
				EditTextLongitude.setText("");
				dialog.dismiss();
			}
		}, 3500);
	}

	public void parseJSON(JSONObject jsonResponse)
	{
		try
		{
			String res = jsonResponse.getString("success");

			if (Integer.parseInt(res) == 1)
			{

				mEdittextLatitude.setText("");
				EditTextLongitude.setText("");
				mEditTextResultQR.setText("");
				mEditTextNoteStockControl.setText("");
				Toast.makeText(getApplicationContext(), "Simpan Berhasil", Toast.LENGTH_SHORT).show();
				dialog.dismiss();

			}
			else
			{
				Toast.makeText(getApplicationContext(), "Vin Number tidak terdaftar di area parkir ini ",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		}
		catch (NumberFormatException e)
		{
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), "Koneksi ke server bermasalah ", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), "JSON to connect server. Please try again later.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void createJSONObjectRequest()
	{
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		currentDateandTime = formatter.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append(LoginActivity.id);
		String idPetugas = sb.toString();
		String mLat = mEdittextLatitude.getText().toString();
		String mLong = EditTextLongitude.getText().toString();

		String vinId = mEditTextResultQR.getText().toString();
		String idParkir = LoginActivity.idParkirPetugas;

		String noteStockControl = mEditTextNoteStockControl.getText().toString();

		params = new ArrayList<NameValuePair>();
		JSONObject jsonData = new JSONObject();

		try
		{
			jsonData.put("vin", vinId);
			jsonData.put("lat", mLat);
			jsonData.put("long", mLong);

			jsonData.put("idparkir", idParkir);
			jsonData.put("accuracy", maccuracy);
			params.add(new BasicNameValuePair("vin", vinId));
			params.add(new BasicNameValuePair("lat", mLat));
			params.add(new BasicNameValuePair("long", mLong));

			params.add(new BasicNameValuePair("idparkir", idParkir));
			params.add(new BasicNameValuePair("accuracy", maccuracyString));
			params.add(new BasicNameValuePair("idPetugas", idPetugas));
			params.add(new BasicNameValuePair("event", idEvent));
			params.add(new BasicNameValuePair("note", noteStockControl));
			Log.e("JSON : ", params.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
		return false;
	}

	private void showSettingAlert()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You need to enable mobile data and GPS in order to use this application:")
				.setCancelable(false).setPositiveButton("Turn on Data and GPS", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
						startActivity(newintent);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});
		builder.show();
		Toast.makeText(this,
				"Data connection unavailable. " + "Please check your network connection or try again later.",
				Toast.LENGTH_LONG).show();
	}

	private void showProgressDialog(String messageLoading)
	{
		dialog = new ProgressDialog(this);
		this.dialog.setMessage(messageLoading);
		this.dialog.setIndeterminate(false);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location)
	{
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		mLastLocationMillis = SystemClock.elapsedRealtime();
		maccuracy = location.getAccuracy();
		mEdittextLatitude.setText(mLatitude + "");
		EditTextLongitude.setText(mLongitude + "");
		mEditTextAccuracy.setText(maccuracy + "");
		maccuracyString = maccuracy + "";
		lastLocation = location;
		// Toast.makeText(getApplicationContext(),
		// maccuracyString,
		// Toast.LENGTH_SHORT).show();
		Log.d("Loc Change", "Masuk");

	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{

		if (started)
		{

			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];

			long timestamp = System.currentTimeMillis();

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			criteria.setAccuracy(Criteria.ACCURACY_FINE);

			provider = locationManager.getBestProvider(criteria, true);
			Location location2 = locationManager.getLastKnownLocation(provider);

			double latitude = 0;
			double longitude = 0;
			if (location2 != null)
			{
				mLatitude = location2.getLatitude();
				mLongitude = location2.getLongitude();
				maccuracy = location2.getAccuracy();
				mEdittextLatitude.setText(mLatitude + "");
				EditTextLongitude.setText(mLongitude + "");
				mEditTextAccuracy.setText(maccuracy + "");
				maccuracyString = maccuracy + "";
				Log.d("Sensor", "Masuk");
			}

		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsStatusChanged(int event)
	{
		boolean isGPSFix = false;
		switch (event)
		{
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (lastLocation != null)
					if ((SystemClock.elapsedRealtime() - mLastLocationMillis) < GPS_UPDATE_TIME_INTERVAL * 2)
					{
						isGPSFix = true;
					}

				if (isGPSFix)
				{ // A fix has been acquired.
					Toast.makeText(getApplicationContext(), "Gps Sudah Fix", Toast.LENGTH_SHORT).show();
					// imgGpsState.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gps_on_green));
				}
				else
				{ // The fix has been lost.
					Toast.makeText(getApplicationContext(), "Gps Belum Fix", Toast.LENGTH_SHORT).show();
					// imgGpsState.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gps_on_orange));
				}

				break;

			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Toast.makeText(getApplicationContext(), "Gps Event First Fix", Toast.LENGTH_SHORT).show();
				// imgGpsState.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gps_on_green));
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				Toast.makeText(getApplicationContext(), "Gps Event Started", Toast.LENGTH_SHORT).show();
				// imgGpsState.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gps_on_orange));
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Toast.makeText(getApplicationContext(), "Gps Event Stopped", Toast.LENGTH_SHORT).show();
				// imgGpsState.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gps_on_red));
				break;
		}

	}

	private void showLogOut()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to log out or exit application ?").setCancelable(false)
				.setPositiveButton("Yes Logout", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// Intent newintent = new
						// Intent(android.provider.Settings.ACTION_SETTINGS);
						// startActivity(newintent);
						SharedPreferences settings = getSharedPreferences(LOGIN_CACHE, Context.MODE_PRIVATE);
						settings.edit().clear().commit();
						Intent i = new Intent(MainStockControl.this, LoginActivity.class);
						startActivity(i);

						finish();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				}).setNeutralButton("Just Exit", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						Intent i = new Intent(MainStockControl.this, LoginActivity.class);
						startActivity(i);
						finish();
					}
				})

		;
		builder.show();
		// Toast.makeText(this,
		// "Data connection unavailable. " +
		// "Please check your network connection or try again later.",
		// Toast.LENGTH_LONG).show();
	}

	private void notifDialog(String pesan)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(pesan).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Intent newintent = new
				// Intent(android.provider.Settings.ACTION_SETTINGS);
				// startActivity(newintent);
				SharedPreferences settings = getSharedPreferences("push", Context.MODE_PRIVATE);
				settings.edit().clear().commit();
				dialog.cancel();
				// Intent i = new Intent(MainStockControl.this,
				// LoginActivity.class);
				// startActivity(i);

				// finish();
			}
		})

		;
		builder.show();
		// Toast.makeText(this,
		// "Data connection unavailable. " +
		// "Please check your network connection or try again later.",
		// Toast.LENGTH_LONG).show();
	}

}
