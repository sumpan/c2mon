/******************************************************************************
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
 *****************************************************************************/
package cern.c2mon.shared.client.configuration.api.tag;

import java.util.ArrayList;
import java.util.List;

import cern.c2mon.shared.client.configuration.api.alarm.Alarm;
import cern.c2mon.shared.common.metadata.Metadata;
import cern.c2mon.shared.client.configuration.api.util.ConfigurationObject;
import cern.c2mon.shared.client.configuration.api.util.DefaultValue;
import cern.c2mon.shared.client.configuration.api.util.IgnoreProperty;
import cern.c2mon.shared.client.tag.TagMode;
import lombok.Data;
import lombok.Singular;

/**
 * Tag class which holds the information to create a {@link cern.c2mon.shared.client.configuration.ConfigurationElement}
 * related to all Tags.
 *
 * @author Franz Ritter
 */
@Data
public abstract class Tag implements ConfigurationObject {

  @IgnoreProperty
  private boolean update = false;

  @IgnoreProperty
  private boolean create = false;

  /**
   * The id of the overlying Object. This field should never set by the user directly.
   */
  @IgnoreProperty
  private Long parentId;

  /**
   * The name of the overlying Object. This field should never set by the user directly.
   */
  @IgnoreProperty
  private String parentName;

  /**
   * determine if the instance of this class defines a DELETE command
   */
  @IgnoreProperty
  private boolean deleted;

  /**
   * Unique datatag identifier (unique across all types of tags: control,
   * datatag and rules).
   */
  @IgnoreProperty
  private Long id;

  /**
   * Unique tag name.
   */
  private String name;

  /**
   * Free-text description of the tag
   */
  @DefaultValue("<no description provided>")
  private String description;

  /**
   * Indicates whether a tag is "in operation", "in maintenance" or "in test".
   */
  @DefaultValue("TEST")
  private TagMode mode = TagMode.TEST;

  /**
   * Meta data of the tag object. Holds arbitrary data which are related to the given Tag.
   */
  private Metadata metadata;

  @IgnoreProperty
  private List<Alarm> alarms = new ArrayList<>();

  public void addAlarm(Alarm alarm) {
    this.alarms.add(alarm);
  }

  public Tag(boolean deleted, Long id, String name, String description, TagMode mode, @Singular List<Alarm> alarms, Metadata metadata) {
    super();
    this.deleted = deleted;
    this.id = id ;
    this.name = name;
    this.description = description;
    this.mode = mode;
    this.alarms = alarms == null ? new ArrayList<Alarm>() : alarms;
    this.metadata = metadata;
  }

  public Tag() {
  }

  /**
   * Checks if all mandatory fields are set.
   * <p/>
   * mode is also a Primary filed. But Because that mode is also a default Value it is not necessary to set it in the POJO
   */
  @Override
  public boolean requiredFieldsGiven() {
    return (getId() != null) && (getName() != null);
  }

}
