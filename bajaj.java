import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class bajaj {
    public static void main(String[] args) {
        // Step 1: Validate Command-Line Arguments
        if (args.length != 2) {
            System.err.println("Usage: java -jar app.jar <roll_number> <path_to_json_file>");
            System.exit(1);
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", ""); // Roll number in lowercase
        String filePath = args[1];

        try {
            // Step 2: Find "destination" in JSON
            String destinationValue = findDestinationValue(filePath);
            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                System.exit(2);
            }

            // Step 3: Generate Random String
            String randomString = generateRandomString(8);

            // Step 4: Compute MD5 Hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = computeMD5Hash(concatenatedString);

            // Step 5: Print Result
            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestinationValue(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        return findKeyRecursively(rootNode, "destination");
    }

    private static String findKeyRecursively(JsonNode node, String key) {
        if (node.isObject()) {
            if (node.has(key)) {
                return node.get(key).asText();
            }
            for (JsonNode child : node) {
                String result = findKeyRecursively(child, key);
                if (result != null) return result;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                String result = findKeyRecursively(child, key);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            stringBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return stringBuilder.toString();
    }

    private static String computeMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
