package userInterface.Utils;


import Common.Column;
import Common.DataType;
import fileSystem.Record;
import java.util.ArrayList;
import java.util.HashMap;

public class Displayer {

    private int PADDING_SIZE = 2;
    private String NEW_LINE = "\n";
    private String TABLE_JOINT_SYMBOL = "+";
    private String TABLE_V_SPLIT_SYMBOL = "|";
    private String TABLE_H_SPLIT_SYMBOL = "-";

    private ArrayList<String> headerList;
    private ArrayList<ArrayList<String>> recordList;
    private TableView table;

    public Displayer(TableView table){
        this.table = table;
        initDisplayer();
    }

    public void initDisplayer(){
        this.headerList = table.getAllColumnNames();
        this.recordList = table.getAllValues();
    }

    public void displayTable(ArrayList<String> headerList, ArrayList<ArrayList<String>> recordList,int[] overRiddenHeaderHeight)
    {
        StringBuilder stringBuilder = new StringBuilder();

        int rowHeight = overRiddenHeaderHeight.length > 0 ? overRiddenHeaderHeight[0] : 1;

        HashMap<Integer,Integer> columnMaxWidth = getMaxWidthOfColumns(headerList,recordList);

        // display the table header
        stringBuilder.append(NEW_LINE);
        createDividerLine(stringBuilder,headerList.size(),columnMaxWidth);
        stringBuilder.append(NEW_LINE);

        for(int headerIndex=0; headerIndex<headerList.size();headerIndex++){
            String header = headerList.get(headerIndex);
            fillCell(stringBuilder,header,headerIndex,columnMaxWidth);
        }
        stringBuilder.append(NEW_LINE);
        createDividerLine(stringBuilder,headerList.size(),columnMaxWidth);

        // display records
        for(ArrayList<String> record : recordList){
            for(int i = 0; i < rowHeight; i++){
                stringBuilder.append(NEW_LINE);
            }

            for(int cellIndex = 0; cellIndex < record.size(); cellIndex++){
                String value = record.get(cellIndex);
                fillCell(stringBuilder,value,cellIndex,columnMaxWidth);
            }
        }

        stringBuilder.append(NEW_LINE);
        createDividerLine(stringBuilder, headerList.size(), columnMaxWidth);
        stringBuilder.append(NEW_LINE);


        System.out.println(stringBuilder.toString());
    }


    private HashMap<Integer,Integer> getMaxWidthOfColumns(ArrayList<String> headersList, ArrayList<ArrayList<String>> recordList) {

        HashMap<Integer,Integer> columnMaxWidth = new HashMap<>();

        //set the column width as each column header length
        for(int i=0; i<headersList.size(); i++){
            columnMaxWidth.put(i,headersList.get(i).length());
        }

        // update the column width with the max length of the record in each column
        for(ArrayList<String> record : recordList){
            for(int i=0; i<headersList.size(); i++){
                if(record.get(i).length() > columnMaxWidth.get(i)){
                    columnMaxWidth.put(i,record.get(i).length());
                }
            }
        }

        // make sure the column width to be an even number
        for(int i=0; i<headersList.size(); i++){
            if(columnMaxWidth.get(i) %2 != 0){
                columnMaxWidth.put(i,columnMaxWidth.get(i)+1);
            }
        }
        return  columnMaxWidth;
    }


    private void createDividerLine(StringBuilder stringBuilder, int size, HashMap<Integer, Integer> columnMaxWidth) {
        for (int i = 0; i < size; i++) {
            if(i == 0)
            {
                stringBuilder.append(TABLE_JOINT_SYMBOL);
            }
            for (int j = 0; j < columnMaxWidth.get(i) + PADDING_SIZE * 2 ; j++) {
                stringBuilder.append(TABLE_H_SPLIT_SYMBOL);
            }
            stringBuilder.append(TABLE_JOINT_SYMBOL);
        }
    }

    private void fillCell(StringBuilder stringBuilder, String cell, int cellIndex, HashMap<Integer, Integer> columnMaxWidth) {

        int cellPadding = getOptimumCellPadding(cellIndex, cell.length(), columnMaxWidth, PADDING_SIZE);

        if(cellIndex == 0)
        {
            stringBuilder.append(TABLE_V_SPLIT_SYMBOL);
        }

        fillSpace(stringBuilder, cellPadding);
        stringBuilder.append(cell);
        if(cell.length() % 2 != 0)
        {
            stringBuilder.append(" ");
        }

        fillSpace(stringBuilder, cellPadding);

        stringBuilder.append(TABLE_V_SPLIT_SYMBOL);
    }


    private int getOptimumCellPadding(int cellIndex,int datalength,HashMap<Integer,Integer> columnMaxWidthMapping,int cellPaddingSize)
    {
        if(datalength % 2 != 0)
        {
            datalength++;
        }

        if(datalength < columnMaxWidthMapping.get(cellIndex))
        {
            cellPaddingSize = cellPaddingSize + (columnMaxWidthMapping.get(cellIndex) - datalength) / 2;
        }

        return cellPaddingSize;
    }

    private void fillSpace(StringBuilder stringBuilder, int length) {
        for (int i = 0; i < length; i++) {
            stringBuilder.append(" ");
        }
    }


    public static void main(String[] args) {
        ArrayList<Column> columns = new ArrayList<>();
        Column column1 = new Column("Name",new DataType("text"),false,false);
        Column column2 = new Column("Age",new DataType("int"),false,false);
        Column column3 = new Column("Phone",new DataType("int"),false,false);
        Column column4 = new Column("Address",new DataType("text"),false,false);
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
        columns.add(column4);

        ArrayList<Record> records = new ArrayList<>();
        ArrayList<String> value1 = new ArrayList<>();
        value1.add("Jhon");
        value1.add("18");
        value1.add("1234567890");
        value1.add("Coit rd,Dallas,Tx");
        Record record1 = new Record(0,(byte) 0,null,value1);
        records.add(record1);


        ArrayList<String> value2 = new ArrayList<>();
        value2.add("Smith");
        value2.add("42");
        value2.add("0123456789");
        value2.add("West Plano pkwy,Dallas,Tx");
        Record record2 = new Record(0,(byte) 0,null,value2);
        records.add(record2);


        ArrayList<String> value3 = new ArrayList<>();
        value3.add("Harry Potter");
        value3.add("15");
        value3.add("9999999999");
        value3.add("Magic world in somewhere,Dallas,Tx");
        Record record3 = new Record(0,(byte) 0,null,value3);
        records.add(record3);

        TableView table = new TableView("tb1",columns,records);
        Displayer displayer = new Displayer(table);
        displayer.displayTable(displayer.headerList,displayer.recordList,new int[]{1});
    }
}
