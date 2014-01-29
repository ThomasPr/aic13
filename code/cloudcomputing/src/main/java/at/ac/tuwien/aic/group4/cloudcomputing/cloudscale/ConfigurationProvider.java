package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleConfigurationProvider;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfiguration;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfigurationBuilder;
import at.ac.tuwien.infosys.cloudscale.vm.ec2.EC2CloudPlatformConfiguration;

public class ConfigurationProvider {
	
	@CloudScaleConfigurationProvider
	public static CloudScaleConfiguration getConfiguration() throws FileNotFoundException, IOException {
		
		EC2CloudPlatformConfiguration ec2Config = new EC2CloudPlatformConfiguration();
		ec2Config.setAwsConfigFile("ec2.properties");
		ec2Config.setAwsEndpoint("ec2.ap-northeast-1.amazonaws.com");
//		ec2Config.setImageName("ami-eac5209d");
		ec2Config.setImageName("CloudScale_v0.2.0-mod");
		ec2Config.setInstanceType("t1.micro");
		ec2Config.setSshKey("aic13-t3-g4-thomas");
		
		CloudScaleConfiguration config = CloudScaleConfigurationBuilder
//				.createLocalConfigurationBuilder()
				.createOpenStackConfigurationBuilder("ec2.properties", "54.238.204.152")
				.with(ec2Config)
				.withGlobalLoggingLevel(Level.WARNING)
				.with(new NumberOfRequestsScalingPolicy())
				.withMonitoring(true)
				.withMonitoringEvents(LoadEvent.class)
				.build();
		
		config.server().setIsAliveInterval(15000);
		
		return config;
	}
}	
