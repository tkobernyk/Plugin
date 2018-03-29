package TeamCity.Helpers;

import TeamCity.Models.Deploy;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

public class PowerShellRunner {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShell.class.getName());
    public static String Run(String scriptPath, Deploy params)
    {
        Log.info(scriptPath);
        PowerShell  powerShell = null;
        try {
            powerShell = PowerShell.openSession();
        } catch (PowerShellNotAvailableException e) {
            Log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        PowerShellResponse response = powerShell.executeScript(scriptPath, "-projectName " + params.ProjectName +
                                                                                   " -buildId " + params.BuildId +
                                                                                   " -environment " + params.Environment);
        return response.getCommandOutput();
    }
}
