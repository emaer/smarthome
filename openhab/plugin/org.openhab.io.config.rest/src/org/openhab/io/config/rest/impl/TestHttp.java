/**
 * 
 */
package org.openhab.io.config.rest.impl;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.json.JSONWithPadding;

/**
 * @author Emaer
 *
 */
@Path("test")
public class TestHttp {

	private static final Logger logger = LoggerFactory.getLogger(TestHttp.class);
	
	@Context UriInfo uriInfo;
    @GET @Path("/{itemname: [a-zA-Z_0-9]*}/state")
	@Produces( { MediaType.TEXT_PLAIN })
    public Response getItems(
    		@Context HttpHeaders headers,
    		@QueryParam("type") String type, 
    		@QueryParam("jsoncallback") @DefaultValue("callback") String callback) {
		logger.debug("Received HTTP GET request at '{}' for media type '{}'.", new String[] { uriInfo.getPath(), type });

		System.out.println("........adfadsfadsf.........");
		String responseType = MediaTypeHelper.getResponseMediaType(headers.getAcceptableMediaTypes(), type);
		if(responseType!=null) {
	    	Object responseObject = "TestInfo";
	    	return Response.ok(responseObject, responseType).build();
		} else {
			return Response.notAcceptable(null).build();
		}
    }
}
