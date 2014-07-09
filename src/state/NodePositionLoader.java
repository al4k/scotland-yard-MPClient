package state;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


public class NodePositionLoader {

	private HashMap<String, Point> nodePositionMap; 
	private final static String NODE_TO_IGNORE = "0";
	private final static int EXPECTED_NUMBER_OF_LINE_TOKENS = 3;
	
	public HashMap<String, Point> getNodePositionMap()
	{
		return nodePositionMap;
	}
	
	public void read(String filename) throws IOException
	{
		nodePositionMap = new HashMap<String,Point>();
		
		Scanner fileScanner = new Scanner(new File(filename));
		while(fileScanner.hasNextLine())
		{
			String line = fileScanner.nextLine();
			String lineTokens[] = line.split(" ");
			
			if(line.isEmpty() || 
					lineIsFromFileHeader(lineTokens)) continue;
			
			
			String nodeId = lineTokens[0];			
			int posX = Integer.parseInt(lineTokens[1]);
			int posY = Integer.parseInt(lineTokens[2]);
			Point p = new Point(posX, posY);
			nodePositionMap.put(nodeId, p);
		}
		
		fileScanner.close();
	}
	
	private boolean lineIsFromFileHeader(String[] parts)
	{
		if(parts[0].equals(NODE_TO_IGNORE) || 
				parts.length < EXPECTED_NUMBER_OF_LINE_TOKENS) return true;
		return false;
	}
	
}
