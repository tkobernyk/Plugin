package TeamCity.util;


import TeamCity.models.Deploy;
import TeamCity.models.HistoricalData;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class PluginUtils {

    public static HistoricalData convertFromDeployToHistoricalData(Deploy deploy, BlockingQueue<String> blockingQueue) {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPhase(deploy.getPhase());
        historicalData.setBuildId(deploy.getBuildId());
        historicalData.setEnvironment(deploy.getEnvironment());
        historicalData.setDeployStatus(deploy.getDeployStatus());
        historicalData.setProjectName(deploy.getProjectName());
        historicalData.setOutput(convertFromStringToClob(blockingQueue));
        return historicalData;
    }


    public static Clob convertFromStringToClob(BlockingQueue<String> blockingQueue) {
        try {
            return new SerialClob(blockingQueue.stream().collect(Collectors.joining()).toCharArray());
        } catch (SQLException e) {
            throw new RuntimeException("Could not convert to CLOB", e);
        }
    }
}
