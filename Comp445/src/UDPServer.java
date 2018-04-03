import java.io.*;
import java.net.*;

public class UDPServer {


	
	public static void echo(String msg){System.out.println(msg);}
	
	public void receiver(int portNr){	
		MulticastSocket socket=null;
	try
	{	
		//InetAddress group= InetAddress.getByName("255.255.255.0");
	//	InetAddress group= InetAddress.getByName("localhost");
	//	System.out.println(group);
		socket= new MulticastSocket(portNr);
		socket.setBroadcast(true);
		//socket.joinGroup(group);
		byte[] buffer = new byte [1024];
		
		echo("Server socket created. Waiting for incomeing data...");
		
		while(true)
		{	
			DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
			socket.receive(incoming);
			byte [] data = incoming.getData();
			String s= new String (data,0,incoming.getLength());
			//echo(incoming.getAddress().getHostAddress()+" : "+s);
			DatagramPacket dp= new DatagramPacket(s.getBytes(),s.getBytes().length,incoming.getAddress(),incoming.getPort());
			socket.send(dp);
		//	socket.leaveGroup(group);
		}
		
	}catch(IOException e){
		System.out.println("Exception "+ e);
	}
};
	
public static void main (String [] args) 
{
	
UDPServer serv= new UDPServer();
serv.receiver(7777);
}	
	
	
	
	

}
