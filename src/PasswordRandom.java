import java.util.Random;

public class PasswordRandom {
    private final Random rand;
    private final char[] characters = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#',
            '%', '^', '&', '*'
    };

    public PasswordRandom() {
        this.rand = new Random();
    }

    public String newPassword(int length, boolean symbols) {
        StringBuilder passBuilder = new StringBuilder();
        int upperLimit = 62; //Non symbols
        if (symbols) upperLimit = characters.length;

        for (int i = 0; i < length; i++) {
            int index = this.rand.nextInt(upperLimit);
            passBuilder.append(characters[index]);
        }

        return passBuilder.toString();
    }
}
