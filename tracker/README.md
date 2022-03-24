# How to compile

Go to the root of the tracker repository and run

```shell
$ make install
```

# How to run the tracker

After compilation, go to the root of the tracker repository. Running the tracker uses the following syntax

```shell
$ ./install/tracker [p]
```

- The ```p``` parameter allows to specify the port number.

## Usage example

- Using the previously defined syntax, a tracker could be run as follows:

```shell
$ ./install/tracker 6666 
```

# How to run tests

0. Compile the project as explained previously

1. At the root of the tracker repository, run

```shell
$ ./install/alltests
```