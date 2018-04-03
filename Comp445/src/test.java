import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class test {

		private boolean running;
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
	
	
	public void receiver(int portNr){	
		MulticastSocket socket=null;
	try
	{
		socket= new MulticastSocket(portNr);
		
		byte[] buffer = new byte [1024];
		DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
		echo("Server socket created. Waiting for incomeing data...");
		running=true;
		while(running)
		{
			socket.receive(incoming);
			byte [] data = incoming.getData();
			String s= new String (data,0,incoming.getLength());
			echo(incoming.getAddress().getHostAddress()+" : "+s);
			byte [] bufferS= s.getBytes();
			DatagramPacket dp= new DatagramPacket(bufferS,bufferS.length,incoming.getAddress(),incoming.getPort());
			socket.send(dp);
			socket.close();
			running=false;
		}
		
	}catch(IOException e){
		System.out.println("Exception "+ e);
	}
};

	
	public String getUserName(InetAddress addressIp, int portN) throws IOException{
		
		MulticastSocket sockett= new MulticastSocket();
		
		sockett.setReuseAddress(true);
		sockett.setBroadcast(true);
	
		BufferedReader cName = new BufferedReader(new InputStreamReader(System.in));
		String userName;
		System.out.println("Enter your name:" );
		userName=(String)cName.readLine();
		byte [] bname = userName.getBytes();
		DatagramPacket dpName= new DatagramPacket(bname,bname.length,addressIp,portN);
		sockett.send(dpName);
		
		byte [] bufferr = new byte [1024];
		DatagramPacket replyy = new DatagramPacket(bufferr,bufferr.length);
		sockett.receive(replyy);
		byte [] dataa = replyy.getData();
		userName= new String(dataa,0,replyy.getLength());
		
		return userName;
		}
	
	public void Sender(String Usernameee, InetAddress addressIp, int portN) throws IOException{

		MulticastSocket socket=null;
		String s;
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			socket=new MulticastSocket();
			socket.setReuseAddress(true);
			socket.setBroadcast(true);
			s=(String)cin.readLine();
			while (true)
			{
				if(s.isEmpty())
					break;
				
				SimpleDateFormat timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				Date date = new Date();
				
				
				byte [] b = s.getBytes();
				DatagramPacket dp= new DatagramPacket(b,b.length,addressIp,portN);
				socket.send(dp);
				
				byte [] buffer = new byte [1024];
				DatagramPacket reply = new DatagramPacket(buffer,buffer.length);
				socket.receive(reply);
				byte [] data = reply.getData();
				s= new String(data,0,reply.getLength());
				
				echo(timeStamp.format(date)+" ["+Usernameee+"]: " + s);
				
				
			}
		}catch(IOException e){System.out.println(e);}
	}
	
	public static void main(String [] args) throws NumberFormatException, IOException{
	int portNumber=7777;
	InetAddress host= InetAddress.getByName("localhost");
	test clientt = new test();
	
clientt.receiver(portNumber);	
	clientt.Sender(clientt.getUserName(host,portNumber ), host, portNumber);
	}
	
}
