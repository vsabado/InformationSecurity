package HMAC;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class AliceHMAC {
    private static SecretKey genKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("HMACSHA256").generateKey();
    }

    private static void storeKey(SecretKey key) throws IOException {
        new FileOutputStream("src/HMAC/outputHMACKey/key").write(key.getEncoded());
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
            ps = new PrintStream("src/HMAC/outputHMACKey/m");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println(m);
        if (notRuntime)
            System.out.println("Message file created!");

        //Make and store the key
        SecretKey key = genKey();
        storeKey(key);
        if (notRuntime)
            System.out.println("Key created and stored!");

        //Make the hmac
        byte[] hmac = generateMessage(key);
        FileOutputStream fos = new FileOutputStream("src/HMAC/outputHMACKey/HMAC");
        fos.write(hmac);
        fos.close();
        if (notRuntime)
            System.out.println("HMAC file created!");
    }


    public static void main(String[] args) throws Exception {
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Would you like to create HMAC or check runtime? c to create HMAC-key-message, C to check runtime");
            char c = reader.next(".").charAt(0);
            while (c != 'c' && c != 'C' && c != 'q') {
                System.out.println("Invalid, please input a valid command: c, C, q");
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
                    System.out.println("Average in ms: " + (float) ((finish - start) / 1000000) / 100);
                    break;
                case 'c':
                    createHmacKeyMessage(true);
                    break;
                case 'q':
                    loop = false;
                    break;
            }
        }
    }

}
