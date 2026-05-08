package protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


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
    public boolean syn;
    public boolean ack;
    public boolean fin;
    public boolean rst;

    // Les données transmises
    public byte[] data;

    // Laisser le paquet calculer lui même sa totalSize
    public Packet( byte[] data
            ) {

        // Le paquet calcule lui même la taille total en additionnant le header à 5 (2 bytes taille
        // totale + 2 bytes numéro de séquence + 1 byte flags + les données
        int header = 5;
        this.totalSize =  header + data.length;
        this.sequenceNumber = 1;
        this.syn = false;
        this.ack = false;
        this.fin = false;
        this.rst = false;
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

        /* On doit utiliser ByteArrayOutputStream pour écrire les bytes dans un tableau en mémoire sans avoir la
        // taille finale à l'avance

        // 1. On crée un flux mémoire vide qui va accueillir les bytes au fur et à mesure qu'on avance pour finalement le transformer en tableau de
         byte */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        String test = baos.toString();

        /* On doit écrire totalSize sur 16 bits NON SIGNÉ mais en Java un int est codé sur 32 bits
        // On doit donc extraire uniquement les 16 bits utiles et les envoyer sur le réseau

        // On nous impose le format Big Endian, donc Byte fort d'abord et Byte faible après

        // Pour obtenir le byte fort, on décale totalSize de 8 bits vers la droite
        // puis on pose le masque & 0xFF pour garder que 8 bits
        // Pour le byte faible on garde directement les 8 bits de droite avec & 0xFF.

        // Avec masque 0xFF on dégage les bits inutiles et on enlève les signes (+ -)
        // 0x = hexadécimal, base 16 (int 32bits en java)

        // 2. On sélectionne les 16bits de totalSize et on les insère dans notre ByteArrayOutputStream
         On s'occupe du Big Endian uniquement */
        int padding = 0xFF;
        byte [] totalSize1 = ((totalSize >> 8) & padding);
        baos.write(totalSize1);
        System.out.println(baos.toByteArray());
        System.out.println(test);



        // 3. On sélectionne les 16 bits de sequenceNumber en BigEndian et les insère dans baos comme on à fait pour le totalSize
        baos.write((sequenceNumber >> 8) & padding);
        System.out.println(baos.toByteArray());
        System.out.println(test);



        /* 3. Maintenant on doit regrouper les flags dans un byte et les rajouter à la suite dans baos
        // Les flags sont au format booléen chacun seront sur un bit
        //.... SYN ACK FIN RST on comble les 4 autres bits à 0
        // 0b = binaire, base 2

        // bit 3 = SYN
        // bit 2 = ACK
        // bit 1 = FIN
         bit 0 = RST */
        int flags = 0;
        if (syn) flags |= 0b00001000;
        if (ack) flags |= 0b00000100;
        if (fin) flags |= 0b00000010;
        if (rst) flags |= 0b00000001;

        // Ici pas besoin de masque car baos.write prend que les 8bits de droite
        baos.write(flags);
        System.out.println(baos.toByteArray());
        System.out.println(test);


        /* 4. On rajoute les données à envoyer
        Voir si data est non null et plus grand que 0 */
        if (data != null && data.length > 0){
            // Ici java ajoute les données byte par byte dans la baos
            baos.write(data);
            System.out.println(baos.toByteArray());
            System.out.println(test);



        }

        // 5. tout passé dans un tableau de byte et le renvoyer
        return baos.toByteArray();
    }

    /* public static Packet decode(byte[] data){

        /* 1. D'abord on vérifie que les données en entré ne soient pas null et supérieur ou égal à 5 (5 bytes => 2 bytes totalSize + 2 bytes
        Numéro de séquence + 1 byte flags)
        if (data != null && data.length >= 5) {
            // 2. On extrait le totalSize data[0] byte fort et data[1] byte faible

            // 3. On extrait le sequenceNumber data[2] byte fort et data[3] byte faible

            // 4. On extrait les flags data[4]

            // 5. On extrait les données s'il y en a  data[5] jusqu'à data.length -1

        } else {
            throw new IllegalArgumentException("Invalid packet data");
        }
    } */
}





