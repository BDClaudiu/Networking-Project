import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class Udp_socket implements Runnable {

	
	
	private MulticastSocket socket;
	private boolean running;
	private static String name;
	private Thread thread;
	public String getUserName()
	{	
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter your name: ");
		 name=scanner.nextLine();
		
		return name;
	}
	
	public  void   StoreUserNames(String name)
	{
		ArrayList<String> StoredNames = new ArrayList<String>();
		StoredNames.add(name);
	     System.out.println("# "+StoredNames.size());
		for (String Name : StoredNames) 
		{
	         System.out.println("Name = " + Name);
		}
	}
	
	

	public void bind(int port) throws IOException
	{
		socket= new MulticastSocket(port);
		InetAddress address= InetAddress.getByName("225.4.5.6");
	
		socket.joinGroup(address);
		
	}
	
	public synchronized  void start()
	{	
		 thread= new Thread(this);
		thread.setName(name);
		thread.start();
		
	}
	
	public synchronized void  stop()
	{
		running=false;
		socket.close();
	}
	
	
	
	public void Sender(String Usernameee, InetAddress addressIp, int portN) throws IOException
	{
		
		
	}
	
	
	public void run() 
	{	
		byte [] buffer = new byte [1024];
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
		//getUserName();
		
		running=true;
		while(running)
		{
			
			try 
			{	
				
				socket.receive(packet);
				String msg= new String(buffer,0,packet.getLength());
				System.out.println(msg);
			} 
			catch (IOException e) 
			{	
				//e.printStackTrace();
				break;
			}
		}
	}
	
public void sendTo(String msg) throws IOException
{	
	
	byte [] buffer= msg.getBytes();
	InetAddress address= InetAddress.getByName("225.4.5.6");
	DatagramPacket packet = new DatagramPacket(buffer,buffer.length,address,5555);
	socket.send(packet);
}
}