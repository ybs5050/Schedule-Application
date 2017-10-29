package edu.psu.teamone.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class Controller implements Initializable {
	@FXML
	private TabPane tabPane;
	@FXML
	private BorderPane root;
	@FXML
	private TextField sectionName, sectionAbb, startTime, endTime;
	// Class and Meeting
	@FXML
	private CheckBox dayMon, dayTues, dayWed, dayThur, dayFri;// MTWRF
	@FXML
	private CheckBox editDayMon, editDayTues, editDayWed, editDayThur, editDayFri;// MTWRF

	@FXML
	private TextField instructorName, instructorDiscipline, instructorId;
	@FXML
	private TextField editInstructorName, editInstructorDiscipline, editInstructorId;
	@FXML
	private TextField editSectionId, editSectionName, editSectionAbb, editSectionStartTime, editSectionEndTime;
	@FXML
	private TableView<SectionPair> sectionsTable;
	@FXML
	private TableColumn idColumn, nameColumn, abbreviationColumn, timeColumn, daysColumn;
	private ArrayList<Section> sections; // sections to be loaded from db
	private ArrayList<Meeting> meetings; // meetings to be loaded from db
	private Schedule schedule;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new Timer().schedule(
			    new TimerTask() {

			        @Override
			        public void run() {
			            loadSections();
			        }
			    }, 0, 5000);
		Rule instructorRule = new Rule.InstructorSchedule();
		idColumn.setCellValueFactory(new Callback<CellDataFeatures<SectionPair, Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<SectionPair, Integer> param) {
				return new ReadOnlyObjectWrapper<Integer>(param.getValue().section.getId());
			}
		});
		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<SectionPair, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SectionPair, String> param) {
				return new ReadOnlyObjectWrapper<String>(param.getValue().section.getName());
			}
		});
		abbreviationColumn
				.setCellValueFactory(new Callback<CellDataFeatures<SectionPair, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<SectionPair, String> param) {
						return new ReadOnlyObjectWrapper<String>(param.getValue().section.getAbbreviation());
					}
				});
		timeColumn.setCellValueFactory(new Callback<CellDataFeatures<SectionPair, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SectionPair, String> param) {
				return new ReadOnlyObjectWrapper<String>(
						param.getValue().meeting.getStartTime().toLocalTime().toString());
			}
		});
		daysColumn.setCellValueFactory(new Callback<CellDataFeatures<SectionPair, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SectionPair, String> param) {
				String daysString = "";
				boolean days[] = param.getValue().meeting.getDays();
				char daysLetter[] = { 'M', 'T', 'W', 'R', 'F', 'S', 'U' };
				for (int i = 0; i < days.length; i++) {
					if (days[i])
						daysString += daysLetter[i];
				}
				return new ReadOnlyObjectWrapper<String>(daysString);
			}
		});
	}

	@FXML
	protected final void handleExitAction(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	protected final void handleAboutAction(ActionEvent event) {
		// Shows About
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About Schedule Application");
		alert.setHeaderText("Copyright © 2016");
		alert.setContentText("By CMPSC 221 Team One");
		alert.showAndWait();
	}

	@FXML
	protected final void handleWebAction(ActionEvent event) {
		// Shows github page
		ScheduleApplication.getStaticHostServices().showDocument("https://github.com/CMPSC221/Schedule-Application");
	}

	@FXML
	protected final void handleDocAction(ActionEvent event) {
		// Shows github page
		ScheduleApplication.getStaticHostServices()
				.showDocument("https://github.com/CMPSC221/Schedule-Application/wiki");
	}

	@FXML
	protected final void deleteSectionAction(ActionEvent event) {
		// Deletes user selected section from db
		Database db = new Database();
		int sectionId = Integer.parseInt(editSectionId.getText().trim());
		db.deleteSection(sectionId);
		resetArea();
		loadSections();
	}

	@FXML
	protected final void deleteInstructorAction(ActionEvent event) {
		// Deletes user selected Instructor from db
		Database db = new Database();
		int instructorId = Integer.parseInt(editInstructorId.getText().trim());
		db.deleteInstructor(instructorId);
		resetArea();
	}

	@FXML
	protected final void editSectionAction(ActionEvent event) {
		// Updates user sellected Section to db
		Database db = new Database();
		int sectionId = Integer.parseInt(this.editSectionId.getText().trim());
		String editSectionName = this.editSectionName.getText().trim();
		String editSectionAbb = this.editSectionAbb.getText().trim();
		String editSectionStartTime = this.editSectionStartTime.getText().trim();
		String editSectionEndTime = this.editSectionEndTime.getText().trim();
		String days = (editDayMon.isSelected() ? "1" : "0") + (editDayTues.isSelected() ? "1" : "0")
				+ (editDayWed.isSelected() ? "1" : "0") + (editDayThur.isSelected() ? "1" : "0")
				+ (editDayFri.isSelected() ? "1" : "0");
		if (editSectionName == "" || editSectionAbb == "") {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Check your boxes!");
			alert.showAndWait();
		} else if (editSectionName.matches(".*\\d+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Check your boxes!");
			alert.showAndWait();
		}
		db.editSection(sectionId, editSectionName, editSectionAbb, editSectionStartTime, editSectionEndTime, days);
		resetArea();
		loadSections();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText("Edit Success");
		alert.setContentText("Succesfully Updated Section " + sectionId);
		alert.showAndWait();
	}

	@FXML
	protected final void editInstructorAction(ActionEvent event) {
		// updates user selected instructor to db
		Database db = new Database();
		int instructorId = Integer.parseInt(editInstructorId.getText().trim());
		String editInstructorName = this.editInstructorName.getText().trim();
		String editInstructorDiscipline = this.editInstructorDiscipline.getText().trim();
		if (editInstructorName == "" || editInstructorDiscipline == "") {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Check your boxes!");
			alert.showAndWait();
		} else if (editInstructorName.matches(".*\\d+.*") || editInstructorDiscipline.matches(".*\\d+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("Check your boxes!");
			alert.showAndWait();
		} else {
			db.editInstructor(instructorId, editInstructorName, editInstructorDiscipline);
		}
		resetArea();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText("Edit Success");
		alert.setContentText("Succesfully Updated Instructor " + editInstructorName);
		alert.showAndWait();
	}

	@FXML
	protected final void loadSectionAction(ActionEvent event) {
		// loads user selected section from db
		editDayMon.setSelected(false);
		editDayTues.setSelected(false);
		editDayWed.setSelected(false);
		editDayThur.setSelected(false);
		editDayFri.setSelected(false);
		Database db = new Database();
		ArrayList<String> sectionData = new ArrayList<String>();
		String sectionName = "", sectionAbb = "", sectionStartTime = "", sectionEndTime = "", days = "";
		if (editSectionId.getText().trim().equals("") || editSectionId.getText().trim().matches(".*[a-zA-z]+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot use letters for sectionId");
			alert.showAndWait();
		} else {
			sectionData = db.getRowData(Integer.parseInt(editSectionId.getText().trim()));
		}
		sectionName = sectionData.get(0);// Course Name
		sectionAbb = sectionData.get(1); // Course Abb
		sectionStartTime = sectionData.get(2); // Course StartTime
		sectionEndTime = sectionData.get(3); // Course EndTime
		days = sectionData.get(4); // Course days
		editSectionName.setText(sectionName);
		editSectionAbb.setText(sectionAbb);
		editSectionStartTime.setText(sectionStartTime);
		editSectionEndTime.setText(sectionEndTime);
		if (days.charAt(0) == '1') {
			editDayMon.setSelected(true);
		}
		if (days.charAt(1) == '1') {
			editDayTues.setSelected(true);
		}
		if (days.charAt(2) == '1') {
			editDayWed.setSelected(true);
		}
		if (days.charAt(3) == '1') {
			editDayThur.setSelected(true);
		}
		if (days.charAt(4) == '1') {
			editDayFri.setSelected(true);
		}
	}

	@FXML
	protected final void loadInstructorAction(ActionEvent event) {
		// loads user selected instructor data from db
		Database db = new Database();
		if (editInstructorId.getText().trim().equals("")
				|| editInstructorId.getText().trim().matches(".*[a-zA-z]+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot use letters for editInstructorId");
			alert.showAndWait();
		} else {
			ArrayList<String> instructorData = db
					.getInstructorData(Integer.parseInt(editInstructorId.getText().trim()));
			this.editInstructorName.setText(instructorData.get(0));
			this.editInstructorDiscipline.setText(instructorData.get(1));
		}
	}

	private class SectionPair {
		private final Section section;
		private final Meeting meeting;

		SectionPair(Section section, Meeting meeting) {
			this.section = section;
			this.meeting = meeting;
		}
	}

	@FXML
	protected final void loadSections(ActionEvent event) {
		// Load Data Button Action
		// Loads all sections and meetings from db to arraylist
		Database db = new Database();
		sections = db.getDataFromDBSection();
		meetings = db.getDataFromDBMeeting();
		// System.out.println(sections.size());
		// System.out.println(meetings.size());
		sectionsTable.getItems().clear();

		for (int i = 0; i < sections.size(); i++) {
			SectionPair sectionPair = new SectionPair(sections.get(i), meetings.get(i));
			sectionsTable.getItems().add(sectionPair);
			/*
			 * System.out.println(sections.get(i).getName() + " " +
			 * sections.get(i).getAbbreviation() + " " + sections.get(i).getId()
			 * + " "); boolean days[] = meetings.get(i).getDays();
			 * System.out.println(meetings.get(i).getStartTime() + " " +
			 * meetings.get(i).getStopTime() + " " + days[0] + days[1] + days[2]
			 * + days[3] + days[4]);
			 */
		}
	}

	public void loadSections() {
		// Load Data Button Action
		// Loads all sections and meetings from db to arraylist
		Database db = new Database();
		sections = db.getDataFromDBSection();
		meetings = db.getDataFromDBMeeting();
		// System.out.println(sections.size());
		// System.out.println(meetings.size());
		sectionsTable.getItems().clear();

		for (int i = 0; i < sections.size(); i++) {
			SectionPair sectionPair = new SectionPair(sections.get(i), meetings.get(i));
			sectionsTable.getItems().add(sectionPair);
			/*
			 * System.out.println(sections.get(i).getName() + " " +
			 * sections.get(i).getAbbreviation() + " " + sections.get(i).getId()
			 * + " "); boolean days[] = meetings.get(i).getDays();
			 * System.out.println(meetings.get(i).getStartTime() + " " +
			 * meetings.get(i).getStopTime() + " " + days[0] + days[1] + days[2]
			 * + days[3] + days[4]);
			 */
		}
	}

	@FXML
	protected final void handleExportAction(ActionEvent event) {
		if (schedule == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Export Error");
			alert.setHeaderText("Schedule Does Not Exist");
			alert.setContentText("You must create a schedule before you can export it.");

			alert.showAndWait();
			return;
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Schedule");
		fileChooser.setInitialFileName("schedule.xml");
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("All Files", "*.*"),
			new FileChooser.ExtensionFilter("XML", "*.xml")
		);

		File file = fileChooser.showSaveDialog(root.getScene().getWindow());
		if (file != null) {
			Exporter.export(file, schedule);
			ScheduleApplication.getStaticHostServices().showDocument(file.getAbsolutePath());
		}
	}

	@FXML
	protected final void createSchedule(ActionEvent event) {

		Schedule temp = new Schedule();
		for (int i = 0; i < sections.size(); i++) {
			temp.addSection(sections.get(i), meetings.get(i));
		}
		// temp.printSections();
		Rule instructorSchedule = new Rule.InstructorSchedule();
		Rule instructorConflict = new Rule.InstructorConflict();
		Rule instructorHours = new Rule.InstructorHours();
		int is = instructorSchedule.getFitness(temp);
		int ic = instructorConflict.getFitness(temp);
		int ih = instructorHours.getFitness(temp);
		if (ic != 0) {
			int sum = is + ic + ih;
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot Create Schedule");
			alert.setContentText("There is a Time Conflict! Check your Sections Your Schedule Score is " + sum);
			alert.showAndWait();
			temp.reset();
		} else {
			int sum = is + ic + ih;
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText("Schedule creation sucess");
			alert.setContentText("Success! Your Schedule Score is " + sum);
			alert.showAndWait();
			schedule = temp;
		}

	}

	@FXML
	protected final void addInstructorAction(ActionEvent event) {
		// adds new instructor and saves it to db
		Database db = new Database();
		if (instructorName.getText().trim().isEmpty() || instructorDiscipline.getText().trim().isEmpty()
				|| instructorId.getText().trim().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Empty Text Box");
			alert.setContentText("Check if you have entered every text box");
			alert.showAndWait();
		} else if (instructorName.getText().trim().matches(".*\\d+.*")
				|| instructorDiscipline.getText().trim().matches(".*\\d+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot use numbers for Instructor Name or Discipline");
			alert.showAndWait();
		} else if (instructorId.getText().trim().matches(".*[a-zA-z]+.*")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Only use numbers for User Id");
			alert.showAndWait();
		} else {
			db.addInstructor(Integer.parseInt(instructorId.getText().trim()), instructorName.getText().trim(),
					instructorDiscipline.getText().trim());
		}
		resetArea();
	}

	@FXML
	protected final void addSectionAction(ActionEvent event) throws Exception {
		// adds new section to db
		boolean checkEmpty = false;
		if (!dayMon.isSelected() && !dayTues.isSelected() && !dayWed.isSelected() && !dayThur.isSelected()
				&& !dayFri.isSelected()) {
			checkEmpty = true;
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Empty Box");
			alert.setContentText("Make sure you check at least one date box!");
			alert.showAndWait();
		}
		if (sectionName.getText().trim().isEmpty() || sectionAbb.getText().trim().isEmpty()
				|| this.startTime.getText().trim().isEmpty() || this.endTime.getText().trim().isEmpty()) {
			// || sectionId.getText().trim().isEmpty()
			checkEmpty = true;
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Empty Text Box");
			alert.setContentText("Check if you have entered every text box");
			alert.showAndWait();
		}
		if (sectionName.getText().matches(".*\\d+.*")) {
			checkEmpty = true;
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot use numbers for Course Name");
			alert.showAndWait();
		}
		if (!sectionAbb.getText().matches(".*[a-zA-z]+.*")) {
			checkEmpty = true;
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Cannot use only numbers for Course Abbreviation");
			alert.showAndWait();
		}
		if (this.startTime.getText().matches(".*[a-zA-z]+.*") || this.endTime.getText().matches(".*[a-zA-z]+.*")) {
			// || sectionId.getText().matches(".*[a-zA-z]+.*")
			Alert alert = new Alert(AlertType.WARNING);
			checkEmpty = true;
			alert.setTitle("Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Check your Time Text Box or Number Text Box");
			alert.showAndWait();
		} else if (!checkEmpty) {
			Database db = new Database();
			// addClass inputs
			String courseName = sectionName.getText();
			String courseAbb = sectionAbb.getText();
			// int courseId = Integer.parseInt(sectionId.getText());
			// addMeeting
			// int meetingId = courseId;
			String startTime = this.startTime.getText() + ":00";
			String endTime = this.endTime.getText() + ":00";
			String days = (dayMon.isSelected() ? "1" : "0") + (dayTues.isSelected() ? "1" : "0")
					+ (dayWed.isSelected() ? "1" : "0") + (dayThur.isSelected() ? "1" : "0")
					+ (dayFri.isSelected() ? "1" : "0");
			int courseId = db.generateId();
			db.addClass(courseId, courseName, courseAbb);
			db.addMeeting(courseId, startTime, endTime, days);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setContentText("Successfully Added a new course");
			alert.showAndWait();
		}
		loadSections();
		resetArea();
	}

	protected void resetArea() {
		// empty out all text box and check box on fx
		editDayMon.setSelected(false);
		editDayTues.setSelected(false);
		editDayWed.setSelected(false);
		editDayThur.setSelected(false);
		editDayFri.setSelected(false);

		dayMon.setSelected(false);
		dayTues.setSelected(false);
		dayWed.setSelected(false);
		dayThur.setSelected(false);
		dayFri.setSelected(false);

		editSectionId.setText("");
		editSectionName.setText("");
		editSectionAbb.setText("");
		editSectionStartTime.setText("");
		editSectionEndTime.setText("");

		sectionName.setText("");
		sectionAbb.setText("");
		startTime.setText("");
		endTime.setText("");

		instructorName.setText("");
		instructorDiscipline.setText("");
		instructorId.setText("");

		editInstructorName.setText("");
		editInstructorDiscipline.setText("");
		editInstructorId.setText("");
	}

}
