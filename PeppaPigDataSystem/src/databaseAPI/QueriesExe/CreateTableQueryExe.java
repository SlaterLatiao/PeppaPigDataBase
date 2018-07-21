package databaseAPI.QueriesExe;

import fileSystem.Table;
import userInterface.QueriesInfo.CreateTableQueryInfo;
import Common.Column;

import java.util.ArrayList;

public class CreateTableQueryExe {
    public static boolean checkTableExists(String tableName){
        Table davisTable = new Table("catalog/davisTable");

        ArrayList<Record> records = davisTable.getAllRecord();

        //TODO Search if tableName exists in davisTable records, and return corresponding boolean
        return false;
    }

    public static boolean executeQuery(CreateTableQueryInfo info){

        //TODO What exactly does this do? @Slater
        //file.Table("User/tableName");

        //Insert table name into davisTable
        Table davisTable = new Table("catalog/davisTable");

        //TODO create Record class, and what parameters does record object need?

        //record contains table names
        Record record_tableName = new Record(info.tableName);
        davisTable.insert(record_tableName);

        //Insert columns into colTable
        Table colTable = new Table("catalog/colTable");

        //each record contains column names
        for(Column col : info.columns){
            Record record_tempCol = new Record(col.getColumnName());
            colTable.insert(record_tempCol);
        }

        //TODO give me a return boolean?
        return false;
    }
}
