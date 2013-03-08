package common;

import edu.vt.middleware.password.*;
import org.bouncycastle.crypto.generators.SCrypt;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 5:39 PM
 */
public class SCUtils {

    /**
     * commented suites are recommended but not supported by Bouncy Castle
     */
    private final static String[] cipherSuites = {
            "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
            //"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256",
            //"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
            //"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
            "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",
            //"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",
            //"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            //"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
            //"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"
    };

    public final static String[] getCipherSuites() {
        String[] copyofCipherSuite = cipherSuites;
        return copyofCipherSuite;
    }


    public static byte[] genSalt() {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[64]; // 512 bits
        random.nextBytes(salt);

        return salt;
    }

    /**
     * @param password
     * @param salt
     * @return
     */
    public static byte[] hash(byte[] password, byte[] salt) {

        int N = 2 ^ 14; // iteration times
        int r = 8; // Memory cost parameter
        int p = 1; // Parallelization parameter
        int dkLen = 64; // Intended length of the derived key

        return SCrypt.generate(password, salt, N, r, p, dkLen);
    }

    public static boolean usernameChecker(String username) {

        LengthRule lengthRule = new LengthRule(3, 16);
        WhitespaceRule whitespaceRule = new WhitespaceRule();

        List<Rule> ruleList = new ArrayList<Rule>();
        ruleList.add(lengthRule);
        ruleList.add(whitespaceRule);

        PasswordValidator validator = new PasswordValidator(ruleList);
        PasswordData passwordData = new PasswordData(new Password(username));

        RuleResult result = validator.validate(passwordData);
        return result.isValid();
    }

    public static boolean passwordStrengthChecker(char[] passoword) {

        // password must be between 8 and 16 chars long
        LengthRule lengthRule = new LengthRule(8, 16);

        // don't allow whitespace
        WhitespaceRule whitespaceRule = new WhitespaceRule();

        // control allowed characters
        CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
        // require at least 1 digit in passwords
        charRule.getRules().add(new DigitCharacterRule(1));
        // require at least 1 non-alphanumeric char
        charRule.getRules().add(new NonAlphanumericCharacterRule(1));
        // require at least 1 upper case char
        charRule.getRules().add(new UppercaseCharacterRule(1));
        // require at least 1 lower case char
        charRule.getRules().add(new LowercaseCharacterRule(1));
        // require at least 3 of the previous rules be met
        charRule.setNumberOfCharacteristics(3);

        // don't allow alphabetical sequences
        AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule();

        // don't allow numerical sequences of length 3
        NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3, true);

        // don't allow qwerty sequences
        QwertySequenceRule qwertySeqRule = new QwertySequenceRule();

        // don't allow 4 repeat characters
        RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);

        // group all rules together in a List
        List<Rule> ruleList = new ArrayList<Rule>();
        ruleList.add(lengthRule);
        //ruleList.add(whitespaceRule);
        //ruleList.add(charRule);
        //ruleList.add(alphaSeqRule);
        //ruleList.add(numSeqRule);
        //ruleList.add(qwertySeqRule);
        //ruleList.add(repeatRule);

        PasswordValidator validator = new PasswordValidator(ruleList);
        PasswordData passwordData = new PasswordData(new Password(String.valueOf(passoword)));

        RuleResult result = validator.validate(passwordData);

        return result.isValid();
    }
}
