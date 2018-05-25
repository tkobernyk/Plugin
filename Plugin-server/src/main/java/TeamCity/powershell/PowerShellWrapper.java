package TeamCity.powershell;

import TeamCity.controllers.DeployControllerAction;
import TeamCity.models.Deploy;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

@Data
public class PowerShellWrapper implements Supplier<Deploy> {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellWrapper.class.getName());

    private BlockingQueue<String> queue;
    private StringBuilder data;
    private String scriptPath;
    private Deploy deploy;
    private PowerShellRunner powerShellRunner;


    @Override
    public Deploy get() {
         Deploy d = powerShellRunner.run(queue, scriptPath, deploy);
         Log.info("Finished processing PowerShellWrapper");
         d.setEnabledDeploy(false);
         return d;
    }



}
