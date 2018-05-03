package TeamCity.dao;

import TeamCity.models.Environment;
import TeamCity.models.HistoricalData;
import TeamCity.powershell.DeployStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoricalDataRowMapper implements RowMapper<HistoricalData> {

    @Override
    public HistoricalData mapRow(ResultSet resultSet, int i) throws SQLException {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setId(resultSet.getLong("id"));
        historicalData.setLocalDateTime(resultSet.getTimestamp("localDateTime").toLocalDateTime());
        historicalData.setUserId(resultSet.getLong("userId"));
        historicalData.setOutput(resultSet.getClob("output"));
        historicalData.setEnvironment(Environment.valueOf(resultSet.getString("environment")));
        historicalData.setProjectName(resultSet.getString("projectName"));
        historicalData.setBuildId(resultSet.getString("buildId"));
        historicalData.setPhase(resultSet.getString("phase"));
        historicalData.setDeployStatus(DeployStatus.valueOf(resultSet.getString("deployStatus")));
        return historicalData;
    }
}
