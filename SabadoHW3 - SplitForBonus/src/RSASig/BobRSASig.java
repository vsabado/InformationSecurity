package RSASig;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class BobRSASig {
    private static PublicKey restorePublic() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/RSASig/outputRSASig/pubkey.pub"));
        X509EncodedKeySpec k = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(k);
    }

    private static String decrypt(String message) throws Exception {
        PublicKey pubKey = restorePublic();
        byte[] text = Base64.getDecoder().decode(message);
        Cipher decrypt = Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, pubKey);
        return new String(decrypt.doFinal(text), StandardCharsets.UTF_8);
    }

    private static boolean verify(String message, String sig) throws Exception {
        PublicKey pubKey = restorePublic();
        Signature pubsig = Signature.getInstance("SHA256withRSA");
        pubsig.initVerify(pubKey);
        pubsig.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signiture = Base64.getDecoder().decode(sig);
        return pubsig.verify(signiture);
    }

    private static void decryptCmd(boolean notRuntime) throws Exception {
        if (notRuntime)
            System.out.println("Decrypting from sigtext.txt");
        String message = Files.readAllLines(Paths.get("src/RSASig/outputRSASig/sigtext.txt")).get(0);
        String signature = Files.readAllLines(Paths.get("src/RSASig/outputRSASig/sigtext.txt")).get(1);
        String decryptedMessage = decrypt(message);
        boolean runTimeVerification;
        if (notRuntime) {
            System.out.println("Decrypted message is: " + decryptedMessage);
            System.out.println("Signiture verified? : " + verify(decryptedMessage, signature));
        }else
            runTimeVerification = verify(decryptedMessage, signature);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running RSASig");

        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to decrypt or check runtime? d to decrypt, q to quit, D for decrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'd' && c != 'q' && c != 'D') {
                System.out.println("Invalid, please input a valid command: d, q, D");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            String confirm = "";
            switch (c) {
                case 'D':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        decryptCmd(false);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
                    break;
                case 'd':
                    decryptCmd(true);
                    break;
                case 'q':
                    loop = false;
                    break;
            }

        }
    }


}
