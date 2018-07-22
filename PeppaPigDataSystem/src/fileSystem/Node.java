package fileSystem;

import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

/**
 * @author Zenong 7-17-2018
 *
 */

class Node {

	protected boolean isLeaf;
	protected List<Entry<Integer, Record>> entries;
	protected List<Node> children;
	protected Page page;
	protected Node parent;

	Node(Page page) {
		this.page = page;
		this.isLeaf = page.isLeaf();
		entries = page.getEntries();
		children = page.getChildrenPlus();
	}

	Record get(Integer key) {
		Entry<Node, Integer> entry = searchNode(key);
		Node node = entry.getKey();
		int k = entry.getValue();
		// key not found
		if (k < 0)
			return null;
		return node.getRecord(k);
	}
	
	boolean reomve(Integer key) {
		Entry<Node, Integer> entry = searchNode(key);
		Node node = entry.getKey();
		int k = entry.getValue();
		// key not found
		if (k < 0)
			return false;
		node.entries.remove(k);
		node.page.remove(k);
		return true;
	}

	Entry<Node, Integer> searchNode(Integer key) {
		// this node is a leaf node
		if (isLeaf) {
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i).getKey().equals(key))
					return new SimpleEntry<Node, Integer>(this, i);
			}
			// key not found
			return new SimpleEntry<Node, Integer>(this, -1);
			// this node is an inner node
		} else {
			// key < leftmost value, go left
			if (key < entries.get(0).getKey()) {
				children.get(0).setParent(this);
				return children.get(0).searchNode(key);
				// key >= rightmost value, go right
			} else if (key >= entries.get(entries.size() - 1).getKey()) {
				children.get(children.size() - 1).setParent(this);
				return children.get(children.size() - 1).searchNode(key);
				// find the correct child to go
			} else {
				int lo = 0, hi = entries.size() - 1, mid = 0;
				int diff;
				// binary search
				while (lo <= hi) {
					mid = (lo + hi) / 2;
					diff = key - entries.get(mid).getKey();
					// found the key, go to right child
					if (diff == 0) {
						children.get(mid + 1).setParent(this);
						return children.get(mid + 1).searchNode(key);
						// key > mid, search right half
					} else if (diff > 0) {
						lo = mid + 1;
						// key < mid, search left half
					} else {
						hi = mid - 1;
					}
				}
				children.get(lo).setParent(this);
				return children.get(lo).searchNode(key);
			}
		}
	}

	void setParent(Node parent) {
		this.parent = parent;
	}
	
	
	int getEntryKey(int k) {
		return entries.get(k).getKey();
	}
	
	int getEntrySize() {
		return entries.size();
	}
	
	int getEmptySpace() {
		return page.getEmptySpace();
	}
	
	Node split() {
		// get split page
		Page split = page.split();
		// refresh entries and children
		entries = page.getEntries();
		children = page.getChildren();
		return new Node(split);
	}

	Node getParent() {
		return parent;
	}

	private Record getRecord(int k) {
		return entries.get(k).getValue();
	}

	void updateRecord(Entry<Integer, Record> row, int k) {
		entries.set(k, row);
		// TODO: Update in page
	}
	
	void addEntry(Entry<Integer, Record> row) {
		entries.add(row);
		// TODO: write into page
	}
	
	void addChild(Node child) {
		children.add(child);
		// TODO: write into page
	}
	
	static Node newRoot() {
		// TODO: need to create a new inner page
		Node newroot = new Node(new Page());
		return newroot;
	}

}
