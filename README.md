# Eirb2Share

```
 ______ _      _    ___   _____ _                     
|  ____(_)    | |  |__ \ / ____| |                   
| |__   _ _ __| |__   ) | (___ | |__   __ _ _ __ ___ 
|  __| | | '__| '_ \ / / \___ \| '_ \ / _` | '__/ _ \
| |____| | |  | |_) / /_ ____) | | | | (_| | | |  __/
|______|_|_|  |_.__/____|_____/|_| |_|\__,_|_|  \___|
```

Eirb2Share est une application permettant le partage de fichiers en réseau en mode pair à pair.

Ce README détaille la manière d'utiliser l'application dans sa version centralisée, c'est à dire fonctionnant avec un tracker central assistant les différents pairs dans la recherche des fichiers à télécharger.

Languages :
* C (tracker)
* Java (pair)

## Tracker

Les fichiers relatifs au tracker se trouvent dans le dossier ``tracker``.

Il est nécessaire de lancer le tracker avant le lancement des différents pairs. 

### Configuration

Le tracker peut être paramétré à l'aide d'un fichier ``config.ini`` se trouvant dans le dossier ``install``.

### Installation

Compilation du tracker et des tests et installation dans le dossier ``install`` :

```bash
make install
```

### Usage

Lancement du tracker :

```bash
./install/tracker
```

Lancement des tests :

```bash
make test
```

## Pair

Les fichiers relatifs au pair se trouvent dans le dossier ``peer``.

Il est nécessaire que le tracker soit lancé avant de lancer des pairs.

### Configuration

Le pair peut être paramétré à l'aide d'un fichier ``config.ini`` se trouvant à la racine du dossier ``peer``.

### Installation

Compilation du pair :

```bash
make build
```

### Usage

Lancement du pair avec l'interface en ligne de commande :

```bash
make peer
```

Lancement du pair avec l'interface graphique :

```bash
make gui
```

Afin de lancer un second pair en parallèle depuis la même installation, il suffit de modifier le ``config.ini`` pour spécifier un port ainsi qu'un dossier de stockage différent, ou de lancer le pair directement avec la commande ``java`` en spécifiant les paramètres :

```bash
java -cp build [-Dpeer-port=<peer-port>] [-Dstorage-path=<storage-path>] peer.Main [-h] # Interface ligne de commande

java -cp build [-Dpeer-port=<peer-port>] [-Dstorage-path=<storage-path>] peer.gui.MainWindow [-h] # Interface graphique
```


Lancement des tests :

```bash
make test
```

### Utilisation

Dans l'interface en ligne de commande il est possible d'utiliser les commandes suivantes :

\- Effectuer une recherche (si aucun critère n'est précisé (i.e ``look []``) on recherche l'ensemble des fichiers disponible) :
```bash
> look [[filename=<filename>] [filesize><filesize>]]
```

\- Récupérer un fichier :
```bash
> getfile <key>
```

\- Quitter l'application :
```bash
> exit
```

Dans l'interface graphique, les actions sont réalisés grâce à différents boutons / clics.