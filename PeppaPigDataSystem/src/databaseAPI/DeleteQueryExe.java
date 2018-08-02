package databaseAPI;

import Common.Constants;
import fileSystem.Table;
import userInterface.QueriesInfo.DeleteQueryInfo;

import java.util.ArrayList;

public class DeleteQueryExe {
    public static void executeQuery(DeleteQueryInfo info){
        // get all rowids for the records which need to be delete
        ArrayList<Integer> rowids = WhereAPI.doWhere_getRowId(info.tableName,info.conditions,info.logiOper);

        String tablePath;
        // check whether the table is a system table
        if(info.tableName.equals(Constants.SYSTEM_TABLES_TABLENAME) || info.tableName.equals(Constants.SYSTEM_COLUMNS_TABLENAME)){
            tablePath = Constants.SYSTEM_CATALOG_PATH + "/"+info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }else{
            tablePath = Constants.SYSTEM_USER_PATH + "/"+ info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }

        Table targetTable = new Table(tablePath);
        //delete records by rowid_list
        for(int rowid: rowids){
            targetTable.deleteRecord(rowid);
        }
    }

}
