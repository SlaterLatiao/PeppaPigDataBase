package databaseAPI;

import Common.Column;
import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.SelectQueryInfo;

import java.util.ArrayList;
import java.util.List;

public class SelectQueryExe {
    public static void executeQuery(SelectQueryInfo info){

        // get target table for the oldRecords
        String tablePath;
        if(info.tableName.equals(Constants.SYSTEM_TABLES_TABLENAME) || info.tableName.equals(Constants.SYSTEM_COLUMNS_TABLENAME)){
            tablePath = Constants.SYSTEM_CATALOG_PATH + info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }else{
            tablePath = Constants.SYSTEM_USER_PATH + info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }
        Table targetTable = new Table(tablePath);

        List<Record> targetBodyRecords = null;

        if(info.isSelectAll){
            targetBodyRecords = targetTable.getAllRecord();
        }
        else{
            // get all rowids for the records which need to be selected
            ArrayList<Integer> rowids = WhereAPI.doWhere_getRowId(info.tableName,info.conditions,info.logiOper);

            targetBodyRecords = targetTable.getRowidsRecord(rowids);
        }

        List<String> targetHeaderColumns = null;

        // get all columns of the table
        ArrayList<Column> columns = General.getColumns(info.tableName);
        for(int i=0; i<columns.size();i++){
            if(columns.get(i).getColumnName().equals(info.columns.get(i))){
                targetHeaderColumns.add(columns.get(i).getColumnName());
            }
        }

        //use records to print out into a table
    }
}
