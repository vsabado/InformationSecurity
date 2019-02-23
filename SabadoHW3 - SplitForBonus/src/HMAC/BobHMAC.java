package HMAC;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class BobHMAC {
    private static SecretKey restoreKey() throws IOException {
        return new SecretKeySpec(Files.readAllBytes(Paths.get("src/HMAC/outputHMACKey/key")), "HMACSHA256");
    }

    private static byte[] generateMessage(SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac m = Mac.getInstance("HMACSHA256");
        m.init(key);
        byte[] mb = new byte[0];
        try (FileInputStream in = new FileInputStream("src/HMAC/outputHMACKey/m")) {
            mb = process(m, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mb;
    }

    static private byte[] process(Mac m, InputStream in) throws java.io.IOException {
        byte[] i = new byte[1024];
        int length;
        while ((length = in.read(i)) != -1) {
            m.update(i, 0, length);
        }
        return m.doFinal();
    }

    private static void verifyHMAC(boolean notRuntime) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        //Restore the original key
        SecretKey restoredKey = restoreKey();

        //Restore the original HMAC
        byte[] message = Files.readAllBytes(new File("src/HMAC/outputHMACKey/HMAC").toPath());
        if (notRuntime) {
            System.out.println("HMAC read from file: ");
            System.out.println(Arrays.toString(message));
        }
        //Make a new HMAC with restored key
        generateMessage(restoredKey);
        if (notRuntime) {
            System.out.println("New HMAC created with restored key: ");
            System.out.println(Arrays.toString(generateMessage(restoredKey)));
        }

        if (notRuntime) {
            if (Arrays.toString(message).equals(Arrays.toString(generateMessage(restoredKey)))) {
                System.out.println("Verification successful!");
            } else
                System.out.println("Verification failed!");
        }
    }

    public static void main(String[] args) throws Exception {
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to verify HMAC or check runtime? v to verify HMAC, V to check rutnime, q to quit");
            char c = reader.next(".").charAt(0);
            while (c != 'v' && c != 'V' && c != 'q') {
                System.out.println("Invalid, please input a valid command: v, V, q");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            switch (c) {
                case 'V':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        verifyHMAC(false);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 verifications in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
                    break;
                case 'v':
                    verifyHMAC(true);
                    break;
                case 'q':
                    loop = false;
                    break;
            }
        }
    }

}
