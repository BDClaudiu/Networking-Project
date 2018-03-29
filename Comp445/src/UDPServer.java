import java.io.*;
import java.net.*;

public class UDPServer {

	private DatagramSocket udpSocket;
	private int port;
	
	
	
	public UDPServer(int port) throws SocketException, IOException{
		
		//lots of ports
		this.port=port;
		this.udpSocket= new DatagramSocket(this.port);
	}
	
	private void listen() throws Exception{
		
		System.out.println("server running at "+InetAddress.getLocalHost()+" ***");	
		
		
		String message;
		while (true)
		{
			byte [] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			
			//waits until packet is reveived
			
			udpSocket.receive(packet);
			message= new String(packet.getData()).trim();
			
			System.out.println("Message from:" + packet.getAddress().getHostAddress()+" with message  "+message);
		}
		
	}
	
	
public static void main (String [] args) throws Exception{
	UDPServer client = new UDPServer(Integer.parseInt(args[0]));
	client.listen();
	
}	
	
	
	
	

}
