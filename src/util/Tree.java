package util;

import game.TextOutput;

import java.util.*;

public class Tree<T> {
	private TreeNode<T> root;
	private ArrayList<TreeNode<T>> nodes;
	private int size = 0;
	
	public Tree(TreeNode<T> root)
	{
		this.root = root;
		nodes = new ArrayList<TreeNode<T>>();
		nodes.add(this.root);
	}
	
	public void addNode(TreeNode<T> parent, TreeNode<T> node)
	{
		// check for the parent
		if(!nodes.contains(parent))
		{
			TextOutput.printError("Trying to add to a non existant parent node\n");
			return;
		}
		
		// add the node
		nodes.get(nodes.indexOf(parent)).addChild(node);
		nodes.add(node);
		size++;
	}
	
	public int size()
	{
		return size;
	}
	
	/**
	 * Function to  get the root of the tree
	 * @return
	 */
	public TreeNode<T> root()
	{
		return this.root;
	}
	
	
	public List<TreeNode<T>> children(TreeNode<T> parent)
	{
		if(!nodes.contains(parent))
		{
			TextOutput.printError("Trying to get children from a non existant parent node\n");
			return null;
		}
		
		return nodes.get(nodes.indexOf(parent)).children();
	}
	
	
}
