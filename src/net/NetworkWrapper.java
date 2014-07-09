package net;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import game.TextOutput;


public class NetworkWrapper {
	private String host;
    private int port;
    private PrintWriter out;
    //private Scanner in;
    private Socket server;
    private BufferedReader textInput;
    private boolean printRequests;
    private boolean printResponses;
    private int id;
    
    
    
    
    
    
    private NetworkWrapper() {	}
    
    /**
     * Static factory function. Pass in the host and port and it will try and make 
     * a connection on those values. If the connection was made, an initialised 
     * NetworkWrapper is returned
     * @param host The host address
     * @param port The port number
     * @return A valid NetwrokWrapper with an active connection to the server
     */
    public static NetworkWrapper getConnection(String host, int port, int id)
	{
		// connect
		NetworkWrapper wrapper = new NetworkWrapper();
		wrapper.host = host;
		wrapper.port = port;
		wrapper.id = id;
		
		wrapper.registerWithServer();

		return wrapper;
	}
    
    
    /**
     * Does the actual registration with server
     * @throws Exception
     */
    private void registerWithServer()
	{
		// server registration
		try {
			server = new Socket(host, port);
			server.setKeepAlive(true);
	        out = new PrintWriter(server.getOutputStream(), true);
	        textInput = new BufferedReader(
		            new InputStreamReader(server.getInputStream()));
	        
		} catch (IOException e) {
			TextOutput.printError("Couldn't connect to the server.\n");
			TextOutput.printError("Check that the server is running and that you are using the correct port and host\n");
			System.exit(1);
		}
		
		

	}
    
    
    /**
     * Function to get a text file from the server
     * @param call The string specify the call
     * @param outputFilename The filename of the output
     * @return success or failure
     */
	public synchronized boolean getTextFile(String call, String outputFilename)
	{	
		
		synchronized (this) {
			
		
    	String response = send(call);
    	String[] parts = response.split(",");
    	int success = Integer.parseInt(parts[0]);
    	
    	if(success == 0)
    	{
    		return false;
    	}
		int stringLength = Integer.parseInt(parts[1]);
		int read = 0;
		
		
		String outputString = "";
		
		while(read < stringLength)
		{
			char [] cbuf = new char[stringLength];
			try
			{
				int readTemp =0;
				synchronized (textInput) {
					readTemp += textInput.read(cbuf,0,stringLength-read);
				}
				
				String part = new String(cbuf,0, readTemp);
				read += readTemp;
				outputString += part;
				
			}
			catch(IOException e)
			{
				TextOutput.printError(e.getMessage());
				
				return false;
			}
		}
			
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(outputFilename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writer.print(outputString);
		writer.close();
		
		
		/*
		String outputString = "";
		
		
		// read each chunk by chunk
		for(int i = 0; i < numChunks; i++)
		{
			int offset = 0;
			int read = 0;
			int stringLength = Integer.parseInt(parts[i+2]);
			char [] cbuf = new char[stringLength];
			
			
			while(read < stringLength)
			{
				try
				{
					synchronized (textInput) {
						read += textInput.read(cbuf);
					}
					
					outputString += new String(cbuf);
					System.out.println("Num chunks: " + i);
					System.out.println("Read: " + read);
					System.out.println("Size: " + stringLength);
				
	
				}
				
				catch(IOException e)
				{
					TextOutput.printError(e.getMessage());
					
					return false;
				}
			}
		}
		
		
		////System.out.println(file);
		//System.out.println(stringLength + " " + read);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(outputFilename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print(outputString);
		writer.close();
		
		/*
		
		
	*/
		}

		
		return true;
	}
	
    
    /**
     * Function to send a string to the server. This is the only function that
     * you will need to use for your work. The function writes the string call 
     * to the server and then waits for a response. This function will not end
     * until a valid response is given. One common issue in which you may get stuck 
     * in this function is that the server response does not have a new line at the
     * end.
     * @param call String the request that is going to be sent to the server
     * @return a String response from the server
     */
    @SuppressWarnings("deprecation")
	public synchronized String send(String call)
	{
    	

    	call = Integer.toString(this.id) + "," + call;
    	if(printRequests) TextOutput.printServer(call);
		out.println(call);
		String response = "";
		try 
		{
		//if(in.hasNextLine())
		//	response = in.nextLine();
			synchronized (textInput) {
				response = textInput.readLine();
			}
			
		} 
		catch (IOException e) 
		{
			TextOutput.printError("Error Communicating With Server\n");			
		}
		
		if(printResponses) TextOutput.printResponse(response);
		return response;
	}
    
    
    
    /**
     * Function to get a binary file from the server
     * @param call The text string request
     * @param outputFilename
     * @return If the file was read ok
     */
	public synchronized boolean getBinaryFile(String call, String outputFilename)
	{
		/*
    	String response = send(call);
    	String[] parts = response.split(",");
    	int success = Integer.parseInt(parts[0]);
    	
    	if(success == 0)
    	{
    		return false;
    	}
    	
    	int fileSize = Integer.parseInt(parts[1]);		
		byte[] data = new byte[fileSize];
		
		try
		{
			FileOutputStream output = new FileOutputStream(new File(outputFilename));
			
			
			int bytesRead = 0;
			int read = 0;
			while(bytesRead < fileSize)
			{
				read = in.read(data);
				if(read < 0)
				{
					TextOutput.printError("Error Reading Image File\n");
					break;
				}
				output.write(data, 0, read);
				bytesRead+=read;
			}
			
			output.close();
		}
		catch(IOException e)
		{
			TextOutput.printError(e.getMessage());
			return false;
		}
		
		*/
		return true;
	}

	/**
	 * @return the printRequests
	 */
	public boolean isPrintRequests() {
		return printRequests;
	}

	/**
	 * @param printRequests the printRequests to set
	 */
	public void setPrintRequests(boolean printRequests) {
		this.printRequests = printRequests;
	}

	/**
	 * @return the printResponses
	 */
	public boolean isPrintResponses() {
		return printResponses;
	}

	/**
	 * @param printResponses the printResponses to set
	 */
	public void setPrintResponses(boolean printResponses) {
		this.printResponses = printResponses;
	}
    
}
