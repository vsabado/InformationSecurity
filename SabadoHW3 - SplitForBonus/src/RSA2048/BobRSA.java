package RSA2048;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;

public class BobRSA {
    private static PrivateKey restorePrivate() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/RSA2048/outputRSA/privkey.key"));
        PKCS8EncodedKeySpec k = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(k);
    }

    private static byte[] decrypt(byte[] encrypted) throws Exception {
        PrivateKey privKey = restorePrivate();
        Cipher cip = Cipher.getInstance("RSA");
        cip.init(Cipher.DECRYPT_MODE, privKey);
        return cip.doFinal(encrypted);
    }

    private static void decryptCmd(boolean notRuntime) throws Exception {
        PrivateKey privKey = restorePrivate();
        String notRun;
        if (notRuntime) {
            System.out.println("Decrypting message");
            System.out.println("Message decrypted: " + new String(decrypt(Files.readAllBytes(new File("src/RSA2048/outputRSA/ctext.txt").toPath()))));
        }else
            notRun = new String(decrypt(Files.readAllBytes(new File("src/RSA2048/outputRSA/ctext.txt").toPath())));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running RSA2048");

        //User interactions
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to encrypt or decrypt? d to decrypt, q to quit,D for decrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'd' && c != 'q' && c != 'D') {
                System.out.println("Invalid, please input a valid command: d, q, D");
                c = reader.next(".").charAt(0);
            }
            long start = 0;
            long finish = 0;
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
