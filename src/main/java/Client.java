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
				System.out.println(tempMessage.clientUser + " " + tempMessage.message + " " + tempMessage.outMessage);

//				message.clientUser = tempMessage.clientUser;


				message.message = tempMessage.message;
				System.out.println("going to else: " + message.message);
				message.outMessage = tempMessage.outMessage;

				if (message.message != null){
					callback2.accept(message.clientUser + ":" + message.message);
				}


//				if(message.firstLog){
////					for(Map.Entry<Integer,String> entry : message.usersOnClient.entrySet()){
////						int key = entry.getKey();
////						String val = entry.getValue();
////						System.out.println(key + " and " + val);
////					}
//					callback.accept(message);
//				}

				System.out.println("Total clients = " + message.usersOnClient.size());
				System.out.println("incomingMessage.clientUser: " + message.clientUser);
				for(Map.Entry<Integer,String> entry : message.usersOnClient.entrySet()){
					int key = entry.getKey();
					String val = entry.getValue();
					System.out.println(key + " and " + val);
				}
				callback.accept(tempMessage);
			}
			catch(Exception e) {}
		}

	}

	public void setMessage(){
		System.out.println("USRRRRRR: " + user);
		message = new Message(user);
		System.out.println("STRING" + message.clientUser);
	}

	public void send(Message m1) {
		System.out.println("Client user: " + m1.clientUser + "Going to: "  + m1.outMessage);
		try {

			out.writeObject(m1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}