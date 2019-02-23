package RSASig;
import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AliceRSASig {

    private static void makeStoreKeys() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keys = KeyPairGenerator.getInstance("RSA");
        keys.initialize(2048, new SecureRandom());
        KeyPair pair = keys.generateKeyPair();
        new FileOutputStream("src/RSASig/outputRSASig/privkey.key").write(pair.getPrivate().getEncoded());
        new FileOutputStream("src/RSASig/outputRSASig/pubkey.pub").write(pair.getPublic().getEncoded());
        System.out.println("Bob and Alice share keys (created and stored)");
    }

    private static String encrypt(String message) throws Exception {
        PrivateKey privKey = restorePrivate();
        Cipher encrypt = Cipher.getInstance("RSA");
        encrypt.init(Cipher.ENCRYPT_MODE, privKey);
        byte[] text = encrypt.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(text);
    }

    private static PrivateKey restorePrivate() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/RSASig/outputRSASig/privkey.key"));
        PKCS8EncodedKeySpec k = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(k);
    }

    private static String sign(String message) throws Exception {
        PrivateKey privKey = restorePrivate();
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privKey);
        sig.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] sign = sig.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    private static void encryptCmd(String m) throws Exception {
        System.out.println("Message to encrypt: " + m);
        String p = encrypt(m);

        PrintStream ps = null;
        try {
            ps = new PrintStream("src/RSASig/outputRSASig/sigtext.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert ps != null;
        ps.println(p);
        System.out.println("Signature: " + m);
        String signed = sign(m);
        ps.print(signed);
        System.out.println("Encryption complete");
        System.out.println("Encrypted message: " + p);
        System.out.println("Signature: " + signed);
        System.out.println("Saved to sigtext.txt");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running RSASig");
        makeStoreKeys();

        //User interactions
        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();

        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to encrypt or check runtime? e to encrypt, q to quit, E for encrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'e' && c != 'q' && c != 'E') {
                System.out.println("Invalid, please input a valid command: e, q, E");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            String confirm = "";
            switch (c) {
                case 'E':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        confirm = encrypt(m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
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
