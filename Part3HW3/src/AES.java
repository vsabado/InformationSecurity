import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class AES {

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    public static String encrypt(String string) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedMessage) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void encryptCmd() {
        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();
        System.out.println("Message to encrypt: " + m);
        String encryptedString = encrypt(m);
        System.out.println("Encryption complete");

        PrintStream ps = null;
        try {
            ps = new PrintStream("output/ctext.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println(encryptedString);
    }

    private static void decryptCmd() {

        try {
            File fileDir = new File("output/ctext.txt");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                System.out.println("Messsage decrypted: " + decrypt(str));
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("What's your message?");
        Scanner scan = new Scanner(System.in);
        String m = scan.nextLine();
        System.out.println("Message to encrypt: " + m);
        boolean loop = true;

        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Would you like to encrypt or decrypt? e to encrypt, d to decrypt, q to quit");
            char c = reader.next(".").charAt(0);
            while (c != 'e' && c != 'd' && c != 'q') {
                System.out.println("Invalid, please input a valid command: e, d, q");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            switch (c) {
                case 'e':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        String confirm = encrypt(m);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 encryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000)/100);
                    break;
                case 'd':
                    start = System.nanoTime();
                    String messageDec = encrypt(m);
                    for (int i = 0; i < 100; i++) {
                        String result = decrypt(messageDec);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 decryptions in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + ((finish - start) / 1000000)/100);
                    break;
                case 'q':
                    loop = false;
                    break;
            }

        }
    }
}
