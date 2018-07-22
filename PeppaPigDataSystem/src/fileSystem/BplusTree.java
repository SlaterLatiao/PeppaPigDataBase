package fileSystem;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

public class BplusTree {
	
	protected Node root;

	public BplusTree(String fileName) {
		root = new Node(new Page(fileName));
	}
	
	public Record get(int key) {
		return root.get(key);
	}
	// TODO: 
	public List<Record> getAll() {
		return null;
	}
	// TODO:
	public Table getAll(List<Integer> row_ids) {
		return null;
	}

	public void insertOrUpdate(Integer key, Record value) {
		Entry<Node, Integer> entry = root.searchNode(key);
		Entry<Integer, Record> row = new SimpleEntry<Integer, Record>(key, value);
		Node node = entry.getKey();
		int k = entry.getValue();

		// found the key, update
		if (k > 0) {
			// TODO: check length of new record
			node.updateRecord(row, k);
			// not found, insert
		} else
			insert(row, node, null);
	}
	
	public boolean remove(Integer key) {
		root.remove(key);
	}

	private void insert(Entry<Integer, Record> row, Node node, Node child) {
		// new record fits, just insert
		if (row.getValue().getSpace() < node.getEmptySpace()) {
			node.addEntry(row);
			if (child != null)
				node.addChild(child);
			// new record doesn't fit, split
		} else {
			// mid key in this node
			int mid = node.getEntryKey((node.getEntrySize() + 1) / 2);
			// create new entry on mid for pop up
			Entry<Integer, Record> midEntry = new SimpleEntry<Integer, Record>(mid, null);
			Node newNode = node.split();
			// TODO: add new node to page list
			
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node
				root = Node.newRoot();
				root.addEntry(midEntry);
				root.addChild(node);
				root.addChild(newNode);
			}
			// parent exists, insert mid into parent
			else {
				insert(midEntry, node.getParent(), newNode);
			}
		}
	}
	
}