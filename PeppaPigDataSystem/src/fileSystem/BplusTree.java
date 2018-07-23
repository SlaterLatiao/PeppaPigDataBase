package fileSystem;

import java.util.ArrayList;
import java.util.List;
import FileAccess.Page;

public class BplusTree {
	
	protected Node root;

	public BplusTree(String path) {
		root = new Node(new Page(path));
	}
	
	public Record get(int key) {
		return root.get(key);
	}
	// TODO: 
	public List<Record> getAll() {
		return null;
	}
	// TODO:
	public List<Record> getByID(List<Integer> row_ids) {
		List<Record> list = new ArrayList<Record>();
		for (int i : row_ids)
			list.add(get(i));
		return list;
	}
	
	public void insert(Record value) {
		int key = root.getMaxIndex();
		insertOrUpdate(key+1, value);
	}
	
	public void update(Integer key, Record value) {
		insertOrUpdate(key, value);
	}

	private void insertOrUpdate(Integer key, Record value) {
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
			int key = row.getKey();
			// create new entry for pop up
			SimpleEntry<Integer, Record> newEntry= new SimpleEntry<Integer, Record>(key, null);
			Node newNode = node.split(row);
			
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node
				root = Node.newRoot();
				root.addEntry(row);
				root.addChild(node);
				root.addChild(newNode);
			}
			// parent exists, insert mid into parent
			else {
				insert(newEntry, node.getParent(), newNode);
			}
		}
	}
	
}