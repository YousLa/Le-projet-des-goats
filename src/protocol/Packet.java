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
        this.syn = 0;
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

    public Packet decode(byte[] data){

        /* 1. D'abord on vérifie que les données en entré ne soient pas null et supérieur ou égal à 5 (5 bytes => 2 bytes totalSize + 2 bytes
        Numéro de séquence + 1 byte flags) */
        if (data != null && data.length >= 5) {
            // 2. On extrait le totalSize data[0] byte fort et data[1] byte faible


            // 3. On extrait le sequenceNumber data[2] byte fort et data[3] byte faible
            ByteBuffer buf = ByteBuffer.allocate(2);
            buf.put(data[2]);
            System.out.println(data[2]);
            buf.put(data[3]);
            System.out.println(data[3]);
            short result = buf.getShort();
            System.out.println("result : " + result);

            // 4. On extrait les flags data[4]

            // 5. On extrait les données s'il y en a  data[5] jusqu'à data.length -1

        } else {
            throw new IllegalArgumentException("Invalid packet data");
        }
        return null;
    }
}
