package eu.soa4all.studio.luf.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.rdf2go.RepositoryModel;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;

import eu.soa4all.studio.luf.controller.exceptions.ConfigurationException;
import eu.soa4all.studio.luf.controller.exceptions.RDFRepositoryException;
import eu.soa4all.studio.luf.controller.impl.ConfigurationImpl;

public class CommonLinkedUserFeedbackService
{
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static RDFRepositoryConnector rdfRepositoryConnector = null;
	protected static Configuration config;
	protected static String LUFBaseUri;
	protected static Map<String,Map<String,String>> credentials;

    public static void addRatingTriples(String itemId, String userId, String value,
    		String minValue, String maxValue, String dateTime, String ratingURIid) {
    	modelInit();
		try {
			RepositoryModel repoModel = rdfRepositoryConnector.openRepositoryModel();
			URI itemURI = repoModel.createURI(itemId);
			URI ratingURI = repoModel.createURI(ratingURIid);
			URI userURI = repoModel.createURI(userId);
			URI reviewType = repoModel.createURI("http://purl.org/stuff/rev#Review");
			URI hasReview = repoModel.createURI("http://purl.org/stuff/rev#hasReview");
			URI hasType = repoModel.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#Type");
			URI hasReviewer = repoModel.createURI("http://purl.org/stuff/rev#reviewer");
			URI hasRating = repoModel.createURI("http://purl.org/stuff/rev#rating");
			URI createdOn = repoModel.createURI("http://purl.org/stuff/rev#createdOn");
			URI hasMinRating = repoModel.createURI("http://purl.org/stuff/rev#minRating");
			URI hasMaxRating = repoModel.createURI("http://purl.org/stuff/rev#maxRating");
			URI dateTimeType = repoModel.createURI("http://www.w3.org/2001/XMLSchema#dateTime");
			DatatypeLiteral dateTimeLiteral = repoModel.createDatatypeLiteral(dateTime, dateTimeType);
			repoModel.addStatement(itemURI, hasReview, ratingURI);
			repoModel.addStatement(ratingURI, hasRating, value);
			repoModel.addStatement(ratingURI, hasType, reviewType);
			repoModel.addStatement(ratingURI, hasReviewer, userURI);
			repoModel.addStatement(ratingURI, createdOn, dateTimeLiteral);
			repoModel.addStatement(ratingURI, hasMinRating, minValue);
			repoModel.addStatement(ratingURI, hasMaxRating, maxValue);
			rdfRepositoryConnector.closeRepositoryModel(repoModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static void addCommentTriples(String itemId, String userId, String comment,
				String dateTime, String commentURIid) {
    	
    	modelInit();
    	try {
	    	RepositoryModel repoModel = rdfRepositoryConnector.openRepositoryModel();
	    	URI itemURI = repoModel.createURI(itemId);
	    	URI commentURI = repoModel.createURI(commentURIid);
	    	URI userURI = repoModel.createURI(userId);
	    	URI reviewType = repoModel.createURI("http://purl.org/stuff/rev#Review");
	    	URI hasReview = repoModel.createURI("http://purl.org/stuff/rev#hasReview");
	    	URI hasType = repoModel.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#Type");
	    	URI hasReviewer = repoModel.createURI("http://purl.org/stuff/rev#reviewer");
	    	URI hasText = repoModel.createURI("http://purl.org/stuff/rev#text");
	    	URI createdOn = repoModel.createURI("http://purl.org/stuff/rev#createdOn");
	    	URI dateTimeType = repoModel.createURI("http://www.w3.org/2001/XMLSchema#dateTime");
	    	DatatypeLiteral dateTimeLiteral = repoModel.createDatatypeLiteral(dateTime, dateTimeType);
	    	repoModel.addStatement(itemURI, hasReview, commentURI);
	    	repoModel.addStatement(commentURI, hasText, comment);
	    	repoModel.addStatement(commentURI, hasType, reviewType);
	    	repoModel.addStatement(commentURI, hasReviewer, userURI);
	    	repoModel.addStatement(commentURI, createdOn, dateTimeLiteral);
	    	rdfRepositoryConnector.closeRepositoryModel(repoModel);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }

    public static void addTaggingTriples(String itemId, String userId, String[] tagList,
			String dateTime, String taggingURIid) {
	
    	modelInit();
    	try {
        	RepositoryModel repoModel = rdfRepositoryConnector.openRepositoryModel();
        	URI itemURI = repoModel.createURI(itemId);
        	URI taggingURI = repoModel.createURI(taggingURIid);
        	URI userURI = repoModel.createURI(userId);
        	URI tagType = repoModel.createURI("http://www.holygoat.co.uk/owl/redwood/0.1/tags/Tagging");
        	URI hasTagging = repoModel.createURI("http://www.holygoat.co.uk/owl/redwood/0.1/tags/tag");
        	URI hasType = repoModel.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#Type");
        	URI taggedBy = repoModel.createURI("http://www.holygoat.co.uk/owl/redwood/0.1/tags/taggedBy");
        	URI associatedTag = repoModel.createURI("http://www.holygoat.co.uk/owl/redwood/0.1/tags/associatedTag");
        	URI taggedOn = repoModel.createURI("http://www.holygoat.co.uk/owl/redwood/0.1/tags/taggedOn");
        	URI dateTimeType = repoModel.createURI("http://www.w3.org/2001/XMLSchema#dateTime");
        	DatatypeLiteral dateTimeLiteral = repoModel.createDatatypeLiteral(dateTime, dateTimeType);
        	repoModel.addStatement(itemURI, hasTagging, taggingURI);
        	repoModel.addStatement(taggingURI, hasType, tagType);
        	repoModel.addStatement(taggingURI, taggedBy, userURI);
        	repoModel.addStatement(taggingURI, taggedOn, dateTimeLiteral);
        	for(String tag : tagList) {
        		if(tag.compareTo("")!=0){
            		repoModel.addStatement(taggingURI, associatedTag, tag);
        		}
        	}
        	rdfRepositoryConnector.closeRepositoryModel(repoModel);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }

    /**
     * 
     * Adds provenance data about the inserted statements
     * 
     * @param reviewURI
     * @param origin
     * @param dateTime
     */
    public static void addProvenanceTriples(String reviewURI, String origin, String dateTime) {
    	modelInit();
		try {
        	RepositoryModel repoModel = rdfRepositoryConnector.openRepositoryModel();
			// Tests to store provenance information
			// TODO: Finish properly
			URI dataCreationType = repoModel.createURI("http://purl.org/net/provenance/ns#DataCreation");
        	URI hasType = repoModel.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#Type");
			URI dataItemType = repoModel.createURI("http://purl.org/net/provenance/ns#DataItem");
			URI prvPerformedAt = repoModel.createURI("http://purl.org/net/provenance/ns#performedAt");
			URI prvPerformedBy = repoModel.createURI("http://purl.org/net/provenance/ns#performedBy");
			URI prvUsedData = repoModel.createURI("http://purl.org/net/provenance/ns#usedData");
			URI createdBy = repoModel.createURI("http://purl.org/net/provenance/ns#createdBy");
			URI graphType = repoModel.createURI("http://www.w3.org/2004/03/trix/rdfg-1/Graph");
			URI primaryTopic = repoModel.createURI("http://xmlns.com/foaf/0.1/primaryTopic");
			URI lufUri = repoModel.createURI("http://soa4all.isoco.net/luf/tools/luf");
			URI dateTimeType = repoModel.createURI("http://www.w3.org/2001/XMLSchema#dateTime");
			URI toolUri = repoModel.createURI(origin);
			DatatypeLiteral dateTimeLiteral = repoModel.createDatatypeLiteral(dateTime, dateTimeType);

			// Data creation 1 (external tool) 
//			BlankNode dataCreation1 = repoModel.createBlankNode();
        	URI dataCreation1 = repoModel.createURI(reviewURI+"/provenanceUsedDataCreation");
			repoModel.addStatement(dataCreation1, hasType, dataCreationType);
			repoModel.addStatement(dataCreation1, prvPerformedAt, dateTimeLiteral);
			repoModel.addStatement(dataCreation1, prvPerformedBy, toolUri);
			// Data item 1 (from external tool)			
//			BlankNode usedData = repoModel.createBlankNode();
        	URI usedData = repoModel.createURI(reviewURI+"/provenanceUsedData");
			repoModel.addStatement(usedData, hasType, dataItemType);
			repoModel.addStatement(usedData, createdBy, dataCreation1);
			// Data creation 2 (LUF)
//			BlankNode dataCreation = repoModel.createBlankNode();
        	URI dataCreation = repoModel.createURI(reviewURI+"/provenanceDataCreation");
			repoModel.addStatement(dataCreation, hasType, dataCreationType);
			repoModel.addStatement(dataCreation, prvPerformedAt, dateTimeLiteral);
			repoModel.addStatement(dataCreation, prvPerformedBy, lufUri);
			repoModel.addStatement(dataCreation, prvUsedData, usedData);
			// Data item 2 (from LUF)
        	URI rev = repoModel.createURI(reviewURI);
        	URI prov = repoModel.createURI(reviewURI+"/provenance");
			repoModel.addStatement(prov, hasType, graphType);
			repoModel.addStatement(prov, hasType, dataItemType);
			repoModel.addStatement(prov, primaryTopic, rev);
			repoModel.addStatement(prov, createdBy, dataCreation);
			rdfRepositoryConnector.closeRepositoryModel(repoModel);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }
    
    public static QueryResultTable evalSPARQLQuery(String paramQuery) {
    	modelInit();
		try {
			RepositoryModel repoModel = rdfRepositoryConnector.openRepositoryModel();
	    	QueryResultTable results = repoModel.sparqlSelect(paramQuery);
			rdfRepositoryConnector.closeRepositoryModel(repoModel);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
	/**
	 * Returns an MD5 hash to be used to create URIs
	 * 
	 * @param hashInput
	 * @return
	 */
    public static String md5(String hashInput){
        String hash = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(hashInput.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++) {
                tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) {
                    hash += "0" + tmp;
                } else {
                    hash += tmp;
                }
            }
        } catch (NoSuchAlgorithmException ex) {}
        return hash;
    }

	private static void modelInit() {
		if(rdfRepositoryConnector==null) {
			try {
				config = new ConfigurationImpl();
				rdfRepositoryConnector = new RDFRepositoryConnector(config.getServiceRepositoryServerUri(), config.getServiceRepositoryName());
			} catch (ConfigurationException e) {
	    		System.out.println("Configuration Exception: " + e.getMessage());
			} catch (RDFRepositoryException e) {
	    		System.out.println("RDF Repository Exception: " + e.getMessage());
			}
		}
	}

	public static String getLUFBaseUri() {

		if(LUFBaseUri==null) {
			try {
				config = new ConfigurationImpl();
				LUFBaseUri = config.getServiceLUFBaseUri();
			} catch (ConfigurationException e) {
	    		System.out.println("Configuration Exception: " + e.getMessage());
			}
		}
		return LUFBaseUri;
	}
	
	public static String verifyOAuth(HttpContext hc, Map<String,String> OAuthParams, List<Map<String,String>> extraParams){

		credentialsInit();
		try {

		    // wrap incoming request for OAuth signature verification
		    OAuthServerRequest request = new OAuthServerRequest(hc.getRequest());

		    System.out.println("request url: "+request.getRequestURL());
		    
		    // get incoming OAuth parameters
		    OAuthParameters params = new OAuthParameters();
		    params.readRequest(request);
		    
		    // get OAuh parameters from the body (bug with the library that does not do it?)
		    if(OAuthParams.get("oauth_consumer_key")!=null){
			    params.setConsumerKey(OAuthParams.get("oauth_consumer_key"));
		    }
		    if(OAuthParams.get("oauth_token")!=null){
		    	params.setToken(OAuthParams.get("oauth_token"));
		    }
		    if(OAuthParams.get("oauth_signature_method")!=null){
		    	params.setSignatureMethod(OAuthParams.get("oauth_signature_method"));
		    }
		    if(OAuthParams.get("oauth_timestamp")!=null){
		    	params.setTimestamp(OAuthParams.get("oauth_timestamp"));
		    }
		    if(OAuthParams.get("oauth_nonce")!=null){
		    	params.setNonce(OAuthParams.get("oauth_nonce"));
		    }
		    if(OAuthParams.get("oauth_version")!=null){
		    	params.setVersion(OAuthParams.get("oauth_version"));
		    }
		    if(OAuthParams.get("oauth_signature")!=null){
		    	params.setSignature(OAuthParams.get("oauth_signature"));
		    }
		    
		    for(Map<String,String> extraParam : extraParams) {
		    	if(extraParam.get("param")!=null){
				    params.put(extraParam.get("param"),extraParam.get("value"));
		    	}
		    }
		    
		    // verify credentials: token 
			Map<String,String> c = credentials.get(params.getConsumerKey());
			if(c==null){
				return null;
			} else {
				if((c.get("token")==null)||(c.get("token").compareTo(params.getToken())!=0)){
					return null;
				} else {
				    OAuthSecrets secrets = new OAuthSecrets();
				    secrets.setConsumerSecret(c.get("consumerSecret"));
				    secrets.setTokenSecret(c.get("tokenSecret"));
				    
				    // TODO: Remove this and substitute for the actual OAuth verification below
				    // when the cross-domain issues are fixed
		        	return c.get("origin");
//				    try {
//				        if(OAuthSignature.verify(request, params, secrets)){
//				        	return c.get("origin");
//				        } else {
//					        return null;
//				        }
//				    }
//				    catch (OAuthSignatureException ose) {
//				        return null;
//				    }	
				}
			}
		} catch (Exception e) {
	        return null;
		}
	}

	private static void credentialsInit() {
		if(credentials==null) {
			try {
				config = new ConfigurationImpl();
				credentials = config.getOAuthCredentials();
			} catch (ConfigurationException e) {
	    		System.out.println("Configuration Exception: " + e.getMessage());
			} 
		}
	}

}