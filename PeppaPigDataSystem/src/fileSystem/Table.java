package fileSystem;

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
        bplusTree.insert(record);
    }

    public List<Record> getAllRecord(){
        List<Record> allRecords = new ArrayList <Record>();
        allRecords = bplusTree.getAll();
        return allRecords;
    }

    public List<Record> getRowidsRecord(List<Integer> row_ids){
        List<Record> rowids_Records = new ArrayList<Record>();
        rowids_Records = bplusTree.getByID(row_ids);
        return rowids_Records;
    }

    public void deleteRecord(int rowid){
        bplusTree.remove(rowid);
    }

    public void updateRecord(int rowid,Record record){
        bplusTree.update(rowid,record);
    }
    
    public void dropTable() {
    	bplusTree.dropTable();
    }


}
