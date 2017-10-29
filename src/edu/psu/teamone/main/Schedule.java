package edu.psu.teamone.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Schedule {
	private Map<Section, Meeting> sections = new HashMap<Section, Meeting>();
	private ArrayList<String> convertedSection = new ArrayList<String>();

	public Schedule() {

	}

	public void addSection(Section section, Meeting meeting) {
		sections.put(section, meeting);
	}

	public boolean removeSection(Section section) {
		// If sections is null, false. Else try to remove section from sections
		// and return result
		return sections.size() == 0 ? false : sections.values().remove(section);
	}

	public Map<Section, Meeting> getSections() {
		return sections;
	}

	public void printSections() {
		for (Entry<Section, Meeting> entry : sections.entrySet()) {
			boolean days[] = entry.getValue().getDays();
			System.out.println(entry.getKey().getId() + " " + entry.getKey().getName() + " "
					+ entry.getKey().getAbbreviation() + " " + entry.getValue().getStartTime() + " "
					+ entry.getValue().getStopTime() + " " + days[0] + days[1] + days[2] + days[3] + days[4]);
		}
	}
	public void reset(){
		this.sections.clear();
	}
	
}
