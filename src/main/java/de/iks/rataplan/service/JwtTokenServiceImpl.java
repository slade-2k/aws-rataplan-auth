package de.iks.rataplan.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.InvalidTokenException;

import java.io.Serializable;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Transactional
public class JwtTokenServiceImpl implements JwtTokenService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7186407209737805247L;
	
	@Value("${jwt.secret}")
	private static String SECRET;

	static final Integer EXPIRATION = 600;

	static final String CLAIM_KEY_USERNAME = "username";
	static final String CLAIM_KEY_MAIL = "mail";
	static final String CLAIM_KEY_AUDIENCE = "audience";
	static final String CLAIM_KEY_CREATED = "created";

	@Override
	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, user.getUsername());
		claims.put(CLAIM_KEY_MAIL, user.getMail());
		claims.put(CLAIM_KEY_CREATED, new Date());
		return generateToken(claims);
	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return !isTokenExpired(claims.getExpiration());
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
	}

	@Override
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);

			if (isTokenExpired(claims.getExpiration())) {
				throw new InvalidTokenException("Invalid JWT");
			}

			username = (String) claims.get(CLAIM_KEY_USERNAME);
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
		return username;
	}

	private boolean isTokenExpired(Date expiration) {
		return expiration.before(new Date());
	}

	private String generateToken(Map<String, Object> claims) {
		return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + EXPIRATION * 1000);
	}

	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid JWT");
		}
		return claims;
	}

}
