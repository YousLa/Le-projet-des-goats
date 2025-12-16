package protocol;

import java.io.ByteArrayOutputStream;

public class PacketCodec {
    // Objectif :
    // Transformer un objet Packet en byte[] (envoie UDP) et transformer un byte[] en Packet
    public static byte [] encode(Packet paquet){

        // On doit utiliser ByteArrayOutputStream pour écrire les bytes dans un tableau en mémoire sans avoir la
        // taille finale à l'avance

        // 1. On crée un flux mémoire vide qui va accueillir les bytes au fur et à mesure qu'on avance pour finalement le transformer en tableau de
        // byte
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // On doit écrire totalSize sur 16 bits NON SIGNÉ mais en Java un int est codé sur 32 bits
        // On doit donc extraire uniquement les 16 bits utiles et les envoyer sur le réseau

        // On nous impose le format Big Endian, donc Byte fort d'abord et Byte faible après

        // Pour obtenir le byte fort, on décale totalSize de 8 bits vers la droite
        // puis on pose le masque & 0xFF pour garder que 8 bits
        // Pour le byte faible on garde directement les 8 bits de droite avec & 0xFF.

        // Avec masque 0xFF on dégage les bits inutiles et on enlève les signes (+ -)
        // 0x = hexadécimal, base 16 (int 32bits en java)

        // 2. On sélectionne les 16buts de totalSize et on les insère dans notre ByteArrayOutputStream
        // D'abord le byte fort
        baos.write((paquet.totalSize >> 8) & 0xFF);

        // Ensuite le byte faible
        baos.write(paquet.totalSize & 0xFF);

        // 3. On sélectionne les 16 bits de sequenceNumber en BigEndian et les insère dans baos comme on à fait pour le totalSize
        baos.write((paquet.sequenceNumber >> 8) & 0xFF);
        baos.write(paquet.sequenceNumber & 0xFF);

        //3. Maintenant on doit regrouper les flags dans un byte et les rajouter à la suite dans baos
        // Les flags sont au format booléen chacun seront sur un bit
        //.... SYN ACK FIN RST on comble les 4 autres bits à 0
        // 0b = binaire, base 2

        // bit 3 = SYN
        // bit 2 = ACK
        // bit 1 = FIN
        // bit 0 = RST
        int flags = 0;
        if (paquet.syn) flags |= 0b00001000;
        if (paquet.ack) flags |= 0b00000100;
        if (paquet.fin) flags |= 0b00000010;
        if (paquet.rst) flags |= 0b00000001;

        // Ici pas besoin de masque car baos.write prend que les 8bits de droite
        baos.write(flags);

        // 4. On rajoute les données à envoyer
        // Voir si data est non null et plus grand que 0
        if (paquet.data != null && paquet.data.length > 0){
            // Ici java ajoute les données byte par byte dans la baos
            baos.write(paquet.data);
        }

        // 5. tout passé dans un tableau de byte et le renvoyer
        return baos.toByteArray();
    }



}
