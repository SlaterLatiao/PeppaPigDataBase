package Common;
/**
 * @author LI LIU 2018-07-21
 * */
public class Column {
    private String columnName;
    private DataType dataType;
    private boolean isPrimary;
    private boolean isNull;
    private boolean hasIndex;

    public Column(String name,DataType type,boolean isPrimary,boolean isNull){
        this.columnName = name;
        this.dataType = type;
        this.isPrimary = isPrimary;
        this.isNull = isNull;
        this.hasIndex = false;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean isHasIndex() {
        return hasIndex;
    }

    public void setHasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
    }
}

