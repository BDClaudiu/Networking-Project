import java.net.*;
import java.util.concurrent.Semaphore;
import java.io.*;

/*
 * COMP 445 
 * Assignment 2
 * Multithread helper class
 * 
 * Bacisor Claudiu ID: 27735332
 * Marzie Shafiee ID:  40016801 
 * 
*/
public class clientHelper implements Runnable {
	
private Socket clientSocket;
private File fileDir;
private boolean requestDetails;

// Multi-Threading semaphores for read/write
static Semaphore readLock = new Semaphore(10);
static Semaphore writeLock = new Semaphore(1);
volatile static int readCount = 0;

// ReadWriteLock readWritelock = new ReentrantReadWriteLock();

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
	
	// Parameterized Constructor that takes as parameters a socket, boolean for server details and a File for directory
	public clientHelper(Socket client, boolean dtls, File fileDirectory )
	{
		this.clientSocket=client;
		this.fileDir=fileDirectory;
		this.requestDetails=dtls;
	}
	
	public void run()
	{
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
					
					//while reading, we increase the readCount by 1, if the read count is 1, we get the writelock;
			readLock.acquire();
					readCount++;
					if(readCount==1)
					{  
						System.out.println("Acquiring writelock in the read section");
						writeLock.acquire();
					}
			readLock.release();
			
					System.out.println("Number of readers: "+ readCount);
					//Creating a new file that has the same directory and command line filename
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
								System.out.println("Content Disposition: attachement; "+"name="+docName+"; filename="+fileName+";");
								System.out.println("Content-Type: text/plain"+";");
								System.out.println("Content-Length: "+file.length()+";");
							}
							else
							{
								System.out.println("Content Disposition: attachement; "+"name="+docName+"; filename="+fileName+";");
								System.out.println("Content-Type: application/"+docType+";");
								System.out.println("Content-Length: "+file.length()+";");		
							}								
						}	//End of bonus part code
						outStream.println("Success");
						
						String fileLine;
						FileReader fileReader = new FileReader(file);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
					
						while((fileLine = bufferedReader.readLine()) != null)
						{
					
							outStream.println(fileLine);
						}
						
						bufferedReader.close();
						fileReader.close();
						outStream.flush();
						outStream.close();
					}

					if (outStream.checkError())
					{
						throw new Exception("Error Sending File"+fileName);
					}
					
					readLock.acquire();
					readCount--;
					if(readCount==0){
						System.out.println("Releasing the writelock in the read section");
						writeLock.release();}
					readLock.release();
					System.out.println("Number of readers: "+ readCount);
				}//end of else 
			
			} //end of if GET (CASE 1)
			 
			//CASE 2 POST
			if (userReq.startsWith("post"))
			{	
				//Acquiring the writeLock, cannot acquire if not available
				System.out.println("Acquiring the writelock in the write section");
				writeLock.acquire();
				
				// Getting the index of  "/"
				int delimiterPosition = userReq.indexOf("/");
				//Removing everything that comes before "/"
				fileName = userReq.substring(4, delimiterPosition).trim();
				
				File filee = new File(fileDir, fileName);

				if(filee.exists())
				{	System.out.println("The file has been overwritten");
					PrintWriter  writer= new PrintWriter(filee);
					writer.print("");
				    writer.close();
				}
				
				FileWriter fileWriter = new FileWriter(filee, true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				
				
				while (EOF() == false)
				{
					String line = line(inStream);
					System.out.println("Line: " + line);
					printWriter.print(line);
				}	
				printWriter.close();
				
				outStream.println("success");
				outStream.flush();
				outStream.close();
			
				if (outStream.checkError())
				{
					throw new Exception();
				}
				System.out.println("Releasing the writelock in the write section");
				writeLock.release();
				
			}//end of if POST loop
			if(requestDetails==true)
			{
				System.out.println("----------------------------------------------");	
			}	
			}//end of while loop 
			catch ( Exception e){System.out.println(e); }
	}
}
