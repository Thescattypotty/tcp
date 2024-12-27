# Démonstration TCP

Cette démonstration explore différentes méthodes de fermeture de connexion TCP pour comprendre le comportement et la fiabilité des échanges en fin de session. Elle s’inspire d’un article expliquant que de nombreux développeurs ne maîtrisent pas toujours les subtilités du protocole TCP quand il s’agit d’interrompre le flux et de garantir la bonne transmission des données.

## Structure

- **C**: Contient des exemples de clients et serveurs TCP utilisant des appels systèmes basiques (socket, connect, shutdown, close…).
- **Java**: Présente une approche utilisant Netty pour illustrer l’envoi et la réception de données, ainsi que différentes façons de fermer une connexion.

## Points Clés

- Les options de socket (par ex. `SO_LINGER`, `TCP_NODELAY`) influencent la façon dont le protocole gère la fin de la connexion et l’acquittement des données.
- Selon la méthode de fermeture choisie (`shutdown`, `close`, `linger`…), certaines données peuvent rester non acquittées et potentiellement perdues.
- L’exemple Netty montre comment vérifier la présence de données en attente et effectuer une fermeture plus sécurisée.

## Usage Simplifié

1. Compiler et lancer les serveurs (C ou Java).
2. Démarrer un client correspondant.
3. Observer les logs pour comparer la fermeture propre (`shutdown`) vs la fermeture brutale (`close` immédiat).

Pour plus d’informations, référez-vous au code et testez les différents cas de figure pour mieux comprendre les nuances de TCP.

## Exécution

### Avec C

```zsh
cd C
make
./server 6969
./client 127.0.0.1 6969 1000000
```

### Avec Java

```zsh
cd JAVA
mvn clean install
mvn exec:java -Dexec.mainClass="com.tcpdemo.server.NettyServer" -Dexec.args="6969"
mvn exec:java -Dexec.mainClass="com.tcpdemo.client.NettyClient" -Dexec.args="localhost 6969 1000000"
//without shutdown & boucle for waiting pending data
mvn exec:java -Dexec.mainClass="com.tcpdemo.client.BadNettyClient" -Dexec.args="localhost 6969 1000000"
//or
mvn exec:java -Dexec.mainClass="com.tcpdemo.server.TCPServer" -Dexec.args="6969"
mvn exec:java -Dexec.mainClass="com.tcpdemo.client.TCPClient" -Dexec.args="localhost 6969 1000000"
```

## Références & Rapports de Bugs

- [The ultimate SO_LINGER page, or why is my tcp not reliable](https://blog.netherlabs.nl/articles/2009/01/18/the-ultimate-so_linger-page-or-why-is-my-tcp-not-reliable)
- [Tsoding Daily Video :)](https://www.youtube.com/watch?v=JRTLSxGf_6w)
- [IBM APAR PH10558](https://www.ibm.com/support/pages/apar/PH10558)
- [lwIP TCP/IP Stack Bug](https://savannah.nongnu.org/bugs/?19157)
- [Rust Programming Language Discussion](https://internals.rust-lang.org/t/tcpstream-always-terminates-connections-successfully-even-on-panic/15109?page=2)
- [Minecraft Java Edition IP packets Lost Not Fixed](https://bugs.mojang.com/browse/MC-270155)
