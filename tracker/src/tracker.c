#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <strings.h>
#include <string.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <signal.h>

#include "include/tracker.h"
#include "include/parser.h"
#include "include/data.h"
#include "include/utils.h"

#define UNUSED(x) (void)(x)

int socket_fd;

// Define a SIGINT handler
void sigint_handler(int sig)
{
	UNUSED(sig);
	debug_log("\n[TRACKER_LOG] SIGINT received, exiting...\n");

	struct peer_t* peer;
	TAILQ_FOREACH(peer, &peers_list, entry)
	{
		debug_log("[TRACKER_LOG] Closing peer %s:%d with sockfd %d\n", peer->ip, peer->port, peer->sockfd);
		shutdown(peer->sockfd, SHUT_RDWR);
		close(peer->sockfd);
	}

	debug_log("[TRACKER_LOG] Closing socket %d\n", socket_fd);
	shutdown(socket_fd, SHUT_RDWR);
	close(socket_fd);

	debug_log("[TRACKER_LOG] Exiting...\n");
	exit(0);
}

struct args
{
	int newsockfd;
	char* ip;
};

void error(char* msg)
{
	perror(msg);
	exit(1);
}

int main(int argc, char* argv[])
{
	signal(SIGINT, sigint_handler);

	UNUSED(argc);
	UNUSED(argv);

	init_lists();

	int client_sock, c, * new_sock;
	int port, verbose, maximum_peers;
	char address[INET6_ADDRSTRLEN];
	struct sockaddr_in server, client;

	// Read file config.ini

	FILE* fptr;
	fptr = fopen("./config.ini", "r");
	if (fptr == NULL)
	{
		error("Couldn't open config.ini");
	}

	char* str = NULL;
	char* tokens[2][MAX_TOKENS];
	ssize_t read;
	size_t len = 50;
	int i = 0;

	while ((read = getline(&str, &len, fptr)) != -1)
	{
		split(str, " ", tokens[i], MAX_TOKENS);

		if (!strcmp("tracker-port", tokens[i][0]))
		{
			port = atoi(tokens[i][2]);
			debug_log("[TRACKER_LOG] port: %d\n", port);
		}
		else if (!strcmp("tracker-ip", tokens[i][0]))
		{
			strcpy(address, tokens[i][2]);
			debug_log("[TRACKER_LOG] adresse: %s", address);
		}
		else if (!strcmp("verbose", tokens[i][0]))
		{
			verbose = atoi(tokens[i][2]);
			set_verbosity(verbose);
			debug_log("[TRACKER_LOG] verbose: %d\n", verbose);
		}
		else if (!strcmp("maximum-peers", tokens[i][0]))
		{
			maximum_peers = atoi(tokens[i][2]);
			debug_log("[TRACKER_LOG] maximum-peers: %d\n", maximum_peers);
		}

		i++;
	}

	fclose(fptr);

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (socket_fd < 0) error("Error creating the socket\n");

	if (setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &(int){ 1 }, sizeof(int)))
	{
		error("Error setting socket options\n");
	}

	debug_log("[TRACKER_LOG] Socket successfully created\n");

	// initialize the server socket's structure
	bzero((char*)&server, sizeof(server));
	server.sin_family = AF_INET; // internet socket
	server.sin_addr.s_addr = inet_addr(address); // IP address
	//server.sin_addr.s_addr = INADDR_ANY; // we listen on every interfaces
	server.sin_port = htons(port);  // listened port

	// Bind the socket
	if (bind(socket_fd, (struct sockaddr*)&server, sizeof(server)) < 0)
		error("Error binding the socket to the server\n");

	debug_log("[TRACKER_LOG] Socket successfully bound\n");


	// Set maximum number of entries & listen
	if (listen(socket_fd, maximum_peers) < -1) error("Error listening to incoming connections\n");
	char server_ip[INET_ADDRSTRLEN];
	inet_ntop(AF_INET, &server.sin_addr, server_ip, INET_ADDRSTRLEN);
	debug_log("[TRACKER_LOG] Tracker %s:%d has started\n", server_ip, port);
	debug_log("[TRACKER_LOG] Tracker waiting for an incoming connection...\n");
	c = sizeof(struct sockaddr_in);

	// Listen for incoming connections
	while ((client_sock = accept(socket_fd, (struct sockaddr*)&client, (socklen_t*)&c)))
	{
		char client_ip[INET_ADDRSTRLEN];
		if (inet_ntop(AF_INET, &client.sin_addr, client_ip, INET_ADDRSTRLEN) == NULL)
			error("Error retrieving the client IP\n");

		printf("[TRACKER_LOG] Accepted a new connection from %s\n", client_ip);

		pthread_t sniffer_thread;
		new_sock = malloc(1);
		*new_sock = client_sock;
		struct args* args = malloc(sizeof(struct args));
		args->newsockfd = *new_sock;
		args->ip = client_ip;
		if (pthread_create(&sniffer_thread, NULL, connection_handler, (void*)args) < 0)
			error("Error creating client's thread\n");

		debug_log("[TRACKER_LOG] Handler assigned\n");

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
	printf("Client's socket: %d\n", sock);
	char* ip = arg->ip;
	//unsigned int port = arg->port;
	ssize_t read_size;
	char client_message[2000];
	char* result;
	//Receive a message from client
	while ((read_size = recv(sock, client_message, 2000, 0)) > 0)
	{
		debug_log("\n[TRACKER_LOG] Received %s", client_message);

		debug_log("[TRACKER_LOG] Analyzing message...\n");
		enum REQUEST_T request_t = get_request_type(client_message);

		switch (request_t)
		{
		case ANNOUNCE:
			debug_log("[TRACKER_LOG] Announce request\n");
			result = parse_announce(client_message, ip, sock);
			debug_log("[TRACKER_LOG] Announce response: %s\n", result);
			write(sock, result, strlen(result));
			debug_log("[TRACKER_LOG] Sent %s\n", result);
			break;
		case LOOK:
			result = parse_look(client_message);
			write(sock, result, strlen(result));
			debug_log("[TRACKER_LOG] Sent %s\n", result);
			free(result);
			break;
		case GETFILE:
			result = parse_getfile(client_message);
			write(sock, result, strlen(result));
			debug_log("[TRACKER_LOG] Sent %s\n", result);
			free(result);
			break;
		case UPDATE:
			result = parse_update(client_message, ip, sock);
			write(sock, result, strlen(result));
			debug_log("[TRACKER_LOG] Sent %s\n", result);
			break;
		case INVALID:
			write(sock, "INVALID\n", strlen("INVALID\n"));
			break;
		default:
			write(sock, "UNKNOWN\n", strlen("UNKNOWN\n"));
			break;
		}
		debug_log("[TRACKER_LOG] Response sent\n");

		// clear the buffer of client_message
		memset(client_message, 0, sizeof(client_message));
	}

    // Remove the seeder/leecher with given ip and sockfd from every files in the file list
    for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
    {
        remove_seeder_from_file(file->key, ip, sock);
        remove_leecher_from_file(file->key, ip, sock);
    }
    puts("Client disconnected");
    fflush(stdout);

	//Free the socket pointer
	//free(socket_desc);
	//printf("Free args\n");
	//free(args);

	return 0;
}
