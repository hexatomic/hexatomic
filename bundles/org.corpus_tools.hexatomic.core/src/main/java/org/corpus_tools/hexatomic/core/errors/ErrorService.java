package org.corpus_tools.hexatomic.core.errors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hexatomic error handling service.
 * 
 * This service allows you to display errors (e.g. exceptions) to the users and
 * log them in the log files and a special view.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Singleton
@Creatable
public class ErrorService {

	private static final String ERROR_OCCURED_MSG = "An error occured";
	private static final org.slf4j.Logger errorServiceLog = org.slf4j.LoggerFactory.getLogger(ErrorService.class);

	/**
	 * Display a (runtime) exception to the user and add it to the log.
	 * 
	 * @param message A message to the user explaining what went wrong.
	 * @param ex      The exception that occurred.
	 * @param context The class in which the error occurred. This is used to extract
	 *                the bundle ID and to get the correct logger when logging the
	 *                error to the console/log file.
	 */
	public void handleException(String message, Throwable ex, Class<?> context) {
		// log error so whatever happens, it is visible in the log-file/console
		Logger log = getLogger(context);
		log.error("Exception occured: {}", message, ex);

		String bundleID = getBundleID(context);
		ErrorDialog.openError(null, ERROR_OCCURED_MSG, message, createStatusFromException(message, bundleID, ex));
	}

	/**
	 * Show an error message to the user
	 * 
	 * @param title   The title, can be null to show a default title
	 * @param message The error message to show.
	 * @param reason  An additional reason for the error.
	 * @param context The class in which the error occurred. This is used to get the
	 *                correct logger when logging the error to the console/log-file.
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
	 * Get the logger for the given class or the logger of <em>this</em> class if
	 * null.
	 * 
	 * @param context The class get the logger for.
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
	 * Create a status from an exception.
	 * 
	 * @param message  The additional message to use.
	 * @param bundleID The bundle ID of the originating error.
	 * @param ex       The exception to create the status from.
	 * @return A status with the stack-trace and the message.
	 */
	private MultiStatus createStatusFromException(String message, String bundleID, Throwable ex) {
		List<Status> children = new ArrayList<>();
		StackTraceElement[] stackTrace = ex.getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			Status status = new Status(IStatus.ERROR, bundleID, stackTraceElement.toString());
			children.add(status);
		}

		return new MultiStatus(bundleID, IStatus.ERROR, children.toArray(new Status[] {}), ex.toString(), ex);

	}

}
