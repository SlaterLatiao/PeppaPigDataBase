package fileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LI LIU 2018-07-21
 * */
public class IndexTable {

    private BTree bTree;

    public IndexTable(String tablePath){
        bTree = new BTree(tablePath);
    }

    public List<IndexRecord> getAllIndexRecord(){
        List<IndexRecord> allRecords = new ArrayList<Record>();
        allRecords = bTree.getAll();
        return allRecords;
    }
}
