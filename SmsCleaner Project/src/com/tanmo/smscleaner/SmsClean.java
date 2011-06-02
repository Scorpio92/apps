package com.tanmo.smscleaner;

import android.app.Activity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.net.Uri;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.*;

import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.Context;

public class SmsClean extends Activity
{
	private String TAG = "smsclean";
	private final int MAX_SMS_BROWSE = 5;
	private String info, dbginfo, info_show;
	private Cursor cur, cur_delete;
	private Uri uri;
	private List<String> arraylist_sms = new ArrayList<String>();
	private ArrayList<Boolean> checkedItem = new ArrayList<Boolean>();
	private ArrayAdapter<String> arrayadapter_sms;
	private ListView list;
	private TextView footview;
	private ArrayList<Map<String, Object>> sms_array1 = new ArrayList<Map<String, Object>>();
	private int iTotal, iSelected;
	static final private int MENU_DELETE_SELECTED = Menu.FIRST;
	static final private int MENU_QUIT = Menu.FIRST + 2;

	// private ListView list;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		list = (ListView) findViewById(R.id.listView1);
		list.setBackgroundColor(Color.BLUE);
		list.setCacheColorHint(Color.BLUE);// Tommy: won't change color when

		// head/foot view
		footview = new TextView(this);
		footview.setTextColor(Color.RED);
		footview.setTypeface(null, Typeface.BOLD);
		list.addHeaderView(footview);

		// select
		Log.i(TAG, "start !");

		// Show list
		browse_sms(MAX_SMS_BROWSE);

