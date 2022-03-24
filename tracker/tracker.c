#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <strings.h>
#include <string.h>
#include <arpa/inet.h>
#include <pthread.h>

#include "tracker.h"

void error(char* msg)
{
	perror(msg);
	exit(1);
}

int main(int argc, char* argv[])
{
	int socket_fd, client_sock, c, * new_sock;
	int port;
	struct sockaddr_in server, client;

	// Retrieve port number
	if (argc < 2)
	{
		printf("Usage: Missing port number\n");
		exit(-1);
	}
	else
	{
		port = atoi(argv[1]);
	}

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (socket_fd < 0) error("Error creating the socket\n");
	else printf("Socket successfully created\n");

	// initialize the server socket's structure
	bzero((char*)&server, sizeof(server));
	server.sin_family = AF_INET; // internet socket
	server.sin_addr.s_addr = INADDR_ANY; // we listen on every interfaces
	server.sin_port = htons(port);  // listened port

	// Bind the socket
	if (bind(socket_fd, (struct sockaddr*)&server, sizeof(server)) < 0)
	{
		error("Error binding the socket to the server\n");
	}
	else
	{
		printf("Socket successfully bound\n");
	}

	// Set maximum number of entries & listen
	if (listen(socket_fd, 5) < -1) error("Error listening to incoming connections\n");
	char server_ip[INET_ADDRSTRLEN];
	inet_ntop(AF_INET, &server.sin_addr, server_ip, INET_ADDRSTRLEN);
	printf("Tracker %s:%d has started\n", server_ip, port);
	printf("Tracker waiting for an incoming connection...\n");
	c = sizeof(struct sockaddr_in);

	// Listen for incoming connections
	while ((client_sock = accept(socket_fd, (struct sockaddr*)&client, (socklen_t * ) & c)))
	{
		// New client
		//printf("Connection accepted\n");

		char client_ip[INET_ADDRSTRLEN];
		if (inet_ntop(AF_INET, &client.sin_addr, client_ip, INET_ADDRSTRLEN) == NULL)
		{
			error("Error retrieving the client IP\n");
		}
		else
		{
			printf("Accepted a new connection from %s\n", client_ip);
		}

		pthread_t sniffer_thread;
		new_sock = malloc(1);
		*new_sock = client_sock;

		if (pthread_create(&sniffer_thread, NULL, connection_handler, (void*)new_sock) < 0)
		{
			error("Error creating client's thread\n");
		}

		// Now join the thread, so that we don't terminate before the thread
		pthread_join(sniffer_thread, NULL);
		printf("Handler assigned\n");
	}

	if (client_sock < 0) error("Error accepting the incoming connection\n");

	return 0;
}

void *connection_handler(void *socket_desc)
{
	//Get the socket descriptor
	int sock = *(int*)socket_desc;
	int read_size;
	//char *message , client_message[2000];

	//Send some messages to the client
	//message = "Greetings! I am your connection handler\n";
	//write(sock , message , strlen(message));

	//message = "Now type something and i shall repeat what you type \n";
	//write(sock , message , strlen(message));

	//Receive a message from client
	while( (read_size = recv(sock , client_message , 2000 , 0)) > 0 )
	{
		//Send the message back to client
		write(sock , client_message , strlen(client_message));
	}

	if(read_size == 0)
	{
		puts("Client disconnected");
		fflush(stdout);
	}
	else if(read_size == -1)
	{
		perror("recv failed");
	}

	//Free the socket pointer
	free(socket_desc);

	return 0;
}