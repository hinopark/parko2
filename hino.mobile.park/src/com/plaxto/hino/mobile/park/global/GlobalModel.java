package com.plaxto.hino.mobile.park.global;

import java.text.NumberFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class GlobalModel {

	public GlobalModel() {
		
	}
	
	public static void finishActivity(Activity activity, Class<?> cls) {
		Intent intent = new Intent(activity, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		activity.startActivity(intent);
	}
	
	/** 
	 * Converting result into JSON OBJECT 
	 */
	public JSONObject ConvertStringToJSON(String data) {
		JSONObject jObj = new JSONObject();
        try {
            jObj = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
	}
	
	public String formatNumber(String pointsStr){
		String points = null;
		try {
			NumberFormat num = NumberFormat.getInstance(); 
			long pointLong 	 = Long.parseLong(pointsStr);
			num.setMaximumFractionDigits(3); 
			points = num.format(pointLong);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return points;
	}
}
