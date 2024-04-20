import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import static jdk.internal.misc.Signal.handle;

public class GuiClient extends Application{


	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;

	String currUser;

	VBox contacts = new VBox();

	ArrayList<String> list = new ArrayList<>();


	ArrayList<HBox> contactlist = new ArrayList<>();
	ArrayList<String> userlist = new ArrayList<>();

	Map<String, ListView<String>> listItems2 = new HashMap<>();

	ListView<String> grpListItem = new ListView<>();
	ListView<String> globalChatList = new ListView<>();
	String messageException;

	ArrayList<String> users = new ArrayList<>();


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(user->{
			Platform.runLater(()->{

				Message m = (Message)user;

				contacts.getChildren().clear();
				userlist.clear();

				for(String entry : m.usersOnClient){
					String key = entry;


					TextField d4 = new TextField(key);
					d4.setAlignment(Pos.CENTER);
					Button chat = new Button("Chat");
					chat.setStyle("-fx-background-radius: 5em; ");
					chat.setOnAction(new ButtonClickHandler(key, primaryStage));

					Image DC = new Image("default-profile-photo.jpg");
					ImageView d0 = new ImageView(DC);
					d0.setFitHeight(55);
					d0.setFitWidth(55);
					d0.setPreserveRatio(true);
					d0.setStyle("-fx-background-radius: 5em; ");

					Button profbtn = new Button();
					profbtn.setGraphic(d0);

					if(!Objects.equals(key, currUser)) {
						if(!userlist.contains(key)) {
							d4.setEditable(false);
							d4.setPrefHeight(60);
							d4.setPrefWidth(350);
							d4.setPadding(new Insets(10, 0 ,0, 0));
							d4.setStyle("-fx-background-radius: 5em; ");
							d4.setFont(Font.font(20));
							HBox box = new HBox(d0,d4);
							box.setAlignment(Pos.CENTER);
							box.setSpacing(20);
							box.getChildren().add(chat);
							contacts.getChildren().add(box);
							userlist.add(key);
							if(!listItems2.containsKey(key)){
								listItems2.put(key, new ListView<String>());
							}
						}

					}
				}

				Map<String,ListView<String>> copied = new HashMap<>();

				copied.putAll(listItems2);

				for(Map.Entry<String,ListView<String>> entry: copied.entrySet()){
					if(!userlist.contains(entry.getKey())){
						listItems2.remove(entry.getKey(),entry.getValue());
					}
				}

			});
		});

		clientConnection.printMessage(msg->{
			Platform.runLater(()->{
					Message m = (Message) msg;

					ListView<String> printHere = listItems2.get(m.clientUser);
					printHere.getItems().add(m.clientUser + ": " + m.message);

			});
		});

		clientConnection.printToEveryone(msg->{
			Platform.runLater(()->{
				globalChatList.getItems().add(msg.toString());
			});
		});

		clientConnection.printToGroup(msg->{
			Platform.runLater(()->{
				grpListItem.getItems().add(msg.toString());
			});
		});


		clientConnection.start();


		c1 = new TextField();
		b1 = new Button("Send");

		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("client",  createClientGui(primaryStage));

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



	public Scene createClientGui(Stage primaryStage) {
		return loginScreen(primaryStage);
	}

