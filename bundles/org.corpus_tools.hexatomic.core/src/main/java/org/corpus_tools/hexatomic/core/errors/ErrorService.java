/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.core.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hexatomic error handling service.
 * 
 * <p>
 * This service allows you to display errors (e.g. exceptions) to the users and log them in the log
 * files and a special view.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
@Singleton
@Creatable
public class ErrorService {

  private static final String ERROR_OCCURED_MSG = "An error occured";
  private static final org.slf4j.Logger errorServiceLog =
      org.slf4j.LoggerFactory.getLogger(ErrorService.class);

  private Optional<IStatus> lastException = Optional.empty();

  @Inject
  UISynchronize sync;

  /**
   * Display a (runtime) exception to the user and add it to the log.
   * 
   * @param message A message to the user explaining what went wrong.
   * @param ex The exception that occurred.
   * @param triggeringClass The class in which the error occurred. This is used to extract the
   *        bundle ID and to get the correct logger when logging the error to the console/log file.
   */
  public void handleException(String message, Throwable ex, Class<?> triggeringClass) {
    // log error so whatever happens, it is visible in the log file/console
    Logger log = getLogger(triggeringClass);
    log.error("Exception occured: {}", message, ex);

    String bundleID = getBundleID(triggeringClass);

    IStatus status = createStatusFromException(message, bundleID, ex);
    lastException = Optional.of(status);

    sync.syncExec(() -> {
      ErrorDialog.openError(null, ERROR_OCCURED_MSG, message, status);
    });


  }

  /**
   * Show an error message to the user.
   * 
   * @param title The title, can be null to show a default title
   * @param message The error message to show.
   * @param context The class in which the error occurred. This is used to get the correct logger
   *        when logging the error to the console/log-file.
   */
  public void showError(String title, String message, Class<?> context) {

    if (title == null) {
      title = ERROR_OCCURED_MSG;
    }

    // log error so whatever happens, it is visible in the log-file/console
    Logger log = getLogger(context);
    log.error("{}: {}", title, message);

    MessageDialog.openError(null, title, message);
  }

  /**
   * Get the logger for the given class or the logger of <em>this</em> class if null.
   * 
   * @param context The class to get the logger for.
   * @return A logger
   */
  private Logger getLogger(Class<?> context) {
    return context == null ? errorServiceLog : LoggerFactory.getLogger(context);
  }

  /**
   * Get the bundle ID for the given class.
   * 
   * @param context Class to get the bundle for
   * @return The bundle ID or "unknown" if unable to get the actual ID.
   */
  private String getBundleID(Class<?> context) {
    String bundleID = "unknown";
    if (context != null) {
      Bundle bundle = FrameworkUtil.getBundle(context);
      if (bundle != null) {
        bundleID = bundle.getSymbolicName();
      }
    }
    return bundleID;
  }

  /**
   * Return the last exception that has been handled with
   * {@link #handleException(String, Throwable, Class)}.
   * 
   * <p>
   * This property can be used to check in unit tests if any error has occurred and was handled in
   * the UI.
   * </p>
   * 
   * @return The {@link IStatus} created by from the exception.
   */
  public Optional<IStatus> getLastException() {
    return lastException;
  }

  /**
   * Clear the last exception that has been handled with
   * {@link #handleException(String, Throwable, Class)}. This means calls to
   * {@link #getLastException()} will return an empty value.
   * 
   */
  public void clearLastException() {
    lastException = Optional.empty();
  }

  /**
   * Create a status from an exception.
   * 
   * @param message The additional message to use.
   * @param bundleID The bundle ID of the originating error.
   * @param ex The exception to create the status from.
   * @return A status with the stack-trace and the message.
   */
  private MultiStatus createStatusFromException(String message, String bundleID, Throwable ex) {
    List<Status> children = new ArrayList<>();
    StackTraceElement[] stackTrace = ex.getStackTrace();

    for (StackTraceElement stackTraceElement : stackTrace) {
      Status status = new Status(IStatus.ERROR, bundleID, stackTraceElement.toString());
      children.add(status);
    }

    return new MultiStatus(bundleID, IStatus.ERROR, children.toArray(new Status[] {}),
        ex.toString(), ex);

  }

}
