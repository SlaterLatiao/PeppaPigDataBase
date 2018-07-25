package databaseAPI;

import Common.Constants;
import Common.DataType;
import fileSystem.Table;
import fileSystem.Record;
import userInterface.QueriesInfo.CreateTableQueryInfo;
import Common.Column;

import java.util.ArrayList;
import java.util.List;

public class CreateTableQueryExe {

    public static void initialCreateSystemTables(){
        Table davisTable = new Table(Constants.SYSTEM_TABLES_PATH);
        Table columnsTable = new Table(Constants.SYSTEM_COLUMNS_PATH);
    }

    public static void executeQuery(CreateTableQueryInfo info){

        String tablePath;
        // check whether the table is system talbes
        if(!(info.tableName.equals(Constants.SYSTEM_TABLES_TABLENAME) || info.tableName.equals(Constants.SYSTEM_COLUMNS_TABLENAME))){
            tablePath = Constants.SYSTEM_USER_PATH;
            // Create new table file
            Table newTable = new Table(tablePath+"/"+info.tableName+Constants.DEFAULT_FILE_EXTENSION);
        }

        //UPDATE SYSTEM TABLES-------------------------

        //Insert table name into davisTable
        Table davisTable = new Table(Constants.SYSTEM_TABLES_PATH);
        Record newRec = new Record();

        newRec.setNumOfColumn(Constants.DAVIS_TABLES_NUM_OF_COLUMNS);
        ArrayList<Byte> dataTypes = new ArrayList<>();
        dataTypes.add((byte)(new DataType("text").serialCode+info.tableName.length()));
        dataTypes.add(new DataType("smallint").serialCode);
        newRec.setDataTypes(dataTypes);

        ArrayList<String> values = new ArrayList<>();
        values.add(info.tableName); // value of 'table_name'column in tables_table
        values.add("0"); // value of 'root_page'column in tables_table
        newRec.setValuesOfColumns(values);

        davisTable.insert(newRec);


        //Insert columns into colTable
        Table colTable = new Table(Constants.SYSTEM_COLUMNS_PATH);

            //Insert column"rowid" into columns_talbe first
        Record rowid = new Record();
        rowid.setNumOfColumn(Constants.DAVIS_COLUMNS_NUM_OF_COLUMNS);

        ArrayList<Byte> rowidDataType = new ArrayList<>();
        rowidDataType.add((byte)(new DataType("text").serialCode + info.tableName.length())); // table_name
        rowidDataType.add((byte)(new DataType("text").serialCode + "rowid".length())); // column_name
        rowidDataType.add((byte)(new DataType("text").serialCode + "int".length()));//dataType
        rowidDataType.add(new DataType("tinyint").serialCode); // ordinal_position
        rowidDataType.add(new DataType("tinyint").serialCode); // is_nullable
        rowidDataType.add(new DataType("tinyint").serialCode); // column_key

        ArrayList<String> rowidValues = new ArrayList<>();
        rowidValues.add(info.tableName);
        rowidValues.add("rowid");
        rowidValues.add("0x03");
        rowidValues.add("1");
        rowidValues.add("0");
        rowidValues.add("0");

        rowid.setDataTypes(rowidDataType);
        rowid.setValuesOfColumns(rowidValues);
        colTable.insert(rowid);

             //Insert other columns into columns_talbe
        for(int i=0; i<info.columns.size(); i++){
            Column c = info.columns.get(i);
            Record columnRecord =  new Record();
            columnRecord.setNumOfColumn(Constants.DAVIS_COLUMNS_NUM_OF_COLUMNS);
            ArrayList<Byte> colDataTypes = new ArrayList<>();
            colDataTypes.add((byte)(new DataType("text").serialCode + info.tableName.length())); // table_name
            colDataTypes.add((byte)(new DataType("text").serialCode + c.getColumnName().length())); // column_name
            colDataTypes.add((byte)(new DataType("text").serialCode + c.getDataType().dataTypeName.length()));//dataType
            colDataTypes.add(new DataType("tinyint").serialCode); // ordinal_position
            colDataTypes.add(new DataType("tinyint").serialCode); // is_nullable
            colDataTypes.add(new DataType("tinyint").serialCode); // column_key

            ArrayList<String> columnValues = new ArrayList<>();

            columnValues.add(info.tableName);
            columnValues.add(c.getColumnName());
            columnValues.add(""+c.getDataType().serialCode);
            columnValues.add(""+i+1);
            if(c.isNull()){
                columnValues.add("1");
            }else{
                columnValues.add("0");
            }
            if(c.isPrimary()){
                columnValues.add("1");
            }else{
                columnValues.add("0");
            }

            columnRecord.setDataTypes(colDataTypes);
            columnRecord.setValuesOfColumns(columnValues);

            colTable.insert(columnRecord);
        }
    }
}
