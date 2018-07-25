package databaseAPI;

import Common.Column;
import Common.Constants;
import Common.DataType;
import fileSystem.Record;
import fileSystem.Table;
import java.util.ArrayList;
import java.util.List;

public class General {
    public static boolean checkTableExists(String tableName) {
        Table davisTable = new Table(Constants.SYSTEM_TABLES_PATH);
        List<Record> records = davisTable.getAllRecord();
        if (records == null) {
            return false;
        } else {
            for (Record r : records) {
                if (tableName.equals(r.getValuesOfColumns().get(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<Column> getColumns(String tableName) {

        ArrayList<Column> columns = new ArrayList<>();

        //get all the records in davisbase_columns table
        Table t = new Table(Constants.SYSTEM_COLUMNS_PATH);
        List<Record> allRecords = t.getAllRecord();

        for (Record r : allRecords) { // for each record, check whether the record belongs to targer table.
            if (r.getValuesOfColumns().get(0).equals(tableName)){
                String columnName = r.getValuesOfColumns().get(1);
                DataType dataType = new DataType(r.getValuesOfColumns().get(2));
                boolean is_primary;
                if(r.getValuesOfColumns().get(5).equals("1")){
                    is_primary = true;
                }else {
                    is_primary = false;
                }
                boolean is_nullable;
                if(r.getValuesOfColumns().get(4).equals("1")){
                    is_nullable = true;
                }else{
                    is_nullable = false;
                }
                Column col = new Column(columnName,dataType,is_primary,is_nullable);
                columns.add(col);
            }
        }
        return  columns;
    }
}
