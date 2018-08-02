package Common;
/**
 * @author LI LIU 2018-07-21
 * */
public class Column {
    private String columnName;
    private DataType dataType;
    private boolean isPrimary;
    private boolean isNullable;
    private String indexName;

    public Column(String name,DataType type,boolean isPrimary,boolean isNull){
        this.columnName = name;
        this.dataType = type;
        this.isPrimary = isPrimary;
        this.isNullable = isNull;
        this.indexName = "";
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

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean aNull) {
        isNullable = aNull;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

}

