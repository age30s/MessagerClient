import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

//	VBox contactslist = new VBox();

	String currUser;

	VBox contacts = new VBox();

	ArrayList<HBox> contactlist = new ArrayList<>();
	ArrayList<String> userlist = new ArrayList<>();

	ListView<String> listItems2 = new ListView<>();
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

				System.out.println( " Callback recieved " + m.usersOnClient.size());

				for(String entry : m.usersOnClient){
					String key = entry;
//					String val = entry.getValue();
//					System.out.println(key + " and " + val);

					TextField d4 = new TextField(key);
					Button chat = new Button("Chat");
					chat.setOnAction(new ButtonClickHandler(key, primaryStage));

					if(!Objects.equals(key, currUser)) {
						if(!userlist.contains(key)) {
							d4.setEditable(false);
							d4.setPrefHeight(40);
							d4.setPrefWidth(250);
							HBox box = new HBox(d4);
							box.setSpacing(20);
							box.getChildren().add(chat);
							contacts.getChildren().add(box);
							userlist.add(key);
						}
//					contactlist.add(box);
//					userlist.add(user.toString());
					}
				}

			});
		});

		clientConnection.printMessage(msg->{
			Platform.runLater(()->{
					listItems2.getItems().add(msg.toString());
			});
		});

		clientConnection.printToEveryone(msg->{
			Platform.runLater(()->{
				globalChatList.getItems().add(msg.toString());
			});
		});


		clientConnection.start();

//		listItems2 = new ListView<String>();

		c1 = new TextField();
		b1 = new Button("Send");
		// when send is pressed -> the message to the server
//		b1.setOnAction(e->{clientConnection.send(clientConnection.message ,""); c1.clear();});

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

		Label reEnterUSer = new Label();
		reEnterUSer.setVisible(false);
		vbox.getChildren().add(reEnterUSer);


		// will be deleted in the future
		// uncomment to see
//     clientBox = new VBox(10, c1,b1,listItems2);
//     borderPane.setBottom(clientBox);

