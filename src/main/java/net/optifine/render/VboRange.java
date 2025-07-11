package net.optifine.render;

import net.optifine.util.LinkedList;

public class VboRange {
	private final LinkedList.Node<VboRange> node = new LinkedList.Node<>(this);
	private int position = -1;
	private int size = 0;

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPositionNext() {
		return this.position + this.size;
	}

	public LinkedList.Node<VboRange> getNode() {
		return this.node;
	}

	public VboRange getPrev() {
		LinkedList.Node<VboRange> node = this.node.getPrev();
		return node == null ? null : node.getItem();
	}

	public VboRange getNext() {
		LinkedList.Node<VboRange> node = this.node.getNext();
		return node == null ? null : node.getItem();
	}

	public String toString() {
		return this.position + "/" + this.size + "/" + (this.position + this.size);
	}
}
