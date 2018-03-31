import java.io.*;
import java.net.*;

public class UDPServer {


	
	public static void echo(String msg){System.out.println(msg);}
	
	public void receiver(int portNr){	
		MulticastSocket socket=null;
	try
	{
		socket= new MulticastSocket(portNr);
		
		byte[] buffer = new byte [1024];
		DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
		echo("Server socket created. Waiting for incomeing data...");
		
		while(true)
		{
			socket.receive(incoming);
			byte [] data = incoming.getData();
			String s= new String (data,0,incoming.getLength());
			//echo(incoming.getAddress().getHostAddress()+" : "+s);
			DatagramPacket dp= new DatagramPacket(s.getBytes(),s.getBytes().length,incoming.getAddress(),incoming.getPort());
			socket.send(dp);
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
