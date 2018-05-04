package TeamCity.util;


import TeamCity.models.Deploy;
import TeamCity.models.HistoricalData;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;

public class PluginUtils {

    public static HistoricalData convertFromDeployToHistoricalData(Deploy deploy, String data) {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPhase(deploy.getPhase());
        historicalData.setBuildId(deploy.getBuildId());
        historicalData.setEnvironment(deploy.getEnvironment());
        historicalData.setDeployStatus(deploy.getDeployStatus());
        historicalData.setProjectName(deploy.getProjectName());
        historicalData.setOutput(convertFromStringToClob(data));
        return historicalData;
    }


    public static Clob convertFromStringToClob(String data) {
        try {
            return new SerialClob(data.toCharArray());
        } catch (SQLException e) {
            throw new RuntimeException("Could not convert to CLOB", e);
        }
    }
}