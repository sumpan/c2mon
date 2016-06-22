/*******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 *
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 *
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package cern.c2mon.server.configuration.parser.factory;

import cern.c2mon.server.cache.DataTagCache;
import cern.c2mon.server.cache.TagFacadeGateway;
import cern.c2mon.server.cache.loading.SequenceDAO;
import cern.c2mon.shared.client.configuration.ConfigConstants;
import cern.c2mon.shared.client.configuration.ConfigurationElement;
import cern.c2mon.shared.client.configuration.api.tag.ControlTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Franz Ritter
 */
@Service
public class ControlTagFactory extends EntityFactory<ControlTag> {
  private DataTagCache dataTagCache;
  private TagFacadeGateway tagFacadeGateway;
  private SequenceDAO sequenceDAO;

  @Autowired
  public ControlTagFactory(DataTagCache dataTagCache, TagFacadeGateway tagFacadeGateway, SequenceDAO sequenceDAO) {
    this.dataTagCache = dataTagCache;
    this.tagFacadeGateway = tagFacadeGateway;
    this.sequenceDAO = sequenceDAO;
  }

  @Override
  public List<ConfigurationElement> createInstance(ControlTag configurationEntity) {
    return Arrays.asList(doCreateInstance(configurationEntity));
  }

  @Override
  Long createId(ControlTag configurationEntity) {
    return configurationEntity.getId() != null ? configurationEntity.getId() : sequenceDAO.getNextTagId();
  }

  @Override
  Long getId(ControlTag configurationEntity) {
    return configurationEntity.getId() != null ? configurationEntity.getId() : dataTagCache.get(configurationEntity.getName()).getId();
  }

  @Override
  boolean cacheHasEntity(Long id) {
    return tagFacadeGateway.isInTagCache(id);
  }

  @Override
  ConfigConstants.Entity getEntity() {
    return ConfigConstants.Entity.CONTROLTAG;
  }

}