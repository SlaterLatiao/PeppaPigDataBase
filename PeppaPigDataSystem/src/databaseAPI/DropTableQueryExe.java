package databaseAPI;

import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.DropTableQueryInfo;

import java.util.List;

public class DropTableQueryExe {
    public static void executeQuery(DropTableQueryInfo info){
        //get all the records in davisbase_table table
        Table davisTable = new Table(Constants.SYSTEM_TABLES_PATH);
        List<Record> allRecordsDavisTable = davisTable.getAllRecord();

        for (Record r : allRecordsDavisTable) { // for each record, check whether the record belongs to targer table.
            if (r.getValuesOfColumns().get(0).equals(info.tableName)){
                davisTable.deleteRecord(r.getRowId());
            }
        }

        //get all the records in davisbase_columns table
        Table davisTableColumn = new Table(Constants.SYSTEM_COLUMNS_PATH);
        List<Record> allRecordsDavisTableColumn = davisTableColumn.getAllRecord();

        for (Record r : allRecordsDavisTableColumn) { // for each record, check whether the record belongs to targer table.
            if (r.getValuesOfColumns().get(0).equals(info.tableName)){
                davisTableColumn.deleteRecord(r.getRowId());
            }
        }

        Table dropTargetTable = new Table(Constants.SYSTEM_USER_PATH +"/"+ info.tableName + Constants.DEFAULT_FILE_EXTENSION);
        dropTargetTable.dropTable();
    }

}
