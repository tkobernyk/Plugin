package TeamCity.util;


import TeamCity.models.Deploy;
import TeamCity.models.HistoricalData;

import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.SQLException;

public class PluginUtils {
    private static final com.intellij.openapi.diagnostic.Logger logger =
            com.intellij.openapi.diagnostic.Logger.getInstance(PluginUtils.class.getName());

    public static HistoricalData convertFromDeployToHistoricalData(Deploy deploy, String data) {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPhase(deploy.getPhase());
        historicalData.setBuildId(deploy.getBuildId());
        historicalData.setEnvironment(deploy.getEnvironment());
        historicalData.setDeployStatus(deploy.getDeployStatus());
        historicalData.setProjectName(deploy.getProjectName());
        historicalData.setUserId(deploy.getUserId());
        historicalData.setOutput(convertFromStringToClob(data));
        return historicalData;
    }


    public static Clob convertFromStringToClob(String data) {
        try {
            return new SerialClob(data.toCharArray());
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Could not convert to CLOB", e);
        }
    }


    public static String createRuntimeFolderIfDoesNotExist(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectory(Paths.get(path));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return path;
    }

    public static void removeFileWithCatchingException(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
