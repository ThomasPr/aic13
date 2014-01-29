package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleShutdown;

public class Main {

	@CloudScaleShutdown
	public static void main(String[] args) throws ServletException, LifecycleException, InterruptedException {

//		new SleepTask((int) (100000.0 * Math.random())).start();

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.setBaseDir(".");
		tomcat.getHost().setAppBase(".");
		tomcat.addWebapp("/", ".");
		tomcat.start();
		tomcat.getServer().await();
	}
}
