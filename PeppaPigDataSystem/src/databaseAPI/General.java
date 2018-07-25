package databaseAPI;

import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;

import java.util.List;

public class General {
    public static boolean checkTableExists(String tableName){
        Table davisTable = new Table(Constants.SYSTEM_TABLES_PATH);
        List<Record> records = davisTable.getAllRecord();
        if(records == null){
            return false;
        }else{
            for(Record r:records){
                if(tableName.equals(r.getValuesOfColumns().get(1))){
                    return true;
                }
            }
        }
        return false;
    }
}
