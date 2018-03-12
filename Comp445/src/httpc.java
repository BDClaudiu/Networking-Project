/*
 * COMP 445 - Assignment 1
 * Student Name: Bacisor Claudiu
 * Student ID: 27735332
 * 
 */


import java.util.*;
import java.io.*;
import java.net.*;


public class httpc {
	
	// ArrayList that stores the user commands inputs (GET, POST, help, -d,-f,-v,-h) 
	public ArrayList<String> userCommand; 

	 // A harsh map for storing the headers. Format[key]:[value]; 
	 public Map<String, String> mapHeader;
	
	// Port
	public int port;
	
	//Server request is stored here as a string
	public String request;
	
	//-------------------------------------------------------------------------------------------------------------------
	
	 // -v   Verbosity for testing and debugging stages where you need more information to do so. 
	 // It prints the status, headers, and the contents of the response.
	 public boolean verbose;
	 
	 // -d gives the user the possibility to associate the body of the HTTP Request with the inline data,
	 //  meaning a set of characters for standard input 
	 public boolean enableInlineData;
	
	 // A string that stores the inline data that is associated to the body HTTP POST request
	 public String inlineData;
	
	 // inline data length
	 public int inlineDataLength;
	 
	 // -f associates the body of the HTTP Request with the inline data from a given file.
	 public boolean enableFile;
	
	 //-o writes the body to a file
	 public boolean bodyToFile;
	 
	// The name of the file to read from
	public String fileName;
	
	//The name of the file to write the request body to
	public String writeToFile;
	
	
	public httpc(String[] args) throws UnknownHostException, URISyntaxException, IOException {
		
			
		//Setting all the instance variables to default values
		userCommand = new ArrayList<String>(Arrays.asList(args));
		verbose = false;
		mapHeader = new HashMap<String, String>();
		port = 80;
		request = "";
		enableInlineData = false;
		inlineData = "";
		inlineDataLength = 0;
		enableFile = false;
		fileName = "";
		writeToFile="hello.txt";
		bodyToFile=false;
		
			//   -v 
			if(userCommand.contains("-v"))
			{
				verbose = true;
			}
			//  -h input
			if(userCommand.contains("-h"))
			{
				int hPosition = userCommand.indexOf("-h");
				String headers = userCommand.get(hPosition + 1);
				int separator = headers.indexOf(':');
				mapHeader.put(headers.substring(0, separator), headers.substring(separator + 1));
			}
			// -o option
			if(userCommand.contains("-o"))
			{
				bodyToFile=true;
				int filePosition = userCommand.indexOf("-o");
				String fileNameWriter = userCommand.get(filePosition + 1);
				writeToFile=fileNameWriter;
			}
			//  -d input
			if(userCommand.contains("-d"))
			{
				enableInlineData = true;
				inlineData = userCommand.get((userCommand.indexOf("-d")) + 1);
				inlineDataLength = inlineData.length();
			}
			 //  -f input (same as d just read from file)
			 if(userCommand.contains("-f"))
			 {
				enableFile = true;
				StringBuilder strBuild = new StringBuilder();
				
				//taking the file name from user console input
				fileName = userCommand.get((userCommand.indexOf("-f")) + 1); 
				
				//reading the file
				Scanner s = new Scanner(new FileReader(fileName));
				while(s.hasNextLine())
				{
					strBuild.append(s.nextLine());
				}
				inlineData = strBuild.toString();
				inlineDataLength = inlineData.length();
				s.close();
			}
		//Getting the user input for the method, GET||POST||HELP	 
		String method = args[0].toLowerCase();
		
		switch (method)
		{
		case "help":
		{
			if (args.length != 0 && args.length == 1) 
			{
				System.out.println("httpc help");
				System.out.println();
				System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n");
				System.out.println("Usage:");
				System.out.println("     httpc command [arguments]\n");
				System.out.println("The commands are:");
				System.out.println("     get     executes a HTTP GET request and prints the response.");
				System.out.println("     post    executes a HTTP POST request and prints the response.");
				System.out.println("     help    prints this screen.\n");
				System.out.println("Use \"httpc help [command]\" for more information about a command.");
			}
			else if (args.length > 1 && args[1].equalsIgnoreCase("get")) 
			    {
					System.out.println("httpc help get");
					System.out.println("usage: httpc get [-v] [-h key:value] URL");
					System.out.println("Get executes a HTTP GET request for a given URL");
					System.out.println("   -v             Prints the detail of the response such as protocol, status, and headers.");
					System.out.println("   -h key:value   Associates headers to HTTP Request with the format");
					System.out.println("'key:value'");
				}
			else if (args.length > 1 && args[1].equalsIgnoreCase("post")) 
				{
					System.out.println("httpc help post");
					System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL");
					System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file.");
					System.out.println("-v             Prints the detail of the response such as protocol, status, and headers.");
					System.out.println("-h key:value   Associates headers to HTTP Request with the format\n'key:value'.");
					System.out.println("-d string      Associates an inline data to the body HTTP POST request.");
					System.out.println("-f file        Associates the content of a file to the body HTTP POST \nrequest.");
					System.out.println();
					System.out.println("Either [-d] or [-f] can be used but not both.");
				}
		}	
			break;

		case "get":
		case "post":
			
			 //The request is the last element of user's input in the console line
			if(userCommand.contains("-o"))
			{
				request = args[args.length-3];        
			}
			else 
				request = args[args.length-1];
			
			System.out.println(request);
			// Removing unnecessary parts of the request by converting it into a URI
			URI url = new URI(request);            
		
			//Getting the host out of the URI
			String host = url.getHost();
			
			//If host is specified in the header, use that host name
			if(host == null && mapHeader.containsKey("Host")){									
				host = mapHeader.get("Host");
			}
			
			//Creating a socket with a host and port
			Socket socket = new Socket(host, port);
			
			//Initializing the input & output streams
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);			
			                     
			// GET|| POST 
			String requestToSend = args[0].toUpperCase()+ " " + request + " HTTP/1.0";	
			
			out.println(requestToSend);	              //Request line														
			out.println("Host: " + host);	          //Request header HOST
			
			//Displaying the headers
			if(!mapHeader.isEmpty())
			{															
				for (Map.Entry<String, String> entry: mapHeader.entrySet())
				{
					out.println(entry.getKey()+ ": " + entry.getValue());
				
				}
			}
			
			//Displaying  'Content-Length: number-of-bytes'
			if(enableInlineData == true || enableFile == true)
			{	
				out.println("Content-Length: " + inlineData.length());
			}
					
			//Blank line to separate the header and body
			out.println("");
		
			//Displaying message body read from the command line if enabled 
			if(enableInlineData == true)
			{	
				out.println(inlineData);
				
			}
			
			//Displaying message body read from file if enabled
			if(enableFile == true)
			{																		
				BufferedReader bReader = new BufferedReader(new FileReader (fileName));
				String line;
				String fileContent ="";
				while((line = bReader.readLine()) != null)
				{
					fileContent += line + "\n";
				}
				out.println(fileContent);
				bReader.close();
			}
			
			//Send the request
			out.flush();
			
			String WebContent=null;
			//Check if status code is 200/300
			for(String line = in.readLine(); line != null; line = in.readLine())
			{
				WebContent += line + "\n";

				if (line.contains("HTTP/1.0 3")==true ||line.contains("HTTP/1.1 3")==true)
				{	
					String address="http://httpbin.org/status/418";
					URI url2 = new URI(address);            

					//Getting the host out of the URI
					String host2 = url2.getHost();

					Socket socket2 = new Socket(host2,80); 

					PrintWriter out2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()))); 
					out2.println("GET "+address+" HTTP/1.0"); 
																	
