package org.openhab.io.config.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	public final static int MAX_TOTAL_CONNECTIONS = 800;
	public final static int WAIT_TIMEOUT = 300000;
	public final static int MAX_ROUT_CONNECTIONS = 400;
	public final static int CONNECT_TIMEOUT = 300000;
	public final static int READ_TIMEOUT = 300000;

	private static BasicHttpParams httpParams = null;

	private static DefaultHttpClient httpClient = null;

	private static ThreadSafeClientConnManager clientConnectionManager = null;

	private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * Initialize httpClient pool
	 */
	static {
		try {
			httpParams = new BasicHttpParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					WAIT_TIMEOUT);

			HttpConnectionParams.setConnectionTimeout(httpParams,
					CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));

			// schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
			// .getSocketFactory()));
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null,
					new TrustManager[] { new TrustAnyTrustManager() }, null);
			SSLSocketFactory sslSf = new SSLSocketFactory(sslContext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, sslSf));

			clientConnectionManager = new ThreadSafeClientConnManager(
					schemeRegistry);
			clientConnectionManager.setDefaultMaxPerRoute(MAX_ROUT_CONNECTIONS);
			clientConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);

			httpClient = new DefaultHttpClient(clientConnectionManager,
					httpParams);

		} catch (Exception e) {
			log.error("Fail to Connect !!");
		}
	}

	/**
	 * Get httpClient for initializing
	 * 
	 * @return HttpClient
	 */
	public static HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * Close the clientConnectionManager
	 */
	public static void release() {
		if (clientConnectionManager != null) {
			clientConnectionManager.shutdown();
		}
	}

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	public static void send(String url, String jsonStr)
			throws ClientProtocolException, IOException {
		StringEntity strEntity = new StringEntity(jsonStr, "UTF-8");
		Header header = new BasicHeader("Content-Type",
				"application/json;charset=UTF-8");
		strEntity.setContentType(header);

		HttpClient httpClient = HttpClientUtil.getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(strEntity);

		HttpResponse httpPostResponse = httpClient.execute(httpPost);
		int code = httpPostResponse.getStatusLine().getStatusCode();
		if (code == 200 || code == 204) {
			System.out.println("send successfully");
		}
	}
	
	public static String get(String url, String params)
			throws ClientProtocolException, IOException {
		
		String tmpUrl = url;
		if(null != params && params.length() != 0){
			
			tmpUrl += "?" + params;
			
		}
		HttpClient httpClient = HttpClientUtil.getHttpClient();
		HttpGet httpGet = new HttpGet(tmpUrl);
		
		
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200 || code == 204) {
			
			HttpEntity entity = response.getEntity();   
			
			if (entity != null) {   
                String charset = getContentCharSet(entity);  
                   // ʹ��EntityUtils��toString���������ݱ��룬Ĭ�ϱ�����ISO-8859-1   
                String result = EntityUtils.toString(entity, charset);   
                
                return result;
			}else{
				
				System.out.println("response.getEntity() is null--");
				
			}
			
//			HttpEntity entity = response.getEntity();    
//	        if (entity != null) {    
//	            InputStream instreams = entity.getContent();    
//	            String str = convertStreamToString(instreams);  
//	            httpGet.abort();
//	           return str;  
//	        }  
//	        else{
//	        	
//	        	System.out.println("response.getEntity() is null--");
//	        	
//	        }
			
			
		}
		
		return null;
	}
	
	/** 
     * Ĭ�ϱ���utf -8 
     * Obtains character set of the entity, if known. 
     *  
     * @param entity must not be null 
     * @return the character set, or null if not found 
     * @throws ParseException if header elements cannot be parsed 
     * @throws IllegalArgumentException if entity is null 
     */    
    public static String getContentCharSet(final HttpEntity entity)   
        throws ParseException {   
   
        if (entity == null) {   
            throw new IllegalArgumentException("HTTP entity may not be null");   
        }   
        String charset = null;   
        if (entity.getContentType() != null) {    
            HeaderElement values[] = entity.getContentType().getElements();   
            if (values.length > 0) {   
                NameValuePair param = values[0].getParameterByName("charset" );   
                if (param != null) {   
                    charset = param.getValue();   
                }   
            }   
        }   
         
        if(null == charset || charset.length() == 0){  
            charset = "UTF-8";  
        }  
        return charset;   
    }  
    
    public static String convertStreamToString(InputStream is) {      
        StringBuilder sb1 = new StringBuilder();      
        byte[] bytes = new byte[4096];    
        int size = 0;    
          
        try {      
            while ((size = is.read(bytes)) > 0) {    
                String str = new String(bytes, 0, size, "UTF-8");    
                sb1.append(str);    
            }    
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb1.toString();      
    }  
	
}
