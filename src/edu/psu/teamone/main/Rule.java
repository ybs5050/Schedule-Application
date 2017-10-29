package edu.psu.teamone.main;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

public abstract class Rule {

	// Represents a schedule that is impossible to
	// realize. For example, an instructor scheduled
	// for two simultaneous classes.
	public final static int IMPOSSIBLE = 1000000;

	// This operation returns a fitness score for the
	// provided schedule. Higher values indicate a less
	// fit schedule. A value of 0 indicates a perfect fit.
	public abstract int getFitness(Schedule schedule);

	public static class InstructorSchedule extends Rule {
		// This operation returns a fitness score for the
		// provided schedule. Higher values indicate a less
		// fit schedule. A value of 0 indicates a perfect fit.
		public int getFitness(Schedule schedule) {
			ArrayList<Meeting> meetingList = new ArrayList<Meeting>();
			for (Entry<Section, Meeting> scheduleEntry : schedule.getSections().entrySet()) {
				// Put all meeting in schedule to meetingList
				meetingList.add(scheduleEntry.getValue());
			}
			// 1 back to back schedule = 10 points, 0 means perfect fit
			return checkBacktoBack(meetingList) * 10;
		}

		private int checkBacktoBack(ArrayList<Meeting> meetingList) {
			// Checks if there is any back to back schedule and how many
			// PRECONDITION : The schedule does not conflict
			int backCount = 0;
			for (int i = 0; i < meetingList.size(); i++) {
				// Go through all meetings and check any time conflicts
				for (int j = i + 1; j < meetingList.size(); j++) {
					// Compare each meeting to all other meetings
					if (Arrays.equals(meetingList.get(i).getDays(), meetingList.get(j).getDays())) {
						// Class on same days
						Time compHourOneEnd = meetingList.get(i).getStopTime();
						Time compHourTwoStart = meetingList.get(j).getStartTime();
						if (compHourOneEnd.compareTo(compHourTwoStart) == 0) {
							// Back to back schedule
							backCount++;
						}
					}
				}
			}
			return backCount;
		}
	}

	public static class InstructorConflict extends Rule {
		// This operation returns 0 if no instructors
		// are scheduled for simultaneous classes and
		// IMPOSSIBLE otherwise.
		public int getFitness(Schedule schedule) {
			ArrayList<Meeting> meetingList = new ArrayList<Meeting>();
			for (Entry<Section, Meeting> scheduleEntry : schedule.getSections().entrySet()) {
				// Put all meeting in schedule to meetingList
				meetingList.add(scheduleEntry.getValue());
			}
			boolean checkConflicts = checkConflicts(meetingList);
			return checkConflicts ? IMPOSSIBLE : 0;
		}

		private boolean checkConflicts(ArrayList<Meeting> meetingList) {
			// Checks if there is any conflicting class schedule
			// Condition: Schedule 1 starts on or before Schedule 2 and Schedule
			// 1 Ends after Schedule 2 Start

			for (int i = 0; i < meetingList.size(); i++) {
				for (int j = i + 1; j < meetingList.size()-1; j++) {
					boolean days1[] = meetingList.get(i).getDays();
					boolean days2[] = meetingList.get(j).getDays();
					for (int k = 0; k < days1.length; k++) {
						if (days1[k] == days2[k]) {
							Time compHourOneEnd = meetingList.get(i).getStopTime();
							Time compHourTwoStart = meetingList.get(j).getStartTime();
							if (meetingList.get(i).getStartTime() == meetingList.get(j).getStartTime()) {
								return true;
							}
							if (compHourOneEnd.compareTo(compHourTwoStart) == 1) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

	public static class InstructorHours extends Rule {
		// This checks if the instructor is working too much
		// Returns 0 if weekly lecture hour <= 40
		// Returns IMPOSSIBLE if weekly lecture hour > 40
		// TODO: Change hourLimit in the future
		public int getFitness(Schedule schedule) {
			ArrayList<Meeting> meetingList = new ArrayList<Meeting>();
			for (Entry<Section, Meeting> scheduleEntry : schedule.getSections().entrySet()) {
				// Put all meeting in schedule to meetingList
				meetingList.add(scheduleEntry.getValue());
			}
			Time totalHours = getTotalHours(meetingList);
			return totalHours.getHours() <= 40 ? 0 : IMPOSSIBLE;
		}

		private Time getTotalHours(ArrayList<Meeting> meetingList) {
			int totalHours = 0;
			int totalMinutes = 0;
			for (Meeting meeting : meetingList) {
				// Sum up all durations of classes in meeting
				Time tempTime = getDuration(meeting);
				totalHours += tempTime.getHours();
				totalMinutes += tempTime.getMinutes();
			}
			if (totalMinutes >= 60) {
				// Convert minutes to hours if minutes exceed 59
				totalHours += totalMinutes / 60;
				totalMinutes = totalMinutes % 60;
			}
			return new Time(totalHours, totalMinutes, 0);
		}

		private Time getDuration(Meeting meeting) {
			// This Calculates the duration of a
			// meeting and returns that Time
			Time time1 = meeting.getStartTime();
			Time time2 = meeting.getStopTime();
			// Eclipse says getHours() is deprecated why?
			// ignore seconds who cares
			int hourDifference = time2.getHours() - time1.getHours();
			int minuteDifference = time2.getMinutes() - time1.getHours();
			return new Time(hourDifference, minuteDifference, 0);
		}
	}
}
