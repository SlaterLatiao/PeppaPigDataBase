package fileSystem;

import fileAccess.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LI LIU 2018-07-21
 * */
public class Table {

    private BplusTree bplusTree;

    public Table(String tablePath){
        //TODO: NEED CONSTRUCTOR SUPPORT FROM BplusTree which take tablePath as argument
        bplusTree = new BplusTree(tablePath);
    }

    //TODO: NEED IMPORT OBJECT CLASS 'RECORD' FROM fileAccess PACKAGE

    public void insert(Record record){
        bplusTree.insertOrUpdate(record);
    }

    public List<Record> getAllRecord(){
        List<Record> allRecords = new List<Record>();
        allRecords = bplusTree.getAll();
        return allRecords;
    }

    public List<Record> getRowidsRecord(List<Integer> row_ids){
        List<Record> allRecords = new List<Record>();
        allRecords = bplusTree.getAll(row_ids);
        return allRecords;
    }


}
