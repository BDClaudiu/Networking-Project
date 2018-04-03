/*
 * COMP 445 
 * Assignment 2
 * Client Server
 * 
 * Bacisor Claudiu ID: 27735332
 * Marzie Shafiee ID: 
 * 
*/


import java.util.*;
import java.io.*;
import java.net.*;

public class httpfc {
	
	private final static String SERVER_ADDRESS="localhost";
	
	//Buffer Reader instance variables
	private static BufferedReader bufferedReader;
	private static String buffer = null;
	private static int nextChar = 0;  

	//Checking the end of file
	public static boolean EOF() throws IOException  
	{ 
		char fileEnd;

		if (buffer == null || nextChar > buffer.length()) 
		{
			buffer = bufferedReader.readLine();	
			nextChar = 0;
		}
	    if (buffer == null) 
	    {
		    fileEnd = (char)0xFFFF; 
	    }
	    else if (nextChar == buffer.length()) 
	    {
		    fileEnd = '\n';
	    }
	    else  
	    {
		    fileEnd = buffer.charAt(nextChar);
	    }
		return fileEnd == (char)0xFFFF;
	}

	//Method that reads a line using the character method
	public static String line(BufferedReader inStream) throws IOException 
	{
	    StringBuffer stringBuffer = new StringBuffer(100);
	    bufferedReader = inStream;
	    
	    //Reading a char until we reach a new line
	    char c = readCharacter();
	    
	    while (c != '\n') 
	    {
	  	  stringBuffer.append(c);
	      c = readCharacter();
	    }
	    
	    return stringBuffer.toString();
	}
	//Method that reads a character
	private static char readCharacter() throws IOException 
	{  
	      char character;
	    
	      if (buffer == null || nextChar > buffer.length()) 
	      {
				buffer = bufferedReader.readLine();
				nextChar = 0;
	      }         
	      if (buffer == null) 
	      {
	  	    character = (char)0xFFFF; 
	      }
	      else if (nextChar == buffer.length()) {
	  	    character = '\n';
	      } 
	      else  
	      {
	  	    character = buffer.charAt(nextChar);
	      }
		  if (buffer == null) 
		  {
		      System.out.println("Empty buffer!");;
		  }
	    
	    nextChar++;
	    return character;
	}
	
public static void main(String[] args) throws Exception {
	int server_port =8080;
	String method="";
	String file_name="";
	String file_content="";
	
	System.out.println("**********Client is Running**********");	
	ArrayList <String> clientRequestCommandLine = new ArrayList<String>(Arrays.asList(args));
	
	
	
/*
 * -------------------------------------------------------------------------------------------------------
 * POST and GET methods options  
 * -------------------------------------------------------------------------------------------------------
 */
	
	//I WANNNA CHANGE THIS...ITS STUPID!!! to have a if condition for GET and get....POST and post (i did not include Post)
	
	//GET request without file name
	if(clientRequestCommandLine.contains("get") && clientRequestCommandLine.size()==1)
	{
		method="GET";
		file_name="";	
	}
	else if(clientRequestCommandLine.contains("GET") && clientRequestCommandLine.size()==1)
	{
		method="GET";
		file_name="";	
	}
	
	//GET request with file name
	else if(clientRequestCommandLine.contains("GET") && clientRequestCommandLine.size()>1)
	{
		method="GET";
		int position =clientRequestCommandLine.indexOf("GET");
		file_name=clientRequestCommandLine.get(position+1);	
	}
	else if(clientRequestCommandLine.contains("get") && clientRequestCommandLine.size()>1)
	{
		method="GET";
		int position =clientRequestCommandLine.indexOf("get");
		file_name=clientRequestCommandLine.get(position+1);	
	}

	//POST request 
	else if(clientRequestCommandLine.contains("POST"))
	{
		method="POST";
		int position =clientRequestCommandLine.indexOf("POST");
		if(clientRequestCommandLine.size()>2)
		{
			file_name=clientRequestCommandLine.get(position+1);
			for(int i=position+2;i<clientRequestCommandLine.size();i++)
			{
				file_content+=clientRequestCommandLine.get(i);
			}
		}
		else
			System.out.println("Please input a file name followed by the file content");
	}	
	else if(clientRequestCommandLine.contains("post"))
	{
		method="POST";
		
		int position =clientRequestCommandLine.indexOf("post");
		if(clientRequestCommandLine.size()>2)
		{
			file_name=clientRequestCommandLine.get(position+1);
			for(int i=position+2;i<clientRequestCommandLine.size();i++)
			{
				file_content+=clientRequestCommandLine.get(i)+" ";
			}
		}
		else
			System.out.println("Please input a file name followed by the file content");	
	}
	else 
	{
		System.out.println("Invalid input.Valid inputs are: GET, GET filename, POST filename filecontent. ");
		System.out.print("System shut down imminent!!");
		System.exit(0);
	}
	
/*
 * --------------------------------------------------------------------------------------------------------------------
 * Opening connection to server
 * Sending requests according to the above mentioned methods
 * --------------------------------------------------------------------------------------------------------------------	
 */
	Socket clientSocket;
	BufferedReader inStream=null;
	PrintWriter outStream=null;
	
	String command;
	
	clientSocket= new Socket(SERVER_ADDRESS,server_port);
	inStream= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	outStream= new PrintWriter(clientSocket.getOutputStream());
	
	// Clients requests
	System.out.println("Client: "+SERVER_ADDRESS);
	//CASE 1 GET
	if(method.equalsIgnoreCase("GET")&& file_name==null)
	{
		command=method;
		outStream.println(command);
		outStream.flush();
//		System.out.println("Client issss : "+SERVER_ADDRESS);
		System.out.println("Sending request " + command +" to server");
	}	
	
	
	//CASE 2 GET filename
	if(method.equalsIgnoreCase("GET")&& file_name!=null)
	{
		command=method+" "+file_name;
		outStream.println(command);
		outStream.flush();
		System.out.println("Sending request " + command +" to server");
	}	
	
	//CASE 3 POST filename content
	if(method.equalsIgnoreCase("POST"))
	{
		command=method+" "+file_name+"/"+file_content;
		outStream.println(command);
		outStream.flush();
		System.out.println("file content " + file_content);
		System.out.println("Sending request " + command +" to server");
		
		File newFile = new File(file_name);
		
		if(!newFile.exists() || newFile.isDirectory())
		{
			outStream.println(file_content);
		}
		else
		{
			FileReader file_Reader = new FileReader(newFile);
			BufferedReader buffered_Reader =  new BufferedReader (file_Reader);
			
			while(buffered_Reader.readLine() !=null)
			{
				outStream.println(buffered_Reader.readLine());	
			}
			file_Reader.close();
			outStream.flush();
			outStream.close();
		}
	}
	
	if(outStream.checkError())
	{
		clientSocket.close();
		throw new Exception("Transfer incomplete.");
	}
	
	//Server Response
	
	//CASE 1 GET 
	if(method.equalsIgnoreCase("GET") && file_name=="")
	{
		System.out.println("File list: ");
		Scanner scan= new Scanner(inStream).useDelimiter("\\A");
		String result=scan.hasNext() ? scan.next(): "";
		System.out.println(result);
		scan.close();
	}
	
	//CASE 2 GET filename  
	if(method.equalsIgnoreCase("GET") && file_name!="")
	{
		String msg = line(inStream);
		if(!msg.equalsIgnoreCase("success"))
		{
			System.out.println("ERROR 404. FILE NOT FOUND");
			clientSocket.close();
			return;
		}
		else
		{
			System.out.println("Content of the requested file: " +file_name);
			System.out.println("-----------------------------------------------------------------");
		}
		while(!EOF())
		{
			String line=line(inStream);
			File file = new File(file_name);
			FileWriter fileWriter = new FileWriter (file,true);
			PrintWriter out= new PrintWriter (fileWriter);
			System.out.println(line);
			out.close();	
		}
	}
		
	//CASE 3 POST
	if(method.equalsIgnoreCase("POST"))
	{	
		System.out.println("File has been succesfully send");
	}
	clientSocket.close();
	
	}
}
