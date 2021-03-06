/*
	C ECHO client example using sockets
*/
#include <stdio.h>	//printf
#include <string.h>	//strlen
#include <sys/socket.h>	//socket
#include <arpa/inet.h>	//inet_addr
#include <unistd.h>
#include <stdlib.h>
int main(int argc , char *argv[])
{
	int sock, port;
	struct sockaddr_in server;
	char message[1000] , server_reply[2000];

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

	//Create socket
	sock = socket(AF_INET , SOCK_STREAM , 0);
	if (sock == -1)
	{
		printf("Could not create socket");
	}
	puts("Socket created");

	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons( port );

	//Connect to remote server
	if (connect(sock , (struct sockaddr *)&server , sizeof(server)) < 0)
	{
		perror("connect failed. Error");
		return 1;
	}

	puts("Connected\n");

	//keep communicating with server
	while(1)
	{
		printf("Enter message : ");
        scanf ("%[^\n]%*c", message);
        printf("Sending message: %s\n", message);
		//Send some data
		if( send(sock , message , strlen(message)+1 , 0) < 0)
		{
			puts("Send failed");
			return 1;
		}
        memset(server_reply, 0, sizeof(server_reply));
		//Receive a reply from the server
		if( recv(sock , server_reply , 2000 , 0) < 0)
		{
			puts("recv failed");
			break;
		}

		puts("Server reply :");
		puts(server_reply);
	}

	close(sock);
	return 0;
}