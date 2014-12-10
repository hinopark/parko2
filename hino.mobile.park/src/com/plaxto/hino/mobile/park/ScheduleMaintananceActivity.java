package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.plaxto.common.gps.GpsMain;
import com.plaxto.common.http.HttpSynch;
import com.plaxto.common.qrcode.ZBarConstants;
import com.plaxto.common.qrcode.ZBarScannerActivity;
import com.plaxto.hino.mobile.park.global.GlobalModel;
import com.plaxto.hino.mobile.park.listSearch.LoadAllProducts;


public class ScheduleMaintananceActivity extends Activity
{
	private Spinner mSpinerScheduleMiantanance;
	private Spinner mSPinerMobilMaintanance;
	private Button mButtonSaveScheduleMaintanance;
	String imc_met;
	Boolean suksesSimpan;
	// url to get all products list
	private ProgressDialog pDialog;

	// Creating JSON Parser object
		JSONParser jParser = new JSONParser();

		ArrayList<HashMap<String, String>> productsList;
		private static String url_save_maintenance = LoginActivity.IP_SERVER + "saveMaintenanceSukses.php";
		private static String url_all_products = LoginActivity.IP_SERVER + "listParkir.php";
		private static String url_all_listPDI = LoginActivity.IP_SERVER + "listWaitingPDI.php";
		private static String url_listPDI_search = LoginActivity.IP_SERVER + "listWaitingPDISearch.php";
		private static final String url_product_detials = LoginActivity.IP_SERVER + "searchDetail.php";
		
		private static final int ZBAR_SCANNER_REQUEST 	 = 0;
		private static final int ZBAR_QR_SCANNER_REQUEST = 1;
		private String vinNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_maintanance);
		Intent i = getIntent();
		suksesSimpan = false;
		vinNumber = checkMaintenance.vin_number;
		mSpinerScheduleMiantanance = (Spinner)findViewById(R.id.spiner_schedule_mobil_mantainance);
		mSpinerScheduleMiantanance.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				imc_met=mSpinerScheduleMiantanance.getSelectedItem().toString();
				 /*
				 Toast.makeText(ScheduleMaintananceActivity.this, 
		            		imc_met, 
		            		Toast.LENGTH_SHORT).show();
				 Toast.makeText(ScheduleMaintananceActivity.this, 
		            		"VIN NUMBR" + vinNumber, 
		            		Toast.LENGTH_SHORT).show();
		         */   		
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//mSpinerScheduleMiantanance = (Spinner)findViewById(R.id.spiner_schedule_mobil_mantainance);
		mButtonSaveScheduleMaintanance = (Button)findViewById(R.id.button_save_schedule_maintanance);
		
		mButtonSaveScheduleMaintanance.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// 
				new SaveMaintenance().execute();
				
			}
		});

	}


	class SaveMaintenance extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(ScheduleMaintananceActivity.this);
			pDialog.setMessage("Save the results. Please wait...");
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

			params.add(new BasicNameValuePair("vin", vinNumber));
			params.add(new BasicNameValuePair("next", imc_met));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_save_maintenance, "GET", params);

			// Check your log cat for JSON reponse
			//Log.d("vinNumber",vinNumber);
			Log.d("All Products: ", json.toString());

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt("success");

				if (success == 1)
				{
					suksesSimpan = true;
					// products found
					// Getting Array of Products
										
				}
				else
				{
					suksesSimpan = false;
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
					if(suksesSimpan==true){
						dialogSuccess();
					}else{
						dialogGagal();
					}
				
				}
			});

		}

	}
	public void addListenerOnSpinnerItemSelection()
	{
		mSpinerScheduleMiantanance = (Spinner)findViewById(R.id.spiner_schedule_mobil_mantainance);
		mSpinerScheduleMiantanance.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				imc_met=mSpinerScheduleMiantanance.getSelectedItem().toString();
				 Toast.makeText(ScheduleMaintananceActivity.this, 
		            		imc_met, 
		            		Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private void dialogSuccess()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScheduleMaintananceActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle("Success :: ");

		// Setting Dialog Message
		alertDialog.setMessage("Sukses menyimpan hasil Maintenance");

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.save);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// User pressed YES button. Write Logic Here
				Intent i = new Intent(ScheduleMaintananceActivity.this, ListWaitingMaintenance.class);
				startActivity(i);
				finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	
	private void dialogGagal()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScheduleMaintananceActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle("Gagal :: ");

		// Setting Dialog Message
		alertDialog.setMessage("Gagal menyimpan hasil Maintenance");

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.save);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// User pressed YES button. Write Logic Here
				
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	
	public void sendScheduleDataMaintanance()
	{
		mSpinerScheduleMiantanance = (Spinner)findViewById(R.id.spiner_schedule_mobil_mantainance);
		mButtonSaveScheduleMaintanance = (Button)findViewById(R.id.button_save_schedule_maintanance);
		
		mButtonSaveScheduleMaintanance.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// 
				
			}
		});
	}

}
