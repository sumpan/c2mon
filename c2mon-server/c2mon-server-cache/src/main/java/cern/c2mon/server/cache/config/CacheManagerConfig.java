package cern.c2mon.server.cache.config;

import java.io.IOException;

import net.sf.ehcache.CacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

import static java.lang.String.format;

/**
 * @author Justin Lewis Salmon
 */
@Configuration
public class CacheManagerConfig {

  private static final int THREAD_TIMEOUT = 5;

  @Resource
  private Environment environment;

  @Bean
  public CacheManager cacheManager() throws IOException {
    return ehCacheManagerFactoryBean().getObject();
  }

  @Bean
  public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() throws IOException {
    String cacheMode = environment.getRequiredProperty("c2mon.server.cache.mode");

    switch (cacheMode) {
      case "single-nonpersistent":
        return getEhCacheManagerFactoryBean("ehcache/ehcache-single-nonpersistent.xml");
      case "single":
        return getEhCacheManagerFactoryBean("ehcache/ehcache-single.xml");
      case "multi":
        return getEhCacheManagerFactoryBean("ehcache/ehcache-multi.xml");
    }

    throw new RuntimeException(format("Unsupported cache mode specified: '%s'", cacheMode));
  }

  private EhCacheManagerFactoryBean getEhCacheManagerFactoryBean(String configLocation) throws IOException {
    EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
    bean.setConfigLocation(new ClassPathResource(configLocation));
    bean.setShared(true);
    return bean;
  }

  @Bean
  public ThreadPoolTaskExecutor cacheThreadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(environment.getRequiredProperty("c2mon.server.cacheloading.maxThreads", Integer.class));
    executor.setMaxPoolSize(environment.getRequiredProperty("c2mon.server.cacheloading.maxThreads", Integer.class));
    executor.setKeepAliveSeconds(THREAD_TIMEOUT);
    executor.setQueueCapacity(environment.getRequiredProperty("c2mon.server.cacheloading.queueSize", Integer.class));
    return executor;
  }
}