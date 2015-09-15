/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 * 
 * Copyright (C) 2004 - 2011 CERN. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Author: TIM team, tim.support@cern.ch
 ******************************************************************************/
 
package cern.c2mon.client.common.listener;

import cern.c2mon.client.common.tag.Tag;
import cern.c2mon.client.common.tag.Tag;
;

/**
 * An update event gets fired when a <code>Tag</code> 
 * changes either its value or its quality property.
 * 
 * You can register a <code>DataTagUpdateListener</code> with a 
 * <code>Tag</code> so as to be notified of these property changes.
 * @see Tag
 * @see DataTagListener
 * @author Matthias Braeger
 */
public interface BaseTagListener extends BaseListener<Tag> {

  /**
   * This method gets called when the value or quality property of a
   * <code>Tag</code> has changed. It receives then a <b>copy</b>
   * of the updated object in the C2MON client cache.<p>
   * Please note that this method will also receive initial tag values, if you
   * did not subscribe with the {@link DataTagListener} interface.
   * 
   * @param tagUpdate A copy of the <code>Tag</code> object with the 
   *                  updated properties
   */
  @Override
  void onUpdate(Tag tagUpdate);
}