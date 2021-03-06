package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Layer {
	private List<Node> nodes = null;
	private Attribute attribute;
	
	public void addNode(Node node) {
		if(nodes == null) {
			nodes = new ArrayList<Node>();
		}
		nodes.add(node);
	}
	
	public Node getNode(int idx) {
		return nodes.get(idx);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
}
