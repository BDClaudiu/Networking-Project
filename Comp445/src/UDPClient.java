import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;



public class UDPClient {

		
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
	
	
	public String getUserName(InetAddress addressIp, int portN) throws IOException
	{
		
		MulticastSocket sockett= new MulticastSocket();	
	
		sockett.setBroadcast(true);
	
		BufferedReader cName = new BufferedReader(new InputStreamReader(System.in));
		String userName;
		System.out.print("Enter your name:" );
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
	
	public void Sender(String Usernameee, InetAddress addressIp, int portN) throws IOException
	{
		MulticastSocket socket=null;
		String s;
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			socket=new MulticastSocket();
	
			socket.setBroadcast(true);
			
			while (true)
			{
				SimpleDateFormat timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				Date date = new Date();
				
				s=(String)cin.readLine();
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
	//InetAddress host= InetAddress.getByName("255.255.255.0");
	InetAddress host= InetAddress.getByName("localhost");
	UDPClient clientt = new UDPClient();
	
	clientt.Sender(clientt.getUserName(host,portNumber ), host, portNumber);
	}
	
}
