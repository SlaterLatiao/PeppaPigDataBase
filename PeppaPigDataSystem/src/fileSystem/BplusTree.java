package fileSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
		Node node = root.getLeftMost();
		List<Record> list = new ArrayList<Record>();
		while (node != null) {
			list.addAll(node.getRecords());
			node = node.getNext();
		}
		return list;
	}

	// TODO:
	public List<Record> getByID(List<Integer> row_ids) {
		List<Record> list = new ArrayList<Record>();
		for (int i : row_ids)
			list.add(get(i));
		return list;
	}

	public void insert(Record record) {
		int key = root.getMaxRowID();
		record.setRowId(key + 1);
		insertOrUpdate(key + 1, record);
	}

	public void update(Integer key, Record value) {
		insertOrUpdate(key, value);
	}

	private void insertOrUpdate(Integer key, Record record) {
		Entry<Node, Integer> entry = root.searchNode(key);
		Node node = entry.getKey();
		int k = entry.getValue();

		// found the key, update
		if (k > 0) {
			// TODO: check length of new record
			node.updateRecord(k, record);
			// not found, insert
		} else
			insert(record, node);
	}

	public boolean remove(Integer key) {
		return root.reomve(key);
	}

	private void insert(Record record, Node node) {
		// new record fits, just insert
		if (record.getSpace() < node.getEmptySpace()) {
			node.addRecord(record);
			// new record doesn't fit, split
		} else {
			// rowid in this node
			int key = record.getRowId();
			// create new entry for pop up
			Node newNode = node.split(record);
			Record inner = new Record(key, newNode.getPageNum());
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node
				Node newroot = root.newRoot();
				int newRootPnum = newroot.getPageNum();
				node.setPNum(newRootPnum);
				newroot.setPNum(0);
				newroot.addLeftChild(node);
				newroot.addChild(newNode, inner);
				root.exchangeContent(newroot);
				root = newroot;
			}
			// parent exists, insert mid into parent
			else {
				insertInner(inner, node.getParent(), newNode);
			}
		}
	}

	private void insertInner(Record record, Node node, Node child) {
		if (record.getSpace() < node.getEmptySpace()) {
			node.addChild(child, record);
			// new record doesn't fit, split
		} else {
			// rowid in this node
			int key = record.getRowId();
			// create new entry for pop up
			Node newInner = node.split(record);
			Record inner = new Record(key, newInner.getPageNum());
			// no parent, create one
			if (node.getParent() == null) {
				// create a new root node 
				Node newroot = root.newRoot();
				int newRootPnum = newroot.getPageNum();
				root.exchangeContent(newroot);
				node.setPNum(newRootPnum);
				newroot.setPNum(0);
				newroot.addLeftChild(node);
				newroot.addChild(newInner, inner);
				root = newroot;
			}
			// parent exists, insert mid into parent
			else {
				insertInner(inner, node.getParent(), newInner);
			}
		}

	}
}
