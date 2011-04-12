package com.lsiegert;

import java.sql.Date;

public class Game {
	private Date date;
	private String opponent;
	private int nuscore;
	private int oppscore;
	private boolean attended;
	private String location;
	
	public Game(Date date, String opponent,
				int nuscore, int oppscore,
				boolean attended, String location) {
		super();
		this.date = date;
		this.opponent = opponent;
		this.nuscore = nuscore;
		this.oppscore = oppscore;
		this.attended = attended;
		this.location = location;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public int getNuscore() {
		return nuscore;
	}

	public void setNuscore(int nuscore) {
		this.nuscore = nuscore;
	}

	public int getOppscore() {
		return oppscore;
	}

	public void setOppscore(int oppscore) {
		this.oppscore = oppscore;
	}

	public boolean isAttended() {
		return attended;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
