package databaseAPI;

import Common.Column;
import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.UpdateQueryInfo;
import java.util.ArrayList;
import java.util.List;

public class UpdateQueryExe {
    public static void executeQuery(UpdateQueryInfo info){

        // get all rowids for the records which need to be deleted
        ArrayList<Integer> rowids = WhereAPI.doWhere_getRowId(info.tableName,info.conditions,info.logiOper);

        // get all columns of the table
        ArrayList<Column> columns = General.getColumns(info.tableName);
        int pos = -1;
        // get the column ordinal_position which need to be updated
        for(int i=0; i<columns.size();i++){
            if(columns.get(i).getColumnName().equals(info.columnName)){
                pos = i-1;
            }
        }

        // if pos == -1, error . This should not happen. Something wired.
        if(pos == -1){
            System.out.println("Update Failed!!!");
            return;
        }

        // get target table for the oldRecords
        String tablePath;
        if(info.tableName.equals(Constants.SYSTEM_TABLES_TABLENAME) || info.tableName.equals(Constants.SYSTEM_COLUMNS_TABLENAME)){
            tablePath = Constants.SYSTEM_CATALOG_PATH +"/"+ info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }else{
            tablePath = Constants.SYSTEM_USER_PATH + "/"+ info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }
        Table targetTable = new Table(tablePath);

        // get the list of old records which need to be updated
        List<Record> targetRecords = targetTable.getRowidsRecord(rowids);

        //change the value of old records, and update records into file
        for(Record r:targetRecords){
            r.getValuesOfColumns().set(pos,info.value);
            targetTable.updateRecord(r.getRowId(),r);
        }
    }

}