//
		borderPane.setStyle("-fx-background-color: #b9dbf6;");

		loginButton.setOnAction(e -> {
					currUser = user.getText();
					clientConnection.user = user.getText();
					clientConnection.setMessage();
					clientConnection.message.login = true;
					clientConnection.send(clientConnection.message, "","");
					clientConnection.printException(msg->{
						Platform.runLater(()->{
							Message newM = (Message) msg;
							System.out.println("newM.exception = " + newM.exception);
							if(newM.exception == null){
								Scene contscene = contactScreen(primaryStage);
								sceneMap.put("Contactlist",contscene);
							}else{
								System.out.println("Why not here??");
								reEnterUSer.setText(newM.exception);
								reEnterUSer.setFont(Font.font("Georgia",50));
								reEnterUSer.setStyle("-fx-padding: 50 0 0 0;");
								reEnterUSer.setVisible(true);

							}
//							globalChatList.getItems().add(msg.toString());
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
		System.out.println("user is this " + toUser);
		ArrayList<Label> labels = new ArrayList<>();
		BorderPane borderPane = new BorderPane();

		Label groupName = new Label("-----Messaging------" + toUser);
		groupName.setFont(Font.font("Georgia",50));
		groupName.setStyle("-fx-padding: 50 0 0 0;");
		groupName.setAlignment(Pos.TOP_CENTER);

		Label label1 = new Label("Hello how are you??");
		Label label2 = new Label("I am good how are you??");
		TextField user = new TextField("Enter the message you want to send");
		Button send = new Button("Send");
		Button back = new Button("Back");

		VBox vbox = new VBox(back,label1,label2);
		back.setAlignment(Pos.TOP_LEFT);
		label1.setAlignment(Pos.TOP_LEFT);
		label2.setAlignment(Pos.CENTER);

		user.setAlignment(Pos.CENTER);
		send.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);
		Label newlabel = new Label();
		Button everyone = new Button("Message everyone");
		VBox newbox = new VBox(listItems2,user,send,everyone);
//     VBox newlabels = new VBox();
		send.setOnAction(e->{
			String text = user.getText();
			user.clear();
			listItems2.getItems().add("You: "+ text);
			Message sendingMessage = new Message(currUser);
			clientConnection.send(sendingMessage, toUser, text);
//			System.out.println(clientConnection.message.clientUser);
//			clientConnection.send(clientConnection.message, toUser, text);
//        newlabels.getChildren().add(newlabel);
		});
		everyone.setOnAction(e->{
			Message eMessage = new Message(currUser);
			eMessage.isEveryone = true;
			for(int i = 0; i < userlist.size(); i++){
				String text = user.getText();
				Message sendingMessage = new Message(currUser);
				clientConnection.send(sendingMessage, userlist.get(i), text);
			}
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
//		System.out.println("user is this " + toUser);
		ArrayList<Label> labels = new ArrayList<>();
		BorderPane borderPane = new BorderPane();

		Label groupName = new Label("Messging everyone");
		groupName.setFont(Font.font("Georgia",50));
		groupName.setStyle("-fx-padding: 50 0 0 0;");
		groupName.setAlignment(Pos.TOP_CENTER);


		TextField user = new TextField("Enter the message you want to send");
		Button send = new Button("Send");
		Button back = new Button("Back");

		VBox vbox = new VBox(back);
		Label newlabel = new Label();
		Button everyone = new Button("Message everyone");
		VBox newbox = new VBox(globalChatList,user,send,everyone);
//     VBox newlabels = new VBox();
		send.setOnAction(e->{
			String text = user.getText();
			user.clear();
			Message eMessage = new Message(currUser);
			eMessage.isEveryone = true;

//			for(int i = 0; i < userlist.size(); i++){

				clientConnection.send(eMessage, "", text);

//			}
		});
//		everyone.setOnAction(e->{
//			Message eMessage = new Message(currUser);
//			eMessage.isEveryone = true;
//			for(int i = 0; i < userlist.size(); i++){
//				String text = user.getText();
//				Message sendingMessage = new Message(currUser);
//				clientConnection.send(sendingMessage, userlist.get(i), text);
//			}
//		});

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


		TextField c1= new TextField("Harrison Ford");
		c1.setEditable(false);
		c1.setPrefHeight(40);
		c1.setPrefWidth(250);

		HBox firstcontact = new HBox(profbtn, c1);

//		contacts.add(firstcontact);
		VBox contactslist = new VBox();

		contactslist.setSpacing(10);

		Button donebtn = new Button("Done");
		donebtn.setPrefHeight(30);
		donebtn.setPrefWidth(50);

		borderPane.setCenter(contactslist);
		borderPane.setBottom(donebtn);


		Scene contactscene = new Scene(borderPane,700,700);
		primaryStage.setScene(contactscene);
		return contactscene;
	}

	public Scene contactScreen(Stage primaryStage){
		BorderPane borderPane = new BorderPane();

//		for(String s : users){
////			System.out.println(s);
//		}

		System.out.println("currently" + clientConnection.message.usersOnClient.size());

		Button createGrpBut = new Button("Create Group");
		createGrpBut.prefHeight(200);
		createGrpBut.prefWidth(150);

		Button moreOptBut = new Button("More options");
		moreOptBut.prefHeight(200);
		moreOptBut.prefWidth(150);

		Button refresh = new Button("Refresh");
		refresh.prefHeight(200);
		refresh.prefWidth(150);

		Button everyone = new Button("Message everyone");
		everyone.prefHeight(200);
		everyone.prefWidth(150);
		HBox bottom = new HBox(createGrpBut,moreOptBut,refresh,everyone);

//		VBox contacts = new VBox();
//		int track = 0;
//		for(HBox box : contactlist){
//			String usertext = userlist.get(track);
//			Button chat = new Button("Chat");
//			chat.setOnAction(new ButtonClickHandler(usertext, primaryStage));
//			box.getChildren().add(chat);
//			track++;
//		}

		borderPane.setBottom(bottom);
		createGrpBut.setOnAction(e -> {
			groupScreen(primaryStage);
		});

		moreOptBut.setOnAction(e -> {
			MoreOptions(primaryStage);
		});


		refresh.setOnAction(e -> {
			contactScreen(primaryStage);
		});

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