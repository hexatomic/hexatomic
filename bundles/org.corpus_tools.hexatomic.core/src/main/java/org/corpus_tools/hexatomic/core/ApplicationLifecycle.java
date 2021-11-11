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

package org.corpus_tools.hexatomic.core;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import java.net.URL;
import org.corpus_tools.hexatomic.core.update.AppStartupCompleteEventHandler;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;


/**
 * Class that hooks into the application lifecycle and configures logging.
 * 
 * <p>
 * Also see https://wiki.eclipse.org/Eclipse4/RCP/Lifecycle
 * </p>
 * 
 * @author Thomas Krause
 *
 */
public class ApplicationLifecycle {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ApplicationLifecycle.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.updates");
  
  /**
   * Called when the model is loaded and initializes the logging.
   */
  @ProcessAdditions
  private void processAdditions() {
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
  
  @PostContextCreate
  void postContextcreate(final IProvisioningAgent agent, 
      UISynchronize sync,
      IProgressMonitor monitor,
      IEventBroker eventBroker,
      IEclipseContext context) {
    eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, 
        new AppStartupCompleteEventHandler(eventBroker, 
            context,
            agent,
            sync,
            monitor));

    if (prefs.getBoolean("autoUpdate", false)) {
      System.out.println("true");
    } else {
      System.out.println("false");
    }   
    
  }
  

  
  

}

