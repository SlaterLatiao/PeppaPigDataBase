package fileSystem;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class BTree{

	protected BNode root;

	public BTree(String path) {
		root = new BNode(new IndexPage(path));
	}
	
	public Record get(IndexRecord key) {
		return root.get(key);
	}
	// TODO: 
	public List<Record> getAll() {
		return null;
	}
	
	public void insertOrUpdate(Comparable key, Record value) {
		Entry<BNode, Integer> entry = root.searchNode(key);
		Entry<Comparable, Record> row = new SimpleEntry<Comparable, Record>(key, value);
		BNode node = entry.getKey();
		int k = entry.getValue();

		// found the key, update
		if (k > 0) {
			// TODO: check length of new record
			node.updateRecord(row, k);
			// not found, insert
		} else
			insert(row, node, null);
	}

	private void insert(Entry<Comparable, Record> row, BNode node, BNode child) {
		// new record fits, just insert
		if (row.getValue().getSpace() < node.getEmptySpace()) {
			node.addEntry(row);
			if (child != null)
				node.addChild(child);
			// new record doesn't fit, split
		} else {
			// mid entry in this node
			Entry<Comparable, Record> mid = node.getEntry((node.getEntrySize() + 1) / 2);
			BNode newNode = node.split();
			// TODO: add new node to page list
			
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node
				root = BNode.newRoot();
				root.addEntry(mid);
				root.addChild(node);
				root.addChild(newNode);
			}
			// parent exists, insert mid into parent
			else {
				insert(mid, node.getParent(), newNode);
			}
		}
	}
	
	

}