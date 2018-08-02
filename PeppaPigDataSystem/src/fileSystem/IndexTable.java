package fileSystem;

import java.util.ArrayList;
import java.util.List;

public class IndexTable {

    private BTree bTree;

    public IndexTable(String indexTablePath){
        bTree = new BTree(indexTablePath);
    }

    public List<IndexRecord> getAllIndexRecord(){
        List<IndexRecord> allRecords = new ArrayList<IndexRecord>();
        allRecords = bTree.getAll();
        return allRecords;
    }

    public void drop(){
    	bTree.drop();
    }
    
}