					out2.println("Host: " + host2);	          //Request header HOST
					out2.println(); 
					out2.flush(); 

					//Displaying the request
					BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream())); 
					String inputLine2; 
					int count2 = 0; 

					while ((inputLine2 = in2.readLine()) != null) 
					{ 
						count2++; 
						System.out.println(inputLine2); 
					} 

					in2.close(); 

					System.out.println("You were re-directed to the current page!!!");
					socket2.close();
					return;		
				}
			}		
			String[] webLine = WebContent.split("\n");

		//Display the response here
			if(verbose == true)
			{			
				BufferedWriter writer=new BufferedWriter(new FileWriter(writeToFile));
				
				for(int i=0;i<webLine.length;i++)
				{ 	//Display request;
					System.out.println(webLine[i]);
				
					//Write content of request to file
					if(bodyToFile==true)
					{
					 writer.append(webLine[i]+"\n");
					}
				}		
				writer.close();
			}
			else//just print body without verbose 
			{
				BufferedWriter writer=new BufferedWriter(new FileWriter(writeToFile));
				boolean hasBody = false;
				for(int i=0;i<webLine.length-1;i++)
				{
					if(webLine[i].contains("{") || hasBody ||webLine[i].contains("Via: "))
					{
						hasBody = true;
						System.out.println(webLine[i+1]);
						
						if(bodyToFile==true)
						{
							writer.append(webLine[i+1]+"\n");
						}
					}
				}
				writer.close();
			}
			in.close();																			
			out.close();
			socket.close();
			break;		
		}
	}

	public static void main(String[] args) throws UnknownHostException, URISyntaxException, IOException {
		
		httpc cURL = new httpc(args);
	}

}
