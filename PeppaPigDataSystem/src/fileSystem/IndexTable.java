package fileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LI LIU 2018-07-21
 * */
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
}
