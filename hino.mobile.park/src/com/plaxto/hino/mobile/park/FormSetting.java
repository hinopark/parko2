package com.plaxto.hino.mobile.park;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.RadioGroup;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.plaxto.common.gps.GpsMain;
import com.plaxto.hino.mobile.park.global.GlobalModel;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;




import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.app.AlertDialog;
import com.plaxto.common.http.HttpSynch;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioButton;;
 
public class FormSetting extends Activity {
	final Context context = this;
	private static final String URL_API_GET = "http://unit-locator.com/parkir/api/changePassword.php?aksi=getpass";
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private String   txtOldPassword;
	private String   txtNewPassword;
	private String   txtConfirmPassword;
	private String statusMessage;
	private String statusPassword;
	private SimpleDateFormat formatter;
	private String currentDateandTime;
	private ArrayList<NameValuePair> params;
	private ProgressDialog dialog;
	private HttpSynch httpSync;
	private GlobalModel globalModel;
	private RadioGroup settingMapGroup;
	private RadioButton radioSelectedMap;
	
	
	
	Spinner sp;
	ListView listView ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        listView = (ListView) findViewById(R.id.listSetting);
       
        String[] values = new String[] { "Setting Gate" , "Setting Map Type"
                
                
                
               };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter); 
        listView.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
               int position, long id) {
              
             // ListView Clicked item index
             int itemPosition     = position;
             
             // ListView Clicked item value
             String  itemValue    = (String) listView.getItemAtPosition(position);
                
              // Show Alert 
              if(itemValue=="Change Password"){
            	 
            	  setContentView(R.layout.ganti_password);
              }
             if(itemValue=="Setting Gate"){
            	
        	  setContentView(R.layout.gatesetting);
        	  List<String> spinnerArray =  new ArrayList<String>();
         	 	spinnerArray.clear();
         	 	if(MainActivity.checkType.toString().equals("I")){
         	    spinnerArray.add("Masuk");
         	    spinnerArray.add("Keluar");
         	 	}else{
         		spinnerArray.add("Keluar");
          	    spinnerArray.add("Masuk");
         		 
         	 	}
         	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormSetting.this, android.R.layout.simple_spinner_item, spinnerArray);
         	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         	    Spinner sItems = (Spinner) findViewById(R.id.spinner1);
         	    sItems.setAdapter(adapter);
        	  
              }
             
             if(itemValue=="Setting Map Type"){
             	
            	 setContentView(R.layout.mapsetting);
            	 List<String> spinnerArray =  new ArrayList<String>();
            	 spinnerArray.clear();
            	 if(map_activity.viewMapSetting == 1){
            	    spinnerArray.add("Standar");
            	    spinnerArray.add("Satelite");
            	 }else{
            		spinnerArray.add("Satelite");
             	    spinnerArray.add("Standar");
            		 
            	 }
            	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormSetting.this, android.R.layout.simple_spinner_item, spinnerArray);
            	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            	    Spinner sItems = (Spinner) findViewById(R.id.spinner1);
            	    sItems.setAdapter(adapter);
            	 
             }
           
            }

       }); 
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }    
    
    
    public void onRadioButtonClicked() {
    	//settingMapGroup = (RadioGroup) findViewById(R.id.radioSettingMap);
        // Is the button now checked?
    	//int selectedId = settingMapGroup.getCheckedRadioButtonId();
    	 
		// find the radiobutton by returned id
	        //radioSelectedMap = (RadioButton) findViewById(selectedId);
	        //String hasilnya = radioSelectedMap.getText().toString();
		Toast.makeText(FormSetting.this,
			"dsfsdf", Toast.LENGTH_SHORT).show();
        
        // Check which radio button was clicked
       // if(hasilnya.equals("Map Standar")){
        	
       // }else if(hasilnya.equals("Map Satelite")){
        	
       // }
    }
    
    private void showSettingAlert(){
    	View checkBoxView = View.inflate(this, R.layout.setting_type_map , null);
    	RadioGroup radioGrupSetting = (RadioGroup) checkBoxView.findViewById(R.id.radioSettingMap);
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to enable mobile data in order to use this application:")
               .setCancelable(false)
               .setView(checkBoxView)
               
               .setPositiveButton("Turn on Data", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        //Intent newintent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        //startActivity(newintent);
                	   settingMapGroup = (RadioGroup) findViewById(R.id.radioSettingMap);
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
    public void changeKlik(View View){
    	oldPassword	= (EditText)findViewById(R.id.txtOldPassword);
 		newPassword	= (EditText)findViewById(R.id.txtNewPassword);
 		confirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);
    	
    	oldPassword.setError(null);
    	newPassword.setError(null);
    	confirmPassword.setError(null);
    	
    	txtOldPassword = oldPassword.getText().toString();
    	txtNewPassword = newPassword.getText().toString();
    	txtConfirmPassword =  confirmPassword.getText().toString();
    	
    	boolean cancel = false;
    	View focusView = null;
    	
    	if (TextUtils.isEmpty(txtOldPassword)) {
			oldPassword.setError(getString(R.string.error_field_required));
			focusView = oldPassword;
			cancel = true;
			//return;
		} 
    	
    	if (TextUtils.isEmpty(txtNewPassword)) {
			newPassword.setError(getString(R.string.error_field_required));
			focusView = newPassword;
			cancel = true;
			//return;
		} 
    	if (TextUtils.isEmpty(txtConfirmPassword)) {
			confirmPassword.setError(getString(R.string.error_field_required));
			focusView = confirmPassword;
			cancel = true;
			//return;
		} 
    	
    	if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//showProgressDialog("Berhasil Masuk Sini ...");
			
			    initializeGetPass(null, null, 0.0, 0.0);
		}
    	
		
		
	}
    
    public void klik4(View View){
    	sp = (Spinner) findViewById(R.id.spinner1);
    	if(sp.getSelectedItem().toString().equals("Masuk")){
    		MainActivity.checkType = "I";
    		 Toast.makeText(this, "Berhasil Setting Gate Masuk", Toast.LENGTH_SHORT).show();
    		 Intent i = new Intent(FormSetting.this, MainActivity.class);
				startActivity(i);
				
				finish();
    	}else{
    		MainActivity.checkType = "O";
    		 Intent i = new Intent(FormSetting.this, MainActivity.class);
				startActivity(i);
				
				finish();
    		Toast.makeText(this, "Berhasil Setting Gate Keluar", Toast.LENGTH_SHORT).show();
    	}
    }
    
    
    public void klikMapSetting(View View){
    	sp = (Spinner) findViewById(R.id.spinner1);
    	if(sp.getSelectedItem().toString().equals("Standar")){
    		map_activity.viewMapSetting = 1;
    		 Toast.makeText(this, "Berhasil Setting Map to STANDAR ", Toast.LENGTH_SHORT).show();
    		 Intent i = new Intent(FormSetting.this, MainActivity.class);
    			startActivity(i);
    			finish();
				
				
    	}else{
    		map_activity.viewMapSetting = 2;
    		
    		Toast.makeText(this, "Berhasil Setting Map to SATELITE", Toast.LENGTH_SHORT).show();
    		//Intent i = new Intent(FormSetting.this, FormSetting.class);
			//startActivity(i);
    		Intent i = new Intent(FormSetting.this, MainActivity.class);
    		startActivity(i);
    		finish();
			
    	}
    }
    
    public void changeKlik3(View View){
    	//oldPassword.setError(null);
    	//newPassword.setError(null);
    	//confirmPassword.setError(null);
    	
    	oldPassword	= (EditText)findViewById(R.id.txtOldPassword);
 		newPassword	= (EditText)findViewById(R.id.txtNewPassword);
 		confirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);
    	
    	txtOldPassword = oldPassword.getText().toString();
    	txtNewPassword = newPassword.getText().toString();
    	txtConfirmPassword =  confirmPassword.getText().toString();
    	
    	boolean cancel = false;
    	View focusView = null;
    	
    	if (txtOldPassword.matches("")) {
    	    Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	
    	if (txtNewPassword.matches("")) {
    	    Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	
    	if (txtConfirmPassword.matches("")) {
    	    Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
    	    return;
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
    
	private void initializeGetPass(String qrCode, String date, double latitude, double longitude){
		if(isOnline() ==  true){
			
			
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
			
			httpSync.execute(URL_API_GET, params);
			
		}else{
			//showSettingAlert();
		}
	}
	
	
	private void showProgressDialog(String messageLoading){
		dialog = new ProgressDialog(this);
		this.dialog.setMessage(messageLoading);
		this.dialog.setIndeterminate(false);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}
    
	public void parseJSON(JSONObject jsonResponse){
		try {
			String res    = jsonResponse.getString("statusId");
			statusPassword = jsonResponse.getString("passwordnya");
		    if(Integer.parseInt(res) == 001){

				
	        	//txtResultQR.setText("");
		    	Toast.makeText(getApplicationContext(), 
	        			statusPassword, 
	        			Toast.LENGTH_SHORT).show();
		    	dialog.dismiss();
		    	
		    }else{	
		    	Toast.makeText(getApplicationContext(), 
		    			statusPassword, 
		    			Toast.LENGTH_SHORT).show();
		    	dialog.dismiss();
		    }
		} catch (NumberFormatException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Unable to connect server. Please try again later.", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (JSONException e) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), 
					"Unable to connect server. Please try again later.", 
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public void onBackPressed() {
		Intent i = new Intent(FormSetting.this, MainActivity.class);
		startActivity(i);
		finish();
	}
	
    
}