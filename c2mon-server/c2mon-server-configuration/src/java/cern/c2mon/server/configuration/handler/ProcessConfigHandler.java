/******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 * 
 * Copyright (C) 2005-2011 CERN.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Author: TIM team, tim.support@cern.ch
 *****************************************************************************/
package cern.c2mon.server.configuration.handler;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cern.c2mon.server.configuration.ConfigurationLoader;
import cern.c2mon.server.configuration.impl.ProcessChange;
import cern.tim.server.cache.ProcessCache;
import cern.tim.server.cache.ProcessFacade;
import cern.tim.server.cache.loading.ProcessDAO;
import cern.tim.server.common.process.Process;
import cern.tim.shared.client.configuration.ConfigurationElement;
import cern.tim.shared.client.configuration.ConfigurationElementReport;
import cern.tim.shared.client.configuration.ConfigConstants.Action;
import cern.tim.shared.client.configuration.ConfigConstants.Entity;
import cern.tim.shared.common.ConfigurationException;
import cern.tim.shared.daq.config.Change;

/**
 * Handles reconfiguration of Process' at runtime.
 * 
 * @author Mark Brightwell
 *
 */
@Service
public class ProcessConfigHandler {

  /**
   * Class logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ProcessConfigHandler.class);
  
  /**
   * Reference to facade.
   */
  private ProcessFacade processFacade;
  
  /**
   * Reference to cache.
   */
  private ProcessCache processCache;
  
  /**
   * Reference to DAO.
   */
  private ProcessDAO processDAO;
  
  /**
   * Reference to Equipment configuration bean.
   */
  private EquipmentConfigHandler equipmentConfigHandler;
  
  /**
   * Reference to ControlTag configuration bean.
   */
  private ControlTagConfigHandler controlTagConfigHandler;
    
  /**
   * Autowired constructor.
   * @param processFacade the facade bean
   * @param processCache the cache bean
   * @param processDAO the DAO bean
   * @param equipmentConfigHandler the Equipment configuration bean
   * @param controlTagConfigHandler the ControlTag configuration bean
   */
  @Autowired
  public ProcessConfigHandler(final ProcessFacade processFacade, final ProcessCache processCache, 
                              final ProcessDAO processDAO, final EquipmentConfigHandler equipmentConfigHandler, 
                              final ControlTagConfigHandler controlTagConfigHandler) {
    super();
    this.processFacade = processFacade;
    this.processCache = processCache;
    this.processDAO = processDAO;
    this.equipmentConfigHandler = equipmentConfigHandler;
    this.controlTagConfigHandler = controlTagConfigHandler;
  }

  /**
   * Creates the process and inserts it into the cache and DB (DB first).
   * 
   * <p>Changing a process id is not currently allowed.
   * 
   * @param element the configuration element
   * @throws IllegalAccessException not thrown (inherited from common facade interface) 
   */
  public void createProcess(final ConfigurationElement element) throws IllegalAccessException {
    if (processCache.hasKey(element.getEntityId())) {
      throw new ConfigurationException(ConfigurationException.ENTITY_EXISTS, 
          "Attempting to create a process with an already existing id: " + element.getEntityId());
    }
    Process process = (Process) processFacade.createCacheObject(element.getEntityId(), element.getElementProperties());
    processDAO.insert(process);
    processCache.putQuiet(process);      
  }
  
  /**
   * No changes to the Process configuration are currently passed to the DAQ layer,
   * but the Configuration object is already build into the logic below (always empty
   * and hence ignored in the {@link ConfigurationLoader}).
   * @param id
   * @param properties
   * @return currently not used DAQ configuration object
   * @throws IllegalAccessException
   */
  public ProcessChange updateProcess(Long id, Properties properties) throws IllegalAccessException {    
    if (properties.containsKey("id")) {
      throw new ConfigurationException(ConfigurationException.UNDEFINED, 
          "Attempting to change the process id - this is not currently supported!");
    }
    Change processUpdate;
    Process process = processCache.get(id);
    try {
      process.getWriteLock().lock();
      processUpdate = processFacade.updateConfig(process, properties); //return always empty
      processDAO.updateConfig(process);      
    } finally {
      process.getWriteLock().unlock();     
    }    
    return new ProcessChange(process.getId(), processUpdate);
  }
  
  /**
   * Tries to remove the process and all its descendents. The process
   * itself is only completely removed if all the equipments, subequipments
   * and associated tags, commands are all removed successfully.
   * 
   * <p>In the case of a failure, the removal is interrupted and the process
   * remains with whatever child objects remain at the point of failure (for tags
   * this is not quite exact: the server will attempt to remove all tags, but will
   * not remove an equipment or any associated subequipments if one tag fails to
   * be removed).
   * @param processId
   * @param elementReport the element report for the removal of the process, to which 
   *                          subreports can be attached
   */
  public void removeProcess(Long processId, ConfigurationElementReport elementReport) {
    
    Process process = processCache.get(processId);
    try {
      process.getWriteLock().lock();
      //remove all associated equipment from system   
      for (Long equipmentId : processFacade.getEquipmentIds(processId)) {
        ConfigurationElementReport childElementReport = new ConfigurationElementReport(Action.REMOVE, Entity.EQUIPMENT, equipmentId);
        try {        
          elementReport.addSubReport(childElementReport);
          equipmentConfigHandler.removeEquipment(equipmentId, childElementReport);
        } catch (Exception ex) {
          LOGGER.error("Exception caught while applying the configuration change (Action, Entity, Entity id) = (" 
              + Action.REMOVE + "; " + Entity.EQUIPMENT + "; " + equipmentId + ")", ex);
          childElementReport.setFailure("Exception caught while applying the configuration change.", ex);          
          throw new RuntimeException(ex);
        }      
      }
            
      //remove process from cache and DB
      processCache.remove(processId);
      processDAO.deleteProcess(processId);
      removeProcessControlTags(process, elementReport);
    } catch (Exception ex) {      
      processFacade.errorStatus(process, "Error during attempted removal of this process.");
      elementReport.setFailure("Exception caught when trying to remove the process.");
      LOGGER.error("Exception caught when attempting to remove a process.", ex);
    } finally {
      process.getWriteLock().unlock();
    }
    
    
  }

  /**
   * Removes process alive and state tags (from DB and cache).
   * @param process
   * @param processReport
   */
  private void removeProcessControlTags(Process process, ConfigurationElementReport processReport) {
    Long aliveTagId = process.getAliveTagId();
    if (aliveTagId != null) {
      ConfigurationElementReport tagReport = new ConfigurationElementReport(Action.REMOVE, Entity.CONTROLTAG, aliveTagId);
      controlTagConfigHandler.removeControlTag(aliveTagId, tagReport);
      processReport.addSubReport(tagReport);
    }          
    Long stateTagId = process.getStateTagId();
    ConfigurationElementReport tagReport = new ConfigurationElementReport(Action.REMOVE, Entity.CONTROLTAG, stateTagId);
    controlTagConfigHandler.removeControlTag(stateTagId, tagReport);
    processReport.addSubReport(tagReport);
  }
}
