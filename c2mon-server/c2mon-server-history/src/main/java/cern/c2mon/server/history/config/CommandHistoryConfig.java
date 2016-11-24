package cern.c2mon.server.history.config;

import cern.c2mon.pmanager.persistence.impl.PersistenceManager;
import cern.c2mon.server.history.alarm.AlarmListener;
import cern.c2mon.server.history.dao.LoggerDAO;
import cern.c2mon.server.history.mapper.CommandRecordMapper;
import cern.c2mon.shared.client.command.CommandRecord;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Justin Lewis Salmon
 */
public class CommandHistoryConfig {

  @Autowired
  private Environment environment;

  @Autowired
  @Qualifier("historySqlSessionFactory")
  private SqlSessionFactoryBean historySqlSessionFactory;

  @Bean
  public PersistenceManager<CommandRecord> commandPersistenceManager(AlarmListener alarmListener) throws Exception {
    String fallbackFile = environment.getRequiredProperty("c2mon.server.history.fallback.command");
    return new PersistenceManager<>(commandLoggerDAO(), fallbackFile, alarmListener, new CommandRecord());
  }

  @Bean
  public LoggerDAO<CommandRecord> commandLoggerDAO() throws Exception {
    return new LoggerDAO<>(historySqlSessionFactory.getObject(), CommandRecordMapper.class.getCanonicalName(),
        environment.getRequiredProperty("c2mon.server.history.jdbc.url"));
  }
}