	public Scene loginScreen(Stage primaryStage){

		BorderPane borderPane = new BorderPane();
		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-font-size:20");

		loginButton.setPrefWidth(100);
		loginButton.setPrefHeight(40);

		loginButton.setStyle(
				"-fx-background-radius: 5em; ");

		Label welcomelabel = new Label("Welcome to UIchat!");
		welcomelabel.setFont(Font.font("Georgia",50));

		welcomelabel.setPadding(new Insets(100,0,0,0));
		welcomelabel.setAlignment(Pos.CENTER);

		TextField user = new TextField("Username");

		user.setPrefHeight(50);
		user.setPrefWidth(200);
		user.setStyle("-fx-background-radius: 5em; ");

		TextField pass = new TextField("Password");
		pass.setStyle("-fx-background-radius: 5em; ");
		pass.setPrefHeight(50);
		pass.setPrefWidth(200);

		VBox vbox = new VBox(user,pass,loginButton);
		vbox.setStyle("-fx-padding: 50 0 0 0;");
		user.setAlignment(Pos.CENTER);
		pass.setAlignment(Pos.CENTER);
		loginButton.setAlignment(Pos.CENTER);

		vbox.setSpacing(20);
		borderPane.setCenter(vbox);
		borderPane.setTop(welcomelabel);
		BorderPane.setAlignment(vbox, Pos.CENTER);

		Label reEnterUSer = new Label();
		reEnterUSer.setVisible(false);
		vbox.getChildren().add(reEnterUSer);

		borderPane.setStyle("-fx-background-color: #b9dbf6;");

		loginButton.setOnAction(e -> {
					currUser = user.getText();
					clientConnection.user = user.getText();
					clientConnection.setMessage();
					clientConnection.message.login = true;
					clientConnection.send(clientConnection.message, "","", null);
					clientConnection.printException(msg->{
						Platform.runLater(()->{
							Message newM = (Message) msg;
							if(newM.exception == null && newM.login){
								Scene contscene = contactScreen(primaryStage);
								sceneMap.put("Contactlist",contscene);
							}
							else if(Objects.equals(newM.exception, "closed")){
								Scene contscene = contactScreen(primaryStage);
								sceneMap.put("Contactlist",contscene);
							}
							else if(newM.exception != null){
								reEnterUSer.setText(newM.exception);
								reEnterUSer.setFont(Font.font("Georgia",20));
								reEnterUSer.setStyle("-fx-padding: 50 0 0 0;");
								reEnterUSer.setVisible(true);
							}
						});
					});
				}
		);

		Scene scene = new Scene(borderPane, 700,700);

		primaryStage.setScene(scene);

		return scene;
	}
	public Scene MoreOptions(Stage primaryStage){
		BorderPane borderPane = new BorderPane();


		Label options = new Label("More Options:");
		options.setFont(Font.font("Georgia",50));
		options.setStyle("-fx-padding: 50 0 0 0;");

		Button group = new Button("Create Group");
		Button contact = new Button("Create Contact");

		group.setOnAction(e ->{
			groupScreen(primaryStage);
		});

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
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		primaryStage.setScene(scene);
		return scene;
	}
	public Scene groupPage(Stage primaryStage, String toUser){

		ArrayList<Label> labels = new ArrayList<>();
		BorderPane borderPane = new BorderPane();

		Label groupName = new Label("-----Messaging------" + toUser);
		groupName.setFont(Font.font("Georgia",50));
		groupName.setStyle("-fx-padding: 50 0 0 0;");
		groupName.setAlignment(Pos.TOP_CENTER);

		TextField user = new TextField("Enter the message you want to send");
		Button send = new Button("Send");
		Button back = new Button("Back");


		back.setAlignment(Pos.TOP_LEFT);
		back.setOnAction((e->{
			contactScreen(primaryStage);
		}));

		user.setAlignment(Pos.CENTER);
		send.setAlignment(Pos.CENTER);

		ListView<String> currView = listItems2.get(toUser);

		VBox newbox = new VBox(currView,user,send,back);

		send.setOnAction(e->{
			String text = user.getText();
			user.clear();
			currView.getItems().add("You: "+ text);
			Message sendingMessage = new Message(currUser);
			clientConnection.send(sendingMessage, toUser, text, null);
		});

		newbox.getChildren().addAll(labels);


		borderPane.setCenter(newbox);
		borderPane.setTop(groupName);
		BorderPane.setAlignment(newbox, Pos.CENTER);
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		primaryStage.setScene(scene);

		return scene;
	}

	public Scene groupChat(Stage primaryStage, String toUser){

		ArrayList<Label> labels = new ArrayList<>();
		BorderPane borderPane = new BorderPane();

		Label groupName = new Label("-----Messaging------" + toUser);
		groupName.setFont(Font.font("Georgia",50));
		groupName.setStyle("-fx-padding: 50 0 0 0;");
		groupName.setAlignment(Pos.TOP_CENTER);

		TextField user = new TextField("Enter the message you want to send");
		Button send = new Button("Send");
		Button back = new Button("Back");


		back.setAlignment(Pos.TOP_LEFT);


		user.setAlignment(Pos.CENTER);
		send.setAlignment(Pos.CENTER);

		VBox newbox = new VBox(grpListItem,user,send);

		send.setOnAction(e->{
			String text = user.getText();
			user.clear();
			grpListItem.getItems().add("You: "+ text);
			Message sendingMessage = new Message(currUser);
			clientConnection.send(sendingMessage, toUser, text, list);
		});

		newbox.getChildren().addAll(labels);


		borderPane.setCenter(newbox);
		borderPane.setTop(groupName);
		BorderPane.setAlignment(newbox, Pos.CENTER);
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		primaryStage.setScene(scene);

		return scene;
	}

