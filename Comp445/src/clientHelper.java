import java.net.*;

import java.io.*;

public class clientHelper implements Runnable {


private File fileDir;

public void setFileDir(File fileDir)
{
	this.fileDir=fileDir;	
}

private Socket clientSocket;


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
	
	//constructor
	public clientHelper(Socket client)
	{
		this.clientSocket=client;
	}
	
	public void run()
	{
		
		System.out.println("*******Server is Running********");
		
		// While true implies it is always the case, as such, the server will always be listening and accepting clients (will not terminate after lets say, a request/of after a clients connects)
		while (true)
		{
			try
			{
			System.out.println("A client connected");
			
			String userReq="";
			String fileName="";
			
			BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter outStream= new PrintWriter(clientSocket.getOutputStream());
			
			userReq= line(inStream);
			userReq=userReq.toLowerCase();
			
			System.out.println("Client Request : " + userReq);
			
			
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
					//creating a new file that has the same directory and command line filename
					File file = new File(fileDir, fileName);

					if ( (!file.exists()) || file.isDirectory() )
					{
						outStream.println("ERROR 404: File Not Found!");
					}

					else
					{
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
			catch ( Exception e){System.out.println(e);}
		}
		}
}
