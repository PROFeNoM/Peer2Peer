#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <strings.h>
#include <string.h>
#include <arpa/inet.h>
#include <pthread.h>

#include "include/tracker.h"
#include "include/parser.h"
#include "include/data.h"

struct args {
    int newsockfd;
    char* ip;
    unsigned int port;
};

void error(char* msg)
{
	perror(msg);
	exit(1);
}

int main(int argc, char* argv[])
{
	init_lists();

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
	while ((client_sock = accept(socket_fd, (struct sockaddr*)&client, (socklen_t*)&c)))
	{
		// New client
		//printf("Connection accepted\n");

        // Retrive clien's port number from the client's structure
        int client_port = ntohs(client.sin_port);

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
        printf("Cli socket: %d\n", client_sock);
        struct args* args = malloc(sizeof(struct args));
        args->newsockfd = *new_sock;
        printf("Args socket: %d\n", args->newsockfd);
        args->ip = client_ip;
        args->port = client_port;
		if (pthread_create(&sniffer_thread, NULL, connection_handler, (void*)args) < 0)
		{
			error("Error creating client's thread\n");
		}
		printf("Handler assigned\n");

		// Now join the thread, so that we don't terminate before the thread
		// pthread_join(sniffer_thread, NULL);
	}

	if (client_sock < 0) error("Error accepting the incoming connection\n");

	return 0;
}

void* connection_handler(void* args)
{
	//Get the socket descriptor
    struct args* arg = (struct args*)args;
    int sock = arg->newsockfd;
    printf("CLeient's socket: %d\n", sock);
    char* ip = arg->ip;
    //unsigned int port = arg->port;
	ssize_t read_size;
	char client_message[2000];
    char* result;
	//Receive a message from client
	while ((read_size = recv(sock, client_message, 2000, 0)) > 0)
	{
		printf("Received %s\n", client_message);

        printf("Analyzing message...\n");
		enum REQUEST_T request_t = get_request_type(client_message);

		switch (request_t)
		{
		case ANNOUNCE:
            printf("[LOG] Announce request\n");
			result = parse_announce(client_message, ip, sock);
            printf("[LOG] Announce response: %s\n", result);
            write(sock, result, strlen(result));
            printf("[LOG] Sent %s\n", result);
			break;
		case LOOK:
            result = parse_look(client_message);
			write(sock, result, strlen(result));
			printf("[LOG] Sent %s\n", result);
            free(result);
			break;
		case GETFILE:
            result = parse_getfile(client_message);
            write(sock, result, strlen(result));
			printf("[LOG] Sent %s\n", result);
            free(result);
			break;
		case UPDATE:
			result = parse_update(client_message, ip, sock);
            write(sock, result, strlen(result));
			printf("[LOG] Sent %s\n", result);
			break;
		case INVALID:
			write(sock, "INVALID\n", strlen("INVALID\n"));
			break;
		default:
			write(sock, "UNKNOWN\n", strlen("UNKNOWN\n"));
			break;
		}
        printf("[LOG] Response sent\n");

		// clear the buffer of client_message
		memset(client_message, 0, sizeof(client_message));
	}

	if (read_size == 0)
	{
		// Remove the seeder/leecher with given ip and sockfd from every files in the file list
		for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
		{
			remove_seeder_from_file(file->key, ip, sock);
			remove_leecher_from_file(file->key, ip, sock);
		}
		puts("Client disconnected");
		fflush(stdout);
	}
	else if (read_size == -1)
	{
		//perror("recv failed");
	}

	//Free the socket pointer
	//free(socket_desc);
    //printf("Free args\n");
    //free(args);

	return 0;
}
