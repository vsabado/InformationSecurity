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

public class AliceAES {

    static String getKey(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static String encrypt(String string, boolean notRuntime) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            //The key that Alice and Bob shared is "AliceBobKey11111" and is retrieved from file k.txt
            String k = getKey("src/AES/outputAES/k.txt", StandardCharsets.UTF_8);
            if (notRuntime)
                System.out.println("Retrieved key " + k);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("encryptionIntVec".getBytes(StandardCharsets.UTF_8)));
            return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void encryptCmd(String m) throws FileNotFoundException {
        System.out.println("Message to encrypt: " + m);

        //Generate the AES key and save to file to distribute
        try (PrintStream out = new PrintStream(new FileOutputStream("src/AES/outputAES/k.txt"))) {
            out.print("AliceBobKey11111");
        }

        System.out.println("Generated key: " + "AliceBobKey11111");
        String encryptedString = encrypt(m, true);
        System.out.println("Encryption complete: " + encryptedString);
        System.out.println("Saved to ctext.txt");

        PrintStream ps = null;
        try {
            ps = new PrintStream("src/AES/outputAES/ctext.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println(encryptedString);

    }


    public static void main(String[] args) throws FileNotFoundException {
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
            System.out.println("Would you like to encrypt or test runtime? e to encrypt, q to quit, E for encrypt runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'e' && c != 'q' && c != 'E') {
                System.out.println("Invalid, please input a valid command: e, q, E");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            switch (c) {
                case 'E':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        String confirm = encrypt(m, false);
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
