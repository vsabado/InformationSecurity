import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.Scanner;

public class RSA2048 {

    public static KeyPair makeKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator pairGen = KeyPairGenerator.getInstance("RSA");
        pairGen.initialize(keySize);
        return pairGen.genKeyPair();
    }

    public static byte[] encrypt(PublicKey pubKey, String m) throws Exception {
        Cipher cip = Cipher.getInstance("RSA");
        cip.init(Cipher.ENCRYPT_MODE, pubKey);
        return cip.doFinal(m.getBytes());
    }

    public static byte[] decrypt(PrivateKey privKey, byte[] encrypted) throws Exception {
        Cipher cip = Cipher.getInstance("RSA");
        cip.init(Cipher.DECRYPT_MODE, privKey);
        return cip.doFinal(encrypted);
    }

    public static void encryptCmd(PublicKey pubKey, String m) throws Exception {
        System.out.println("Message to encrypt: " + m);
        byte[] p = encrypt(pubKey, m);
        System.out.println("Encryption complete: " + p);
        System.out.println("Saved to ctext.txt");


        FileOutputStream fos = new FileOutputStream("outputRSA/ctext.txt"); //create an outputRSA folder!
        fos.write(p);
        fos.close();
    }

    private static void decryptCmd(PrivateKey privKey) throws Exception {
        byte[] m = Files.readAllBytes(new File("outputRSA/ctext.txt").toPath());//create an outputRSA folder!
        byte[] verified = decrypt(privKey, m);
        System.out.println("Decrypting message");
        System.out.println("Message decrypted: " + new String(verified));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running RSA2048");
        // generate public and private keys
        KeyPair keyPair = makeKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privKey = keyPair.getPrivate();

        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();

        //User interactions
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
            byte[] confirm = new byte[0];
            switch (c) {
                case 'E':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        confirm = encrypt(pubKey, m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000) / 100);
                    break;
                case 'D':
                    confirm = encrypt(pubKey, m);
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        byte[] test = decrypt(privKey, confirm);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000) / 100);
                    break;
                case 'e':
                    encryptCmd(pubKey, m);
                    break;
                case 'd':
                    decryptCmd(privKey);
                    break;
                case 'q':
                    loop = false;
                    break;
            }

        }


    }


}
