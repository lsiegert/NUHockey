package com.lsiegert;

public class Player {
	private int id;
	private int number;
	private String name;
	private String year;
	private String position;
	private String hometown;
	
	public Player(int id, int number,
					String name, String year,
					String position, String hometown){
		super();
		this.id = id;
		this.number = number;
		this.name = name;
		this.year = year;
		this.position = position;
		this.hometown = hometown;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
}
