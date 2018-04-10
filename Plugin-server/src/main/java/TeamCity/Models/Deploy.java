package TeamCity.Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Deploy {
    private String buildId;
    private String projectName;
    private String environment;
    private String phase;


    @Override
    public String toString() {
        return "\"" + projectName + "\" " + buildId + " \"" + environment + "\" " + "\"" + phase + "\"";
    }
}
