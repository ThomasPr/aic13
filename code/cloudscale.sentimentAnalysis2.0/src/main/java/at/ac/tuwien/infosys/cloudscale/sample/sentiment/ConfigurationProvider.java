/*
   Copyright 2013 Philipp Leitner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleConfigurationProvider;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfiguration;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfigurationBuilder;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.NumberOfRequestEvent;

public class ConfigurationProvider {
	
	@CloudScaleConfigurationProvider
	public static CloudScaleConfiguration getConfiguration()
			throws FileNotFoundException, IOException
	{
		
		// this method delivers the configuration for cloudscale
		CloudScaleConfiguration cfg = CloudScaleConfigurationBuilder
				// enable local configuration for testing ...
				 .createLocalConfigurationBuilder()
				// or Openstack configuration to actually deploy to the cloud
//				.createOpenStackConfigurationBuilder("openstack.props",
//						"128.130.172.197")
				.withGlobalLoggingLevel(Level.SEVERE)
// 				.with(new SentimentScalingPolicy())
//				.with(new CPUScalingPolicy())
//				.with(new FixedNumberScalingPolicy())
				.with(new NumberOfRequestsScalingPolicy())
				.withMonitoring(true)
// 				.withMonitoringEvents(ClassificationDurationEvent.class)
				.withMonitoringEvents(NumberOfRequestEvent.class)
				.build();
		
		// this governs how often we run the scaling-down check for each thread
		// (check every 5 minutes)
		cfg.common().setScaleDownIntervalInSec(60 * 5);
		
		// as we will get some classloading exceptions from Twitter4J (expected), we disable some loggers
		cfg.server().logging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.RemoteClassLoader", Level.OFF);
		cfg.server().logging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.fileCollectors.FileBasedFileCollector", Level.OFF);
		cfg.common().clientLogging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.RemoteClassProvider", Level.OFF);
		cfg.common().clientLogging().setCustomLoggingLevel(
				"at.ac.tuwien.infosys.cloudscale.classLoader.caching.fileCollectors.FileBasedFileCollector", Level.OFF);
		
		return cfg;
		
	}
	
}	
