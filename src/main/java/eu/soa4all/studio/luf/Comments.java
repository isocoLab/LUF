package eu.soa4all.studio.luf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.oauth.server.OAuthServerRequest;

import eu.soa4all.studio.luf.controller.CommonLinkedUserFeedbackService;

@Path("comments")
public class Comments {
    // and implement the following GET method 
    @GET
    @Produces("text/plain")
    @Path("/{id}")
    public String getComment(@PathParam("id") String commentId) {

    	String resp = "<response>";

    	String commentUri = CommonLinkedUserFeedbackService.getLUFBaseUri()+"comments/"+commentId;

    	String respItem = "";
    	String respText = "";
    	String respReviewer = "";
    	String respCreatedOn = "";

    	String commentQuery = "PREFIX rev: <http://purl.org/stuff/rev#> "
    		+ "SELECT ?item ?text ?reviewer ?createdOn "
    		+ "WHERE " 
    		+ "{ "
    		+ " ?item rev:hasReview <"+commentUri+"> . "
    		+ " <"+commentUri+"> rev:text ?text . "
    		+ " <"+commentUri+"> rev:reviewer ?reviewer . "
    		+ " <"+commentUri+"> rev:createdOn ?createdOn "
    		+ "}";
 
    	QueryResultTable response = CommonLinkedUserFeedbackService.evalSPARQLQuery(commentQuery);

    	for(QueryRow row : response) {
        	respItem = row.getValue("item").toString();
        	respText = row.getValue("text").toString();
        	respReviewer = row.getValue("reviewer").toString();
        	respCreatedOn = row.getValue("createdOn").toString();
		}
    	if(respItem!="") {
        	resp += "<item>" + respItem + "</item>"
        	+ "<text>" + respText + "</text>"
    		+ "<reviewer>" + respReviewer+ "</reviewer>"
    		+ "<createdOn>" + respCreatedOn + "</createdOn>";
    	} else {
    		resp += "<error>No comment found with that id</error>";
    	}
	
    	resp += "</response>";
    	return resp;
    }
    
    @POST
    @Produces("text/plain")
	public String commentItem(
			@FormParam("itemId") String itemId,
			@FormParam("userId") String userId,
			@FormParam("comment") String comment,
			@FormParam("oauth_consumer_key") String oauth_consumer_key,
			@FormParam("oauth_token") String oauth_token,
			@FormParam("oauth_signature_method") String oauth_signature_method,
			@FormParam("oauth_timestamp") String oauth_timestamp,
			@FormParam("oauth_nonce") String oauth_nonce,
			@FormParam("oauth_version") String oauth_version,
			@FormParam("oauth_signature") String oauth_signature,
			@Context HttpContext hc
		) {
    	
    	String response = "<response>";

    	// In case the params are in the POST body and not in the URL:
		Map<String,String> OAuthParams = new HashMap<String,String>();
		OAuthParams.put("oauth_consumer_key", oauth_consumer_key);
		OAuthParams.put("oauth_token", oauth_token);
		OAuthParams.put("oauth_signature_method", oauth_signature_method);
		OAuthParams.put("oauth_timestamp", oauth_timestamp);
		OAuthParams.put("oauth_nonce", oauth_nonce);
		OAuthParams.put("oauth_version", oauth_version);
		OAuthParams.put("oauth_signature", oauth_signature);
		// Also to check the OAuth signature we need to pass these:
		List<Map<String,String>> extraParams = new ArrayList<Map<String,String>>();
		Map<String,String> extraParam1 = new HashMap<String,String>();
		extraParam1.put("param", "itemId");
		extraParam1.put("value", itemId);
		extraParams.add(extraParam1);
		Map<String,String> extraParam2 = new HashMap<String,String>();
		extraParam2.put("param", "userId");
		extraParam2.put("value", userId);
		extraParams.add(extraParam2);
		Map<String,String> extraParam3 = new HashMap<String,String>();
		extraParam3.put("param", "comment");
		extraParam3.put("value", comment);
		extraParams.add(extraParam3);
		
    	String verification = CommonLinkedUserFeedbackService.verifyOAuth(hc, OAuthParams, extraParams);

    	if(verification==null) {
    		response += "<error>Could not authenticate with OAuth</error>";
    	} else {
        	boolean error = false;
    		if(itemId==null){
    			response += "<error>Missing parameter: itemId</error>";
    			error = true;
    		}
    		if(userId==null){
    			response += "<error>Missing parameter: userId</error>"; 
    			error = true;
    		}
    		if(comment==null){
    			response += "<error>Missing parameter: comment</error>"; 
    			error = true;
    		} 
    		
    		if(!error){
    			Calendar cal = Calendar.getInstance();
    	        SimpleDateFormat sdf = new SimpleDateFormat(CommonLinkedUserFeedbackService.DATE_FORMAT_NOW);
    	        String dateTime = sdf.format(cal.getTime());

    	        // Get a hash to create the URI
    			String hash = CommonLinkedUserFeedbackService.md5(itemId + userId + dateTime);
    	        // Create the URI with the hash
    			String commentURI = CommonLinkedUserFeedbackService.getLUFBaseUri()+"comments/"+hash;

    	        CommonLinkedUserFeedbackService.addCommentTriples(itemId, userId, comment, dateTime, commentURI);
    	        CommonLinkedUserFeedbackService.addProvenanceTriples(commentURI, verification, dateTime); 
    	        response += "<status>ok</status><commentId>"+commentURI+"</commentId>";
    		}
    	}
		
		response += "</response>";
    	return response;
    }

}