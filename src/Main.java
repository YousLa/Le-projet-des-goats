import protocol.Packet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
       /* String phrase = "Yo!";
        Packet paquet = new Packet(phrase.getBytes());
        paquet.encode();
        byte[] paquet2 = paquet.encode();
        paquet.decode(paquet2);*/

                String phrase = "Yo!";

                // Encode
                Packet paquet = new Packet(phrase.getBytes(StandardCharsets.UTF_8));
                byte[] paquet2 = paquet.encode();

                // Decode
                Packet decoded = Packet.decode(paquet2);

                // Reconvertir les bytes en String
                String result = new String(decoded.data, StandardCharsets.UTF_8);
                System.out.println("Résultat : " + result); // "Yo!"
            }
        }