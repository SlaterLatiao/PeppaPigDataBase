package fileSystem;

import java.util.ArrayList;


/**
 * @author LI LIU 2018-07-16
 * */
public class Record {

    private int pageNumOfRec;
    private short payLoad;
    private int rowid;
    private byte numOfColumn;
    private ArrayList<Byte> dataTypes;
    private ArrayList<Object> valuesOfColumns;

    /*public Record(int pageNumOfRec,int rowid,byte numOfColumn,ArrayList<Byte> dataTypes,ArrayList<String> valuesOfColumns){
        this.pageNumOfRec = pageNumOfRec;
        this.rowid = rowid;
        this.numOfColumn = numOfColumn;
        this.dataTypes = dataTypes;
        this.valuesOfColumns = valuesOfColumns;

    }*/

    public int getPageNumOfRec() {
        return pageNumOfRec;
    }

    public void setPageNumOfRec(int pageNumOfRec) {
        this.pageNumOfRec = pageNumOfRec;
    }

    public short getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(short payLoad) {
        this.payLoad = payLoad;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public byte getNumOfColumn() {
        return numOfColumn;
    }

    public void setNumOfColumn(byte numOfColumn) {
        this.numOfColumn = numOfColumn;
    }

    public ArrayList<Byte> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(ArrayList<Byte> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public ArrayList<Object> getValuesOfColumns() {
        return valuesOfColumns;
    }

    public void setValuesOfColumns(ArrayList<Object> valuesOfColumns) {
        this.valuesOfColumns = valuesOfColumns;
    }
    public short getChildrenRecord(){return 0;}

}

