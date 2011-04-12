package com.lsiegert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends SimpleCursorAdapter implements OnClickListener{
	private Context context;
	private Cursor games;
	private DatabaseHelper dbHelper;

	public ScheduleAdapter(Context context,
							int layout,
							Cursor c,
							String[] from,
							int[] to,
							DatabaseHelper dbHelper) {
		super(context, 0, c, new String[0], new int[0]);
		this.context = context;
		this.games = c;
		this.dbHelper = dbHelper;
	}
	
	public Object getItem(int position) {
		return games.moveToPosition(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		games.moveToPosition(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.schedule_row, null);
		}

		TextView gameDate = (TextView) convertView.findViewById(R.id.date);
		gameDate.setText(games.getString(1));
		
		TextView gameOpponent = (TextView) convertView.findViewById(R.id.opponent);
		String location = games.getString(7);
		String opponent = games.getString(3);
		if (location.contains("home")) {
			gameOpponent.setText("vs " + opponent);
		}
		else if (location.contains("away")) {
			gameOpponent.setText("@ " + opponent);
		}
		else {
			gameOpponent.setText(opponent);
		}
		
		TextView gameScore = (TextView) convertView.findViewById(R.id.score);
		gameScore.setText(games.getString(4) + "-" + games.getString(5));
		
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.attended);
		checkBox.setTag(Integer.parseInt(this.games.getString(this.games.getColumnIndex("_id"))));

		if (this.games.getString(this.games.getColumnIndex("attended")) != null
				&& Integer.parseInt(this.games.getString(this.games.getColumnIndex("attended"))) != 0) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}
		checkBox.setOnClickListener(this);
		
		return convertView;
	}

	public void onClick(View view) {
		CheckBox checkbox = (CheckBox) view;
		Integer _id = (Integer) checkbox.getTag();
		
		ContentValues values = new ContentValues();
		values.put(" attended", checkbox.isChecked() ? 1 : 0);
		this.dbHelper.myDb.update("Games", values, "_id=?", new String[]{Integer.toString(_id)});		
	}
}
