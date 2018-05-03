package TeamCity.Models;

import TeamCity.powershell.DeployStatus;
import lombok.Data;

import java.sql.Blob;
import java.sql.Types;
import java.time.LocalDateTime;


@Data
public class HistoricalData {
    private Long id;
    private LocalDateTime localDateTime;
    private Long userId;
    private Blob output;
    private Environment environment;
    private String projectName;
    private int buildId;
    private String phase;
    private DeployStatus deployStatus;

    public Object[] listOValues() {
        return new Object[]{getId(), getLocalDateTime(), getUserId(), getOutput(), getEnvironment(),
                getProjectName(), getBuildId(), getPhase(), getDeployStatus()};
    }

    public int[] listOfTypes() {
        return new int[]{Types.BIGINT, Types.TIMESTAMP, Types.BIGINT, Types.BLOB, Types.NVARCHAR,
                Types.NVARCHAR, Types.INTEGER, Types.NVARCHAR, Types.NVARCHAR};
    }
}
