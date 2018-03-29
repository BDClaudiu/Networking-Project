/*
 * COMP 445 Data Communication and Computer Networks 
 * Assignment 3
 * Peer-to-Peer application based on UDP
 * Bacisor Claudiu ID 27735332
 * Sharfiee Marzie 40016801
 */

import java.io.*;
import java.net.*;
import java.util.*;
public class PeerToPeer {

	
	
	
	
	public void Sender(int port) throws IOException
	{	
		//-------------------Server------------------
			DatagramSocket udpSocket = new DatagramSocket(port);
			System.out.println("---Server running at "+ InetAddress.getLocalHost()+" ---");
			String msg;
		//-------------------------------Client-----------------------------------------
			
			//read stuff 
			BufferedReader rrr = new BufferedReader (new InputStreamReader(System.in));
			String readd= rrr.readLine();
			System.out.println("You typed: "+readd);
			
		while (true)
		{
			byte[] buff = new byte[1024];
			DatagramPacket packet = new DatagramPacket (buff, buff.length);
			
			udpSocket.receive(packet);
			msg=new String (packet.getData()).trim();
			System.out.println("Message from "+packet.getAddress().getHostAddress()+" : "+ msg);
		}
	}
	
	public void receiver (String destinationAddr, int port) throws IOException, SocketException, UnknownHostException
	{
		
		 InetAddress serverAddress= InetAddress.getByName(destinationAddr);
		 DatagramSocket udpSocket= new DatagramSocket(port);
		 Scanner scanner = new Scanner (System.in);
		 
		 String in;
		 while(true)
		 {
			 in=scanner.nextLine();
			 DatagramPacket p = new DatagramPacket(in.getBytes(),in.getBytes().length,serverAddress,port);
			 udpSocket.send(p);
		 }
	}
	
	public static void main(String [] args) throws IOException
	{   
		int portt=7071;
		PeerToPeer peerObj = new PeerToPeer();
		peerObj.Sender(portt);
		String addr="localhost";
		peerObj.receiver(addr, portt);
	}	
}

