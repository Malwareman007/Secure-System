import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;

public class FileEncryption {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine();

        System.out.print("Encrypt (E) or Decrypt (D) the file? ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("E")) {
            encryptFile(filename);
        } else if (choice.equalsIgnoreCase("D")) {
            decryptFile(filename);
        } else {
            System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    public static void encryptFile(String filename) {
        try {
            // Generate random key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Save public key to file
            Files.write(Paths.get("public_key.txt"), keyPair.getPublic().getEncoded(), StandardOpenOption.CREATE);

            // Read file contents
            byte[] fileContents = Files.readAllBytes(Paths.get(filename));

            // Encrypt file contents with public key
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptedData = cipher.doFinal(fileContents);

            // Compress encrypted data
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(encryptedData);
            byte[] compressedData = new byte[encryptedData.length];
            int compressedDataLength = deflater.deflate(compressedData);
            deflater.end();

            byte[] compressedDataTrimmed = new byte[compressedDataLength];
            System.arraycopy(compressedData, 0, compressedDataTrimmed, 0, compressedDataLength);

            // Generate authentication hash of compressed data
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(compressedDataTrimmed);

            // Write compressed data and digest to file
            Files.write(Paths.get(filename + ".enc"), compressedDataTrimmed, StandardOpenOption.CREATE);
            Files.write(Paths.get(filename + ".enc"), hash, StandardOpenOption.APPEND);

            System.out.println("Encryption successful!");
            System.out.println("Hash: " + bytesToHex(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(String filename) {
        try {
            // Read private key from file
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get("private_key.txt"));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            // Read encrypted file contents
            byte[] encryptedData = Files.readAllBytes(Paths.get(filename));

            // Separate compressed data and digest
            byte[] compressedData = new byte[encryptedData.length - 32];
            byte[] digest = new byte[32];
            System.arraycopy(encryptedData, 0, compressedData, 0, compressedData.length);
            System.arraycopy(encryptedData, compressedData.length, digest, 0, digest.length);

            // Verify digest
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] calculatedDigest = messageDigest.digest(compressedData);
            if (!MessageDigest.isEqual(calculatedDigest, digest)) {
                System.out.println("Authentication failed!");
                return;
            }

            // Decompress encrypted data
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);
            byte[] decompressedData = new byte[encryptedData.length];
            int decompressedDataLength = inflater.inflate(decompressedData);
            inflater.end();

            byte[] decompressedDataTrimmed = new byte[decompressedDataLength];
            System.arraycopy(decompressedData, 0, decompressedDataTrimmed, 0, decompressedDataLength);

            // Decrypt encrypted data with private key
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(decompressedDataTrimmed);

            // Write decrypted file contents to file
            Files.write(Paths.get(filename + ".dec"), decryptedData, StandardOpenOption.CREATE);

            System.out.println("Decryption successful!");
            System.out.println("Hash: " + bytesToHex(digest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
