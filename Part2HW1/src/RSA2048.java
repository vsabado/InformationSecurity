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

    public static void encryptCmd(PublicKey pubKey) throws Exception {
        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();
        System.out.println("Message to encrypt: " + m);
        System.out.println("Encryption finished");
        byte[] p = encrypt(pubKey, m);

        FileOutputStream fos = new FileOutputStream("output/ctext.txt");
        fos.write(p);
        fos.close();
    }

    private static void decryptCmd(PrivateKey privKey) throws Exception {
        byte[] m = Files.readAllBytes(new File("output/ctext.txt").toPath());
        byte[] verified = decrypt(privKey, m);
        System.out.println("Decrypting message");
        System.out.println("Message decrypted: " + new String(verified));
    }

    public static void main(String[] args) throws Exception {
        // generate public and private keys
        KeyPair keyPair = makeKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privKey = keyPair.getPrivate();

        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Would you like to encrypt or decrypt? e to encrypt, d to decrypt, q to quit");
            char c = reader.next(".").charAt(0);
            while (c != 'e' && c != 'd' && c != 'q') {
                System.out.println("Invalid, please input a valid command: e, d, q");
                c = reader.next(".").charAt(0);
            }

            switch (c) {
                case 'e':
                    encryptCmd(pubKey);
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
