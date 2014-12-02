package com.plaxto.hino.mobile.park;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AndroidDashboardDesignActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_layout);

		/**
		 * Creating all buttons instances
		 * */
		// Dashboard News feed button
		Button btn_newsfeed = (Button) findViewById(R.id.btn_news_feed);

		// Dashboard Friends button
		Button btn_friends = (Button) findViewById(R.id.btn_friends);

		// Dashboard Messages button
		Button btn_messages = (Button) findViewById(R.id.btn_messages);

		// Dashboard Places button
		Button btn_places = (Button) findViewById(R.id.btn_places);

		Button btn_events = (Button) findViewById(R.id.btn_events);

		Button btn_photos = (Button) findViewById(R.id.btn_photos);

		Button btn_transfer_out = (Button) findViewById(R.id.btn_transfer_out);
		
		Button btn_transfer_in = (Button) findViewById(R.id.btn_transfer_in);
		
		Button btn_maintanance = (Button) findViewById(R.id.button_maintanance);
		// Dashboard Events button

		/**
		 * Handling all button click events
		 * */

		// Listening to News Feed button click
		btn_newsfeed.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(AndroidDashboardDesignActivity.this, listWaitingPdi.class);
				startActivity(i);
				finish();
			}
		});

		// Listening Friends button click
		btn_friends.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(AndroidDashboardDesignActivity.this, OutOffice.class);
				startActivity(i);
				finish();
			}
		});

		// Listening Messages button click
		btn_messages.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				Intent i = new Intent(AndroidDashboardDesignActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});

		// Listening to Places button click
		btn_places.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(AndroidDashboardDesignActivity.this, introCheckOutPdi.class);
				startActivity(i);
				finish();
			}
		});

		btn_events.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(), listStockEvent.class);
				startActivity(i);
			}
		});

		btn_photos.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(), MainActivityParkingCheck.class);
				startActivity(i);
				finish();
			}
		});

		btn_transfer_out.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(), transferOut.class);
				startActivity(i);
				finish();
			}
		});
		btn_transfer_in.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(), transferIn.class);
				startActivity(i);
				finish();
			}
		});

		// Listening to Events button click
		btn_maintanance.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), ListWaitingMaintenance.class);
				startActivity(intent);
			}
		});
		

	}
}