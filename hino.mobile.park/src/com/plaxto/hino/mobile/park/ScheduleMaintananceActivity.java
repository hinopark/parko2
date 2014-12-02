package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ScheduleMaintananceActivity extends Activity
{
	private Spinner mSpinerScheduleMiantanance;
	private Spinner mSPinerMobilMaintanance;
	private Button mButtonSaveScheduleMaintanance;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_maintanance);

	}

	public void addItemOnSpinerMobilMaintanance()
	{
		mSpinerScheduleMiantanance = (Spinner) findViewById(R.id.spiner_list_mobil_maintanace);
		List<String> listMobilMaintanace = new ArrayList<String>();
		listMobilMaintanace.add("mobil 1");
		listMobilMaintanace.add("mobil 2");
		listMobilMaintanace.add("mobil 3");
		ArrayAdapter<String> adapterMobilMaintanance = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listMobilMaintanace);

		adapterMobilMaintanance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSPinerMobilMaintanance.setAdapter(adapterMobilMaintanance);

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
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void sendScheduleDataMaintanance()
	{
		mSpinerScheduleMiantanance = (Spinner)findViewById(R.id.spiner_schedule_mobil_mantainance);
		mSPinerMobilMaintanance = (Spinner)findViewById(R.id.spiner_list_mobil_maintanace);
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
