package agh.asthmasupport.global;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BCryptOperations {

    public static String generateHashedPass(String pass) {
        // hash a plaintext password using the typical log rounds (10)
        return BCrypt.withDefaults().hashToString(12, pass.toCharArray());
    }

    public static boolean isValid(String passToCheck, String hashedPass) {
        // returns true if password matches hash
        BCrypt.Result result = BCrypt.verifyer().verify(passToCheck.toCharArray(), hashedPass);
        return result.verified;
    }

}
