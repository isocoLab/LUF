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

import eu.soa4all.studio.luf.controller.CommonLinkedUserFeedbackService;

@Path("ratings")
public class Ratings {
    // and implement the following GET method 
    @GET
    @Produces("text/plain")
    @Path("/{id}")
    public String getRating(@PathParam("id") String ratingId) {
    	String ratingUri = CommonLinkedUserFeedbackService.getLUFBaseUri()+"ratings/"+ratingId;
    	String resp = "<response>";

    	String respItem = "";
    	String respRating = "";
    	String respMinRating = "";
    	String respMaxRating = "";
    	String respReviewer = "";
    	String respCreatedOn = "";
    	
    	String ratingQuery = "PREFIX rev: <http://purl.org/stuff/rev#> "
    		+ "SELECT ?item ?rating ?minRating ?maxRating ?reviewer ?createdOn "
    		+ "WHERE " 
    		+ "{ "
    		+ " ?item rev:hasReview <"+ratingUri+"> . "
    		+ " <"+ratingUri+"> rev:rating ?rating . "
    		+ " <"+ratingUri+"> rev:minRating ?minRating . "
    		+ " <"+ratingUri+"> rev:maxRating ?maxRating . "
    		+ " <"+ratingUri+"> rev:reviewer ?reviewer . "
    		+ " <"+ratingUri+"> rev:createdOn ?createdOn "
    		+ "}";
 
    	QueryResultTable response = CommonLinkedUserFeedbackService.evalSPARQLQuery(ratingQuery);

    	for(QueryRow row : response) {
        	respItem = row.getValue("item").toString();
        	respRating = row.getValue("rating").toString();
        	respMinRating = row.getValue("minRating").toString();
        	respMaxRating = row.getValue("maxRating").toString();
        	respReviewer = row.getValue("reviewer").toString();
        	respCreatedOn = row.getValue("createdOn").toString();
		}
    	if(respRating!="") {
        	resp += "<item>" + respItem + "</item>"
        	+ "<rating>" + respRating + "</rating>"
    		+ "<minRating>" + respMinRating + "</minRating>"
    		+ "<maxRating>" + respMaxRating + "</maxRating>"
    		+ "<reviewer>" + respReviewer+ "</reviewer>"
    		+ "<createdOn>" + respCreatedOn + "</createdOn>";
    	} else {
    		resp += "<error>No rating found with that id</error>";
    	}

    	resp += "</response>";
    	return resp;
    }
    
    @POST
    @Produces("text/plain")
	public String rateItem(
			@FormParam("itemId") String itemId,
			@FormParam("userId") String userId,
			@FormParam("rating") String rating,
			@FormParam("minValue") String minValue,
			@FormParam("maxValue") String maxValue,
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
		extraParam3.put("param", "rating");
		extraParam3.put("value", rating);
		extraParams.add(extraParam3);
		Map<String,String> extraParam4 = new HashMap<String,String>();
		extraParam4.put("param", "minValue");
		extraParam4.put("value", minValue);
		extraParams.add(extraParam4);
		Map<String,String> extraParam5 = new HashMap<String,String>();
		extraParam5.put("param", "maxValue");
		extraParam5.put("value", maxValue);
		extraParams.add(extraParam5);

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
    		if(minValue==null){
    			minValue = "1";
    		}
    		if(maxValue==null){
    			maxValue = "5";
    		}
    		if(rating==null){
    			response += "<error>Missing parameter: rating</error>"; 
    			error = true;
    		} else {
    			try {
    				if((Double.parseDouble(minValue)>Double.parseDouble(rating))
    						||(Double.parseDouble(maxValue)<Double.parseDouble(rating))){
    					response = "<error>Wrong rating value outside min and max</error>";
    					error = true;
    				}
    			} catch (NumberFormatException e) {
    				response += "<error>Wrong numeric value</error>";
    				error = true;
    			}
    		}
    		
    		if(!error){
    			Calendar cal = Calendar.getInstance();
    	        SimpleDateFormat sdf = new SimpleDateFormat(CommonLinkedUserFeedbackService.DATE_FORMAT_NOW);
    	        String dateTime = sdf.format(cal.getTime());

    	        // Get a hash to create the URI
    			String hash = CommonLinkedUserFeedbackService.md5(itemId + userId + dateTime);
    	        // Create the URI with the hash
    			String ratingURI = CommonLinkedUserFeedbackService.getLUFBaseUri()+"ratings/"+hash;

    	        CommonLinkedUserFeedbackService.addRatingTriples(itemId, userId, rating, minValue, maxValue, dateTime, ratingURI); 
    	        CommonLinkedUserFeedbackService.addProvenanceTriples(ratingURI, verification, dateTime); 
    	        response = "<status>ok</status><ratingId>"+ratingURI+"</ratingId>";
    		}
    	}

    	response += "</response>";
    	return response;
    }

}