package edu.psu.teamone.main;

import java.util.ArrayList;

public class Instructor {
	private int id; // instructor id (user-assigned)
	private String name; // instructor name
	private final ArrayList<String> disciplines;

	Instructor(int id, String name, ArrayList<String> disciplines) {
		this.id = id;
		this.name = name;
		this.disciplines = disciplines;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getDisciplines() {
		return disciplines;
	}
}
