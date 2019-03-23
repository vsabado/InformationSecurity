import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class PasswordGenerator {

    private static String getSha256Pass(String password, byte[] randSalt) {
        String resultingPass = null;
        try {
            MessageDigest digestedMessage = MessageDigest.getInstance("SHA-256");
            digestedMessage.update(randSalt);
            byte[] bytes = digestedMessage.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            resultingPass = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resultingPass;
    }

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        byte[] makeRandSalt = new byte[4];
        rand.nextBytes(makeRandSalt);
        return makeRandSalt;
    }

    private static void saveIntoText(String username, String password) throws NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        String securePassword = getSha256Pass(password, salt);
        PrintStream ps = null;
        try {
            ps = new PrintStream("pwd.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.println("[" + username + "," + salt + "," + securePassword +"]");
        System.out.println("Password generated and stored into pwd.txt");
        System.out.println("[" + username + "," + salt + "," + securePassword +"]");
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Running password generator");
        System.out.println("What's your username?");
        Scanner scan = new Scanner(System.in);
        String username = scan.nextLine();
        System.out.println("What is your desire password?");
        String password = scan.nextLine();

        //User interactions
        boolean loop = true;
        while (loop) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("==========================================================================================================================");
            System.out.println("Press r to change username, t to change password, y to create .txt file, or q to quit");
            char c = reader.next(".").charAt(0);
            while (c != 'r' && c != 'q' && c != 't' && c != 'y') {
                System.out.println("Invalid, please input a valid command: r, t, y, q");
                c = reader.next(".").charAt(0);
            }

            switch (c) {
                case 'r':
                    System.out.println("What's your username?");
                    username = scan.nextLine();
                    break;
                case 't':
                    System.out.println("What is your desired password?");
                    password = scan.nextLine();
                    break;
                case 'y':
                    saveIntoText(username, password);
                    break;
                case 'q':
                    loop = false;
                    break;
            }
        }
    }
}