.PHONY: tracker

all: tracker

tracker: tracker.o
	gcc -Wall -pthread tracker.o -o tracker

tracker.o:
	gcc -Wall -pthread -c tracker.c

clean:
	rm -rf *.o tracker