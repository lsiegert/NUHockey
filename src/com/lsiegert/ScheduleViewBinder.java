package com.lsiegert;

import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class ScheduleViewBinder implements ViewBinder {

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		TextView opponent = (TextView) view.findViewById(R.id.opponent);
		TextView score = (TextView) view.findViewById(R.id.score);
		CheckBox attended = (CheckBox) view.findViewById(R.id.attended);

		if (columnIndex == 7) {
			
			return true;
		}
		else if (columnIndex == 4) {
			
		}
		else if (columnIndex == 5) {
			return true;
		}
		else if (columnIndex == 3) {
			
			return true;
		}
		return false;
	}
}