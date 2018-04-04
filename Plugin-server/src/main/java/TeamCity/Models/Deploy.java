package TeamCity.Models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Deploy {
    public String BuildId;
    public String ProjectName;
    public String Environment;
    public String Phase;


    @Override
    public String toString() {
        return "Deploy{" + "BuildId=" + BuildId + ", " +
                "Environment='" + Environment + '\'' + ", " +
                "ProjectName='" + ProjectName + '\'' + ", " +
                "Phase='" + Phase + '\'' + '}';
    }
}
