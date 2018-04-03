import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Udp_socket implements Runnable {

	
	
	private MulticastSocket socket;
	private boolean running;
	
	public void bind(int port) throws IOException
	{
		socket= new MulticastSocket(port);
		InetAddress address= InetAddress.getByName("225.4.5.6");
		socket.joinGroup(address);
	}
	
	public void start()
	{
		Thread thread= new Thread(this);
		thread.start();
	}
	
	public void stop()
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