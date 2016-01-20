package org.cycleourcity.server.middleware;

import java.security.Key;
import java.security.SecureRandom;
import java.sql.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.exceptions.InvalidIdentifierException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class CycleOurCitySecurityManager {

	//Logging
	private static Logger LOG = LoggerFactory.getLogger(CycleOurCitySecurityManager.class);

	//Driver
	private AccountManagementDriver driver = AccountManagementDriverImpl.getManager();

	//Singleton
	private static CycleOurCitySecurityManager MANAGER = new CycleOurCitySecurityManager();

	private HashMap<String, String> authenticationTokens;
	

	private CycleOurCitySecurityManager(){
		authenticationTokens = new HashMap<>();
	}

	public static CycleOurCitySecurityManager getManager(){
		return MANAGER;
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

	private SecretKey secret = MacProvider.generateKey();

	private String createJWT(String id, String issuer, String subject, long expiration){
		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret.toString());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id)
				.setIssuedAt(now)
				.setSubject(subject)
				.setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);

		//if it has been specified, let's add the expiration
		if (expiration >= 0) {
			long expMillis = nowMillis + expiration;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	private String generateSecureJTI(){
		byte[] jti = new byte[64];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(jti);
		return new String(jti);
	}
	
	// We need a signing key, so we'll create one just for this example. Usually
	// the key would be read from your application configuration instead.
	public String issueToken(String user){
		
		String jwt, jti;
		
		do{
			jti = generateSecureJTI();
		}while(authenticationTokens.containsKey(jti));
		
		
		jwt = createJWT(new String(jti), user, "cycleourcity", 300000);
		
		authenticationTokens.put(new String(jti), jwt);
		
		return jwt;
	}

	public void validateToken(String token){
		Jwts.parser()
		.setSigningKey(DatatypeConverter.parseBase64Binary(secret.toString()))
		.parseClaimsJws(token);
	}

	public String extractSubject(String token){
		Claims claims = Jwts.parser()         
				.setSigningKey(DatatypeConverter.parseBase64Binary(secret.toString()))
				.parseClaimsJws(token).getBody();
		
		return claims.getSubject();
	}
}
