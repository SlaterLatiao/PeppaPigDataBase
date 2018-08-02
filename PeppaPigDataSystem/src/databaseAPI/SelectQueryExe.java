package databaseAPI;

import Common.Column;
import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.SelectQueryInfo;
import userInterface.Utils.Displayer;
import userInterface.Utils.TableView;

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

        List<Record> targetBodyFullRecords = null;

        if(info.isSelectAll){
            targetBodyFullRecords = targetTable.getAllRecord();
        }
        else{
            // get all rowids for the records which need to be selected
            ArrayList<Integer> rowids = WhereAPI.doWhere_getRowId(info.tableName,info.conditions,info.logiOper);

            targetBodyFullRecords = targetTable.getRowidsRecord(rowids);
        }

        // get all targetHeaderFullColumns of the table
        ArrayList<Column> targetHeaderFullColumns = General.getColumns(info.tableName);

        // Filter column full to column filter
        ArrayList<Column> targetHeaderFilterColumns = new ArrayList<>();

        // get the column ordinal_position which need to be updated
        ArrayList<Integer> pos = null;
        for(int i=0; i<targetHeaderFullColumns.size();i++){
            if(targetHeaderFullColumns.get(i).getColumnName().equals(info.columns.get(i))){
                pos.add(i);
                targetHeaderFilterColumns.add(targetHeaderFullColumns.get(i));
            }
        }

        ArrayList<Record> targetBodyFilterRecords = null;

        //Filter record full to record filter
        for(Record r : targetBodyFullRecords){
            ArrayList<String> valCol = null;

            for(int p : pos){
                valCol.add(r.getValuesOfColumns().get(p));
            }

            Record tempR = new Record(valCol);

            targetBodyFilterRecords.add(tempR);
        }

        if(info.isSelectAll){
            TableView selectAllTableView = new TableView(info.tableName, targetHeaderFullColumns,targetBodyFullRecords);
            Displayer tempDisplay = new Displayer(selectAllTableView);
        }
        else{
            TableView selectAllTableView = new TableView(info.tableName, targetHeaderFilterColumns,targetBodyFilterRecords);
            Displayer tempDisplay = new Displayer(selectAllTableView);
        }


    }
}