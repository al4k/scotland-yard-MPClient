package game;

import java.awt.Color;
import java.awt.TextArea;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class TextOutput {
	
	private static boolean debugMode = false;
	private static boolean debugDeep = false;
	private static boolean useGUI = false;
	private static JTextArea textArea = null;
	private static JLabel countdownLabel = null;
	
	
	public static void setCountdownLabel(JLabel label)
	{
		countdownLabel = label;
	}
	
	public static void setDebugMode(boolean mode)
	{
		debugMode = mode;
	}
	
	public static void useGUI(boolean val)
	{
		useGUI = val;
	}
	
	public static void setTextArea(JTextArea ta)
	{
		textArea = ta;
	}
	
	public static void setCountDownTime(double time)
	{
		String output;
		if(time < 0.0)
		{
			output = "---";
		}
		else
		{
			output = Double.toString(time);
		}
		
		
		if(time < 5.0 && time >= 0.0)
		{
			countdownLabel.setForeground(Color.RED);
		}
		else
		{
			countdownLabel.setForeground(Color.WHITE);
		}
		
		countdownLabel.setText(output);
		
	}
	
	public static void printDebug(String message)
	{
		if(useGUI)
		{
			if(textArea != null)
			{
				textArea.append(message);
				System.out.print(message);
				return;
			}
		}
		
		if(debugDeep)
		{
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			StackTraceElement currentElement = stackTraceElements[2];
			message = "["+ currentElement.getClassName() + " > " +  currentElement.getMethodName() + "]: " + message;
		}
		else
		{
			message = "[Debug]: " + message;
		}
		
		if(debugMode == true) System.out.print(message);
	}
	
	public static void printServer(String message)
	{
		if(debugMode)
			System.out.println("[Server Call]: " + message);
	}
	
	public static void printResponse(String message)
	{
		if(debugMode)
			System.out.println("[Server Reponse]: " + message);
	}
	
	public static void printFunction(int depth)
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement currentElement = stackTraceElements[depth];
		String message = "[Function]: "+ currentElement.getClassName() + " > " +  currentElement.getMethodName() + "\n";
		System.out.print(message);
	}
	
	public static void  printStandard(String message)
	{
		message = "[Standard]: " + message; 
		System.out.print(message);
	}
	
	public static void  printStandardLn(String message)
	{
		message = "[Standard]: " + message; 
		System.out.println(message);
	}
	
	
	
	public static void printError(String message)
	{
		System.err.print("[error]: " + message);
	}
}
