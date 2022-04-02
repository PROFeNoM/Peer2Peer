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

void remove_characters(char* str, char to_remove[])
{
	// Remove every character that are in to_remove from str
	for (unsigned int i = 0; i < strlen(to_remove); i++)
	{
		for (unsigned int j = 0; j < strlen(str); j++)
		{
			if (str[j] == to_remove[i])
			{
				// left shift every character by one
				for (unsigned int k = j; k < strlen(str) - 1; k++)
				{
					str[k] = str[k + 1];
				}
				str[strlen(str) - 1] = '\0';
			}
		}
	}
}

unsigned int tokenize_criteria(char* str, char delimiters[], char* tokens[], char* used_delimiter[])
{
	remove_characters(str, "[\"]");

	// Split str into tokens at each character that are in delimiters
	char buffer[strlen(str)];
	buffer[0] = '\0';
	unsigned int buffer_idx = 0;

	char delimiter_buffer[strlen(str)];
	delimiter_buffer[0] = '\0';
	unsigned int delimiter_buffer_idx = 0;

	unsigned int current_token = 0;
	unsigned int current_delimiter = 0;

	for (unsigned int i = 0; i < strlen(str); i++)
	{
		// Check if str[i] is in delimiters
		int is_delimiter = 0;
		for (unsigned j = 0; j < strlen(delimiters); j++)
		{
			if (str[i] == delimiters[j])
			{
				delimiter_buffer[delimiter_buffer_idx++] = delimiters[j];

				is_delimiter = 1;
				// Add current buffer to the tokens array and then reset buffer
				if (strlen(buffer) > 0)
				{
					buffer[buffer_idx] = '\0';
					tokens[current_token] = (char*)malloc(sizeof(char) * strlen(buffer) + 1);
					strcpy(tokens[current_token++], buffer);
					buffer[0] = '\0';
					buffer_idx = 0;
				}
			}
		}
		// If str[i] isn't a delimiter, add it to buffer
		if (!is_delimiter)
		{
			buffer[buffer_idx++] = str[i];
			if (strlen(delimiter_buffer) > 0)
			{
				delimiter_buffer[delimiter_buffer_idx] = '\0';
				used_delimiter[current_delimiter] = (char*)malloc(sizeof(char) * strlen(delimiter_buffer) + 1);
				strcpy(used_delimiter[current_delimiter++], delimiter_buffer);
				delimiter_buffer[0] = '\0';
				delimiter_buffer_idx = 0;
			}
		}
	}

	if (strlen(buffer) > 0)
	{
		buffer[buffer_idx] = '\0';
		tokens[current_token] = (char*)malloc(sizeof(char) * strlen(buffer) + 1);
		strcpy(tokens[current_token++], buffer);
	}

	return current_token;
}

int index_of_file_in_array(struct files_t* files[], unsigned int files_size, struct files_t* file)
{
	for (unsigned int i = 0; i < files_size; i++)
		if (files[i] == file)
			return (int)i;
	return -1;
}

int is_file_in_array(struct files_t* files[], unsigned int files_size, struct files_t* file)
{
	return index_of_file_in_array(files, files_size, file) != -1;
}

char* parse_look(char* request)
{
	char* tokens[MAX_TOKENS];
	int size_tokens = split(request, " ", tokens, MAX_TOKENS);

	struct files_t* files[100] = { NULL };
	unsigned int files_size = 0;

	for (int i = 0; i < size_tokens; i++)
	{
		char* sub_tokens[MAX_TOKENS];
		char* operator[1] = { NULL };
		unsigned int size_subt = tokenize_criteria(tokens[i], "><=", sub_tokens, operator);

		// Check if sub_tokens[0] is either filename or filesize
		if (strcmp("filename", sub_tokens[0]) == 0 && sub_tokens[1])
		{
			char* filename = sub_tokens[1];

			struct files_list_t* files_filename = get_files_by_criteria(
					(int (*)(struct files_t*, void*))criteria_filename,
					filename);

			if (get_file(files_filename) == NULL) files_size = 0;

			struct files_list_t* current = files_filename;
			while (current && get_file(current) != NULL)
			{
				if (is_file_in_array(files, files_size, get_file(current)))
				{
					if (!criteria_filename(get_file(current), filename))
					{
						int file_idx = index_of_file_in_array(files, files_size, get_file(current));
						for (unsigned int idx = file_idx; idx < files_size - 1; idx++) files[idx] = files[idx + 1];
						files_size--;
					}
				}
				else
				{
					files[files_size++] = get_file(current);
				}

				current = get_next_file(current);
			}

			free_files_list(files_filename);
		}
		else if (strcmp("filesize", sub_tokens[0]) == 0 && sub_tokens[1])
		{
			char* filesize = sub_tokens[1];

			char* function_tokens[2] = { operator[0], filesize };
			struct files_list_t* files_filesize = get_files_by_criteria(
					(int (*)(struct files_t*, void*))criteria_filesize,
					function_tokens);

			if (get_file(files_filesize) == NULL) files_size = 0;

			struct files_list_t* current = files_filesize;
			while (current && get_file(current) != NULL)
			{
				if (is_file_in_array(files, files_size, get_file(current)))
				{
					if (!criteria_filesize(get_file(current), function_tokens))
					{
						int file_idx = index_of_file_in_array(files, files_size, get_file(current));
						for (unsigned int idx = file_idx; idx < files_size - 1; idx++) files[idx] = files[idx + 1];
						files_size--;
					}
				}
				else
				{
					files[files_size++] = get_file(current);
				}

				current = get_next_file(current);
			}

			free_files_list(files_filesize);
		}

		// Free strings from sub_tokens
		for (unsigned int j = 0; j < size_subt; j++)
		{
			free(sub_tokens[j]);
		}

		// Free strings from operator
		if (operator[0]) free(operator[0]);
	}

	char* message = malloc(sizeof(char) * MAX_MESSAGE_SIZE);
	strcpy(message, "list [");

	for (unsigned int i = 0; i < files_size; i++)
	{
		struct files_t* current_file = files[i];

		strcat(message, get_file_name(current_file));
		strcat(message, " ");

		unsigned int size = get_file_size(current_file);
		char size_str[64];
		sprintf(size_str, "%d", size);
		strcat(message, size_str);
		strcat(message, " ");

		unsigned int piece_size = get_file_piece_size(current_file);
		char piece_size_str[64];
		sprintf(piece_size_str, "%d", piece_size);
		strcat(message, piece_size_str);
		strcat(message, " ");

		strcat(message, get_file_key(current_file));
		if (i != files_size - 1) strcat(message, " ");
	}

	strcat(message, "]");

	return message;
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

	char* message = malloc(sizeof(char) * MAX_MESSAGE_SIZE);
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

	//print_tokens(tokens, i);

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
