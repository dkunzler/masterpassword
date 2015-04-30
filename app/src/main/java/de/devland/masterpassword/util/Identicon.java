package de.devland.masterpassword.util;

import com.lyndir.lhunath.opal.system.MessageAuthenticationDigests;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.lyndir.lhunath.opal.system.util.StringUtils.strf;


/**
 * @author lhunath, 15-03-29
 */
public class Identicon {

    private static final Charset charset = Charset.forName("UTF-8");
    private static final Color[] colors = new Color[]{
            Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.MONO};
    private static final char[] leftArm = new char[]{'╔', '╚', '╰', '═'};
    private static final char[] rightArm = new char[]{'╗', '╝', '╯', '═'};
    private static final char[] body = new char[]{'█', '░', '▒', '▓', '☺', '☻'};
    private static final char[] accessory = new char[]{
            '◈', '◎', '◐', '◑', '◒', '◓', '☀', '☁', '☂', '☃', '☄', '★', '☆', '☎', '☏', '⎈', '⌂', '☘', '☢', '☣', '☕', '⌚', '⌛', '⏰', '⚡',
            '⛄', '⛅', '☔', '♔', '♕', '♖', '♗', '♘', '♙', '♚', '♛', '♜', '♝', '♞', '♟', '♨', '♩', '♪', '♫', '⚐', '⚑', '⚔', '⚖', '⚙', '⚠',
            '⌘', '⏎', '✄', '✆', '✈', '✉', '✌'};

    private final String fullName;
    private final Color color;
    private final String text;

    public Identicon(String fullName, String masterPassword) {
        this(fullName, masterPassword.toCharArray());
    }

    public Identicon(String fullName, char[] masterPassword) {
        this.fullName = fullName;

        byte[] masterPasswordBytes = charset.encode(CharBuffer.wrap(masterPassword)).array();
        ByteBuffer identiconSeedBytes = ByteBuffer.wrap(
                MessageAuthenticationDigests.HmacSHA256.of(masterPasswordBytes, fullName.getBytes(charset)));
        Arrays.fill(masterPasswordBytes, (byte) 0);

        IntBuffer identiconSeedBuffer = IntBuffer.allocate(identiconSeedBytes.capacity());
        while (identiconSeedBytes.hasRemaining())
            identiconSeedBuffer.put(identiconSeedBytes.get() & 0xFF);
        int[] identiconSeed = identiconSeedBuffer.array();

        color = colors[identiconSeed[4] % colors.length];
        text = strf("%c%c%c%c", leftArm[identiconSeed[0] % leftArm.length], body[identiconSeed[1] % body.length],
                rightArm[identiconSeed[2] % rightArm.length], accessory[identiconSeed[3] % accessory.length]);
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public enum Color {
        RED,
        GREEN,
        YELLOW,
        BLUE,
        MAGENTA,
        CYAN,
        MONO;

        public int getColorCode() {
            int color = android.graphics.Color.BLACK;
            switch (this) {
                case RED:
                    color = android.graphics.Color.argb(255, 220, 50, 47);
                    break;
                case GREEN:
                    color = android.graphics.Color.argb(255, 133, 153, 0);
                    break;
                case YELLOW:
                    color = android.graphics.Color.argb(255, 181, 137, 0);
                    break;
                case BLUE:
                    color = android.graphics.Color.argb(255, 38, 139, 210);
                    break;
                case MAGENTA:
                    color = android.graphics.Color.argb(255, 211, 54, 130);
                    break;
                case CYAN:
                    color = android.graphics.Color.argb(255, 42, 161, 152);
                    break;
                case MONO:
                    color = android.graphics.Color.argb(255, 88, 110, 117);
                    break;
            }
            return color;
        }
    }
}
