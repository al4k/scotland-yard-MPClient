package state;
/**
 * Implements a point in euclidean space
 */
public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public Point(Point a)
	{
		this.x = a.x();
		this.y = a.y();
	}
	
	Boolean equals(Point a)
	{
		if(this.x == a.x() && this.y == a.y())
		{
			return true;
		}
		return false;
	}
	
	
	public String toString()
	{
		String output = String.format("[%d, %d]", x,y);
		return output;
	}

	public double distanceTo(Point a)
	{
		double xdiff = (double) x - a.x();
		double ydiff = (double) y - a.y();
		
		
		return Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2));
	}
	
	
	/**
	 * @return the x
	 */
	public int x() 
	{
		return x;
	}
	
	/**
	 * @param x the x to set
	 */
	public void x(int x) 
	{
		this.x = x;
	}
	
	/**
	 * @return the y
	 */
	public int y() 
	{
		return y;
	}
	
	/**
	 * @param y the y to set
	 */
	public void y(int y) 
	{
		this.y = y;
	}
	

}
