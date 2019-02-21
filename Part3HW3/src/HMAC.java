import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {

    public static SecretKey genKey(String algorithm) throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(algorithm).generateKey();
    }

    public static void storeKey(SecretKey key) throws IOException {
        new FileOutputStream("outputHMACKey/key").write(key.getEncoded());
    }

    public static SecretKey restoreKey() throws IOException {
        return new SecretKeySpec(Files.readAllBytes(Paths.get("outputHMACKey/key")), "HMACSHA256");
    }


    public static byte[] generateMessage(SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HMACSHA256");
        mac.init(key);
        byte[] macb = new byte[0];
        try (FileInputStream in = new FileInputStream("outputHMACKey/m")) {
            macb = processFile(mac, in);
//            System.out.println("test " + ": " + Arrays.toString(macb));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macb;
    }

    static private final byte[] processFile(Mac mac, InputStream in)
            throws java.io.IOException {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            mac.update(ibuf, 0, len);
        }
        return mac.doFinal();
    }

    public static void createHmacKeyMessage(boolean notRuntime) throws NoSuchAlgorithmException, IOException, InvalidKeyException {

        String m = "";
        if (notRuntime) {
            System.out.println("What's your message?");
            Scanner scan = new Scanner(System.in);
            m = scan.nextLine();
        } else
            m = "Testing the runtime";

        PrintStream ps = null;
        try {
            ps = new PrintStream("outputHMACKey/m");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println(m);
        if (notRuntime)
            System.out.println("Message file created!");

        //Make and store the key
        SecretKey key = genKey("HMACSHA256");
        storeKey(key);
        if (notRuntime)
            System.out.println("Key created and stored!");

        //Make the hmac
        byte[] hmac = generateMessage(key);
        FileOutputStream fos = new FileOutputStream("outputHMACKey/HMAC");
        fos.write(hmac);
        fos.close();
        if (notRuntime)
            System.out.println("HMAC file created!");
    }

    public static void verifyHMAC(boolean notRuntime) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        //Restore the original key
        SecretKey restoredKey = restoreKey();

        //Restore the original HMAC
        byte[] message = Files.readAllBytes(new File("outputHMACKey/HMAC").toPath());
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
            System.out.println("Would you like to create HMAC or verify? c to create HMAC-key-message, v to verify HMAC, C to check runtime, V to check rutnime");
            char c = reader.next(".").charAt(0);
            while (c != 'c' && c != 'C' && c != 'v' && c != 'V' && c != 'q') {
                System.out.println("Invalid, please input a valid command: c, C, v, V, q");
                c = reader.next(".").charAt(0);
            }

            long start = 0;
            long finish = 0;
            switch (c) {
                case 'C':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        createHmacKeyMessage(false);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 HMAC-Key-Message creations in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float)((finish - start) / 1000000) / 100);
                    break;
                case 'V':
                    start = System.nanoTime();
                    for (int i = 0; i < 100; i++) {
                        verifyHMAC(false);
                    }
                    finish = System.nanoTime();
                    System.out.println("100 verifications in ms: " + ((finish - start) / 1000000));
                    System.out.println("Average in ms: " + (float)((finish - start) / 1000000) / 100);
                    break;
                case 'c':
                    createHmacKeyMessage(true);
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