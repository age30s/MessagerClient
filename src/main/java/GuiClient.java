
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{

	
	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;
	
	ListView<String> listItems2;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
				Platform.runLater(()->{listItems2.getItems().add(data.toString());
			});
		});
							
		clientConnection.start();

		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{clientConnection.send(c1.getText()); c1.clear();});
		
		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


		primaryStage.setScene(sceneMap.get("client"));
		primaryStage.setTitle("Client");
		primaryStage.show();
		
	}
	

	
	public Scene createClientGui() {

		return loginScreen();
	}

	public Scene loginScreen(){

		BorderPane borderPane = new BorderPane();
		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-font-size:20");

		loginButton.setPrefWidth(100);
		loginButton.setPrefHeight(40);

		loginButton.setStyle(
				"-fx-background-radius: 5em; ");

		Label welcomelabel = new Label("Welcome to UIchat!");
		welcomelabel.setFont(Font.font("Georgia",50));
		welcomelabel.setStyle("-fx-padding: 50 0 0 0;");

		TextField user = new TextField("Username");
		TextField pass = new TextField("Password");

		user.maxWidth(500);
		pass.maxWidth(500);

		user.minWidth(100);
		pass.minWidth(100);

		VBox vbox = new VBox(user,pass,loginButton);
		user.setAlignment(Pos.CENTER);
		pass.setAlignment(Pos.CENTER);
		loginButton.setAlignment(Pos.CENTER);


		vbox.setSpacing(20);
		borderPane.setCenter(vbox);
		borderPane.setTop(welcomelabel);
		BorderPane.setAlignment(vbox, Pos.CENTER);

		// this is what sends the messages
		// will be deleted in the future
		// uncomment to see
//		clientBox = new VBox(10, c1,b1,listItems2);
//		borderPane.setBottom(clientBox);

//
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		return scene;
	}
	public Scene MoreOptions(){

		BorderPane borderPane = new BorderPane();


		Label options = new Label("More Options:");
		options.setFont(Font.font("Georgia",50));
		options.setStyle("-fx-padding: 50 0 0 0;");

		Button group = new Button("Create Group");
		Button contact = new Button("Create Contact");

		group.maxWidth(500);
		contact.maxWidth(500);

		group.minWidth(100);
		contact.minWidth(100);

		VBox vbox = new VBox(group,contact);
		group.setAlignment(Pos.CENTER);
		contact.setAlignment(Pos.CENTER);

		vbox.setSpacing(20);
		borderPane.setCenter(vbox);
		borderPane.setTop(options);
		BorderPane.setAlignment(vbox, Pos.CENTER);

		// this is what sends the messages
		// will be deleted in the future
		// uncomment to see
//		clientBox = new VBox(10, c1,b1,listItems2);
//		borderPane.setBottom(clientBox);

//
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		return scene;
	}




}