		Log.i(TAG, "stop !");
	}

	private void list_update_headview()
	{
		// footview.setText("Total " + iTotal + ", " + "Selected " + iSelected);
		footview.setText(" " + iSelected + "/" + iTotal + " SMS Selected. ");
	}

	// Browser all the SMS
	private void browse_sms(int num)
	{
		int i;

		if (num == 0)
			num = 99999;

		// String[] sw = new String[100];
		// for (i = 0; i < 100; i++)
		// {
		// sw[i] = "listtest_" + i;
		// }

		// for (i = 0; i < 10; i++)
		// {
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("ADDR", "Test Title");
		// map.put("BODY", "listtest_" + i);
		// sms_array1.add(map);
		// }

		i = 1;
//		arrayadapter_sms.clear();
		arraylist_sms.clear();
		checkedItem.clear();
		sms_array1.clear();
		

		uri = Uri.parse("content://sms/inbox");
		// cur = this.managedQuery(uri, null, null, null, null);
		cur = getContentResolver().query(uri, null, null, null, null);

		iSelected = 0;
		if (cur.getCount() == 0)
		{
			Log.i(TAG, "No sms found !");
		} else
		{
			Log.i(TAG, "Total " + cur.getCount() + " sms !!!");
			// arraylist_sms.add("Total " + cur.getCount() + " sms !!!");

			iSelected = 0;
			iTotal = 0;
			if (cur.moveToFirst())
			{
				do
				{
					Map<String, Object> map = new HashMap<String, Object>();

					info = "Sms " + String.valueOf(i) + ": ";
					info_show = "";
					for (int j = 0; j < cur.getColumnCount(); j++)
					{
						if (cur.getColumnName(j).equals("body"))
						{
							Log.i(TAG, "Msg is " + cur.getString(j));
							info_show += cur.getString(j);
							map.put("BODY", cur.getString(j));
						} else if (cur.getColumnName(j).equals("address"))
						{
							Log.i(TAG, "From " + cur.getString(j));
							info_show += "From:" + cur.getString(j) + "\n";
							map.put("ADDR", cur.getString(j));
						} else if (cur.getColumnName(j).equals("person"))
						{
							if (cur.getString(j) == null)
							{
								map.put("CHECKED", true);
								if (false)
								{
									checkedItem.add(true);
									iSelected++;
								} else
								{
									checkedItem.add(false);
								}
							} else
							{
								checkedItem.add(false);
								map.put("CHECKED", false);
							}
						} else if (cur.getColumnName(j).equals("_id"))
						{
							map.put("ID", cur.getString(j));
						}else if (cur.getColumnName(j).equals("thread_id"))
						{
							map.put("THREAD_ID", cur.getString(j));
						}
						
						{
							info += cur.getColumnName(j) + "="
									+ cur.getString(j) + ";";
						}

					}
					Log.i(TAG, info);
					arraylist_sms.add(info_show);
					sms_array1.add(map);
					iTotal++;

				} while (cur.moveToNext() && (i++ < num));
			}
		}

		list_update_headview();

		{ // if extends ListActivity, Tommy: this mode, can't change textsize.
			// arrayadapter_sms = new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_multiple_choice,
			// arraylist_sms);
			// setListAdapter(arrayadapter_sms);
			// final ListView listView = getListView();
			// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}

		{ // Use customer listview
			// arrayadapter_sms = new ArrayAdapter<String>(this,
			// R.layout.smslist_layout, arraylist_sms);
			// list.setAdapter(arrayadapter_sms);
		}

		// Use SimpleAdapter
		{
			// SimpleAdapter smslistadapter = new SimpleAdapter(this,
			// sms_array1,
			// R.layout.smslist_map, new String[] { "ADDR", "BODY" },
			// new int[] { R.id.sms_address, R.id.sms_body });
			//
			// list.setAdapter(smslistadapter);
		}

		// Use My Adapter
		{
			MyAdapter smslistadapter = new MyAdapter(this);

			list.setAdapter(smslistadapter);
		}

		Toast.makeText(getApplicationContext(),
				"Total " + cur.getCount() + " sms !!!", Toast.LENGTH_LONG)
				.show();

		// listView.setItemsCanFocus(false);

		// listView.setItemChecked(1, true);

	} // browse_sms

	private void delete_sms_selected()
	{
		Boolean bDel;

		if (iTotal > 0 && iSelected > 0)
		{
			for (int i = 0; i < iTotal; i++)
			{
				bDel = (Boolean) checkedItem.get(i);
				if (bDel)
				{	// DELETE
					Log.i(TAG, "Pos " + i + " will be delete." + " Tel is "
							+ sms_array1.get(i).get("ADDR") + "thread_id is " + sms_array1.get(i).get("THREAD_ID"));
//					this.getContentResolver().delete(Uri.parse("content://sms/inbox"), "_id=?", new String[]{"357"});// {sms_array1.get(i).get("ID"));
				} else
				{
					Log.i(TAG, "Pos " + i + " SKIP." + " Tel is "
							+ sms_array1.get(i).get("ADDR"));
				}
			}
		}
		
		//update
		browse_sms(MAX_SMS_BROWSE);
	}

	public final class ViewHolder
	{
		public TextView addr;
		public TextView body;
		public CheckBox checked;
	}

	// ///////////////////////////////
	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return sms_array1.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final int p = position; // Must be final.
			ViewHolder holder = null;

			// Log.i(TAG, "MyAdapter getView " + position);
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.smslist_map, null);
				holder.addr = (TextView) convertView
						.findViewById(R.id.sms_address);
				holder.body = (TextView) convertView
						.findViewById(R.id.sms_body);
				holder.checked = (CheckBox) convertView
						.findViewById(R.id.checkBox_delete);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.addr.setText("From:"
					+ (String) sms_array1.get(position).get("ADDR"));
			holder.body.setText((String) sms_array1.get(position).get("BODY"));
			// holder.checked.setChecked((Boolean) sms_array1.get(position).get(
			// "CHECKED"));
			holder.checked.setChecked(checkedItem.get(position));
			holder.checked
					.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
					{
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked)
						{
							// sms_array1.get(p);
							// Toast.makeText(getApplicationContext(),
							// "Pos " + p + " will be deleted !!!",
							// Toast.LENGTH_LONG)
							// .show();
							if (isChecked)
							{
								checkedItem.set(p, true);
								iSelected++;
							} else
							{
								checkedItem.set(p, false);
								iSelected--;
							}
							list_update_headview();
						}
					});

			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		int idGroup1 = 0;

		/* The order position of the item */
		int orderItem1 = Menu.NONE;
		int orderItem3 = Menu.NONE + 2;

		menu.add(Menu.NONE, MENU_DELETE_SELECTED, Menu.NONE, "Delete Selected")
				.setIcon(android.R.drawable.ic_delete);
		int MENU_DRAW;
		menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, android.R.string.ok).setIcon(
				android.R.drawable.ic_dialog_alert);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
			case (MENU_DELETE_SELECTED):
				delete_sms_selected();
				break;

		}
		return super.onMenuItemSelected(featureId, item);
	}

}
