package protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class Packet {

    // Champs d'un paquet :
        // Taille totale (16 bits), Numéro de séquence (16 bits), SYN, ACK, FIN, RST, les données
        // totalSize occupe 16 bits dans le paquet mais sa valeur représente la taille totale du paquet en octets

    // Pour garder seulement les 16 bits de droite on utilise un masque binaire 0xFFFF
    // totalSize = totalSize & 0xFFFF;
    // sequenceNumber = sequenceNumber & 0xFFFF;
    public int totalSize;
    public int sequenceNumber;

    // Booléens seront stocké dans 1 byte plus tard (8 bits)
    // Booléen ici et transformé en bit plus tard avec la classe PacketCodec (4bits)
    public int syn;
    public int ack;
    public int fin;
    public int rst;

    // Les données transmises
    public byte[] data;

    // Laisser le paquet calculer lui même sa totalSize
    public Packet( byte[] data
            ) {

        // Le paquet calcule lui même la taille total en additionnant le header à 5 (2 bytes taille
        // totale + 2 bytes numéro de séquence + 1 byte flags + les données
        int header = 5;
        this.totalSize =  header + data.length;
        this.sequenceNumber = 5;
        this.syn = 1;
        this.ack = 0;
        this.fin = 0;
        this.rst = 0;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Paquet : Numéro de séquence = %d, SYN = %b, ACK = %b, FIN = %b, RST %b, données = %d bytes",
                sequenceNumber, syn, ack, fin, rst, (data == null ? 0 : data.length)
        );
    }

    // Objectif :
    // Transformer un objet Packet en byte[] (envoie UDP) et transformer un byte[] en Packet
    public byte [] encode() throws IOException {


             /* 3. Maintenant on doit regrouper les flags dans un byte et les rajouter à la suite dans baos
        // Les flags sont au format booléen chacun seront sur un bit
        //.... SYN ACK FIN RST on comble les 4 autres bits à 0
        // 0b = binaire, base 2

        // bit 3 = SYN
        // bit 2 = ACK
        // bit 1 = FIN
         bit 0 = RST */
        int flags = 0;
        if (syn == 1) flags |= 0b00001000;
        if (ack == 1) flags |= 0b00000100;
        if (fin == 1) flags |= 0b00000010;
        if (rst == 1) flags |= 0b00000001;

        ByteBuffer buf = ByteBuffer.allocate(totalSize);
        buf.order(ByteOrder.BIG_ENDIAN);

        buf.putShort((short) totalSize);
        System.out.println("après totalSize : " + Arrays.toString(buf.array()));

        buf.putShort((short) sequenceNumber);
        System.out.println("après sequenceNumber : " + Arrays.toString(buf.array()));

        buf.put((byte) flags);
        System.out.println("après flags : " + Arrays.toString(buf.array()));

        if (data != null && data.length > 0) buf.put(data);
        System.out.println("final : " + Arrays.toString(buf.array()));

        return buf.array();

    }

    public static Packet decode(byte[] data){

        /* 1. D'abord on vérifie que les données en entré ne soient pas null et supérieur ou égal à 5 (5 bytes => 2 bytes totalSize + 2 bytes
        Numéro de séquence + 1 byte flags) */
        if (data != null && data.length >= 5) {

            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.BIG_ENDIAN);

            // 2. totalSize
            int totalSize = buf.getShort() & 0xFFFF;
            System.out.println("totalSize : " + totalSize);

            // 3. sequenceNumber
            int sequenceNumber = buf.getShort() & 0xFFFF;
            System.out.println("sequenceNumber : " + sequenceNumber);


            // 4. flags
            int flags = buf.get() & 0xFF;
            int syn = (flags >> 3) & 1;
            int ack = (flags >> 2) & 1;
            int fin = (flags >> 1) & 1;
            int rst = flags & 1;
            System.out.println("flags : syn=" + syn + " ack=" + ack + " fin=" + fin + " rst=" + rst);

            // 5. données
            byte[] payload = new byte[buf.remaining()];
            buf.get(payload);
            System.out.println("payload : " + Arrays.toString(payload));

            Packet p = new Packet(payload);
            p.sequenceNumber = sequenceNumber;
            p.syn = syn;
            p.ack = ack;
            p.fin = fin;
            p.rst = rst;
            return p;

        } else {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }
}
