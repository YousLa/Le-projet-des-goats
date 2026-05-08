## Format du programme
### Récepteur
- Arguments :
    1. Port d’écoute
    2. Nom du fichier de destination
- Écoute sur toutes les IP v4 (`0.0.0.0`).

### Émetteur
- Arguments :
    1. IP du récepteur
    2. Port du récepteur
    3. Fichier à envoyer

## Protocole
- Basé sur **UDP**.
- Structure d’un paquet :
    - Taille totale des données (16 bits)
    - Numéro de séquence (16 bits)
    - Flags (SYN, ACK, FIN, RST - 1 bit chacun)
    - Données (taille variable)
- **Tous les nombres sont non signés**.
- **Byte padding** : compléter jusqu’à 8 bits avec des 0 si nécessaire.

### Ouverture de connexion
- SYN initial envoyé par l’émetteur.
- Récepteur répond avec SYN + ACK.
- Émetteur répond avec SYN.
- **Retry** : après 3 tentatives sans réponse, la connexion est abandonnée.

### Fermeture de connexion
1. **Reset** : flag RST → fermeture immédiate, écritures dans fichier et message d’erreur.
2. **Fin normale** :
    - Émetteur → FIN
    - Récepteur → FIN + ACK
    - Émetteur → ACK

### Échanges fiables
- **ACK** : récepteur envoie numéro de séquence du dernier paquet continu reçu.
- **Fenêtre de l’émetteur** : 256 paquets par défaut.
- **Retransmission** : si 3 ACKs consécutifs pour le même numéro (avec 3 numéros de séquence différents).

### Numéros de séquence
- Initialisés aléatoirement (émetteur pour SYN, récepteur pour SYN + ACK).
- Incrément de 1 pour chaque nouveau paquet (sauf retransmission).

### Big Endian vs Little Endian
- Les paquets doivent être **écrits en big endian**.
- Vérifier l’endianness native de la machine avant encodage.