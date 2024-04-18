import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	String user = "default";

	Message message;

	private Consumer<Serializable> callback;

	private Consumer<Serializable> callback2;

	Client(Consumer<Serializable> call){
		callback = call;
	}

	public void printMessage(Consumer<Serializable> call){
		callback2 = call;
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

				System.out.println(" Client recieved " + tempMessage.usersOnClient.size() + " amount of clients");
				callback.accept(tempMessage);
				callback2.accept(tempMessage.clientUser + ": " + tempMessage.message);


//				if(message.firstLog){
////					for(Map.Entry<Integer,String> entry : message.usersOnClient.entrySet()){
////						int key = entry.getKey();
////						String val = entry.getValue();
////						System.out.println(key + " and " + val);
////					}
//					callback.accept(message);
//				}

//				System.out.println("Total clients = " + message.usersOnClient.size());
//				System.out.println("incomingMessage.clientUser: " + message.clientUser);
//				for(Map.Entry<Integer,String> entry : message.usersOnClient.entrySet()){
//					int key = entry.getKey();
//					String val = entry.getValue();
//					System.out.println(key + " and " + val);
//				}

			}
			catch(Exception e) {}
		}

	}

	public void setMessage(){
		System.out.println("USRRRRRR: " + user);
		message = new Message(user);
		System.out.println("STRING" + message.clientUser);
	}

	public void send(Message m1, String recipient, String text) {
//		m1.outMessage = recipient.toString();
		m1.setRecipient(recipient);
		m1.setText(text);

		System.out.println("Client user: " + m1.clientUser + " sending " + m1.message + " Going to: "  + m1.outMessage);

		Message sendingMessage = new Message(m1.clientUser);
		sendingMessage.setText(text);
		sendingMessage.setRecipient(recipient);


		try {

			out.writeObject(sendingMessage);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}