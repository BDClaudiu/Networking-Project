import java.net.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.*;

public class clientHelper implements Runnable {



private int counter=1;
private Socket clientSocket;
private File fileDir;
private boolean requestDetails;
private String threadClientNr;
private Thread t;

//Multi-Threading locks

ReadWriteLock readWritelock = new ReentrantReadWriteLock();

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
	
	// Constructor
	public clientHelper(Socket client, boolean dtls, File fileDirectory, String threadName )
	{
		this.clientSocket=client;
		this.fileDir=fileDirectory;
		this.requestDetails=dtls;
		this.threadClientNr=threadName;
	}
	
	public void run()
	{
		counter++;
		
		System.out.println("*******"+ threadClientNr+ ""+counter +" is Running********");
		
			try
			{
				if(requestDetails==true)
				{
					System.out.println("A client connected");	
				}	
			String userReq="";
			String fileName="";
			
			BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter outStream= new PrintWriter(clientSocket.getOutputStream());
			
			userReq= line(inStream);
			userReq=userReq.toLowerCase();
			
			if(requestDetails==true)
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
					readWritelock.readLock().lock(); 
					//creating a new file that has the same directory and command line filename
					File file = new File(fileDir, fileName);
					
					if ( (!file.exists()) || file.isDirectory() )
					{
						outStream.println("ERROR 404: File Not Found!");
					}
					
					else
					{	
						if(requestDetails==true)
						{
							//BONUS PART
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
												
						}	//End of bonus part code
						
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
					
					readWritelock.readLock().unlock();
				}//end of else 
				
			} //end of if GET (CASE 1)
			 
			//CASE 2 POST
			if (userReq.startsWith("post"))
				
			{	// Getting the index of  "/"
				
				readWritelock.writeLock().lock();
				
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
				readWritelock.writeLock().unlock();
			}//end of if POST loop
				
		}//end of while loop 
			catch ( Exception e){System.out.println(e);}
		}
	
	/*
	public void start(){
		System.out.println("connecting this shit: "+threadClientNr);
		if(t==null){
			t= new Thread (this, threadClientNr);
		t.start();
		}
		
	}
	*/
}

