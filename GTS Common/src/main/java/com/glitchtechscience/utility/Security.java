package com.glitchtechscience.utility;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security {

	private final static String HEX = "0123456789ABCDEF";

	private static String encrypt( String seed, String cleartext ) throws Exception {

		byte[] rawKey = getRawKey( seed.getBytes() );
		byte[] result = encrypt( rawKey, cleartext.getBytes() );

		return toHex( result );
	}

	private static byte[] encrypt( byte[] raw, byte[] clear ) throws Exception {

		SecretKeySpec key = new SecretKeySpec( raw, "AES" );
		Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
		cipher.init( Cipher.ENCRYPT_MODE, key );

		return cipher.doFinal( clear );
	}

	private static String decrypt( String seed, String encrypted ) throws Exception {

		byte[] rawKey = getRawKey( seed.getBytes() );
		byte[] enc = toByte( encrypted );
		byte[] result = decrypt( rawKey, enc );

		return new String( result );
	}

	private static byte[] decrypt( byte[] raw, byte[] encrypted ) throws Exception {

		SecretKeySpec skeySpec = new SecretKeySpec( raw, "AES" );
		Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
		cipher.init( Cipher.DECRYPT_MODE, skeySpec );

		return cipher.doFinal( encrypted );
	}

	private static byte[] getRawKey( byte[] seed ) throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance( "AES" );
		SecureRandom sr = SecureRandom.getInstance( "SHA1PRNG" );
		sr.setSeed( seed );
		keyGenerator.init( 128, sr ); // 192 and 256 bits may not be available

		SecretKey secretKey = keyGenerator.generateKey();

		return secretKey.getEncoded();
	}

	private static byte[] toByte( String hexString ) {

		int len = hexString.length() / 2;
		byte[] result = new byte[len];

		for( int i = 0; i < len; i++ ) {

			result[i] = Integer.valueOf( hexString.substring( 2 * i, 2 * i + 2 ), 16 )
					.byteValue();
		}

		return result;
	}

	private static String toHex( byte[] buf ) {

		if( buf == null ) {
			return "";
		}

		StringBuffer result = new StringBuffer( 2 * buf.length );

		for( byte aBuf : buf ) {

			appendHex( result, aBuf );
		}

		return result.toString();
	}

	private static void appendHex( StringBuffer sb, byte b ) {

		sb.append( HEX.charAt( ( b >> 4 ) & 0x0f ) )
				.append( HEX.charAt( b & 0x0f ) );
	}
}
