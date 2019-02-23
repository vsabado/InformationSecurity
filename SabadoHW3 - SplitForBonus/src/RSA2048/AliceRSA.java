package RSA2048;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Scanner;

public class AliceRSA {

    private static void makeStoreKeys() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator pairGen = KeyPairGenerator.getInstance("RSA");
        pairGen.initialize(2048);
        KeyPair keyPair = pairGen.genKeyPair();
        new FileOutputStream("src/RSA2048/outputRSA/privkey.key").write(keyPair.getPrivate().getEncoded());
        new FileOutputStream("src/RSA2048/outputRSA/pubkey.pub").write(keyPair.getPublic().getEncoded());
        System.out.println("Bob and Alice share keys (created and stored)");
    }

    private static PublicKey restorePublic() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/RSA2048/outputRSA/pubkey.pub"));
        X509EncodedKeySpec k = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(k);
    }

    private static byte[] encrypt(String m) throws Exception {
        PublicKey pubKey = restorePublic();
        Cipher cip = Cipher.getInstance("RSA");
        cip.init(Cipher.ENCRYPT_MODE, pubKey);
        return cip.doFinal(m.getBytes());
    }

    private static void encryptCmd(String m) throws Exception {
        PublicKey pubKey = restorePublic();
        System.out.println("Message to encrypt: " + m);
        byte[] p = encrypt(m);
        System.out.println("Encryption complete: " + Arrays.toString(p));
        System.out.println("Note: encryption message converted to string");
        System.out.println("Saved to ctext.txt");

        FileOutputStream fos = new FileOutputStream("src/RSA2048/outputRSA/ctext.txt"); //create an outputRSA folder!
        fos.write(p);
        fos.close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running RSA2048");
        // generate public and private keys
        makeStoreKeys();

        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();

        //User interactions
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to encrypt? e to encrypt,q to quit, E for encrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'e'&& c != 'q' && c != 'E') {
                System.out.println("Invalid, please input a valid command: e, q, E");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            byte[] confirm = new byte[0];
            switch (c) {
                case 'E':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        confirm = encrypt(m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float)((finish - start) / 1000000) / 100);
                    break;
                case 'e':
                    encryptCmd(m);
                    break;
                case 'q':
                    loop = false;
                    break;
            }
        }
    }
}
