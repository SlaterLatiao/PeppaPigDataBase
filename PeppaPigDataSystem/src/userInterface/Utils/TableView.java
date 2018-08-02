package userInterface.Utils;

import Common.Column;
import fileSystem.Record;
import java.util.ArrayList;
import java.util.List;

public class TableView {

    private String tableName;
    private ArrayList<Column> columns;
    private List<Record> records;

    public TableView(String tableName, ArrayList<Column> columns, List<Record> records){
        this.tableName = tableName;
        this.columns = columns;
        this.records = records;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ArrayList<String> getAllColumnNames(){
        ArrayList<String> colNames = new ArrayList<>();
        for(Column c : columns){
            colNames.add(c.getColumnName());
        }
        return colNames;
    }

    public ArrayList<ArrayList<String>> getAllValues() {
        ArrayList<ArrayList<String>> allValues = new ArrayList<>();
        ArrayList<String> recordValues;
        for(Record r:records){
            recordValues = new ArrayList<>();
            recordValues.add(r.getRowId().toString());
            for(String s:r.getValuesOfColumns()){
                recordValues.add(s.trim());
            }
            allValues.add(recordValues);

        }
        return  allValues;
    }


    public int getNumofColumns(){
        return columns.size();
    }

}
