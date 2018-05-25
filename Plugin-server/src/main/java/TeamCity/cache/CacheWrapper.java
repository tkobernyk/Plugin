package TeamCity.cache;

import TeamCity.powershell.PowerShellWrapper;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;


@Getter
@Setter
public class CacheWrapper {

    private static final String POWER_SHELL_OUTPUT_CACHE = "powerShellOutputCache";
    private static final String PROCESS_CACHE = "processCache";
    private CacheManager cacheManager;
    private Cache<Integer, PowerShellWrapper> powerShellCache;
    private Cache<Integer, Process> processCache;

    public CacheWrapper() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        powerShellCache = createCasheForPowerSHellWrapper(cacheManager);
        processCache = createCasheForProccess(cacheManager);
    }

    public Cache<Integer, PowerShellWrapper> getPowerShellOutputCache() {
        return cacheManager.getCache(POWER_SHELL_OUTPUT_CACHE, Integer.class, PowerShellWrapper.class);
    }

    public Cache<Integer, Process> getJavaProccessCache() {
        return cacheManager.getCache(PROCESS_CACHE, Integer.class, Process.class);
    }

    private Cache<Integer, PowerShellWrapper> createCasheForPowerSHellWrapper(CacheManager cacheManager) {
        return cacheManager.createCache(POWER_SHELL_OUTPUT_CACHE,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, PowerShellWrapper.class, ResourcePoolsBuilder.heap(10).build()));
    }

    private Cache<Integer, Process> createCasheForProccess(CacheManager cacheManager) {
        return cacheManager.createCache(PROCESS_CACHE,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, Process.class, ResourcePoolsBuilder.heap(10).build()));
    }

}
