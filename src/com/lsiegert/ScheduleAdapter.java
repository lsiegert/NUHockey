package com.lsiegert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
		int nuscore = games.getInt(4);
		int oppscore = games.getInt(5);
		
		gameScore.setText(nuscore + "-" + oppscore);
		if (nuscore > oppscore) {
			gameScore.setTextColor(Color.GREEN);
		}
		else if (nuscore < oppscore) {
			gameScore.setTextColor(Color.RED);
		}		
		else { gameScore.setTextColor(Color.WHITE); }
		
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.attended);
		
		int tag = Integer.parseInt(this.games.getString(this.games.getColumnIndex("_id")));
		checkBox.setTag(tag);

		String attended = this.games.getString(this.games.getColumnIndex("attended"));
		if (attended != null && Integer.parseInt(attended) != 0) {
			checkBox.setOnCheckedChangeListener(null);
			checkBox.setChecked(true);
		} else {
			checkBox.setOnCheckedChangeListener(null);
			checkBox.setChecked(false);
		}
		checkBox.setOnClickListener(this);
		
		System.out.println(this.games.getString(this.games.getColumnIndex("attended")));
		//System.out.println(tag + " " + checkBox.isChecked() + " " + attended);
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
