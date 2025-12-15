package protocol;

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
    public Packet(int sequenceNumber, boolean syn, boolean ack, boolean fin,
                  boolean rst, byte[] data
            ) {

        // Le paquet calcule lui même la taille total en additionnant le header à 5 (2 bytes taille
        // totale + 2 bytes numéro de séquence + 1 byte flags + les données
        this.totalSize =  5 + data.length;
        this.sequenceNumber = sequenceNumber;
        this.syn = syn;
        this.ack = ack;
        this.fin = fin;
        this.rst = rst;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Paquet : Numéro de séquence = %d, SYN = %b, ACK = %b, FIN = %b, RST %b, données = %d bytes",
                sequenceNumber, syn, ack, fin, rst, (data == null ? 0 : data.length)
        );
    }
}
