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
package cern.c2mon.server.eslog.structure.types;

import cern.c2mon.pmanager.IFallback;
import cern.c2mon.pmanager.fallback.exception.DataFallbackException;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an Alarm Event for ElasticSearch.
 *
 * @author Alban Marguet
 */
@Slf4j
@Data
public class EsAlarm implements IFallback {
  @NonNull
  private final transient Gson gson = GsonSupplier.INSTANCE.get();

  private long tagId;
  private long alarmId;

  private String faultFamily;
  private String faultMember;
  private int faultCode;

  private boolean active;
  private String activity;
  private double activeNumeric;
  private int priority;
  private String info;
  private long serverTimestamp;

  private final Map<String, String> metadata = new HashMap<>();

  /**
   * JSON representation of the EsAlarm
   */
  @Override
  public String toString() {
    String json = gson.toJson(this);
    log.debug(json);
    return json;
  }

  @Override
  public IFallback getObject(String line) throws DataFallbackException {
    return gson.fromJson(line, EsAlarm.class);
  }

  @Override
  public String getId() {
    return String.valueOf(alarmId);
  }

}