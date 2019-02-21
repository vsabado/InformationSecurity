import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import javax.crypto.Cipher;

public class RSASig {
    public static void makeStoreKeys() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keys = KeyPairGenerator.getInstance("RSA");
        keys.initialize(2048, new SecureRandom());
        KeyPair pair = keys.generateKeyPair();
        new FileOutputStream("outputRSASig/privkey.key").write(pair.getPrivate().getEncoded());
        new FileOutputStream("outputRSASig/pubkey.pub").write(pair.getPublic().getEncoded());
        System.out.println("Created and stored a public and a private key");
    }

    public static PrivateKey restorePrivate() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("outputRSASig/privkey.key"));
        PKCS8EncodedKeySpec k = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(k);
    }

    public static PublicKey restorePublic() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("outputRSASig/pubkey.pub"));
        X509EncodedKeySpec k = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(k);
    }


    public static String encrypt(String message) throws Exception {
        PublicKey pubKey = restorePublic();
        Cipher encrypt = Cipher.getInstance("RSA");
        encrypt.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] text = encrypt.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(text);
    }

    public static String decrypt(String message) throws Exception {
        PrivateKey privKey = restorePrivate();
        byte[] text = Base64.getDecoder().decode(message);
        Cipher decrypt = Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        return new String(decrypt.doFinal(text), StandardCharsets.UTF_8);
    }

    public static String sign(String message) throws Exception {
        PrivateKey privKey = restorePrivate();
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privKey);
        sig.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] sign = sig.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    public static boolean verify(String message, String sig) throws Exception {
        PublicKey pubKey = restorePublic();
        Signature pubsig = Signature.getInstance("SHA256withRSA");
        pubsig.initVerify(pubKey);
        pubsig.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signiture = Base64.getDecoder().decode(sig);
        return pubsig.verify(signiture);
    }

    public static void encryptCmd(String m) throws Exception {
        PublicKey pubKey = restorePublic();
        PrivateKey privKey = restorePrivate();
        System.out.println("Message to encrypt: " + m);
        String p = encrypt(m);

        PrintStream ps = null;
        try {
            ps = new PrintStream("outputRSASig/sigtext.txt");
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

    private static void decryptCmd() throws Exception {
        System.out.println("Decrypting from sigtext.txt");
        String message = Files.readAllLines(Paths.get("outputRSASig/sigtext.txt")).get(0);
        String signature = Files.readAllLines(Paths.get("outputRSASig/sigtext.txt")).get(1);
        String decryptedMessage = decrypt(message);
        System.out.println("Decrypted message is: " + decryptedMessage);
        System.out.println("Signiture verified? : " + verify(decryptedMessage, signature));

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
            System.out.println("Would you like to encrypt or decrypt? e to encrypt, d to decrypt, q to quit, E for encrypt runtime, D for decrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'e' && c != 'd' && c != 'q' && c != 'E' && c != 'D') {
                System.out.println("Invalid, please input a valid command: e, d, q, E, D");
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
                case 'D':
                    confirm = encrypt(m);
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        String test = decrypt(confirm);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
                    break;
                case 'e':
                    encryptCmd(m);
                    break;
                case 'd':
                    decryptCmd();
                    break;
                case 'q':
                    loop = false;
                    break;
            }

        }
    }
}
