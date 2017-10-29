package edu.psu.teamone.main;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScheduleApplication extends Application {

	private static HostServices hostServices;

	public static HostServices getStaticHostServices() {
		return hostServices;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public final void start(Stage primaryStage) throws IOException {
		hostServices = this.getHostServices();

		final Parent root = FXMLLoader.load(getClass().getResource("main.fxml"),
				ResourceBundle.getBundle("edu.psu.teamone.main.messages"));

		primaryStage.setTitle("Schedule Application");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
