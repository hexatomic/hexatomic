package org.corpus_tools.hexatomic.core;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Class that hooks into the application lifecycle and configures logging.
 * 
 * Also see https://wiki.eclipse.org/Eclipse4/RCP/Lifecycle
 * 
 * @author Thomas Krause
 *
 */
public class ApplicationLifecycle {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationLifecycle.class);

	public static final String EDITOR_TAG = "org.corpus_tools.hexatomic.editor";

	/**
	 * Called when the model is loaded and initializes the logging.
	 */
	@ProcessAdditions
	private void processAdditions(EModelService modelService, MApplication application) {

		initLogging();
		registerEditors(modelService, application);
	}

	private void initLogging() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();

		Location installationLocation = Platform.getInstallLocation();
		File productionConfig = new File(installationLocation.getURL().getPath(), "logback.xml");
		try {
			if (productionConfig.isFile()) {
				// use the customized logging configuration
				jc.doConfigure(productionConfig);
				log.info("Logging configured from logback.xml in the Hexatomic root folder");
			} else {
				Bundle bundle = FrameworkUtil.getBundle(ApplicationLifecycle.class);
				URL url = FileLocator.find(bundle, new Path("logback-test.xml"), null);
				if (url != null) {
					// use the default configuration from the classpath for tests
					jc.doConfigure(url);
					log.info("Logging configured from internal configuration");
				}
			}
		} catch (JoranException ex) {
			log.error("Could not configure logging", ex);
		}

	}

	private void registerEditors(EModelService modelService, MApplication application) {
		// Find all descriptors with the correct tag
		List<MPartDescriptor> editorParts = application.getDescriptors().stream()
				.filter((p) -> p.getTags().contains(EDITOR_TAG)).collect(Collectors.toList());
		
		for(MPartDescriptor desc : editorParts) {
			// Create a menu item for this editor
			MHandledMenuItem menuItem = modelService.createModelElement(MHandledMenuItem.class);
			// TODO: set menu item command and at it to the menu containing all editors
		}

	}

}
