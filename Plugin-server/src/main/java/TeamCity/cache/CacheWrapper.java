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
    private CacheManager cacheManager;
    private Cache<Integer, PowerShellWrapper> cache;

    public CacheWrapper() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        cache = cacheManager.createCache(POWER_SHELL_OUTPUT_CACHE,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, PowerShellWrapper.class, ResourcePoolsBuilder.newResourcePoolsBuilder()));
    }

    public Cache<Integer, PowerShellWrapper> getPowerShellOutputCache() {
        return cacheManager.getCache(POWER_SHELL_OUTPUT_CACHE, Integer.class, PowerShellWrapper.class);
    }

}
