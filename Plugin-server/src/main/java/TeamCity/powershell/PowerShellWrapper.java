package TeamCity.powershell;

import TeamCity.Models.Deploy;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Data
public class PowerShellWrapper implements Callable<DeployStatus> {


    private DeployStatus deployStatus;
    private BlockingQueue<String> blockingQueue;
    private String scriptPath;
    private Deploy deploy;
    private PowerShellRunner powerShellRunner;

    @Override
    public DeployStatus call() throws Exception {
        return  powerShellRunner.run(blockingQueue, scriptPath, deploy.toString());
    }
}
