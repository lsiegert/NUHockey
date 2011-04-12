package com.lsiegert;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RosterAdapter extends BaseAdapter{
	private Context context;
	private Cursor players;
	
	public RosterAdapter(Context context, Cursor players) {
		this.context = context;
		this.players = players;
	}
	
	public int getCount() {
		return players.getCount();
	}

	public Object getItem(int position) {
		return players.moveToPosition(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		players.moveToPosition(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.roster_row, null);
		}
		
		TextView playerNumber = (TextView) convertView.findViewById(R.id.number);
		playerNumber.setText("#" + players.getString(1));
		
		TextView playerName = (TextView) convertView.findViewById(R.id.name);
		playerName.setText(players.getString(2));
		
		TextView playerYear = (TextView) convertView.findViewById(R.id.year);
		playerYear.setText(players.getString(3));
		
		TextView playerPosition = (TextView) convertView.findViewById(R.id.position);
		playerPosition.setText(players.getString(4));
		
		TextView playerHometown = (TextView) convertView.findViewById(R.id.hometown);
		playerHometown.setText(players.getString(5));
		
		return convertView;
	}
}
