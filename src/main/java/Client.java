import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;

	String user = "default";

	Message message;

	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
	
		callback = call;
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
			message = (Message)in.readObject();

			System.out.println("Total clients = " + message.usersOnClient.size());
			System.out.println("incomingMessage.clientUser: " + message.clientUser);
			for(int i =0 ; i < message.usersOnClient.size(); i++){
				System.out.println("users: " +  message.usersOnClient.get(i));
			}
			callback.accept(message);

			}
			catch(Exception e) {}
		}
	
    }

	public void setMessage(){
		message = new Message(user);
		System.out.println("STRING" + message.clientUser);
	}

	public void send(Message m1) {
		
		try {
			out.writeObject(m1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
