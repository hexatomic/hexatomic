package org.corpus_tools.hexatomic.core.errors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.dialogs.ErrorDialog;
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

	private static final String ERROR_OCCURED_MSG = "Error occured";
	private static final org.slf4j.Logger errorServiceLog = org.slf4j.LoggerFactory.getLogger(ErrorService.class);

	/**
	 * Display an (runtime-) exception to the user and add it to the log.
	 * 
	 * @param message A message to the user what went wrong.
	 * @param ex      The exception that occurred.
	 * @param context The class in which the error occured. This is used to extract
	 *                the bundle ID.
	 */
	public void handleException(String message, Throwable ex, Class<?> context) {
		// log error so whatever happens, it is visible in the logfile/console
		Logger log = getLogger(context);
		log.error("Exception occured: {}", message, ex);

		String bundleID = getBundleID(context);
		ErrorDialog.openError(null, ERROR_OCCURED_MSG, message, createStatusFromException(message, bundleID, ex));
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
	 * @param bundleID The bundle ID of the orignating error.
	 * @param ex The exception to create the status from.
	 * @return A status with the stacktrace and the message.
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
