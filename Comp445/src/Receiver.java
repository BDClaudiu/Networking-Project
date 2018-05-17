import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.*;

/*
 * COMP 445 
 * Assignment 3
 * Peer to Peer Chat Application 
 * Bacisor Claudiu ID: 27735332
 * Marzie Shafiee ID:  40016801 
 * 
*/

public class Receiver extends Thread {
	
	private TreeMap<String, Peer> connectedUsers;
	private Peer currentUser;
	private BufferedReader inFromUser;
	private byte[] sendData, receiveData;
	private DatagramPacket receivePacket;
	private DatagramSocket serverSocket;
	private boolean active;
	
	

	public synchronized void udp_socket() {
		try {
			this.serverSocket = new DatagramSocket(9000);
			this.serverSocket.setBroadcast(true);
			this.sendData = new byte[1024];
			this.receiveData = new byte[1024];
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized String readUserNameFromInput() 
	{
		// Read a username from the console
		String user = null;
		try {
				System.out.print("Enter your username: ");
				this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
				user = inFromUser.readLine();
			} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

	public synchronized void sendTo(String sendData, InetAddress IPAddress, int port) {
	
		try 
		{
			this.sendData = sendData.getBytes();
			DatagramPacket 	sendPacket = new DatagramPacket(this.sendData, this.sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public synchronized DatagramPacket receiveMessage() 
	{
		 
		this.receiveData = new byte[1024];
		try {
			DatagramPacket	receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receivePacket;
	}
	
	public Receiver() 
	{
		connectedUsers = new TreeMap<String, Peer>();
		this.active = true;
		this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
	}

	public synchronized void run() 
	{
		udp_socket();
		try {
			while (active) 
			{
				receivePacket = receiveMessage();
				build_message(receivePacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public synchronized void build_command(String respose,String user,String cmd,String msg,String channel, Date date,SimpleDateFormat ft,DatagramPacket p) throws UnknownHostException
	{

		String response = new String(p.getData());
		switch (cmd) {
		case "JOIN":
			System.out.println(ft.format(date) + " " + user + " Joined!");
			if (p.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
				this.currentUser = new Peer(user, channel);
			}
			connectedUsers.put(p.getAddress().getHostAddress(), new Peer(user, channel));
			sendTo("username:" + this.currentUser.getUsername() + "\ncommand:PING\nmessage:\nchannel:" + channel
					+ "\n\n", InetAddress.getByName("255.255.255.255"), 9000);
			break;
		case "PING":
			connectedUsers.put(p.getAddress().getHostAddress(), new Peer(user, channel));
			sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:\nchannel:" + channel + "\n\n", p.getAddress(), 9888);
			
			break;
		case "WHO":
			String userNamesWho="";
			for(Entry<String, Peer> entry : connectedUsers.entrySet()) 
				{
				  String key = entry.getKey();
				  Peer value = entry.getValue();

				  System.out.println(key + " => " + value.getUsername());
				  userNamesWho+=userNamesWho+""+value.getUsername()+"/ ";
				}
			System.out.println(userNamesWho);
			sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:\nchannel:" + channel + "\n\n", p.getAddress(), 9888);
			
			break;
		case "LEAVE":
			connectedUsers.remove(p.getAddress().getHostAddress());
			System.out.println(ft.format(date) + " " + user + " has left!");
			break;
		case "PRIVATE-TALK":
			String privateUser = response.split("\n")[4].split(":")[1];
			Peer pUser;
			String privateAddress = "127.0.0.1";
			String privateMessage;
			
			TreeMap<String, Peer> dubliUsers = new TreeMap<String, Peer>();

			// Find the matching user in the connected users collection
			for (String ip : connectedUsers.keySet()) 
			{
				pUser = connectedUsers.get(ip);
				if (pUser.getUsername().equals(privateUser)) 
				{
					dubliUsers.put(ip, pUser);
				}
			}

			// If no matches were found
			if (dubliUsers.size() == 0) 
			{
				System.out.println(ft.format(date) + " User does not exist! ");
			}
			else if (dubliUsers.size() > 1) 
			{// Case where multiples users were found
				System.out.println("Multiple users found!! ");
				for (String ip : dubliUsers.keySet()) 
				{
					System.out.println(dubliUsers.get(ip).getUsername() + " : " + ip);
				}
				
				Scanner scanner = new Scanner(System.in);
				System.out.println("Please choose IP: ");
				privateAddress = scanner.nextLine();
				while (dubliUsers.get(privateAddress) == null) 
				{
					System.out.println("Please a valid one: ");
					privateAddress = scanner.nextLine();
					
				}
				System.out.print("<Private message> "+ privateUser +": ");
				privateMessage = scanner.nextLine();
				sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:" + privateMessage
						+ "\nchannel:" + channel + "\n\n", InetAddress.getByName(privateAddress), 9000);
			} 
			else 
			{
				Scanner scanner = new Scanner(System.in);
				System.out.print("<Private message> "+ privateUser +": ");
		
				// String ipPrivateReceiver=" ";		
				privateMessage = scanner.nextLine();
					
				//LOOPING THROUGHT THE LIST OF PRIVATE-USERS AND GETTING THE ID, IN THIS CASE, THERES ONLY 1 USER
				for (String ip : dubliUsers.keySet()) 
				{	//THIS System.out.print IS JUST FOR DEBUGING, DELETE AFTER PRIVATE MESSAGE WORKS
					channel="private ";
					System.out.println("user: "+privateUser+"ip: "+ip);
					
					sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:" + privateMessage
							+ "\nchannel:" + channel + "\n\n", InetAddress.getByName(ip), 9000);
				}		
			}
			break;
		case "CHANNEL":
			Peer u = connectedUsers.get(p.getAddress().getLocalHost().getHostAddress());
			System.out.println(ft.format(date) + " Switched to channel " + channel);
			if (u != null) 
			{
				if (!u.getChannel().equals(channel)) 
				{
					u.setChannel(channel);
					this.currentUser.setChannel(channel);
					connectedUsers.put(p.getAddress().getLocalHost().getHostAddress(), u);
				}
			}
			else 
			{
				connectedUsers.put(p.getAddress().getHostAddress(), new Peer(user, channel));
			}
			sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:\nchannel:" + channel + "\n\n", p.getAddress(), 9888);
			break;
		case "QUIT":
			if (user.equals(this.currentUser.getUsername())) 
			{
				System.out.println("Bye Now!");
				active = false;
			}
			break;
		default:
			if (this.currentUser.getChannel().equals(channel)) 
			{
				System.out.println(ft.format(date) + " [" + user + " #" + channel + "]: " + msg);
			}
			sendTo("username:" + this.currentUser.getUsername() + "\ncommand:TALK\nmessage:\nchannel:" + channel + "\n\n", p.getAddress(), 9888);
			break;
		}
		
	}
	
	public synchronized void build_message(DatagramPacket pa) throws IOException 
	{
		
		String responseIN = new String(pa.getData());
		String userIn = responseIN.split("\n")[0].split(":")[1];
		String cmdIn = responseIN.split("\n")[1].split(":")[1];
		String msgIn = "";
		String channelIn = responseIN.split("\n")[3].split(":")[1];
		
		if (responseIN.split("\n")[2].split(":").length > 1) 
		{
			msgIn = responseIN.split("\n")[2].split(":")[1];
		}

		SimpleDateFormat ftIN = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
		Date dateIN = new Date();

		build_command(responseIN,userIn,cmdIn,msgIn,channelIn, dateIN,ftIN,pa);		
	}
	
	public class Peer {
		private String channel;
		private String username;
		
		public Peer(String username, String channel) 
		{	this.channel = channel;
			this.username = username;
		
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}
	}
	
}