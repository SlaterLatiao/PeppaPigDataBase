package fileSystem;

import java.util.ArrayList;
import Common.DataType;

/**
 * @author Jinru Shi & Li Liu 2018-07-23
 */
public class indexRecord extends Record{

    public indexRecord(ArrayList<String> valuesOfColumns){
        super(valuesOfColumns);
    }

    public indexRecord(int pageNumOfRec, byte numOfColumn, ArrayList<Byte> dataTypes, ArrayList<String> valuesOfColumns) {
        super(pageNumOfRec,numOfColumn, dataTypes, valuesOfColumns);
    }

    public indexRecord(int rowId, short payLoad, byte numOfColumn, ArrayList<Byte> dataTypes,
                  ArrayList<String> valuesOfColumns) {
        super(rowId,payLoad,numOfColumn,dataTypes,valuesOfColumns);
    }
    public indexRecord(int rowId, int child) {
        super(rowId,child);
    }

}
