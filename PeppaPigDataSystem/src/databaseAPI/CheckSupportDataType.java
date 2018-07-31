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

        if(-pow(2,7) <= num && num >= pow(2,7) - 1){
            //its tinyInt
            intType = "tinyint";
        }
        else if(-pow(2,15) <= num && num >= pow(2,15) - 1){
            //its smallInt
            intType = "smallint";
        }
        else if(-pow(2,31) <= num && num >= pow(2,31) - 1){
            //its Int
            intType = "int";
        }
        else if(-pow(2,63) <= num && num >= pow(2,63) - 1){
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
        else if(isDouble(str)){
            //its double
            dataType = "double";
        }
        else if(isInt(str)){
            //its int

            int testInteger = Integer.parseInt(str);

            dataType = getIntType(testInteger);
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

    public static boolean checkSupportInt(String strDataTypeCond, String strDataTypeRecord){
        boolean condition = false;

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

    public static boolean checkSupportFloatingNum(String strDataTypeCond, String strDataTypeRecord){
        boolean condition = false;

        if(strDataTypeCond.equals("double")){
            if(strDataTypeRecord.equals("double")){
                condition = true;
            }
        }
        else if(strDataTypeCond.equals("float")){
            if(strDataTypeRecord.equals("double")){
                condition = true;
            }
        }
        else{
            //weird happen
        }

        return condition;
    }

    public static Boolean CheckSupportDataType(String strDataTypeCond, String strDataTypeRecord){
        Boolean condition = false;

        //Smaller Ints can fit into larger ints?
        if(getDataType(strDataTypeCond).equals("bigint") || getDataType(strDataTypeCond).equals("int") || getDataType(strDataTypeCond).equals("smallint") || getDataType(strDataTypeCond).equals("tinyint")){
            if(checkSupportInt(strDataTypeCond, strDataTypeRecord)){
                condition = true;
            }
        }
        else if(getDataType(strDataTypeCond).equals("double")){
            if(checkSupportFloatingNum(strDataTypeCond, strDataTypeRecord)){
                condition = true;
            }
        }
        else if(getDataType(strDataTypeCond).equals(getDataType(strDataTypeRecord))){
            condition = true;
        }
        else if(strDataTypeCond == null){
            condition = false;
        }
        else{
            //Something weird happened if you reach here
        }
        return condition;
    }
}
