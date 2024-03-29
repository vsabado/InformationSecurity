import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class AES {
    private static String encrypt(String string) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            //Created the secret key k within this call (aesEncryptionKey)
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("aesEncryptionKey".getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("encryptionIntVec".getBytes(StandardCharsets.UTF_8)));
            return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String decrypt(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("aesEncryptionKey".getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("encryptionIntVec".getBytes(StandardCharsets.UTF_8)));
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void encryptCmd(String m) {
        System.out.println("Message to encrypt: " + m);
        String encryptedString = encrypt(m);
        System.out.println("Encryption complete: " + encryptedString);
        System.out.println("Saved to ctext.txt");

        PrintStream ps = null;
        try {
            ps = new PrintStream("outputAES/ctext.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println(encryptedString);
    }

    private static void decryptCmd() {

        try {
            File fileDir = new File("outputAES/ctext.txt");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                System.out.println("Messsage decrypted: " + decrypt(str));
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Running AES");
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
            switch (c) {
                case 'E':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        String confirm = encrypt(m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
                    break;
                case 'D':
                    start = System.nanoTime();
                    String messageDec = encrypt(m);
                    for (int i = 0; i < 100; i++) {
                        String result = decrypt(messageDec);
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
