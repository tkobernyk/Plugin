package TeamCity.Helpers;

import TeamCity.Models.Deploy;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

public class PowerShellRunner {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShell.class.getName());

    public static PowerShellResponse run(String scriptPath, Deploy params) throws PowerShellNotAvailableException {
        Log.info(scriptPath);
        PowerShell powerShell = PowerShell.openSession();
        return powerShell.executeScript(scriptPath, "-projectName " + params.ProjectName +
                " -buildId " + params.BuildId +
                " -environment " + params.Environment);
    }
}
