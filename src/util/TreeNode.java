package util;

import java.util.*;

public class TreeNode<T> {
	T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children = new ArrayList<TreeNode<T>>();
	
	public TreeNode(T data)
	{
		this.data = data;
	}
	
	public List<TreeNode<T>> children()
	{
		return children;
	}
	
	public TreeNode<T> parent()
	{
		return parent;
	}
	
	public void addChild(TreeNode<T> child)
	{
		children.add(child);
	}
	
	public void addChildren(List<TreeNode<T>> children)
	{
		this.children.addAll(children);
	}
	
	/**
	 * Function to get the data held in the node
	 * @return
	 */
	public T data()
	{
		return this.data;
	}
	
	public boolean isLeaf()
	{
		if(children.size() == 0)
			return true;
		else
			return false;
	}
	
}
