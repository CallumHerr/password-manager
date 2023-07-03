import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class FileManager {
    private File file;
    private SecretKeySpec keySpec;
    private String encData;
    private final List<String[]> accounts = new ArrayList<>();

    public boolean setFile(File file) {
        if (!file.getName().endsWith(".dat")) file = new File(file.getName() + ".dat");
        this.file = file;
        try {
            file.createNewFile();
            Scanner reader = new Scanner(file);
            StringBuilder readData = new StringBuilder();
            while (reader.hasNextLine()) {
                readData.append(reader.nextLine());
            }
            reader.close();
            this.encData = readData.toString();
            return true;
        } catch (IOException err) {
            return false;
        }
    }

    public boolean hasFile() {
        return this.file != null;
    }

    public void genKey(String password, LoginPage gui) {
        byte[] key;
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            key = password.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            this.keySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException err) {
            JOptionPane.showMessageDialog(gui, "Something went wrong while attempting to decrypt, try again or make an issue on github.");
        }
    }

    public String encrypt(String rawStr) {
        try {
            SecretKeySpec keySpec = this.keySpec;
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] cipherTxt = cipher.doFinal(rawStr.getBytes());
            byte[] encrypted = new byte[iv.length + cipherTxt.length];

            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(cipherTxt, 0, encrypted, iv.length, cipherTxt.length);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception err) {
            return null;
        }
    }

    public boolean save() {
        StringBuilder rawData = new StringBuilder();
        for (String[] values : this.accounts) {
            rawData.append(values[0]);
            rawData.append("\r");
            rawData.append(values[1]);
            rawData.append("\r");
            rawData.append(values[2]);
            rawData.append("\r");
        }
        String encData = encrypt(rawData.toString());
        if (encData == null) {
            return false;
        }
        this.encData = encData;
        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write(encData);
            writer.close();
        } catch (IOException err) {
            return false;
        }
        return true;
    }

    public boolean decrypt() {
        if (this.encData == null || this.encData.length() == 0) return false;
        String decData;
        try {
            SecretKeySpec keySpec = this.keySpec;
            byte[] encrypted = Base64.getDecoder().decode(this.encData.getBytes());
            byte[] iv = new byte[16];

            System.arraycopy(encrypted, 0, iv, 0, iv.length);
            byte[] cipherTxt = new byte[encrypted.length - iv.length];
            System.arraycopy(encrypted, iv.length, cipherTxt, 0, cipherTxt.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            decData = new String(cipher.doFinal(cipherTxt), StandardCharsets.UTF_8);
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }

        String[] values = decData.split("\r");
        if (values.length < 3) return false;
        for (int i = 0; i < values.length; i += 3) {
            this.accounts.add(new String[]{values[i], values[i+1], values[i+2]});
        }
        return true;
    }

    public List<String[]> getAccounts() {
        return this.accounts;
    }

    public boolean addAccount(String platform, String name, String password, int index) {
        if (index >= 0) {
            this.accounts.set(index, new String[]{platform, name, password});
        } else {
            this.accounts.add(new String[]{platform, name, password});
        }
        boolean success = save();

        if (!success) {
            this.accounts.remove(index);
            return false;
        }
        return true;
    }

    public boolean removeAccount(int index) {
        String[] account = this.accounts.get(index);
        this.accounts.remove(index);
        boolean success = save();

        if (!success) {
            this.accounts.add(account);
            return false;
        }
        return true;
    }

    public void closeFile() {
        save();
        this.accounts.clear();
        this.keySpec = null;
    }
}
