package fileSystem;
import java.util.ArrayList;


/**
 * @author Jinru Shi & Li Liu 2018-07-23
 * */
public class Record {

    private int pageNumOfRec;
    private short payLoad;
    protected int rowId;
    private byte numOfColumn;
    private ArrayList<Byte> dataTypes;
    private ArrayList<Object> valuesOfColumns;
    // only for inner page
    private short childrenRecord;

    public Record(){
    }


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

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
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

    public short getChildrenRecord(){
        return this.childrenRecord;}

    public void setChildrenRecord(short childrenRecord){
        this.childrenRecord=childrenRecord;
    }

}

