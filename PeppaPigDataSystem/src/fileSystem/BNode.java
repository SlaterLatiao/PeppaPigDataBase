package fileSystem;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class BNode{
	
	protected boolean isLeaf;
	protected List<Entry<Comparable, Record>> entries;
	protected List<BNode> children;
	protected Page page;
	protected BNode parent;

	BNode(Page page) {
		this.page = page;
		this.isLeaf = page.isLeaf();
		entries = page.getEntries();
		children = page.getChildren();
	}
	

	Record get(Comparable key) {
		Entry<BNode, Integer> entry = searchNode(key);
		BNode node = entry.getKey();
		int k = entry.getValue();
		// key not found
		if (k < 0)
			return null;
		return node.getRecord(k);
	}

	Entry<BNode, Integer> searchNode(Comparable key) {
		// this node is a leaf node
		if (isLeaf) {
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i).getKey().equals(key))
					return new SimpleEntry<BNode, Integer>(this, i);
			}
			// key not found
			return new SimpleEntry<BNode, Integer>(this, -1);
			// this node is an inner node
		} else {
			// key < leftmost value, go left
			if (key.compareTo(entries.get(0).getKey())<0) {
				children.get(0).setParent(this);
				return children.get(0).searchNode(key);
				// key > rightmost value, go right
			} else if (key.compareTo(entries.get(entries.size() - 1).getKey()) > 0) {
				children.get(children.size() - 1).setParent(this);
				return children.get(children.size() - 1).searchNode(key);
				// find the correct child to go
			} else {
				int lo = 0, hi = entries.size() - 1, mid = 0;
				int diff;
				// binary search
				while (lo <= hi) {
					mid = (lo + hi) / 2;
					Comparable midKey = entries.get(mid).getKey();
					// find the key, return
					if (key.compareTo(midKey) == 0) {
						return new SimpleEntry<BNode, Integer>(this, mid);
						// key > mid, search right half
					} else if (key.compareTo(midKey) > 0) {
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

	void setParent(BNode parent) {
		this.parent = parent;
	}
	
	Entry<Comparable, Record> getEntry(int k) {
		return entries.get(k);
	}
	
	
	Comparable getEntryKey(int k) {
		return entries.get(k).getKey();
	}
	
	int getEntrySize() {
		return entries.size();
	}
	
	int getEmptySpace() {
		return page.getEmptySpace();
	}
	
	BNode split() {
		// get split page
		Page split = page.split();
		// refresh entries and children
		entries = page.getEntries();
		children = page.getChildren();
		return new BNode(split);
	}

	BNode getParent() {
		return parent;
	}

	private Record getRecord(int k) {
		return entries.get(k).getValue();
	}

	void updateRecord(Entry<Comparable, Record> row, int k) {
		entries.set(k, row);
		// TODO: Update in page
	}
	
	void addEntry(Entry<Comparable, Record> row) {
		entries.add(row);
		// TODO: write into page
	}
	
	void addChild(BNode child) {
		children.add(child);
		// TODO: write into page
	}
	
	static BNode newRoot() {
		// TODO: need to create a new inner page
		BNode newroot = new BNode(new Page());
		return newroot;
	}
}