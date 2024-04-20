import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	String user = "default";

	Message message;

	private Consumer<Serializable> callback;

	private Consumer<Serializable> callback2;
	private Consumer<Serializable> callback3;
	private Consumer<Serializable> callback4;
	private Consumer<Serializable>  callback5;

	Client(Consumer<Serializable> call){
		callback = call;
	}

	public void printMessage(Consumer<Serializable> call){
		callback2 = call;
	}
	public void printToEveryone(Consumer<Serializable> call){
		callback3 = call;
	}
	public void printException(Consumer<Serializable> call){
		callback4 = call;
	}

	public void printToGroup(Consumer<Serializable> call){
		callback5 = call;
	}

	public void run() {

		try {
			socketClient = new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		while(true) {

			try {
				Message tempMessage  = (Message)in.readObject();

				if(tempMessage.grpMsg){
					callback5.accept(tempMessage.message);
				}

				if(tempMessage.login || Objects.equals(tempMessage.exception, "closed")) {
					callback4.accept(tempMessage);
				}

				callback.accept(tempMessage);

				if(tempMessage.message != null && tempMessage.isEveryone == false){
					callback2.accept(tempMessage);
				}

				if(tempMessage.message != null && tempMessage.isEveryone == true){
					callback3.accept(tempMessage.clientUser + ": " + tempMessage.message);
				}


			}
			catch(Exception e) {}
		}

	}

	public void setMessage(){

		message = new Message(user);

	}

	public void send(Message m1, String recipient, String text, ArrayList<String> list) {
		m1.setRecipient(recipient);
		m1.setText(text);

		Message sendingMessage = new Message(m1.clientUser);
		if(m1.login){
			sendingMessage.login = true;
		}

		if(Objects.equals(recipient, "group") && list != null){
			sendingMessage.grpMsg = true;
			sendingMessage.grpList = list;
		}

		sendingMessage.setText(text);
		sendingMessage.setRecipient(recipient);
		sendingMessage.exception = null;

		if (m1.isEveryone == true){
			sendingMessage.isEveryone = true;
		}

		try {
			out.writeObject(sendingMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}