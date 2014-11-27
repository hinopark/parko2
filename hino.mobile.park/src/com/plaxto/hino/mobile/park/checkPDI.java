package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class checkPDI extends ListActivity
{

	// Progress Dialog
	private ProgressDialog pDialog;
	ArrayList<ItemPdi> newPdi = new ArrayList<ItemPdi>();
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	static int jumlahPDI;
	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = LoginActivity.IP_SERVER + "listParkir.php";
	private static String url_all_listPDI = LoginActivity.IP_SERVER + "listOpsiPDI.php";
	private static String url_save_pdi = LoginActivity.IP_SERVER + "savePDI.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "vinNumber";
	private static final String TAG_NAME = "namaPdi";
	private String vin_number;
	private String vinkirim;
	private Button btnShowMap;
	// products JSONArray
	ListAdapter boxAdapter;
	JSONArray products = null;
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
	boolean lanjutkan;
	private String vinPilih;
	private static HashMap<String, String[]> optionPDIList;
	 private int jumlahOption, jumlahNotGood;
	 private String idMobil, blacklist, whitelist, keterangan, lolos, kumpulanremark;
	private EditText editTextKeterangan;
//	private EditText txtRemark;
	private TextView text_view_note;
	private String stringTxtKeterangan;
	List<String> vinIndex = new ArrayList<String>();
	boolean statusSimpan, statusPassedPDI;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.checking_pdi_intro);
		Intent i = getIntent();
		jumlahOption = 0;
		jumlahNotGood = 0;
		blacklist = "";
		keterangan = "";
		whitelist = "";
		statusSimpan = false;
		statusPassedPDI = false;
		kumpulanremark = "";
		editTextKeterangan = (EditText) findViewById(R.id.edit_text_note);
		text_view_note = (TextView)findViewById(R.id.text_view_note);
		text_view_note.setText(Html.fromHtml(getBaseContext().getString(R.string.text_view_note)));

		// getting product id (pid) from intent
		vin_number = i.getStringExtra("vinNumber");
		idMobil = i.getStringExtra("idMobil");
		// Hashmap for ListView
		getActionBar().setTitle("PDI Checking : " + vin_number);
		productsList = new ArrayList<HashMap<String, String>>();

		optionPDIList = new HashMap<String, String[]>();
		// Loading products in Background Thread
		new LoadAllProducts().execute();

		// Get listview
		ListView lv = getListView();
		lv.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

		Button btnSimpan = (Button) findViewById(R.id.button_save_pdi_checking);
		btnSimpan.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				stringTxtKeterangan = editTextKeterangan.getText().toString();
				Log.d("Jumlah Not Good", jumlahNotGood + "");
				periksaNotGood();
				if (jumlahNotGood == 0)
				{
					statusPassedPDI = true;
					lolos = "Ya";
					new simpanPDI().execute();
				}
				else
				{
					statusPassedPDI = false;
					lolos = "Tidak";
					int jumlahnot = jumlahNotGood;
					dialogConfirm(jumlahnot);
				}
			}
		});

	}

	public void showResult(View v)
	{
		String result = "Selected Product are :";
		String Unresult = "UnSelected Product are :";
		int totalAmount = 0;
		for (ItemPdi p : boxAdapter.getBox())
		{
			if (p.box)
			{
				result += "\n" + p.remark;

			}
			else
			{
				Unresult += "\n" + p.remark;
			}
		}
		Toast.makeText(this, result + "\n", Toast.LENGTH_LONG).show();
		Toast.makeText(this, Unresult + "\n", Toast.LENGTH_LONG).show();
	}

	private void prepareKirim()
	{
		String idPdi, namaPdi, userSelect, remarknya;
		idPdi = "";
		namaPdi = "";
		userSelect = "";
		remarknya = "";
		jumlahNotGood = 0;
		for (ItemPdi p : boxAdapter.getBox())
		{
			if (p.box)
			{
				if (whitelist.toString().equals(""))
				{
					whitelist = p.idChildPdi;
				}
				else
				{
					whitelist = whitelist + "," + p.idChildPdi;
				}
				// catat remark
				if (kumpulanremark == "")
				{
					kumpulanremark = p.idChildPdi + "@@" + p.remark;
				}
				else
				{
					kumpulanremark = kumpulanremark + "#*#" + p.idChildPdi + "@@" + p.remark;
				}

			}
			else
			{
				jumlahNotGood++;
				if (blacklist.toString().equals(""))
				{
					blacklist = p.idChildPdi;
				}
				else
				{
					blacklist = blacklist + "," + p.idChildPdi;
				}
				// catat remark
				if (kumpulanremark == "")
				{
					kumpulanremark = p.idChildPdi + "@@" + p.remark;
				}
				else
				{
					kumpulanremark = kumpulanremark + "#*#" + p.idChildPdi + "@@" + p.remark;
				}
			}

		}

		/**
		 * OLD BUTTON for (int i = 0; i < jumlahOption; i++) { for(String
		 * s:optionPDIList.get("option"+i)){
		 * 
		 * Log.d("hasil decode", s);
		 * 
		 * 
		 * String[] pecahOption = s.toString().split(",!,");
		 * if(pecahOption[0].toString().equals("idPdi")){
		 * 
		 * idPdi = pecahOption[1].toString(); }else
		 * if(pecahOption[0].toString().equals("namaPdi")){ namaPdi =
		 * pecahOption[1].toString(); }else
		 * if(pecahOption[0].toString().equals("userSelect")){ userSelect =
		 * pecahOption[1].toString(); Log.d("Pilih "+namaPdi, userSelect); }else
		 * if(pecahOption[0].toString().equals("remark")){ remarknya =
		 * pecahOption[1].toString(); } //PERIKSA GOOD or NOT GOOD
		 * 
		 * 
		 * //PERIKSA GOOD SELESAI
		 * 
		 * } if(userSelect.toString().equals("Good")){
		 * 
		 * if(whitelist.toString().equals("")){ whitelist = idPdi; }else{
		 * whitelist = whitelist +","+idPdi; } }else{
		 * if(blacklist.toString().equals("")){ blacklist = idPdi; }else{
		 * blacklist = blacklist +","+idPdi; } }
		 * if(kumpulanremark.toString().equals("")){ kumpulanremark = remarknya;
		 * }else{ kumpulanremark = kumpulanremark+"-.-"+remarknya; } }
		 **/
		Log.d("white", whitelist);
		Log.d("black", blacklist);
	}

	private void periksaNotGood()
	{
		jumlahNotGood = 0;
		/**
		 * String idPdi,namaPdi,userSelect; idPdi=""; namaPdi=""; userSelect="";
		 **/
		for (ItemPdi p : boxAdapter.getBox())
		{
			if (p.box)
			{

			}
			else
			{
				jumlahNotGood++;

			}
		}
	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100)
		{
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(checkPDI.this);
			pDialog.setMessage("Loading list pdi. Please wait...");
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

			params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_listPDI, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					lanjutkan = true;
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);
					vinIndex.clear();
					// looping through All Products
					jumlahPDI = products.length();
					for (int i = 0; i < products.length(); i++)
					{

						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable

						String name = c.getString(TAG_NAME);
						String namaChildPdi = c.getString("namaChildPdi");

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						newPdi.add(new ItemPdi(c.getString("namaChildPdi"), c.getString(TAG_NAME),
								R.drawable.ic_launcher, c.getString("idChildPdi"), true));
						map.put(TAG_NAME, name);
						map.put("namaChildPdi", namaChildPdi);
						optionPDIList.put("option" + i, new String[] { "idPdi,!," + c.getString("idChildPdi"),
								"namaPdi,!," + c.getString("namaChildPdi"), "userSelect,!," + "Not Good",
								"remark,!," + c.getString("idChildPdi") + ",None" });
						jumlahOption++;

						// adding HashList to ArrayList
						productsList.add(map);
					}
				}
				else if (success == 0)
				{
					lanjutkan = false;
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
					/**
					 * Updating parsed JSON data into ListView
					 * */
					if (lanjutkan)
					{
						boxAdapter = new ListAdapter(checkPDI.this, newPdi);

						ListView lvMain = getListView();
						lvMain.setAdapter(boxAdapter);
					}
					else
					{
						dialogEmpty();
					}

				}
			});

		}

	}

	private void dialogEmpty()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Tidak ada mobil yang siap untuk Check PDI Entry ").setCancelable(false)
				.setNegativeButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						Intent i = new Intent(getApplicationContext(), AndroidDashboardDesignActivity.class);
						startActivity(i);
						finish();
					}
				});
		builder.show();

	}

	// public void klikRadio(int posisiNavigasi, String userPilih, String
	// remark)
	// {
	//
	// int indexnya = 0;
	// String idPdi, namaPdi, userSelect, remarknya;
	// idPdi = "";
	// namaPdi = "";
	// userSelect = "";
	// remarknya = "";
	// for (String s : optionPDIList.get("option" + posisiNavigasi))
	// {
	// indexnya++;
	// Log.d("hasil decode", s);
	//
	// String[] pecahOption = s.toString().split(",!,");
	// // if (pecahOption[0].toString().equals("idPdi"))
	// if (pecahOption[0].toString().equals("namaPdi"))
	// {
	//
	// // idPdi = pecahOption[1].toString();
	// namaPdi = pecahOption[1].toString();
	//
	// }
	// else if (pecahOption[0].toString().equals("idPdi"))
	// {
	// // namaPdi = pecahOption[1].toString();
	// idPdi = pecahOption[1].toString();
	// }
	// else if (pecahOption[0].toString().equals("userSelect"))
	// {
	// userSelect = userPilih.toString();
	// Log.d("Pilih " + namaPdi, userSelect);
	// }
	// else if (pecahOption[0].toString().equals("remark"))
	// {
	// remarknya = idPdi + "," + remark.toString();
	// Log.d("Remark " + namaPdi, remarknya);
	// }
	//
	// }
	//
	// // optionPDIList.put("option" + posisiNavigasi, new String[] {
	// // "idPdi,!," + idPdi, "namaPdi,!," + namaPdi,
	// optionPDIList.put("option" + posisiNavigasi, new String[] { "namaPdi,!,"
	// + namaPdi, "idPdi,!," + idPdi,
	// "userSelect,!," + userSelect, "remark,!," + remarknya });
	//
	// }

	class simpanPDI extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(checkPDI.this);
			pDialog.setMessage("Saving PDI Checking. Please wait...");
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
			prepareKirim();

			Log.d("white", whitelist);
			Log.d("black", blacklist);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("idPetugas", LoginActivity.id + ""));
			params.add(new BasicNameValuePair("idMobil", idMobil));
			params.add(new BasicNameValuePair("blacklist", blacklist.toString()));
			params.add(new BasicNameValuePair("whitelist", whitelist.toString()));
			params.add(new BasicNameValuePair("lulus", lolos));
			params.add(new BasicNameValuePair("remark", kumpulanremark));
			params.add(new BasicNameValuePair("keterangan", stringTxtKeterangan));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_save_pdi, "GET", params);
			Log.d("remarkKirim", kumpulanremark.toString());

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					statusSimpan = true;

				}
				else
				{
					statusSimpan = false;
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
					if (statusSimpan)
					{
						dialogSuccess();
					}
					else
					{
						Toast.makeText(checkPDI.this, "Failed to save the PDI Check. ", Toast.LENGTH_LONG).show();
					}
				}
			});

		}

	}

	class ShowDetail extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(checkPDI.this);
			pDialog.setMessage("Getting Search Detail Information . Please wait...");
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
			Intent in = new Intent(getApplicationContext(), searchDetail.class);
			// sending pid to next activity
			in.putExtra("vinnya", vinkirim);

			// starting new activity and expecting some response back
			startActivityForResult(in, 100);

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url)
		{
			// dismiss the dialog after getting all products

			// updating UI from Background Thread
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					/**
					 * Updating parsed JSON data into ListView
					 * */
					pDialog.dismiss();
				}
			});

		}

	}

	class showMap extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(checkPDI.this);
			pDialog.setMessage("Loading Map. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params)
		{

			// updating UI from Background Thread
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					// Check for success tag
					int success;
					try
					{
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("vin", vinPilih));
						params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
						// getting product details by making HTTP request
						// Note that product details url will use GET request
						Log.e("JSON kirim aa : ", params.toString());

						JSONObject json = jParser.makeHttpRequest(LoginActivity.IP_SERVER + "searchDetail.php", "GET",
								params);

						// check your log for json response
						Log.d("Respon JSON nya ", json.toString());

						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1)
						{
							// successfully received product details

							JSONArray productObj = json.getJSONArray(TAG_PRODUCTS); // JSON
																					// Array

							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// product with this pid found
							// Edit Text

							vinNya = product.getString("vinNumber");

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

							// /// display product data in EditText
							// txtName.setText(product.getString(TAG_NAME));
							// txtPrice.setText(product.getString(TAG_PRICE));
							// txtDesc.setText(product.getString(TAG_DESCRIPTION));

						}
						else
						{
							// product with pid not found
							Log.e("SQL NOT FOUND", json.toString());
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			});

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url)
		{
			// dismiss the dialog once got all details
			Intent i = new Intent(checkPDI.this, map_activity2.class);
			startActivity(i);
			pDialog.dismiss();

		}
	}

	private void dialogConfirm(int JumlahNotGood)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ada " + jumlahNotGood + " point yang tidak good ? ").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						new simpanPDI().execute();
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

	public void onBackPressed()
	{
		Intent i = new Intent(checkPDI.this, listWaitingPdi.class);
		startActivity(i);
		finish();
	}

	private void dialogSuccess()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(checkPDI.this);

		// Setting Dialog Title
		alertDialog.setTitle("Success :: ");

		// Setting Dialog Message
		alertDialog.setMessage("Success save PDI Check");

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.save);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// User pressed YES button. Write Logic Here
				Intent i = new Intent(checkPDI.this, AndroidDashboardDesignActivity.class);
				startActivity(i);
				finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

}