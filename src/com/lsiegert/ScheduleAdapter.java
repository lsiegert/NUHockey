package com.lsiegert;

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
	private GamesDbAdapter dbHelper;

	public ScheduleAdapter(Context context,
							int layout,
							Cursor c,
							String[] from,
							int[] to,
							GamesDbAdapter dbHelper) {
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
		gameDate.setText(games.getString(games.getColumnIndex("date")));
		
		TextView gameOpponent = (TextView) convertView.findViewById(R.id.opponent);
		String location = games.getString(games.getColumnIndex("location"));
		String opponent = games.getString(games.getColumnIndex("opponent"));
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
		int nuscore = games.getInt(games.getColumnIndex("nuscore"));
		int oppscore = games.getInt(games.getColumnIndex("oppscore"));
		boolean overtime = Boolean.parseBoolean(games.getString(games.getColumnIndex("overtime")));
		
		if (nuscore < 0 || oppscore < 0) {
			gameScore.setText("");
		} else {
			String score = nuscore + "-" + oppscore;
			
			if (overtime) {
				score = score + " (OT)";
			}
			
			gameScore.setText(score);
			
			if (nuscore > oppscore) {
				gameScore.setTextColor(Color.GREEN);
			} else if (nuscore < oppscore) {
				gameScore.setTextColor(Color.RED);
			} else {
				gameScore.setTextColor(Color.WHITE);
			}
		}
		
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
		boolean toggled = dbHelper.toggleAttended(_id, checkbox.isChecked());
		if (toggled) {
			games.requery();
		}
	}
}