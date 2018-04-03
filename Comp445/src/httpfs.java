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

private File fileDir;
private Socket clientSocket;
private ServerSocket serverSocket;
private int port;
private ArrayList<String> userCommandLine;

//Buffer Reader instance variables
private static BufferedReader bufferedReader;
private static String buffer = null;
private static int nextChar = 0;  

//Checking the end of file
public static boolean EOF() throws IOException  { 
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
private static char readCharacter() throws IOException {  
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
	public httpfs(String[] args) throws Exception {
		
		// Default port number 
		port=8080;
		
		// Creates a new File instance by converting the given pathname string into an abstract path name
		fileDir= new File("C:\\Core\\WorkBenches\\Java WorkBench\\Comp445\\StorageDirectory");
		
		//Here we are storing the command line commands inputs from user into a ArrayList of Strings (same as in assignment1)
		userCommandLine = new ArrayList<String>(Arrays.asList(args));
				
	/* 3 options: 
	 * -v : Prints debugging messages
	 * -p : Specifies a new port number that the server will listen and serve at. 
	 * Default: 8080
	 * -d : Specifies a new directory that the server will use to read/write requested files. 
	 * Default: C:\\Core\\WorkBenches\\Java WorkBench\\Comp445\\StorageDirectory
	 */
		
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
}
	
	private void setConnection() throws Exception {
		
	serverSocket = new ServerSocket(port);	
	System.out.println("*******Server is Running********");
	
	// While true implies it is always the case, as such, the server will always be listening and accepting clients (will not terminate after lets say, a request/of after a clients connects)
	while (true)
	{
		clientSocket=serverSocket.accept();
		
		if(userCommandLine.contains("-v"))
		{
			System.out.println("A client connected");	
		}
		
		String userReq="";
		String fileName="";
		
		BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter outStream= new PrintWriter(clientSocket.getOutputStream());
		
		userReq= line(inStream);
		userReq=userReq.toLowerCase();
		
		if(userCommandLine.contains("-v"))
		{
			System.out.println("Client Request : " + userReq);	
		}
		
		//CASE 1 (GET)
		if (userReq.startsWith("get")) 
		{
			fileName=userReq.substring(3).trim();
			
			// CASE 1.1 GET 	
			// If the command is GET, we loop through the file directory and display the files
			if(fileName.equals("")) 
			{
					String[] files = fileDir.list();
					for (int i = 0; i < files.length; i++)
					{
						outStream.println(files[i]);
					}
					outStream.flush();
					outStream.close();

				if (outStream.checkError())
				{
					throw new Exception("Error. File Display Incomplete...");
				}
			} //end if statement
			
			// CASE 1.2 GET with file name
			else 
			{    
				// Creating a new file that has the same directory and command line filename
				File file = new File(fileDir, fileName);

				if ( (!file.exists()) || file.isDirectory() )
				{
					outStream.println("ERROR 404: File Not Found!");
				}
				else
				{	//BONUS PART
					//Content-Disposition and  Content-Type
					String docName=fileName.substring(0,fileName.lastIndexOf("."));
					String docType = fileName.substring(fileName.lastIndexOf(".")+1);
					
					if(docType.equalsIgnoreCase("txt"))
					{
						System.out.println("Content Disposition: data; "+"name="+docName+"; filename="+fileName+";");
						System.out.println("Content-Type: text/plain"+";");
						System.out.println("Content-Length: "+file.length()+";");
					}
					else
					{
						System.out.println("Content Disposition: data; "+"name="+docName+"; filename="+fileName+";");
						System.out.println("Content-Type: application/"+docType+";");
						System.out.println("Content-Length: "+file.length()+";");		
					}	
					//End of bonus part code
					
					outStream.println("Success");
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);

					String fileLine;
					while((fileLine = bufferedReader.readLine()) != null)
					{   
						outStream.println(fileLine);
					}
					fileReader.close();

					outStream.flush();
					outStream.close();
				}

				if (outStream.checkError())
				{
					throw new Exception();
				}

			}//end of else
		} //end of if GET (CASE 1)
		 
		//CASE 2 POST
		if (userReq.startsWith("post")) 
		{	// Getting the index of  "/"
			int delimiterPosition = userReq.indexOf("/");
			//Removing everything that comes before "/"
			fileName = userReq.substring(4, delimiterPosition).trim();

			File file = new File(fileDir, fileName);

			if (file.exists())
			{
				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();
			}

			while (EOF() == false)
			{
				String line = line(inStream);
				FileWriter fileWriter = new FileWriter(file, true);
				PrintWriter printWriter = new PrintWriter(fileWriter);

				printWriter.print(line);
				printWriter.close();

			}

			outStream.println("success");
			outStream.flush();
			outStream.close();

			if (outStream.checkError())
			{
				throw new Exception();
			}

		}//end of if POST loop
		
	}//end of while loop
}
	

//Main Method	
public static void main(String[] args) throws Exception {
		
	httpfs server = new httpfs(args);

	server.setConnection();

	}
}
