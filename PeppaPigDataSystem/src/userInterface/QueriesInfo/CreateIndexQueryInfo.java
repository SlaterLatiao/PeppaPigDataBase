package userInterface.QueriesInfo;

public class CreateIndexQueryInfo {

    public String indexName;
    public String tableName;
    public String columnName;

    public CreateIndexQueryInfo(String indexName, String tableName, String columnName){
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

}
