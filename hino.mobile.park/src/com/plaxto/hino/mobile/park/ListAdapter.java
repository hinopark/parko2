package com.plaxto.hino.mobile.park;

import java.util.ArrayList;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter
{
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<ItemPdi> objects;
	int jumlahPdi;
	public ArrayList<ListItem> myItems = new ArrayList<ListItem>();

	/**
	 * private int lastFocussedPosition = -1; private Handler handler = new
	 * Handler();
	 **/

	ListAdapter(Context context, ArrayList<ItemPdi> products)
	{
		ctx = context;
		objects = products;
		if (checkPDI.jumlahPDI != 0)
		{
			jumlahPdi = checkPDI.jumlahPDI;
		}
		else if(checkPDIOut.jumlahPDI != 0)
		{
			jumlahPdi = checkPDIOut.jumlahPDI;
		}else{
			jumlahPdi = checkMaintenance.jumlahPDI;
		}
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < jumlahPdi; i++)
		{
			ListItem listItem = new ListItem();
			listItem.caption = "";
			myItems.add(listItem);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return objects.size();
	}

	@Override
	public Object getItem(int position)
	{
		return objects.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		ViewHolder holder;
		if (view == null)
		{
			holder = new ViewHolder();
			view = lInflater.inflate(R.layout.item_pdi, parent, false);
			holder.caption = (EditText) view.findViewById(R.id.edit_text_detail_remark);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}
		/**
		 * final EditText edittext = ((EditText)
		 * view.findViewById(R.id.txtRemark));
		 * edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		 * 
		 * 
		 * public void onFocusChange(View v, boolean hasFocus) { if (hasFocus) {
		 * handler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { if (lastFocussedPosition == -1 ||
		 *           lastFocussedPosition == position) { lastFocussedPosition =
		 *           position; edittext.requestFocus(); } } }, 200);
		 * 
		 *           } else { lastFocussedPosition = -1; } } });
		 **/
		// end di sini

		holder.caption.setText(myItems.get(position).caption);
		holder.caption.setId(position);

		// we need to update adapter once we finish with editing
		holder.caption.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					final int position = v.getId();
					final EditText Caption = (EditText) v;
					Log.d("posisi nyah", position + "");
					myItems.get(position).caption = Caption.getText().toString();
				}
			}
		});

		ItemPdi p = getProduct(position);

		((TextView) view.findViewById(R.id.text_view_sub_title_part)).setText(p.nameChild);
		((TextView) view.findViewById(R.id.text_view_title_part)).setText(p.nameMaster + "");
		((TextView) view.findViewById(R.id.text_view_global_remark)).setText(p.idChildPdi);

		((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);

		CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
		cbBuy.setOnCheckedChangeListener(myCheckChangList);
		cbBuy.setTag(position);
		cbBuy.setChecked(p.box);
		return view;
	}

	ItemPdi getProduct(int position)
	{
		return ((ItemPdi) getItem(position));
	}

	ArrayList<ItemPdi> getBox()
	{
		ArrayList<ItemPdi> box = new ArrayList<ItemPdi>();
		int a;
		a = 0;
		for (ItemPdi p : objects)
		{
			p.remark = myItems.get(a).caption;
			if (p.box)
			{
				box.add(p);
			}
			else
			{
				box.add(p);
			}
			a++;
		}
		return box;
	}

	OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener()
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			getProduct((Integer) buttonView.getTag()).box = isChecked;
		}
	};

	class ViewHolder
	{
		EditText caption;
	}

	class ListItem
	{
		String caption;
	}
}