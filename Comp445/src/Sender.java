import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
 * COMP 445 
 * Assignment 3
 * Peer to Peer Chat Applicationn
 * 
 * Bacisor Claudiu ID: 27735332
 * Marzie Shafiee ID:  40016801 
 * 
*/


public class Sender extends Thread {
	
	private String channel;
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private String username;
	byte[] sendData, receiveData; // holds send data the client receives
	BufferedReader userInput;
	private boolean activeee;

	public Sender() 
	{
		this.username = readUserNameFromInput();// Prompts the user to input his username
		this.channel = "general";
	}

	public synchronized String readUserNameFromInput() 
	{
		// Read a username from the console
		String user = null;
		try {
			System.out.print("Enter your username: ");
			this.userInput = new BufferedReader(new InputStreamReader(System.in));
			user = userInput.readLine();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return user;
	}

	// UDP is a connectionless protocol, it does not use any connect, listen or accept function.
	public synchronized void udp_socket() 
	{
		try {
			this.clientSocket = new DatagramSocket(9888);
			this.receiveData = new byte[1024];
			this.sendData = new byte[1024];
			this.clientSocket.setBroadcast(true);
			this.IPAddress = InetAddress.getByName("255.255.255.255");// Broadcast IP
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	//Build message is a function that you must write to build an application message from the user name and message, and command	
	public synchronized String build_message(String command, String message, String username) {
		String sendM="user:" + this.username + "\ncommand:" + command + "\nmessage:" + message + "\nchannel:#" + this.channel+ "\n\n";
		
		return  sendM;
	}
	
	
	public synchronized String receiveMessage() {
		DatagramPacket receivePacket = null;
		this.receiveData = new byte[1024];
		try
		{
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return new String(receivePacket.getData());
	}
	
	//sendto Function
	public synchronized void sendto(String sendData, InetAddress IPAddress) {
		
		DatagramPacket sendPacket = null;
		this.sendData = sendData.getBytes();
		
		try {
			sendPacket = new DatagramPacket(this.sendData, this.sendData.length, IPAddress, 9000);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void parse_and_send_message()
		
		{
				try {
					
					String responseReceived = receiveMessage();								
					String console_input_commands;
					String privateUser = "n/a";
					String user = responseReceived.split("\n")[0].split(":")[1];
					String cmdReceived = responseReceived.split("\n")[1].split(":")[1];
					
					if (cmdReceived.equals("TALK")) 
					{
						console_input_commands = userInput.readLine();
						// Process the user input and send the appropriate message
						if (console_input_commands.contains("/channel")) {
							if (console_input_commands.split(" ").length > 1) {
								this.channel = console_input_commands.split(" ")[1];
							}
							console_input_commands = "/channel";
						} else if (console_input_commands.contains("/private")) {
							if (console_input_commands.split(" ").length > 1) {
								privateUser = console_input_commands.split(" ")[1];
							}
							console_input_commands = "/private";
						}
						
						switch (console_input_commands) {
						case "/who":
							sendto("user:" + this.username + "\ncommand:WHO\nmessage:none\nchannel:" + this.channel
									+ "\n\n", InetAddress.getByName("localhost"));
							break;
						case "/leave":
							sendto("user:" + this.username + "\ncommand:LEAVE\nmessage:none\nchannel:" + this.channel
									+ "\n\n", InetAddress.getByName("255.255.255.255"));
							sendto("user:" + this.username + "\ncommand:QUIT\nmessage:none\nchannel:" + this.channel
									+ "\n\n", InetAddress.getByName("localhost"));
							activeee = false;
							break;
						case "/private":
							sendto("user:" + this.username + "\ncommand:PRIVATE-TALK\nmessage:none\nchannel:"
							+ "private" + "\nprivateUser:" + privateUser + "\n\n", InetAddress.getByName("localhost"));
							
							break;
						case "/channel":
							sendto("user:" + this.username + "\ncommand:CHANNEL\nmessage:none\nchannel:" + this.channel
									+ "\n\n", InetAddress.getByName("localhost"));
							break;
						
							//Default = talk command 
						default:
							sendto("user:" + this.username + "\ncommand:TALK\nmessage:" + console_input_commands + "\nchannel:"
									+ this.channel + "\n\n", InetAddress.getByName("255.255.255.255"));
							break;
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
		
	public synchronized void run() {

			//UDP socket
			udp_socket();
		
			//The first message that is send as soon as the server run is the input your name message + the join command
			sendto("user:" + this.username + "\ncommand:JOIN\nmessage:none\nchannel:" + this.channel + "\n\n",this.IPAddress);

			activeee = true;
			//while true, parse and send then message
			while (activeee)
			{
				this.parse_and_send_message();
			}
		}
	
	public static void main(String[] args) throws IOException {
		
		Receiver serverStart = new Receiver();
        Sender clientStart = new Sender();
        
        serverStart.start();
        clientStart.start();
		
	}
}
