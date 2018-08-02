package databaseAPI;

import Common.Column;

import static java.lang.Math.pow;

public class CheckSupportDataType {
    public static boolean isDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInt(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String getIntType(int num) {
        String intType;

        if(num >= -pow(2,7) && num <= pow(2,7) - 1){
            //its tinyInt
            intType = "tinyint";
        }
        else if(num >= -pow(2,15) && num <= pow(2,15) - 1){
            //its smallInt
            intType = "smallint";
        }
        else if(num >= -pow(2,31) && num <= pow(2,31) - 1){
            //its Int
            intType = "int";
        }
        else if(num >= -pow(2,63) && num <= pow(2,63) - 1){
            //its bigInt
            intType = "bigint";
        }
        else{
            //error
            intType = null;
        }
        return intType;
    }

    public static String getDataType(String str){
        String dataType;

        if(str.charAt(0) == '\'' ){
            //it can be text, date, or datetime

            if(str.matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}:\\d{2}:\\d{2}")){
                //its a datetime
                dataType = "datetime";
            }
            else if(str.matches("\\d{4}-\\d{2}-\\d{2}")){
                //its a date
                dataType = "date";
            }
            else{
                //its text
                dataType = "text";
            }
        }
        else if(isInt(str)){
            //its int

            int testInteger = Integer.parseInt(str);

            dataType = getIntType(testInteger);
        }
        else if(isDouble(str)){
            //its double
            dataType = "double";
        }
        else if(str.equals("null")){
            //its null
            dataType = "null";
        }
        else{
            //its not a supported datatype
            dataType = null;
        }
        return dataType;
    }

    public static boolean checkSupportInt(String strDataTypeRecordRaw, String strDataTypeCondRaw){
        boolean condition = false;

        String strDataTypeRecord=getDataType(strDataTypeRecordRaw);
        String strDataTypeCond=getDataType(strDataTypeCondRaw);

        if(strDataTypeRecord.equals("bigint")){
            if(strDataTypeCond.equals("bigint") || strDataTypeCond.equals("int") || strDataTypeCond.equals("smallint") || strDataTypeCond.equals("tinyint")){
                condition = true;
            }
        }
        else if(strDataTypeRecord.equals("int")){
            if(strDataTypeCond.equals("int") || strDataTypeCond.equals("smallint") || strDataTypeCond.equals("tinyint")){
                condition = true;
            }
        }
        else if(strDataTypeRecord.equals("smallint")){
            if(strDataTypeCond.equals("smallint") || strDataTypeCond.equals("tinyint")){
                condition = true;
            }
        }
        else if(strDataTypeRecord.equals("tinyint")){
            if(strDataTypeCond.equals("tinyint")){
                condition = true;
            }
        }
        else{
            //weird happen
        }

        return condition;
    }

    public static boolean checkSupportFloatingNum(String strDataTypeRecordRaw, String strDataTypeCondRaw){
        boolean condition = false;

        String strDataTypeRecord=getDataType(strDataTypeRecordRaw);
        String strDataTypeCond=getDataType(strDataTypeCondRaw);

        if(strDataTypeRecord.equals("double")){
            if(strDataTypeCond.equals("double")){
                condition = true;
            }
        }
        else if(strDataTypeRecord.equals("float")){
            if(strDataTypeCond.equals("double")){
                condition = true;
            }
        }
        else{
            //weird happen
        }

        return condition;
    }

    public static Boolean CheckSupportDataType(String strDataTypeRecord, String strDataTypeCond){
        Boolean condition = false;

        //Smaller Ints can fit into larger ints?
        if(getDataType(strDataTypeCond).equals("bigint") || getDataType(strDataTypeCond).equals("int") || getDataType(strDataTypeCond).equals("smallint") || getDataType(strDataTypeCond).equals("tinyint")){
            if(checkSupportInt(strDataTypeRecord, strDataTypeCond)){
                condition = true;
            }
        }
        else if(getDataType(strDataTypeCond).equals("double")){
            if(checkSupportFloatingNum(strDataTypeRecord, strDataTypeCond)){
                condition = true;
            }
        }
        else if(getDataType(strDataTypeCond).equals(getDataType(strDataTypeRecord))){
            condition = true;
        }
        else{
            //Something weird happened if you reach here
        }
        return condition;
    }

}
