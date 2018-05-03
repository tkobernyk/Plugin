package TeamCity.powershell;

import TeamCity.models.Deploy;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

@Data
public class PowerShellWrapper implements Supplier<Deploy> {


    private BlockingQueue<String> blockingQueue;
    private String scriptPath;
    private Deploy deploy;
    private PowerShellRunner powerShellRunner;
    private long userId;


    @Override
    public Deploy get() {
        return powerShellRunner.run(blockingQueue, scriptPath, deploy);
    }
}
