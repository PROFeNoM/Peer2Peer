```
  ______ _____ _____  ____ ___   _____ _                    
 |  ____|_   _|  __ \|  _ \__ \ / ____| |                   
 | |__    | | | |__) | |_) | ) | (___ | |__   __ _ _ __ ___ 
 |  __|   | | |  _  /|  _ < / / \___ \| '_ \ / _` | '__/ _ \
 | |____ _| |_| | \ \| |_) / /_ ____) | | | | (_| | | |  __/
 |______|_____|_|  \_\____/____|_____/|_| |_|\__,_|_|  \___|
                                                            
                                                            
```

# How to compile

Go to the root of the peer directory and run

```shell
$ make
```

# How to run a peer

After compilation, go tho the root of the peer directory. Parametrizing a peer can be done through the config.ini, or with the following command-line syntax:
```shell
java [-DpeerPort=<peerPort>] [-DpeerMax=<peerMax>] peer.Main
```

## Usage example

Using the previously defined syntax, a peer could be run as follows:
```shell
java -DpeerPort="2222" -DpeerMax="2" peer.Main
```