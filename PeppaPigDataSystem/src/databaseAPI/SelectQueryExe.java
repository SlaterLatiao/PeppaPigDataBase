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
            tablePath = Constants.SYSTEM_CATALOG_PATH +"/"+ info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }else{
            tablePath = Constants.SYSTEM_USER_PATH + "/"+info.tableName + Constants.DEFAULT_FILE_EXTENSION;
        }
        Table targetTable = new Table(tablePath);


        List<Record> targetBodyFullRecords = new ArrayList<>();

        if(info.conditions.size() == 0 || (info.conditions.size()==1 && info.conditions.get(0)==null)){
            targetBodyFullRecords = targetTable.getAllRecord();
        }
        else{
            //get all rowids for the records which need to be selected
            ArrayList<Integer> rowids = WhereAPI.doWhere_getRowId(info.tableName,info.conditions,info.logiOper);
            targetBodyFullRecords = targetTable.getRowidsRecord(rowids);
        }

        // get all targetHeaderFullColumns of the table
        ArrayList<Column> targetHeaderFullColumns = General.getColumns(info.tableName);

        if(info.isSelectAll){
            TableView selectAllTableView = new TableView(info.tableName, targetHeaderFullColumns,targetBodyFullRecords);
            Displayer tempDisplay = new Displayer(selectAllTableView);
        }
        else{
            // Filter column full to column filter
            ArrayList<Column> targetHeaderFilterColumns = new ArrayList<>();
            targetHeaderFilterColumns.add(targetHeaderFullColumns.get(0));

            //Filter record full to record filter
            ArrayList<Record> targetBodyFilterRecords = new ArrayList<>();

            // get the column ordinal_position which need to be updated
            ArrayList<Integer> pos = new ArrayList<>();
            for (int i = 0; i < targetHeaderFullColumns.size(); i++) {
                for (String col: info.columns){
                    if (targetHeaderFullColumns.get(i).getColumnName().equals(col)) {
                        pos.add(i);
                        targetHeaderFilterColumns.add(targetHeaderFullColumns.get(i));
                    }
                }
            }

            for (Record r : targetBodyFullRecords) {
                ArrayList<String> valCol = new ArrayList<>();
                for (int p : pos) {
                    valCol.add(r.getValuesOfColumns().get(p-1));
                }
                Record tempR = new Record(valCol);
                tempR.setRowId(r.getRowId());
                targetBodyFilterRecords.add(tempR);
            }

            TableView selectAllTableView = new TableView(info.tableName, targetHeaderFilterColumns,targetBodyFilterRecords);
            Displayer tempDisplay = new Displayer(selectAllTableView);

        }
    }

}