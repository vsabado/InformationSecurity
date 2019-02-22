import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Scanner;

public class RSA2048 {
    private static void makeStoreKeys() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator pairGen = KeyPairGenerator.getInstance("RSA");
        pairGen.initialize(2048);
        KeyPair keyPair = pairGen.genKeyPair();
        new FileOutputStream("outputRSA/AlicePrivateKey/privkey.key").write(keyPair.getPrivate().getEncoded());
        new FileOutputStream("outputRSA/pubkey.pub").write(keyPair.getPublic().getEncoded());
        System.out.println("Created and stored a public and a private key");
    }

    private static PrivateKey restorePrivate() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("outputRSA/AlicePrivateKey/privkey.key"));
        PKCS8EncodedKeySpec k = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(k);
    }

    private static PublicKey restorePublic() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get("outputRSA/pubkey.pub"));
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

    private static byte[] decrypt(byte[] encrypted) throws Exception {
        PrivateKey privKey = restorePrivate();
        Cipher cip = Cipher.getInstance("RSA");
        cip.init(Cipher.DECRYPT_MODE, privKey);
        return cip.doFinal(encrypted);
    }

    private static void encryptCmd(String m) throws Exception {
        PublicKey pubKey = restorePublic();
        System.out.println("Message to encrypt: " + m);
        byte[] p = encrypt(m);
        System.out.println("Encryption complete: " + Arrays.toString(p));
        System.out.println("Note: encryption message converted to string");
        System.out.println("Saved to ctext.txt");

        FileOutputStream fos = new FileOutputStream("outputRSA/ctext.txt"); //create an outputRSA folder!
        fos.write(p);
        fos.close();
    }

    private static void decryptCmd() throws Exception {
        PrivateKey privKey = restorePrivate();
        System.out.println("Decrypting message");
        System.out.println("Message decrypted: " + new String(decrypt(Files.readAllBytes(new File("outputRSA/ctext.txt").toPath())))); //create an outputRSA folder!
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
                        confirm = encrypt(m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float)((finish - start) / 1000000) / 100);
                    break;
                case 'D':
                    confirm = encrypt(m);
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        byte[] test = decrypt(confirm);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float)((finish - start) / 1000000) / 100);
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
