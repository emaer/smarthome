package org.openhab.io.config.rest.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhab.io.config.rest.beans.IConfigRest;
import org.openhab.io.config.rest.impl.ConfigRestImpl;

public class ConfigRestServlet  extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8086622978263151294L;

	/** 
     * 实现测试 . 
     * @param request the req. 
     * @param response the res. 
     * @throws IOException io exception. 
     */ 
    public void doGet( 
            HttpServletRequest request, 
            HttpServletResponse response 
            ) throws IOException { 
    	try {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	String strUrl = request.getParameter("uri");
    	String strItemID = request.getParameter("iid");
    	String strPid = request.getParameter("pid");
    	String strName = request.getParameter("name");

    	System.out.println("------------------------------------------------------");
    	System.out.println("uri:" + strUrl);
    	System.out.println("Pid:" + strPid);
    	System.out.println("iid:" + strItemID);
    	System.out.println("Name:" + strName);
    	System.out.println("------------------------------------------------------");
		if (strUrl == null || strUrl.isEmpty() || 
				strItemID == null || strItemID.isEmpty()){
			System.out.println("parameter error.");
			ServletOutputStream stream = response.getOutputStream();
			stream.write("-Error".getBytes("UTF-8"));
			stream.flush();
			stream.close();
			return;
		}
		
//		if (strPid == null || strPid.isEmpty() || 
//				strName == null || strName.isEmpty()){
//			
//		}

//		response.getWriter()
//        .write("URL:" + strUrl + "\r\n");
//		response.getWriter()
//        .write("Type:" + strPid + "\r\n");
//		response.getWriter()
//        .write("ItemID:" + strItemID + "\r\n");
//		response.getWriter()
//        .write("Name" + strName + "\r\n");

		
//        response.getWriter()
//            .write("hello osgi http servlet.time now is "+new Date() + "\r\n");
//
		IConfigRest configRest = new ConfigRestImpl();//"http://localhost:8080/rest/sitemaps/default/0000"; //
//		String sitmap = "http://localhost:8080/rest/sitemaps/default/default";
		if (!configRest.Configure(strUrl, strItemID, strPid, strName)){
			response.getWriter()
            .write("-Error");
		}
    }
    
    public void doPost(
            HttpServletRequest request, 
            HttpServletResponse response 
            ) throws IOException { 
    	doGet(request, response );
    }
}
