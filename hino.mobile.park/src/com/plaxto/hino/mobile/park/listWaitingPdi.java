package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.plaxto.hino.mobile.park.listWaitingPdi;
import com.plaxto.hino.mobile.park.searchDetail;

import com.plaxto.hino.mobile.park.MainActivity.LoadHistory;
import com.plaxto.hino.mobile.park.MainActivity.showPush;
import com.plaxto.hino.mobile.park.listWaitingPdi.ShowDetail;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class listWaitingPdi extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = LoginActivity.IP_SERVER+"listParkir.php";
	private static String url_all_listPDI = LoginActivity.IP_SERVER+"listWaitingPDI.php";
	private static String url_listPDI_search = LoginActivity.IP_SERVER+"listWaitingPDISearch.php";
	private static final String url_product_detials = LoginActivity.IP_SERVER+"searchDetail.php";
	// JSON Node names\
	List<String> indexMobil = new ArrayList<String>();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "idMobil";
	private static final String TAG_NAME = "vinNumber";
	private String vin_number;
	private String vinkirim;
	private Button btnShowMap;
	// products JSONArray
	JSONArray products = null;
	static double Slatitude;
	String idMobil;
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
	private String vinPilih;
	boolean hasilPencarian;
	EditText txtSearch;
	List<String> vinIndex = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_waiting_pdi_intro);
        Intent i = getIntent();
        
		// getting product id (pid) from intent
		//vin_number = i.getStringExtra("VIN_NUMBER");
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();
		
		
		// Loading products in Background Thread
		new LoadAllProducts().execute();
		txtSearch = (EditText)findViewById(R.id.edit_text_search_pdi);
		// Get listview
		ListView lv = getListView();
		
        
        lv.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view,
    					int position, long id) {
    			//View parentView = (View) view.getParent();
    				// getting values from selected ListItem
    				//vinkirim = ((TextView) view.findViewById(R.id.VINNUMBEROK)).getText()
    				//.toString();
    				//Toast.makeText(listSearch.this,"Vin di List "+vinkirim,Toast.LENGTH_SHORT).show();

    				// Starting new intent
    			//	new ShowDetail().execute();
    			}
    		});
		// on seleting single product
		// launching Edit Product Screen
		
        Button btnCari = (Button) findViewById(R.id.button_search_pdi);
        btnCari.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new LoadPencarian().execute();
			}
		});
	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
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
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(listWaitingPdi.this);
			pDialog.setMessage("Loading list vehicle. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_listPDI, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);
					vinIndex.clear();
					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString("idMobil");
						String name = c.getString(TAG_NAME);
						String brand = c.getString("brand");
						String typenya = c.getString("type");
						
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_NAME, name);
                        map.put("brand", brand + " - " + typenya);
                        vinIndex.add(name);
						map.put("vin", c.getString("vinNumber"));
						indexMobil.add(id);
						
                        // adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
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
							listWaitingPdi.this, productsList,
							R.layout.list_waiting_pdi, new String[] { TAG_PID, "vin", 
									TAG_NAME,"brand"},
							new int[] { R.id.pid, R.id.VINNUMBEROK, R.id.name, R.id.brandnya }){
						@Override
				        public View getView (int position, View convertView, ViewGroup parent)
				        {
				            View v = super.getView(position, convertView, parent);
				            
				            //View parentView = (View) convertView.getParent();
				            
				            
				             Button b=(Button)v.findViewById(R.id.btnLaunchPdi);
				             b.setTag(position);
				             b.setOnClickListener(new OnClickListener() {
				            
				                @Override
				                public void onClick(View arg0) {
				                	
				                    // TODO Auto-generated method stub
				                	int position2=(Integer)arg0.getTag();
				                	//vinPilih = vinIndex.get(position2).toString();
				                	ListView lv = getListView();
				                	Object o = lv.getItemAtPosition(position2);
				                	//
				                	
				                	idMobil = indexMobil.get(position2).toString();
				                	vinkirim = vinIndex.get(position2).toString();
				                	//TextView text = (TextView) arg0.findViewById(R.id.VINNUMBEROK);
			                           
			                        //String tEXT = text.getText().toString();
				                    
				                    //Toast.makeText(listSearch.this,"Posisi "+position2,Toast.LENGTH_SHORT).show();
				                    //new showMap().execute();
				                	
				                	Intent i = new Intent(listWaitingPdi.this, checkPDI.class);
				    				i.putExtra("idMobil", idMobil);
				    				i.putExtra("vinNumber", vinkirim);
				                	startActivity(i);
				    				
				    				finish();
				                }
				            });
				            return v;
				        }
					};
					// updating listview
					
					
					setListAdapter(adapter);
				}
			});

		}

	}
	
	
	class LoadPencarian extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(listWaitingPdi.this);
			pDialog.setMessage("Searching. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			params.add(new BasicNameValuePair("requestVin", txtSearch.getText().toString()));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_listPDI_search, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					hasilPencarian = true;
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);
					vinIndex.clear();
					indexMobil.clear();
					productsList.clear();
					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString("idMobil");
						String name = c.getString(TAG_NAME);
						String brand = c.getString("brand");
						String typenya = c.getString("type");
						
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_NAME, name);
                        map.put("brand", brand + " - " + typenya);
                        vinIndex.add(name);
						map.put("vin", c.getString("vinNumber"));
						indexMobil.add(id);
						
                        // adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					hasilPencarian = false;
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
					if(hasilPencarian){
					ListAdapter adapter = new SimpleAdapter(
							listWaitingPdi.this, productsList,
							R.layout.list_waiting_pdi, new String[] { TAG_PID, "vin", 
									TAG_NAME,"brand"},
							new int[] { R.id.pid, R.id.VINNUMBEROK, R.id.name, R.id.brandnya }){
						@Override
				        public View getView (int position, View convertView, ViewGroup parent)
				        {
				            View v = super.getView(position, convertView, parent);
				            
				            //View parentView = (View) convertView.getParent();
				            
				            
				             Button b=(Button)v.findViewById(R.id.btnLaunchPdi);
				             b.setTag(position);
				             b.setOnClickListener(new OnClickListener() {
				            
				                @Override
				                public void onClick(View arg0) {
				                	
				                    // TODO Auto-generated method stub
				                	int position2=(Integer)arg0.getTag();
				                	//vinPilih = vinIndex.get(position2).toString();
				                	ListView lv = getListView();
				                	Object o = lv.getItemAtPosition(position2);
				                	//
				                	
				                	idMobil = indexMobil.get(position2).toString();
				                	vinkirim = vinIndex.get(position2).toString();
				                	//TextView text = (TextView) arg0.findViewById(R.id.VINNUMBEROK);
			                           
			                        //String tEXT = text.getText().toString();
				                    
				                    //Toast.makeText(listSearch.this,"Posisi "+position2,Toast.LENGTH_SHORT).show();
				                    //new showMap().execute();
				                	
				                	Intent i = new Intent(listWaitingPdi.this, checkPDI.class);
				    				i.putExtra("idMobil", idMobil);
				    				i.putExtra("vinNumber", vinkirim);
				                	startActivity(i);
				    				
				    				finish();
				                }
				            });
				            return v;
				        }
					};
					// updating listview
					
					
					setListAdapter(adapter);
				
					}else{
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(listWaitingPdi.this);
				    	 
		                // Setting Dialog Title
		                alertDialog.setTitle("Info :: ");
		                
		                // Setting Dialog Message
		                alertDialog.setMessage("Maaf, data pencarian tidak ditemukan");
		  
		                // Setting Icon to Dialog
		                alertDialog.setIcon(R.drawable.save);
		 
		                // Setting Positive "Yes" Button
		                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface dialog, int which) {
		                    // User pressed YES button. Write Logic Here
		                    //Toast.makeText(getApplicationContext(), "Data Updated", 
		                      //                  Toast.LENGTH_SHORT).show();
		                    
		                    Log.d("Masuk ke row ", "B01");
		                    }
		                });
		 
		 
		 
		                // Showing Alert Message
		                alertDialog.show();
					}
				}
			});

		}

	}
	
	
	class tampilMap extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(listWaitingPdi.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("vin", vin_number));
			params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);
					productsList.clear();
					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString("vinNumber");
						String name = c.getString(TAG_NAME);
						String brand = c.getString("brand");
						String typenya = c.getString("type");
						String areaparkir = c.getString("namaParkir");
						String jamMasuk = c.getString("TimeIn");
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_NAME, "Vin Number : "+name);
                        map.put("brand", brand + " - " + typenya);
                        map.put("namaParkir", c.getString("namaParkir"));
                        map.put("jamMasuk", c.getString("TimeIn"));
						map.put("vin", c.getString("vinNumber"));
                        // adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
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
							listWaitingPdi.this, productsList,
							R.layout.list_item, new String[] { TAG_PID, "vin", 
									TAG_NAME,"brand","brand","vin"},
							new int[] { R.id.pid, R.id.VINNUMBEROK, R.id.name, R.id.brandnya, R.id.AreaParkirnyaGan , R.id.JamMasuknyaGan }){
						@Override
				        public View getView (int position, View convertView, ViewGroup parent)
				        {
				            View v = super.getView(position, convertView, parent);

				             Button b=(Button)v.findViewById(R.id.btnShowMap2);
				             b.setOnClickListener(new OnClickListener() {

				                @Override
				                public void onClick(View arg0) {
				                	 int position=(Integer)arg0.getTag();
				                    // TODO Auto-generated method stub
//				               	 vinkirim = ((TextView) view.findViewById(R.id.VINNUMBEROK)).getText()
				    				//	.toString();   	
				                    Toast.makeText(listWaitingPdi.this,"save"+vinkirim,Toast.LENGTH_SHORT).show();
				                }
				            });
				             
				            return v;
				        }
					};
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
	
	class ShowDetail extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(listWaitingPdi.this);
			pDialog.setMessage("Getting Search Detail Information . Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			Intent in = new Intent(getApplicationContext(),
					searchDetail.class);
			// sending pid to next activity
			in.putExtra("vinnya", vinkirim);
			
			// starting new activity and expecting some response back
			startActivityForResult(in, 100);

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					pDialog.dismiss();
				}
			});

		}

	}
	
	
	class showMap extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(listWaitingPdi.this);
			pDialog.setMessage("Loading Map. Please wait...");
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
						params.add(new BasicNameValuePair("vin", vinPilih));
						params.add(new BasicNameValuePair("idParkir", LoginActivity.idParkirPetugas));
						// getting product details by making HTTP request
						// Note that product details url will use GET request
						Log.e("JSON kirim aa : ", params.toString());
						
						JSONObject json = jParser.makeHttpRequest(
								LoginActivity.IP_SERVER+"searchDetail.php", "GET", params);

						// check your log for json response
						Log.d("Respon JSON nya ", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCTS); // JSON Array
							
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
			Intent i = new Intent(listWaitingPdi.this, map_activity2.class);
						startActivity(i);
			pDialog.dismiss();
			
				
				
		}
	}
	
	public void onBackPressed() {
		Intent i = new Intent(listWaitingPdi.this, AndroidDashboardDesignActivity.class);
		startActivity(i);
		finish();
	}
	
}