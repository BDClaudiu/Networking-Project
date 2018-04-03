import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
public class Chat {

	
	
	
	public String getUserName()
	{	
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter your name: ");
		String name=scanner.nextLine();
		
		return name;
	}
	
	public void receiver(int portNr,String nameee) throws IOException
	{	
		Scanner scanner = new Scanner(System.in);
		Udp_socket s = new Udp_socket();
		
		s.bind(portNr);
		//Start receive
		s.start();
		
		
		SimpleDateFormat timeJoined=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date dateJoined = new Date();
		String userJoined;
		userJoined=timeJoined.format(dateJoined)+" "+nameee+" joined";
		System.out.println(userJoined);
		
		while(true)
		{
			SimpleDateFormat timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			Date date = new Date();
			
			String msg=scanner.nextLine();
		
		if(msg.equalsIgnoreCase("/quit"))
		break;
		msg=timeStamp.format(date)+" ["+nameee+"]: "+msg;
		
		s.sendTo(msg);
		}
		
		scanner.close();
		s.stop();
	
		

		SimpleDateFormat timeLeft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date dateLeft = new Date();
		
		
		String userLeft;
		userLeft=timeLeft.format(dateLeft)+" "+nameee+" left";
		System.out.println(userLeft);	
	}
	
	
	public static void main(String[] args) throws IOException {

		
		int port=5555;
		Chat obj = new Chat();
	
		String name=obj.getUserName();
		obj.receiver(port,name);
		
	
		/* For later, private messaging....
		 * peer1 enters source (peer1) and destination (peer2) and peer 2 enters sourse (p2) and destination (p1)
		System.out.print("Source port: ");
		int sourcePort=Integer.parseInt(scanner.nextLine());
		
		System.out.print("Destination IP: ");
		String destinationIp= scanner.nextLine();
			
		System.out.print("Destination Port: ");
		int destinationPort=Integer.parseInt(scanner.nextLine());
		
		DatagramSocket socket= new DatagramSocket(sourcePort);
		InetSocketAddress address = new InetSocketAddress(destinationIp,destinationPort);
		
		*/
		
	

	}
}
