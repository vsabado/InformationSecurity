import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;
import javax.crypto.Cipher;

public class RSASig {
    public static String encrypt(String message, PublicKey pubKey) throws Exception {
        Cipher encrypt = Cipher.getInstance("RSA");
        encrypt.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] text = encrypt.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(text);
    }

    public static String decrypt(String message, PrivateKey privKey) throws Exception {
        byte[] text = Base64.getDecoder().decode(message);
        Cipher decrypt = Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        return new String(decrypt.doFinal(text), "UTF-8");
    }

    public static String sign(String message, PrivateKey privKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privKey);
        sig.update(message.getBytes("UTF-8"));
        byte[] sign = sig.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    public static boolean verify(String message, String sig, PublicKey pubKey) throws Exception {
        Signature pubsig = Signature.getInstance("SHA256withRSA");
        pubsig.initVerify(pubKey);
        pubsig.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signiture = Base64.getDecoder().decode(sig);
        return pubsig.verify(signiture);
    }

    public static void encryptCmd(PublicKey pubKey, String m, PrivateKey privKey) throws Exception {
        System.out.println("Message to encrypt: " + m);
        String p = encrypt(m, pubKey);

        PrintStream ps = null;
        try {
            ps = new PrintStream("outputRSASig/ctext.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert ps != null;
        ps.println(p);
        System.out.println("Signature: " + m);
        String signed = sign(m, privKey);
        ps.print(signed);
        System.out.println("Encryption complete");
    }

    private static void decryptCmd(PrivateKey privKey, PublicKey pubKey) throws Exception {
        String message = Files.readAllLines(Paths.get("outputRSASig/ctext.txt")).get(0);
        String signature =  Files.readAllLines(Paths.get("outputRSASig/ctext.txt")).get(1);
        String decryptedMessage = decrypt(message, privKey);
        System.out.println("Decrypted message is: " + decryptedMessage);
        System.out.println("Signiture verified? : " + verify(decryptedMessage, signature, pubKey));

    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keys = KeyPairGenerator.getInstance("RSA");
        keys.initialize(2048, new SecureRandom());
        KeyPair pair = keys.generateKeyPair();
        PublicKey pubKey = pair.getPublic();
        PrivateKey privKey = pair.getPrivate();

        //User interactions
        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();

        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
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
                        confirm = encrypt(m, pubKey);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000) / 100);
                    break;
                case 'D':
                    confirm = encrypt(m, pubKey);
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        String test = decrypt(confirm, privKey);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000) / 100);
                    break;
                case 'e':
                    encryptCmd(pubKey, m, privKey);
                    break;
                case 'd':
                    decryptCmd(privKey, pubKey);
                    break;
                case 'q':
                    loop = false;
                    break;
            }

        }
    }
}
