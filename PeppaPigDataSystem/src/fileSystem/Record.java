package fileSystem;
import java.util.ArrayList;
import Common.DataType;

/**
 * @author Jinru Shi & Li Liu 2018-07-23
 * */
public class Record {

    private int pageNumOfRec;
    private short payLoad;
    protected int rowId;
    private byte numOfColumn;
    private ArrayList<Byte> dataTypes;
    private ArrayList<String> valuesOfColumns;
    // only for inner page
    private short childrenRecord;
    private DataType data;
    public Record(){
        data = new DataType();
    }


    public int getPageNumOfRec() {
        return pageNumOfRec;
    }
    public int getSpace(){
        return this.payLoad+6;
    }
    public void setPageNumOfRec(int pageNumOfRec) {
        this.pageNumOfRec = pageNumOfRec;
    }

    public short getPayLoad() {
        return payLoad;
    }

    public void calculatePayLoad() {
        for(int i=0;i<this.getNumOfColumn();i++) {
            String object = this.getValuesOfColumns().get(i);
            if(this.getDataTypes().get(i)==data.nameToSerialCode("null")) {
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("tinyint")) {
                this.payLoad+= 1;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("smallint")) {
                this.payLoad+= 2;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("int")) {
                this.payLoad+= 4;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("bigint")) {
                this.payLoad+= 8;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("float")) {
                this.payLoad+= 4;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("double")) {
                this.payLoad+= 8;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("datetime")) {
                this.payLoad+= 8;
            }
            if(this.getDataTypes().get(i)==data.nameToSerialCode("date")) {
                this.payLoad+= 8;
            }
            if(this.getDataTypes().get(i)>data.nameToSerialCode("text")){
                byte length = (byte) (this.getDataTypes().get(i) - data.nameToSerialCode("text"));
                this.payLoad+= length;
            }
        }
        this.payLoad+= 1;
    }

    public void setPayLoad(short payLoad ) {
        this.payLoad = payLoad ;
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

    public ArrayList<String> getValuesOfColumns() {
        return valuesOfColumns;
    }

    public void setValuesOfColumns(ArrayList<String> valuesOfColumns) {
        this.valuesOfColumns = valuesOfColumns;
    }

    public short getChildrenRecord(){
        return this.childrenRecord;}

    public void setChildrenRecord(short childrenRecord){
        this.childrenRecord=childrenRecord;
    }

}

