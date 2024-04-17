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
			Message incomingMessage = (Message) in.readObject();

			System.out.println(incomingMessage.clientUser);
			for(int i =0 ; i < incomingMessage.usersOnClient.size(); i++){
				System.out.println("users: " +  incomingMessage.usersOnClient.get(i));
			}
			callback.accept(incomingMessage);

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
