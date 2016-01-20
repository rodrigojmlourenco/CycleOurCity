package org.cycleourcity.server.middleware;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.exceptions.InvalidIdentifierException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.server.security.exceptions.SecretKeyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CycleOurCitySecurityManager {

	public final static int TOKEN_MAX_TRIES = 10000;
	public final static int TOKEN_TTL		= 3600000; //1hour
	public final static String TOKEN_ISSUER = "www.cycleourcity.org";
	
	//Logging
	private static Logger LOG = LoggerFactory.getLogger(CycleOurCitySecurityManager.class);

	//Driver
	private AccountManagementDriver driver = AccountManagementDriverImpl.getManager();

	//Singleton
	private static CycleOurCitySecurityManager MANAGER = new CycleOurCitySecurityManager();

	//Support Data Structures
	private ConcurrentHashMap<String, Date>	expirationDates;
	private ConcurrentHashMap<String, String> authenticationTokens;
	
	//<user,jti>
	private ConcurrentHashMap<String, String> userTokens;
	
	private final String SECRET;
	private final String SECRET_FILE = System.getenv("HOME")+"/.secret/key";
	
	private String loadSecretFromFile() throws IOException{
		File key = new File(SECRET_FILE);
		return new String(Files.readAllBytes(key.toPath()));
			
	}
	
	private CycleOurCitySecurityManager(){
		userTokens			= new ConcurrentHashMap<>();
		authenticationTokens= new ConcurrentHashMap<>();
		expirationDates		= new ConcurrentHashMap<>();
		try {
			SECRET =loadSecretFromFile();
		} catch (IOException e) {
			throw new SecretKeyNotFoundException();
		}
	}

	/**
	 * Returns an instance of the CycleOurCitySecurityManager singleton. 
	 * @return CycleOurCitySecurityManager instance
	 */
	public static CycleOurCitySecurityManager getManager(){ return MANAGER; }

	/**
	 * Checks if a certain user has already activated its account.
	 * 
	 * @param user The user identification, which may either be its username or email.
	 * 
	 * @return True if the user's account is active, false otherwise.
	 */
	public boolean isActiveUser(String user){
		int userID;
		try {
			userID = driver.getUserID(user);
			return !driver.isPendingActivation(userID);
		} catch (UnknownUserException e) {
			return false;
		} catch (UnableToPerformOperation e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

	/**
	 * Given a user identifier, which may be either its email or username,
	 * this method checks if the password matches the one provided upon
	 * registry.
	 * 
	 * @param user The user's email or username
	 * @param password The user's password
	 * 
	 * @return True if the password is correct, false otherwise.
	 */
	public boolean validateUser(String user, String password){
		try {
			return driver.isValidPassword(user, password);
		} catch (InvalidIdentifierException | UnableToPerformOperation e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
	
	/**
	 * Issues a new JWT token for the specified user.
	 * @param user The user that will be the subject to the token
	 * 
	 * @return A JTW token.
	 */
	public String issueToken(String user){ 

		int tries = 0;
		Date expiration;
		String jwt, jti;
		
		//Step 1 - Generate random JTI
		// According to the RFC there should not be repeated JTIs
		do{
			jti = generateSecureJTI();
			
			if(tries >= TOKEN_MAX_TRIES){
				cleanExpiredTokens();
				tries = 0;
			}else
				tries++;
			
		}while(authenticationTokens.containsKey(jti));

		
		//Step 2 - Generate the token
		expiration = new Date(System.currentTimeMillis()+TOKEN_TTL);
		
		jwt = createJWT(jti, user, TOKEN_ISSUER, expiration);
		authenticationTokens.put(jti, jwt);
		expirationDates.put(jti, expiration);
		userTokens.put(user, jti);

		return jwt;
	}

	/**
	 * Checks if the provided token is a valid one.
	 * <br>
	 * A token is considered to be valid if its claims are asserted,
	 * and if it is still in the list of tokens stored in memory.
	 * 
	 * @param token The jwt token
	 * 
	 * @return True if the token is valid, false otherwise.
	 * @throws Exception If the token is invalid or expired
	 */
	public boolean validateToken(String token) throws Exception{
		
		Date expiration;
		String jti, subject, issuer;
		
		//Step 1 - Verify the signature and extract the body
		Claims claims = Jwts.parser()         
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
				.parseClaimsJws(token).getBody();
		
		jti = claims.getId();
		subject = claims.getSubject();
		issuer	= claims.getIssuer();
		expiration = claims.getExpiration();

		//Step 2 - Validate the token
		// a) The token is registered in memory
		// b) The jti matches the one in memory
		// c) The subject matches
		return authenticationTokens.containsKey(jti)
				&& authenticationTokens.get(jti).equals(token)
				&& userTokens.get(subject).equals(jti);
	}
	
	/**
	 * Invalidates a token by removing it from the list
	 * of tokens stored in memory.
	 * 
	 * @param user The token's owner/subject
	 */
	public void invalidateUserToken(String user){
		String jti = userTokens.get(user);
		userTokens.remove(user);
		expirationDates.remove(jti);
		authenticationTokens.remove(jti);
	}

	/**
	 * Validates the token and returns the subject's identity.
	 * 
	 * @param token The JWT token
	 * 
	 * @return The token's subject
	 * @throws Exception If the token is invalid or expired.
	 */
	public String validateAndExtractSubject(String token) throws Exception{
		Claims claims = Jwts.parser()         
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
				.parseClaimsJws(token).getBody();

		return claims.getSubject();
	}
	
	private Date validateAndExtractDate(String token) throws Exception{
		Claims claims = Jwts.parser()         
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
				.parseClaimsJws(token).getBody();

		return claims.getExpiration();
	}
	
	private String validateAndExtractJTI(String token) throws Exception{
		Claims claims = Jwts.parser()         
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
				.parseClaimsJws(token).getBody();

		return claims.getId();
	}
	
	private String createJWT(String id, String issuer, String subject, Date expiration){
		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		Date now = new Date(System.currentTimeMillis());
		
		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id)
				.setIssuedAt(now)
				.setSubject(subject)
				.setIssuer(issuer)
				.setExpiration(expiration)
				.signWith(signatureAlgorithm, signingKey);

		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	private String generateSecureJTI(){
		byte[] jti = new byte[64];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(jti);
		return new String(jti);
	}

	private void cleanExpiredTokens(){
		
		Date now = new Date(System.currentTimeMillis());
		
		for(String id : authenticationTokens.keySet()){
			try {
				if(validateAndExtractDate(authenticationTokens.get(id)).after(now))
					authenticationTokens.remove(id);
			} catch (Exception e) {
			}
		}
	}
	
	private class ClearnerTask extends TimerTask{

		@Override
		public void run() {
			
			Date now = new Date(System.currentTimeMillis());
			
			for(String jti : expirationDates.keySet()){
				if(now.after(expirationDates.get(jti))){
					expirationDates.remove(jti);
					authenticationTokens.remove(jti);
				}
			}
			
			for(String user : userTokens.keySet()){
				if(!authenticationTokens.containsKey(userTokens.get(user)))
					userTokens.remove(user);
			}
		}
	}
}