	public Scene everyOneScene(Stage primaryStage, String toUser){
		ArrayList<Label> labels = new ArrayList<>();
		BorderPane borderPane = new BorderPane();

		Label groupName = new Label("Messaging everyone");
		groupName.setFont(Font.font("Georgia",50));
		groupName.setStyle("-fx-padding: 50 0 0 0;");
		groupName.setAlignment(Pos.TOP_CENTER);


		TextField user = new TextField("Enter the message you want to send");
		Button send = new Button("Send");


		Button back = new Button("Back");
		HBox thing = new HBox(back,send);
		VBox newbox = new VBox(globalChatList,user,thing);

		send.setOnAction(e->{
			String text = user.getText();
			user.clear();
			Message eMessage = new Message(currUser);
			eMessage.isEveryone = true;


				clientConnection.send(eMessage, "", text, null);

		});

		back.setOnAction((e->{
			contactScreen(primaryStage);
		}));

		newbox.getChildren().addAll(labels);

		borderPane.setCenter(newbox);
		borderPane.setTop(groupName);
		BorderPane.setAlignment(newbox, Pos.CENTER);
		borderPane.setStyle("-fx-background-color: #b9dbf6;");


		Scene scene = new Scene(borderPane, 700,700);

		primaryStage.setScene(scene);

		return scene;
	}
	public Scene groupScreen(Stage primaryStage){

		BorderPane borderPane = new BorderPane();

		borderPane.setStyle("-fx-background-color: #b9dbf6;");

		Image DC = new Image("default-profile-photo.jpg");
		ImageView d0 = new ImageView(DC);
		d0.setFitHeight(100);
		d0.setFitWidth(100);
		d0.setPreserveRatio(true);

		Button profbtn = new Button();
		profbtn.setGraphic(d0);



		Button donebtn = new Button("Done");
		donebtn.setPrefHeight(30);
		donebtn.setPrefWidth(50);


		VBox copiedContacts = new VBox();
		int i;

		for(Node node : contacts.getChildren()){
			if (node instanceof HBox) {
				HBox hBox = (HBox) node;

				HBox copiedHbox = new HBox();
				i = 0;
				copiedHbox.setSpacing(20);
				for (Node child : hBox.getChildren()) {

					if (i == 0){
						TextField clientName = (TextField) child;

						TextField newField = new TextField(clientName.getText());
						newField.setPrefHeight(40);
						newField.setPrefWidth(250);
						copiedHbox.getChildren().add(newField);
					}
					i++;
				}
				copiedContacts.getChildren().add(copiedHbox);
			}
		}

		for (Node node : copiedContacts.getChildren()) {
			if (node instanceof HBox) {
				HBox hBox = (HBox) node;
				Button add = new Button("Add to group");
				hBox.getChildren().add(add);
				add.setOnAction(e -> {
					add.setDisable(true);
					int u = 0;
					for (Node child : hBox.getChildren()) {
						if (u == 0){
							TextField clientName = (TextField) child;
							String name = clientName.getText();
							list.add(name);
						}
						u++;
					}
				});
			}
		}

		donebtn.setOnAction(e -> {

			groupChat(primaryStage, "group");
		});

		borderPane.setCenter(copiedContacts);
		BorderPane.setAlignment(copiedContacts,Pos.CENTER);

		borderPane.setBottom(donebtn);

		Scene addScene = new Scene(borderPane,700,700);
		primaryStage.setScene(addScene);
		return addScene;
	}

	public Scene contactScreen(Stage primaryStage){
		BorderPane borderPane = new BorderPane();

		Button everyone = new Button("Message everyone");
		everyone.prefHeight(200);
		everyone.prefWidth(150);
		HBox bottom = new HBox(everyone);

		bottom.setSpacing(15);

		bottom.setAlignment(Pos.CENTER);
		borderPane.setBottom(bottom);


		everyone.setPrefWidth(150);
		everyone.setPrefHeight(30);

		everyone.setStyle(
				"-fx-background-radius: 5em; ");

		everyone.setOnAction(e -> {
			everyOneScene(primaryStage,"");
		});

		borderPane.setCenter(contacts);
		BorderPane.setAlignment(contacts,Pos.CENTER);

		borderPane.setStyle("-fx-background-color: #b9dbf6;");

		Scene contactScene = new Scene(borderPane, 700,700);
		primaryStage.setScene(contactScene);

		return contactScene;
	}

	class ButtonClickHandler implements EventHandler<ActionEvent> {
		private String user;

		Stage primaryStage;

		public ButtonClickHandler(String user, Stage primaryStage) {
			this.user = user;
			this.primaryStage = primaryStage;
		}
		@Override
		public void handle(ActionEvent event) {
			// Perform different actions based on the index of the button
			Scene s = groupPage(primaryStage, user);
		}
	}

}