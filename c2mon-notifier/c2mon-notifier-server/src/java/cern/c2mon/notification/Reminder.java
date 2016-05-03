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
package cern.c2mon.notification;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import cern.c2mon.notification.shared.Subscriber;

/**
 * An interface which describes the methods which has to be provided by a implementation of this service. The Reminder
 * Service is intended to run regularly and check if a reminder message for a problem has to be send to the user. The
 * interval for this check can be set with {@link #setReminderTime(long, TimeUnit)}. The {@link Notifier} is then called
 * to send the notification message to the user.
 * 
 * @author felixehm
 */
public interface Reminder {

    /**
     * Sets the {@link Notifier} Service which is used to call
     * {@link Notifier#sendReminder(cern.c2mon.notification.shared.Subscription, Tag)}.
     * 
     * @param notifier a Notifier implementation.
     */
    public void setNotifier(Notifier notifier);

    /**
     * Sets the SubscriptionRegistry which holds all {@link Subscriber} objects.
     * 
     * @param registry the {@link SubscriptionRegistry} which provides access to the Subscribers.
     */
    public void setRegistry(SubscriptionRegistry registry);

    /**
     * Lifecycle method. Used to start the Reminder service.
     */
    public void start();

    /**
     * Lifecycle method. Used to stop the Reminder service.
     */
    public void stop();

    /**
     * @return the last time the Reminder service checked the {@link Subscriber}s.
     */
    public Timestamp lastReminderRound();

    /**
     * 
     * @return the time interval in milliseconds the Reminder service checks for reminders to be sent.
     */
    public long getReminderTime();

    /**
     * Sets the time interval the Reminder service has to check
     * 
     * @param duration the duration
     * @param unit a {@link TimeUnit}
     */
    public void setReminderTime(long duration, TimeUnit unit);

}
