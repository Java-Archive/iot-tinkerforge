package org.rapidpm.book.iot.tinkerforge.weatherfx.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Sven Ruppert on 15.04.14.
 */
public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    primaryStage.setTitle("TinkerForge Weatherstation");
    primaryStage.setScene(new Scene(root, 1024, 275));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
