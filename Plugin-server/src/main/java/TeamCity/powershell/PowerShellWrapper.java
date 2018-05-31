package TeamCity.powershell;

import TeamCity.models.Deploy;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

@Data
public class PowerShellWrapper implements Supplier<Deploy> {
    private static final com.intellij.openapi.diagnostic.Logger logger  =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellWrapper.class.getName());

    private BlockingQueue<String> queue;
    private StringBuilder data;
    private String scriptPath;
    private Deploy deploy;
    private PowerShellRunner powerShellRunner;


    @Override
    public Deploy get() {
        Deploy d = powerShellRunner.run(queue, scriptPath, deploy);
        d.setEnabledDeploy(false);
        return d;
    }


}
