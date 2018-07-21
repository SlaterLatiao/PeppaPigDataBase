package fileSystem;

import fileAccess.*;

import java.util.ArrayList;

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
        //TODO: NEED METHOD SUPPORT FROM BplusTree
        bplusTree.insert(record);
    }

    public ArrayList<Record> getAllRecord(){
        ArrayList<Record> allRecords = new ArrayList<>();
        //TODO: NEED METHOD SUPPORT FROM BplusTree
        allRecords = bplusTree.getAllRecord;
        return allRecords;
    }






}
