package TeamCity.Models;

public class Deploy {
    public String BuildId;
    public String ProjectName;
    public String Environment;
    public String Phase;
    public String psScript;


    @Override
    public String toString() {
        return "Deploy{" + "BuildId=" + BuildId + ", " +
                            "Environment='" + Environment + '\'' + ", " +
                            "ProjectName='" + ProjectName + '\'' + ", " +
                            "Phase='" + Phase + '\'' + '}';
    }
}
