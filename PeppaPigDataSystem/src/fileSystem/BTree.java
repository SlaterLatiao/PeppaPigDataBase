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

	public List<IndexRecord> getAll() {
		return root.getAll();
	}
	
	public void insertOrUpdate(IndexRecord record) {
		Entry<BNode, Integer> entry = root.searchNode(record);
		BNode node = entry.getKey();
		int k = entry.getValue();

		// found the key, update
		if (k > 0) {
			// TODO: check length of new record
			node.updateRecord(record, k);
			// not found, insert
		} else
			insert(record, node, null);
	}

	private void insert(IndexRecord record, BNode node, BNode child) {
		// new record fits, just insert
		if (record.getSpace() < node.getEmptySpace()) {
			node.addEntry(record);
			if (child != null)
				node.addChild(child);
			// new record doesn't fit, split
		} else {
			// mid entry in this node
			IndexRecord mid = node.getRecord((node.getRecordSize() + 1) / 2);
			BNode newNode = node.split();
			// TODO: add new node to page list
			
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node
				root = root.newRoot();
				root.addRecord(mid);
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