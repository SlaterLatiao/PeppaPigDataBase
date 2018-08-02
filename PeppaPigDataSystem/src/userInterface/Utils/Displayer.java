package userInterface.Utils;

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
    private int[] overRiddenHeaderHeight;
    private TableView table;

    public Displayer(TableView table){
        this.table = table;
        initDisplayer();
        displayTable(headerList,recordList,overRiddenHeaderHeight);
    }

    public void initDisplayer(){
        this.headerList = table.getAllColumnNames();
        this.recordList = table.getAllValues();
        this.overRiddenHeaderHeight = new int[]{1};
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
        /*for(int j=0; j<recordList.size(); j++){
            for(int i = 0; i < rowHeight; i++){
                stringBuilder.append(NEW_LINE);
            }

            for(int cellIndex = 0; cellIndex < recordList.get(j).size(); cellIndex++){
                String value = recordList.get(j).get(cellIndex);
                fillCell(stringBuilder,value,cellIndex,columnMaxWidth);
            }
        }*/

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

}
