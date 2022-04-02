#include <stdlib.h>
#include <stdio.h>
#include <regex.h>
#include <string.h>

#include "include/parser.h"
#include "include/data.h"

#define KNOWN_REQUEST_REGEX "^(announce|look|getfile|update).*"
#define ANNOUNCE_REQUEST_REGEX "^announce listen [0-9]*\\( seed \\[\\(.* [0-9]\\+ [0-9]\\+ \\w\\+\\s\\?\\)\\+\\]\\)\\?\\( leech \\[.*\\]\\)\\?\\s*$"
#define LOOK_REQUEST_REGEX "^look \\[.*\\]\\s*$"
#define GETFILE_REQUEST_REGEX "^getfile \\w\\+\\s*$"
#define UPDATE_REQUEST_REGEX "^update seed \\[.*\\] leech \\[.*\\]\\s*$"

#define ANNOUNCE_NB_ARGS 4
#define SIZE_CALLS 3

char* calls[3] = { "listen", "seed", "leech" };

void error_tmp(char* msg)
{
	perror(msg);
	exit(1);
}

/*
 * Check if the request is among the known request types
 * @param request - Request to check
 */
int is_request_type_known(char* request)
{
	regex_t regex;
	int reti;

	// Compile regular expression
	reti = regcomp(&regex, KNOWN_REQUEST_REGEX, REG_EXTENDED);
	if (reti) error_tmp("Error compiling regex\n");

	// Execute regular expression
	reti = regexec(&regex, request, 0, NULL, 0);
	if (!reti)
	{
		regfree(&regex);
		return 1;
	}
	else if (reti == REG_NOMATCH)
	{
		regfree(&regex);
		return 0;
	}
	else
	{
		regfree(&regex);
		error_tmp("Error executing regex\n");
		return 0;
	}
}

/*
 * Check if the request is valid according a give type
 * @param request - Request to check
 * @param request_t - Request type
 */
int is_request_valid(char* request, enum REQUEST_T request_t)
{
	regex_t regex;
	int reti;

	// Compile regular expression
	switch (request_t)
	{
	case ANNOUNCE:
		reti = regcomp(&regex, ANNOUNCE_REQUEST_REGEX, 0);
		break;
	case LOOK:
		reti = regcomp(&regex, LOOK_REQUEST_REGEX, 0);
		break;
	case GETFILE:
		reti = regcomp(&regex, GETFILE_REQUEST_REGEX, 0);
		break;
	case UPDATE:
		reti = regcomp(&regex, UPDATE_REQUEST_REGEX, 0);
		break;
	case INVALID:
	case UNKNOWN:
		regfree(&regex);
		return 0;
	}
	if (reti) error_tmp("Error compiling regex\n");

	// Execute regular expression
	reti = regexec(&regex, request, 0, NULL, 0);
	if (!reti)
	{
		regfree(&regex);
		return 1;
	}
	else if (reti == REG_NOMATCH)
	{
		regfree(&regex);
		return 0;
	}
	else
	{
		regfree(&regex);
		error_tmp("Error executing regex\n");
		return 0;
	}
}

enum REQUEST_T get_request_type(char* request)
{
	if (!is_request_type_known(request)) return UNKNOWN;

	enum REQUEST_T potential_request_type;
	switch (request[0])
	{
	case 'a':
		potential_request_type = ANNOUNCE;
		break;
	case 'l':
		potential_request_type = LOOK;
		break;
	case 'g':
		potential_request_type = GETFILE;
		break;
	case 'u':
		potential_request_type = UPDATE;
		break;
	}

	return is_request_valid(request, potential_request_type) ? potential_request_type : INVALID;
}

/*
*
*	@return Port value
*/
int parse_announce(char* to_parse[], int size_parse, char* seeder[], char* leech[])
{

	for (int i = 1; i < size_parse; i++)
	{
		for (int j = 0; j < SIZE_CALLS; j++)
		{
			if (!strcmp(calls[j], to_parse[i]))
			{
				fprintf(stderr, "Special word caught: %s\n", calls[j]);
				if (!strcmp(calls[j], "seed")) copy_without_brackets(seeder, to_parse, i + 1);
				else if (!strcmp(calls[j], "leech")) copy_without_brackets(leech, to_parse, i + 1);
			}
		}
	}

	fprintf(stderr, "========================================\n");
	print_tokens(seeder, 100);
	print_tokens(leech, 100);
	fprintf(stderr, "========================================\n");

	int port = atoi(to_parse[3]);

	return port;
}

char* parse_getfile(char* request)
{
	/*
	 * Given a request of the form:
	 * getfile $file_key
	 * return a message of the form:
	 * peers $file_key [$ip1:$port1 $ip2:$port2 ...]
	 */

	// Get the file key
	char* file_key = strtok(request, " ");
	file_key = strtok(NULL, " ");

	// Get the list of peers having the file represented by file_key
	struct peers_list_t* peers = get_peers_having_file(file_key);

	char* message = malloc(
			strlen("peers ")
			+ strlen(file_key)
			+ 2
			+ (strlen("255.255.255.255:65535 ") * get_peers_list_size(peers))
			+ 2);

	strcpy(message, "peers ");
	strcat(message, file_key);
	strcat(message, " [");

	while (peers != NULL)
	{
		strcat(message, get_peer_ip(get_peer(peers)));
		strcat(message, ":");

		unsigned int port = get_peer_port(get_peer(peers));
		char port_str[6];
		sprintf(port_str, "%d", port);
		strcat(message, port_str);

		peers = get_next_peer(peers);
		if (peers != NULL) strcat(message, " ");
	}

	strcat(message, "]");

	free(peers);

	return message;
}

int copy_without_brackets(char* array[], char* to_parse[], int i)
{
	int i_array = 0;

	while (1)
	{
		if (to_parse[i][0] == '[')
		{
			char* result = to_parse[i] + 1;
			array[i_array] = malloc(70);
			strcpy(array[i_array], result);
		}
		else if (to_parse[i][strlen(to_parse[i]) - 1] == ']')
		{
			array[i_array] = malloc(70);
			strncpy(array[i_array], to_parse[i], strlen(to_parse[i]) - 1);
			return i;
		}
		else
		{
			array[i_array] = malloc(70);
			strcpy(array[i_array], to_parse[i]);
		}

		i_array++;
		i++;
	}
}

/*
 * Split a message for every blank space
 * @param message - Message to split
 * @param spliter - Array to fill
 */
int split(char message[], char* separator, char* tokens[], int max_tokens)
{
	int i = 0;
	char* token = strtok(message, separator);

	while (token != NULL && i < max_tokens)
	{
		tokens[i] = token;
		token = strtok(NULL, separator);
		i++;
	}

	print_tokens(tokens, i);

	return i;
}

/*
*	DEBUG
*
*/
void print_tokens(char* token[], int nb_token)
{
	for (int i = 0; i < nb_token; i++)
	{
		if (token[i] != NULL)
			fprintf(stderr, "%s\n", token[i]);
	}
}
