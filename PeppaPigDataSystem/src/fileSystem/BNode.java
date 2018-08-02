package fileSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class BNode{
	
	protected boolean isLeaf;
	protected List<IndexRecord> records;
	protected List<BNode> children;
	protected IndexPage page;
	protected BNode parent;

	BNode(IndexPage page) {
		this.page = page;
		this.isLeaf = page.isLeaf();
		records = new ArrayList<IndexRecord>(page.getRecords());
		List<IndexPage> list = page.getChildren();
		children = new ArrayList<BNode>();
		for (IndexPage p : list)
			children.add(new BNode(p));
	}
	

	Record get(IndexRecord key) {
		Entry<BNode, Integer> entry = searchNode(key);
		BNode node = entry.getKey();
		int k = entry.getValue();
		// key not found
		if (k < 0)
			return null;
		return node.getRecord(k);
	}

	Entry<BNode, Integer> searchNode(IndexRecord key) {
		// this node is a leaf node
		if (isLeaf) {
			for (int i = 0; i < records.size(); i++) {
				if (records.get(i).equals(key))
					return new SimpleEntry<BNode, Integer>(this, i);
			}
			// key not found
			return new SimpleEntry<BNode, Integer>(this, -1);
			// this node is an inner node
		} else {
			// key < leftmost value, go left
			if (key.compareTo(records.get(0))<0) {
				children.get(0).setParent(this);
				return children.get(0).searchNode(key);
				// key > rightmost value, go right
			} else if (key.compareTo(records.get(records.size() - 1)) > 0) {
				children.get(children.size() - 1).setParent(this);
				return children.get(children.size() - 1).searchNode(key);
				// find the correct child to go
			} else {
				int lo = 0, hi = records.size() - 1, mid = 0;
				int diff;
				// binary search
				while (lo <= hi) {
					mid = (lo + hi) / 2;
					IndexRecord midKey = records.get(mid);
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
	
	IndexRecord getEntry(int k) {
		return records.get(k);
	}
	
	
	Comparable getEntryKey(int k) {
		return records.get(k);
	}
	
	int getEntrySize() {
		return records.size();
	}
	
	int getEmptySpace() {
		return page.getEmptySpace();
	}
	
	BNode split() {
		// get split page
		IndexPage split = page.split();
		// refresh entries and children
		records = page.getRecordList();
		List<IndexPage> list = page.getChildren();
		children = new ArrayList<BNode>();
		for (IndexPage p : list)
			children.add(new BNode(p));
		return parent;
	}

	BNode getParent() {
		return parent;
	}

	IndexRecord getRecord(int k) {
		return records.get(k);
	}
	
	List<IndexRecord> getAll() {
		return records;
	}

	void updateRecord(IndexRecord record, int k) {
		records.set(k, record);
	}
	
	void addEntry(IndexRecord record) {
		records.add(record);
	}
	
	void addChild(BNode child) {
		children.add(child);
		// TODO: write into page
	}
	
	BNode newRoot() {
		BNode newroot = new BNode(page.split());
		return newroot;
	}


	int getRecordSize() {
		return records.size();
	}


	void addRecord(IndexRecord mid) {
		records.add(mid);
	}




}