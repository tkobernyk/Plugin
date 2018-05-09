package TeamCity.models;

import TeamCity.powershell.DeployStatus;
import lombok.Data;

@Data
public class Deploy {
    private static final String POWER_SHELL_EXTENSION = ".ps1";
    private static final String DELIMITER = "_";
    private String buildId;
    private String projectName;
    private Environment environment;
    private String phase;
    private DeployStatus deployStatus;
    private long userId;

    public String getFileNameFromDeploy() {
        return projectName + DELIMITER + phase + DELIMITER + environment + DELIMITER + buildId + POWER_SHELL_EXTENSION;
    }

    public String getParametersAsString() {
        return "\"" + projectName + "\" " + buildId + " \"" + environment + "\" " + "\"" + phase + "\"";
    }


}