package TeamCity.dao;


import TeamCity.models.HistoricalData;

import java.util.List;

public interface HistoricalDataDao {

    String SELECT_QUERY = "SELECT [Id]\n" +
            "      ,[BuildId]\n" +
            "      ,[ProjectName]\n" +
            "      ,[Environment]\n" +
            "      ,[Phase]\n" +
            "      ,[DeployDateTime]\n" +
            "      ,[DeployStatus]\n" +
            "      ,[DeployOutput]\n" +
            "      ,[UserId]\n" +
            "  FROM [TeamCity].[dbo].[historical_deployment_data]";

    String INSERT_QUERY = "INSERT INTO [TeamCity].[dbo].[historical_deployment_data]([BuildId],[ProjectName],[Environment],[Phase]," +
            "[DeployStatus],[DeployOutput],[UserId]) values(?,?,?,?,?,?,?)";

    int save(HistoricalData historicalData);

    List<HistoricalData> getAll();
}
