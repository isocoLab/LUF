package eu.soa4all.studio.luf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import eu.soa4all.studio.luf.controller.CommonLinkedUserFeedbackService;

@Path("search")
public class Search {
    // and implement the following GET method 
    @GET
    @Produces("text/plain")
    public String getTagging(@QueryParam("itemId") String itemId, @QueryParam("userId") String userId) {
    	
    	String resp = "<response>";
    	
    	if((itemId==null)&&(userId==null)) {
        	resp += "<error>No search params specified</error>";
    	} else {

    		try {
    			
	    	    // 1) Retrieve the ratings:
	    		// ------------------------
	
	        	// Use search terms (itemId, userId)
	        	String searchTerms = "";
	        	if (itemId!=null) {
	        		searchTerms += "<" + itemId + "> rev:hasReview ?ratingUri . ";
	        	}
	        	if (userId!=null) {
	        		searchTerms += "?ratingUri rev:reviewer <" + userId + "> . ";
	        	}
	
	        	// Create and send query
	        	String searchQuery = "PREFIX rev: <http://purl.org/stuff/rev#> "
	        		+ "SELECT ?ratingUri ?rating ?minRating ?maxRating "
	        		+ "WHERE " 
	        		+ "{ "
	        		+ searchTerms
	        		+ " ?ratingUri rev:rating ?rating . "
	        		+ " ?ratingUri rev:minRating ?minRating . "
	        		+ " ?ratingUri rev:maxRating ?maxRating "
	        		+ "}";
	            	QueryResultTable response = CommonLinkedUserFeedbackService.evalSPARQLQuery(searchQuery);
	
	        	// Analyse the query response to extract the rating URIs and totals (number of ratings, average)
	        	double ratingsVal = 0;
	        	int ratingsNum = 0;
	        	double ratingsAverage = 0;
	        	String ratingsList = "<ratings>";
	
	        	for(QueryRow row : response) {
	        		ratingsList += "<ratingUri>" + row.getValue("ratingUri").toString() + "</ratingUri>";
	            	// We "normalise" the value in case it's not a 1..5 rating (using minRating and max Rating)
	        		double ratV = Double.parseDouble(row.getValue("rating").toString());
	        		double minV = Double.parseDouble(row.getValue("minRating").toString());
	        		double maxV = Double.parseDouble(row.getValue("maxRating").toString());
	        		double normalisedVal = (((ratV-minV)/(maxV-minV))*4)+1;
	    			ratingsVal += normalisedVal;
	    			ratingsNum += 1;
	    		}
	        	
	        	if(ratingsNum==0) {
	        		ratingsList += "No ratings";
	        	} else {
	        		ratingsAverage = ratingsVal / ratingsNum;
	        		ratingsList += "<ratingsAverage>" + Double.toString(ratingsAverage)  + "</ratingsAverage>";
	        		ratingsList += "<ratingsNumber>" + Integer.toString(ratingsNum) + "</ratingsNumber>";
	        	}
	        	ratingsList += "</ratings>";
	
	
	           	// 2) Retrieve the comments:
	        	// -------------------------
	
	        	// Use search terms (itemId, userId)
	        	searchTerms = "";
	        	if (itemId!=null) {
	        		searchTerms += "<" + itemId + "> rev:hasReview ?commentUri . ";
	        	}
	        	if (userId!=null) {
	        		searchTerms += "?commentUri rev:reviewer <" + userId + "> . ";
	        	}
	
	        	// Create and send query
	        	searchQuery = "PREFIX rev: <http://purl.org/stuff/rev#> "
	        		+ "SELECT ?commentUri "
	        		+ "WHERE " 
	        		+ "{ "
	        		+ searchTerms
	        		+ "?commentUri rev:text ?text . "
	        		+ "}";
	        	response = CommonLinkedUserFeedbackService.evalSPARQLQuery(searchQuery);
	       
	        	// Analyse the query response to extract the comment URIs and total number
	        	String commentsList = "<comments>";
	        	int commentsNum = 0;
	        	for(QueryRow row : response) {
	        		commentsList += "<commentUri>" + row.getValue("commentUri").toString() + "</commentUri>";
	    			commentsNum += 1;
	    		}
	        	if(commentsNum==0) {
	        		commentsList += "No comments";
	        	} else {
	        		commentsList += "<commentsNumber>" + Integer.toString(commentsNum) + "</commentsNumber>";
	        	}
	        	commentsList += "</comments>";
	
	
	        	// 3) Retrieve the taggings:
	        	// -------------------------
	        	
	        	// Use search terms (itemId, userId)
	        	searchTerms = "";
	        	if (itemId!=null) {
	        		searchTerms += "<" + itemId + "> tags:tag ?taggingUri . ";
	        	}
	        	if (userId!=null) {
	        		searchTerms += "?taggingUri tags:taggedBy <" + userId + "> . ";
	        	}
	
	        	// Create and send query
	        	searchQuery = "PREFIX rev: <http://purl.org/stuff/rev#> "
	        		+ "PREFIX tags: <http://www.holygoat.co.uk/owl/redwood/0.1/tags/> "
	        		+ "SELECT ?taggingUri ?associatedTag "
	        		+ "WHERE " 
	        		+ "{ "
	        		+ searchTerms
	        		+ " ?taggingUri tags:associatedTag ?associatedTag "
	        		+ "}";
	        	response = CommonLinkedUserFeedbackService.evalSPARQLQuery(searchQuery);
	
	        	// Analyse the query response to extract the tagging URIs and total tagAggregates
	        	String taggingsList = "<taggings>";
	        	int taggingsNum = 0;
	        	Map<String,Integer> tagWeights = new HashMap<String,Integer>();
	        	Map<String,Integer> taggingPre = new HashMap<String,Integer>();
	        	for(QueryRow row : response) {
	            	// Add taggingUri to the list only if not added before
	        		String respTaggingUri = row.getValue("taggingUri").toString();
	            	if(taggingPre.get(respTaggingUri)==null) {
	            		taggingsList += "<taggingUri>" + row.getValue("taggingUri").toString() + "</taggingUri>";
	        			taggingsNum += 1;
	            		taggingPre.put(respTaggingUri,1);
	            	} 
	            	String respAssociatedTag = row.getValue("associatedTag").toString();
	            	if(tagWeights.get(respAssociatedTag)==null) {
	                	tagWeights.put(respAssociatedTag,1);
	            	} else {
	            		tagWeights.put(respAssociatedTag, tagWeights.get(respAssociatedTag)+1);
	            	}
	    		}
	        	if(taggingsNum==0) {
	        		taggingsList += "No taggings";
	        	} else {
	            	taggingsList += "<taggingsNumber>" + Integer.toString(taggingsNum) + "</taggingsNumber>";
	            	Set<Map.Entry<String, Integer>> set = tagWeights.entrySet();
	                for (Map.Entry<String, Integer> tagWeight : set) {
	                	taggingsList += "<tagAggregate>"
	                		+ "<tag>" + tagWeight.getKey() + "</tag>"
	                		+ "<times>" + Integer.toString(tagWeight.getValue()) + "</times>"
	                		+ "</tagAggregate>";
	                }
	        	}
	        	taggingsList += "</taggings>";
	
	        	resp += ratingsList + commentsList + taggingsList;

    		} catch(Exception e) {
            	resp += "<error>Invalid query/parameters</error>";
        	}
    	}

    	resp += "</response>";
    	return resp;
    }
    	

}