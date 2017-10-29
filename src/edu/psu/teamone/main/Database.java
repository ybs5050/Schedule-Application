package edu.psu.teamone.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Database {
	private Connection con = null;
	private Statement st = null;
	private Statement et = null;
	private ResultSet rs = null;
	// db connection credentials
	private String url = "jdbc:mysql://localhost:3306/schedule";
	private String user = "root";
	private String password = "root";

	public void addClass(int courseId, String courseName, String courseAbb) {
		// adds new class to classes table in db
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			// ResultSet = st.executeQuery("SELECT COUNT(*) FROM classes");
			// Add classes to db
			// Ex) 100000, Mec
			String command = String.format("INSERT INTO classes VALUES ('%d', '%s', '%s')", courseId, courseName,
					courseAbb);
			st.executeUpdate(command);
			st.close();

		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void addMeeting(int meetingId, String startTime, String endTime, String days) {
		// adds new meeting to meetings table in db
		// course Id and meeting Id are same, thus meeting 0000001 means it
		// teaches course 0000001
		// days indicate which days the meeting is held. "00101" means
		// Wednesdays and Fridays
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			// Add meeting to db
			// Ex) 100000, Mec
			String command = String.format(
					"INSERT INTO meetings VALUES ('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", meetingId,
					startTime, endTime, days.charAt(0), days.charAt(1), days.charAt(2), days.charAt(3), days.charAt(4));
			st.executeUpdate(command);
			st.close();

		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void addInstructor(int instructorId, String instructorName, String instructorDiscipline) {
		// adds new instructor to instructors table in db
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			String command = String.format("INSERT INTO instructors VALUES ('%d', '%s', '%s')", instructorId,
					instructorName, instructorDiscipline);
			st.executeUpdate(command);
			st.close();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText("Success");
			alert.setContentText("Successfully added Instructor " + instructorName);
			alert.showAndWait();
		} catch (SQLException ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("SQL Error");
			alert.setContentText("You may be using a duplicate user id, try a different user id");
			alert.showAndWait();
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public int generateId() {
		// Checks if database is empty, if yes give 1000000 to course id.
		// If not, give last row's course id added by 1.
		// Ex) If Last row's course id is 1000003, give 1000004
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			ResultSet getData = st.executeQuery("SELECT * FROM classes");
			int rowCount = 0;
			String lastId = "";
			while (getData.next()) {
				rowCount++;
				lastId = getData.getString(1);
			} /*
				 * if (rowCount == 0) { return 1000000; } else { return
				 * Integer.parseInt(lastId) + 1; }
				 */
			return rowCount == 0 ? 1000000 : Integer.parseInt(lastId) + 1;
		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return 0;
	}

	ArrayList<Section> getDataFromDBSection() {
		// load all rows from sections table in tb
		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			ArrayList<Section> tempSection = new ArrayList<Section>();
			ResultSet getData = st.executeQuery("SELECT * FROM classes");
			while (getData.next()) {
				tempSection.add(new Section(getData.getString(2), getData.getString(3),
						Integer.parseInt(getData.getString(1))));
			}
			return tempSection;
		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return null;
	}

	ArrayList<Meeting> getDataFromDBMeeting() {
		// loads all rows from meetings table in db
		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			ArrayList<Meeting> tempMeeting = new ArrayList<Meeting>();
			ResultSet getData2 = st.executeQuery("SELECT * FROM meetings");
			while (getData2.next()) {
				boolean[] days = new boolean[7];
				// Time startTime = new Time(getData2.getString(2)+ ":00");
				Time startTime = java.sql.Time.valueOf(getData2.getString(2));
				Time endTime = java.sql.Time.valueOf(getData2.getString(3));
				days[0] = Integer.parseInt(getData2.getString(4)) == 1 ? true : false;
				days[1] = Integer.parseInt(getData2.getString(5)) == 1 ? true : false;
				days[2] = Integer.parseInt(getData2.getString(6)) == 1 ? true : false;
				days[3] = Integer.parseInt(getData2.getString(7)) == 1 ? true : false;
				days[4] = Integer.parseInt(getData2.getString(8)) == 1 ? true : false;
				// System.out.println(days[0]+ " " +days[1]+" " +days[2]+" "
				// +days[3]+" " +days[4]);
				// System.out.println(startTime);
				// System.out.println(endTime);
				tempMeeting.add(new Meeting(days, startTime, endTime));
			}
			return tempMeeting;
		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return null;
	}

	void deleteSection(int sectionId) {
		// deletes sectionId corresponding class and meeting from classes and
		// meetings table in db
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			String command = String.format("DELETE FROM classes" + " WHERE `course id` = '%d' ", sectionId);
			String command2 = String.format("DELETE FROM meetings" + " WHERE `meeting id` = '%d' ", sectionId);
			st.executeUpdate(command);
			st.executeUpdate(command2);
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Success");
			alert.setHeaderText("Deleted Success");
			alert.setContentText("Delete section " + sectionId + " successful");
			alert.showAndWait();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	void deleteInstructor(int instructorId) {
		// deletes instructorId corresponding row from instructors table in db
		try {

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			String command = String.format("DELETE FROM instructors" + " WHERE `user id` = '%d' ", instructorId);
			st.executeUpdate(command);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText("Deleted Success");
			alert.setContentText("Delete Instructor " + instructorId + " successful");
			alert.showAndWait();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	void editSection(int sectionId, String editSectionName, String editSectionAbb, String editSectionStartTime,
			String editSectionEndTime, String days) {
		// updates db with new information in row corresponding to sectionId
		try {
			con = DriverManager.getConnection(url, user, password);
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String command = String.format("SELECT * FROM classes");
			String command2 = String.format("SELECT * FROM meetings");
			ResultSet sectionRS = stmt.executeQuery(command);
			ResultSet sectionRS2 = stmt2.executeQuery(command2);
			while (sectionRS.next()) {
				if (sectionRS.getString(1).toString().equals(Integer.toString(sectionId))) {
					sectionRS.updateString("course name", editSectionName);
					sectionRS.updateRow();
					sectionRS.updateString("course abbreviation", editSectionAbb);
					sectionRS.updateRow();
				}
			}
			while (sectionRS2.next()) {
				if (sectionRS2.getString(1).toString().equals(Integer.toString(sectionId))) {
					sectionRS2.updateString("start time", editSectionStartTime + ":00");
					sectionRS2.updateString("end time", editSectionEndTime + ":00");
					sectionRS2.updateString("M", Character.toString(days.charAt(0)));
					sectionRS2.updateString("T", Character.toString(days.charAt(1)));
					sectionRS2.updateString("W", Character.toString(days.charAt(2)));
					sectionRS2.updateString("TR", Character.toString(days.charAt(3)));
					sectionRS2.updateString("F", Character.toString(days.charAt(4)));
					sectionRS2.updateRow();
				}
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	void editInstructor(int instructorId, String editInstructorName, String editInstructorDiscipline) {
		// updates db with new information in row corresponding to sectionId
		try {
			con = DriverManager.getConnection(url, user, password);
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String command = String.format("SELECT * FROM instructors");
			ResultSet sectionRS = stmt.executeQuery(command);
			while (sectionRS.next()) {
				if (sectionRS.getString(1).toString().equals(Integer.toString(instructorId))) {
					sectionRS.updateString("name", editInstructorName);
					sectionRS.updateRow();
					sectionRS.updateString("discipline", editInstructorDiscipline);
					sectionRS.updateRow();
				}
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public ArrayList<String> getInstructorData(int instructorId) {
		// loads instructorId corresponding row from instructor table in db
		try {
			String instructorName = "", instructorDiscipline = "";
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			String instructorCommand = String.format("SELECT * FROM instructors" + " WHERE `user id` = '%d' ",
					instructorId);
			ResultSet getInstructorData = st.executeQuery(instructorCommand);
			getInstructorData.next();
			instructorName = getInstructorData.getString(2);
			instructorDiscipline = getInstructorData.getString(3);
			st.close();
			ArrayList<String> sectionData = new ArrayList<String>();
			sectionData.add(instructorName);// 0
			sectionData.add(instructorDiscipline);// 1
			return sectionData;

		} catch (SQLException ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot find corresonding Instructor");
			alert.showAndWait();
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return null;
	}

	public ArrayList<String> getRowData(int sectionId) {
		// loads sectionId corresponding row from sections and meetings table in
		// db
		try {
			String sectionName, sectionAbb, sectionStartTime, sectionEndTime, days = "";
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			et = con.createStatement();
			String sectionCommand = String.format("SELECT * FROM classes" + " WHERE `course id` = '%d' ", sectionId);
			String meetingCommand = String.format("SELECT * FROM meetings" + " WHERE `meeting id` = '%d' ", sectionId);
			ResultSet getSectionData = st.executeQuery(sectionCommand);
			ResultSet getMeetingData = et.executeQuery(meetingCommand);
			getSectionData.next();
			getMeetingData.next();
			sectionName = getSectionData.getString(2);
			sectionAbb = getSectionData.getString(3);
			sectionStartTime = getMeetingData.getString(2).substring(0,getMeetingData.getString(2).length()-3);
			sectionEndTime = getMeetingData.getString(3).substring(0,getMeetingData.getString(3).length()-3);
			days += getMeetingData.getString(4) + getMeetingData.getString(5) + getMeetingData.getString(6)
					+ getMeetingData.getString(7) + getMeetingData.getString(8);
			st.close();
			et.close();
			ArrayList<String> sectionData = new ArrayList<String>();
			sectionData.add(sectionName);// 0
			sectionData.add(sectionAbb);// 1
			sectionData.add(sectionStartTime);// 2
			sectionData.add(sectionEndTime);// 3
			sectionData.add(days);// 4
			return sectionData;

		} catch (SQLException ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot find corresonding Section and Meeting");
			alert.showAndWait();
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			// close db connection
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return null;
	}

}