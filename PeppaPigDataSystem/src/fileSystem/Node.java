package fileSystem;

import java.util.List;
import java.util.Map.Entry;
import FileAccess.Page;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 * @author Zenong 7-17-2018
 *
 */

class Node {

	protected boolean isLeaf;
	protected List<Record> records;
	protected List<Node> children;
	protected Page page;
	protected Node parent;

	Node(Page page) {
		this.page = page;
		this.isLeaf = page.isLeaf();
		records = page.getRecordList();
		List<Page> list = page.getChildren();
		children = new ArrayList<Node>();
		for (Page p : list)
			children.add(new Node(p));
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
		int row_id = records.get(k).getRowId();
		node.records.remove(k);
		node.page.remove(row_id);
		return true;
	}

	Entry<Node, Integer> searchNode(Integer key) {
		// this node is a leaf node
		if (isLeaf) {
			for (int i = 0; i < records.size(); i++) {
				if (records.get(i).getRowId().equals(key))
					return new SimpleEntry<Node, Integer>(this, i);
			}
			// key not found
			return new SimpleEntry<Node, Integer>(this, -1);
			// this node is an inner node
		} else {
			// key < leftmost value, go left
			if (key < records.get(0).getRowId()) {
				children.get(0).setParent(this);
				return children.get(0).searchNode(key);
				// key >= rightmost value, go right
			} else if (key >= records.get(records.size() - 1).getRowId()) {
				children.get(children.size() - 1).setParent(this);
				return children.get(children.size() - 1).searchNode(key);
				// find the correct child to go
			} else {
				int lo = 0, hi = records.size() - 1, mid = 0;
				int diff;
				// binary search
				while (lo <= hi) {
					mid = (lo + hi) / 2;
					diff = key - records.get(mid).getRowId();
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

	int getRecordRowID(int k) {
		return records.get(k).getRowId();
	}

	int getRecordsSize() {
		return records.size();
	}

	int getEmptySpace() {
		return page.getEmptySpace();
	}

	Node split(Record r) {
		// get split page
		Page split = page.getNewPage(true);
		Node newLeaf = new Node(split);
		newLeaf.addRecord(r);
		return newLeaf;
	}

	Node getParent() {
		return parent;
	}

	private Record getRecord(int k) {
		return records.get(k);
	}

	void updateRecord(int k, Record r) {
		records.set(k, r);
		page.update(k, r);
	}

	void addRecord(Record r) {
		records.add(r);
		page.addRecord(r);
	}

	void addChild(Node child) {
		children.add(child);
		page.addChild(child.page);
	}

	void addInner(Record r) {
		records.add(r);
		int key = r.getRowId();
		page.addInner(key);
	}

	 Node newRoot() {
	 // TODO: need to create a new inner page
	 Node newroot = new Node(page.getNewPage(false));
	 return newroot;
	 }

	public int getMaxIndex() {
		return page.getMaxIndex();
	}
	
	void setRootPointer() {
		page.setRootPointer();
	}
}
