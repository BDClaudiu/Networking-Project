/*
 * COMP 445 
 * Assignment 2
 * Server Server
 * 
 * Bacisor Claudiu ID: 27735332
 * Marzie Shafiee ID: 
 * 
*/


import java.util.*;
import java.io.*;
import java.net.*;

public class httpfs {

//Main Method	
@SuppressWarnings("resource")
public static void main(String[] args) throws Exception {

		
	File fileDir;
	 ServerSocket serverSocket=null;
	 int port;
	 ArrayList<String> userCommandLine;
	
	// Default port number 
	port=8080;
	
	// Creates a new File instance by converting the given pathname string into an abstract path name
	fileDir= new File("C:\\Core\\WorkBenches\\Java WorkBench\\Comp445\\StorageDirectory");
	
	//Here we are storing the command line commands inputs from user into a ArrayList of Strings (same as in assignment1)
	userCommandLine = new ArrayList<String>(Arrays.asList(args));
			
	
while(true)

	
	
{
clientHelper chelper;

try
{
	/* 3 options: 
	 * -v : Prints debugging messages
	 * -p : Specifies a new port number that the server will listen and serve at. 
	 * Default: 8080
	 * -d : Specifies a new directory that the server will use to read/write requested files. 
	 * Default: C:\\Core\\WorkBenches\\Java WorkBench\\Comp445\\StorageDirectory
	 */
		
	if(userCommandLine.contains("-v"))
	{
		System.out.println("Prints debugging messages");	
	}
		
	if(userCommandLine.contains("-p"))
	{	// Setting the port number with the value preceding the command line with the index "-p";
		// Since value is a string, we need to convert it into integer
		System.out.println("Specifies the port number that the server will listen and server at.\n Default: 8080\n New: "+ Integer.parseInt(userCommandLine.get(userCommandLine.indexOf("-p")+1)));	
		port=Integer.parseInt(userCommandLine.get(userCommandLine.indexOf("-p")+1));
	}
		
	if(userCommandLine.contains("-d"))
	{// Setting the file directory with the string value preceding the command line with the index "-d";
		System.out.println("Specifies the directory that the server will use to read/write requested files.\n Default:C:\\Core\\WorkBenches\\Java WorkBench\\Comp445\\StorageDirectory\n New: "+ userCommandLine.get(userCommandLine.indexOf("-d")+1));
		fileDir= new File (userCommandLine.get(userCommandLine.indexOf("-d")+1));
		
	}

	try{
			serverSocket= new ServerSocket(port);
		}
		catch(IOException e)
		{
			System.out.println("Exception occord while listening to port " + port);
			System.out.println(e.getMessage());
		}

	
	
	
	
chelper= new clientHelper(serverSocket.accept());
chelper.setFileDir(fileDir);
Thread thr= new Thread(chelper);
thr.start();


}
catch(IOException e){System.out.println("accept failed");
}


}


	

	}
}
