package org.openhab.io.config.rest;

import org.openhab.io.config.rest.beans.IConfigRest;
import org.openhab.io.config.rest.impl.ConfigRestImpl;
import org.openhab.io.config.rest.servlet.ConfigRestServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
	
	public static final String REST_SERVLET_ALIAS = "/config";
    
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    HttpService httpService;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
//		System.out.println("Hello World!!");
		logger.info("Register Config Rest");
	    context.registerService(
	            IConfigRest.class.getName(),
	            new ConfigRestImpl(),
	            null);
	    ConfigRestServlet configRestServlet = new ConfigRestServlet();
	    httpService = (HttpService)context.getService 
	    	    (context.getServiceReference(HttpService.class.getName()));
	    httpService.registerServlet(REST_SERVLET_ALIAS, configRestServlet, null, null);
//	    httpService.registerServlet(REST_SERVLET_ALIAS, new TestHttp(), null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
//		System.out.println("Goodbye World!!");
//		logger.info("Unregister Config Rest ");
		if (this.httpService != null) {
            httpService.unregister(REST_SERVLET_ALIAS);
            logger.info("Stopped CONFIG REST API");
        }
	}

}
