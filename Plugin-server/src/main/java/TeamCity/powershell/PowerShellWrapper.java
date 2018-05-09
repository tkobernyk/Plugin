package TeamCity.powershell;

import TeamCity.models.Deploy;
import lombok.Data;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Data
public class PowerShellWrapper implements Supplier<Deploy> {


    private Queue<String> queue;
    private StringBuilder data;
    private String scriptPath;
    private Deploy deploy;
    private PowerShellRunner powerShellRunner;



    @Override
    public Deploy get() {
        return powerShellRunner.run(queue, scriptPath, deploy);
    }

}
