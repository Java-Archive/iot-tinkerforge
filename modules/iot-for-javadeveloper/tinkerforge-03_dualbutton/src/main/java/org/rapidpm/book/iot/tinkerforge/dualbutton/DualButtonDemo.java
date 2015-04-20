package org.rapidpm.book.iot.tinkerforge.dualbutton;

import com.tinkerforge.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static com.tinkerforge.BrickletDualButton.*;
import static javafx.application.Platform.runLater;

/**
 * Created by Sven Ruppert on 30.12.2014.
 */
public class DualButtonDemo extends Application {
  private static final String host = "localhost";
  private static final int port = 4223;
  private static final String UID = "mMV";
  public static String newline = System.getProperty("line.separator");

  private static final IPConnection ipcon = new IPConnection();
  private static final BrickletDualButton db = new BrickletDualButton(UID, ipcon);

  public static void main(String[] args) {
    try {
      ipcon.connect(host, port);
      db.addStateChangedListener(
          (buttonL, buttonR, ledL, ledR) -> runLater(() -> {
            if (buttonL == BUTTON_STATE_PRESSED) {
              setMsg("Left button pressed");
              activateLeftButton();
            }
            if (buttonL == BUTTON_STATE_RELEASED) {
              setMsg("Left button released");
            }
            if (buttonR == BUTTON_STATE_PRESSED) {
              setMsg("Right button pressed");
              activateRightButton();
            }
            if (buttonR == BUTTON_STATE_RELEASED) {
              setMsg("Right button released");
            }
          }));
      db.setLEDState(LED_STATE_ON, LED_STATE_ON);
    } catch (IOException
        | AlreadyConnectedException
        | TimeoutException
        | NotConnectedException e) {
      e.printStackTrace();
    }


    launch(args);
  }

  private static Button bL;
  private static Button bR;
  private static TextArea tx;

  @Override
  public void start(Stage stage) {
    stage.setTitle("Button TinkerForge Sample");

    final VBox vBox = new VBox();
    setAnchorZero(vBox);
    final HBox hBox = new HBox();
    setAnchorZero(hBox);
    bL = new Button("LED links");
    bR = new Button("LED rechts");

    setAnchorZero(bL);
    setAnchorZero(bR);

    bL.setOnAction(actionEvent -> activateLeftButton());
    bR.setOnAction(actionEvent -> activateRightButton());

    hBox.getChildren().add(bL);
    hBox.getChildren().add(bR);

    vBox.getChildren().add(hBox);

    tx = new TextArea();
    setAnchorZero(tx);
    vBox.getChildren().add(tx);

    Scene scene = new Scene(vBox);
    stage.setScene(scene);
    stage.show();
  }

  private void setAnchorZero(final Node node) {
    AnchorPane.setBottomAnchor(node, 0.0);
    AnchorPane.setLeftAnchor(node, 0.0);
    AnchorPane.setRightAnchor(node, 0.0);
    AnchorPane.setTopAnchor(node, 0.0);
  }

  private static void setMsg(String msg) {
    Platform.runLater(() -> tx.setText(msg.concat(newline).concat(tx.getText())));

  }

  private static void activateRightButton() {
    try {
      db.setLEDState(LED_STATE_OFF, LED_STATE_ON);
      Platform.runLater(() -> {
        bL.setText("InActive");
        bR.setText("Active");
      });
    } catch (TimeoutException | NotConnectedException e) {
      e.printStackTrace();
    }
  }

  private static void activateLeftButton() {
    try {
      db.setLEDState(LED_STATE_ON, LED_STATE_OFF);
      Platform.runLater(() -> {
        bL.setText("Active");
        bR.setText("InActive");
      });
    } catch (TimeoutException | NotConnectedException e) {
      e.printStackTrace();
    }
  }
}