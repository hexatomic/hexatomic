package org.corpus_tools.hexatomic.logging;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		configureLogback(context.getBundle());
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	private void configureLogback(Bundle bundle) throws JoranException, IOException {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();
		
		Location installationLocation = Platform.getInstallLocation();
		File productionConfig = new File(installationLocation.getURL().getPath(), "logback.xml");
		if(productionConfig.isFile()) {
			// use the customized logging configuration
			jc.doConfigure(productionConfig);
		} else {
			URL url = FileLocator.find(bundle, new Path("logback-test.xml"), null);
			if(url != null ) {
				// use the default configuration from the classpath for tests
				jc.doConfigure(url);
			}
		}
	}

}
