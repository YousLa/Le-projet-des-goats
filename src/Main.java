import protocol.Packet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        String phrase = "Yo!";
        Packet paquet = new Packet(phrase.getBytes());
        paquet.encode();
    }
}