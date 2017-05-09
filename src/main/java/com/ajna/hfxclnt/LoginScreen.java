package com.ajna.hfxclnt;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

import com.ajna.hfxclnt.connection.HfxClient;
import com.ajna.hfxclnt.connection.HfxResponseInterface;
import com.ajna.hfxclnt.model.HfxMessage;
import com.ajna.hfxclnt.model.Order;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginScreen extends Application implements HfxResponseInterface {

	HfxClient mHfxConnection;
	String mUserId;
	boolean isLoggedIn;
	Stage mPrimaryStage;
	Text actiontarget;

	public LoginScreen() {
		mHfxConnection = new HfxClient();
		mHfxConnection.setInterface(this);
	}
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		mPrimaryStage=primaryStage;
		primaryStage.setTitle("HighFreq Exchange");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("HFX Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField("USER1");
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);


		Label hostname = new Label("Host name:");        
		grid.add(hostname, 0, 3);

		TextField hostNameField = new TextField("10.0.1.15");
		grid.add(hostNameField, 1, 3);

		Label portnumb = new Label("Port:");
		grid.add(portnumb, 0, 4);

		TextField portField = new TextField("7777");
		grid.add(portField, 1, 4);


		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 5);

		actiontarget = new Text();
		grid.add(actiontarget, 0, 6);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Logging in...");


				try {
					String hostnm = hostNameField.getText();
					int portnm_int = Integer.parseInt(portField.getText());
					String user = userTextField.getText();

					mHfxConnection.setHost(hostnm);
					mHfxConnection.setPort(portnm_int);
					mHfxConnection.setmUserId(user.trim());

					boolean connected = mHfxConnection.connectToHfx();


					if(connected){


						ByteBuffer bb = HfxMessage.createLoginMessage(user);
						mHfxConnection.sendMessage(bb);
					}

				} catch (Exception ex) {

					ex.printStackTrace();
				}


			}
		});

		Scene scene = new Scene(grid, 300, 275);
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public void launchMainUI() {

		try {
			MainApp ctc = new MainApp(mHfxConnection);
			ctc.start(MainApp.classStage);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mPrimaryStage.close();
	}



	@Override
	public void onHfxUpdate(int type, ByteBuffer msg) {

		switch (type) {
		case  HfxMessage.HFX_MSGTYP_LOGIN_RESPONSE: // login response

			int status = msg.getInt(8);

			byte[] user  = new byte[8]; 
			msg.get(user, 0, 8);
			String userID = new String(user);

			System.out.println("Login Respone status : [" + status + "], UserID: [" + userID + "]");

			if(status == HfxMessage.HFX_STATUS_LOGIN_SUCCESS){

				actiontarget.setText("Logging success, loading UI...");

				mHfxConnection.setLoggedIn(true);

				Platform.runLater(
						() -> {
							launchMainUI( );
						});
			} else {
				actiontarget.setText("Logging failed.");
				System.out.println("User " + mUserId + " log-in attempt failed!");
			}
			break;

		default: //unknown
			System.out.println("Unknown type received: " + type);
			break;
		}

	}
}
