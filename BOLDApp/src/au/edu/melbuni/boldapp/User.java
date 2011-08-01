package au.edu.melbuni.boldapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * A User has
 * - name
 * - identifier (for file directories etc.)
 * 
 * - a number of files attached to it
 * 
 */
public class User {
	
	String name;
	String identifier;

	public User(String name) {
		this.name = name;
		this.identifier = generateHex(name);
	}
	
	protected String generateHex(String name) {
		try {
			// Create MD5 Hash.
			//
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(name.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String.
			//
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}
