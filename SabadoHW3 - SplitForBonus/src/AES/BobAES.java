package AES;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class BobAES {
    static String getKey(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static String decrypt(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            //The key that Alice and Bob shared is "AliceBobKey11111" and is retrieved from file k.txt
            String k = getKey("src/AES/outputAES/k.txt", StandardCharsets.UTF_8);
            System.out.println("Retrieved key " + k);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("encryptionIntVec".getBytes(StandardCharsets.UTF_8)));
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void decryptCmd(boolean notRuntime) {
        try {
            File fileDir = new File("src/AES/outputAES/ctext.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), StandardCharsets.UTF_8));
            String str;
            String decryptedString;
            while ((str = in.readLine()) != null) {
                if (notRuntime)
                    System.out.println("Messsage decrypted: " + decrypt(str));
                else
                    decryptedString = decrypt(str);
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        System.out.println("Running AES");

        //User interactions
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to encrypt or decrypt? d to decrypt, q to quit, D for decrypt runtime");
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
