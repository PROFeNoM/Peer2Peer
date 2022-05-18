#include <stdlib.h>
#include <stdio.h>
#include <regex.h>
#include <string.h>

#include "include/parser.h"
#include "include/data.h"
#include "include/utils.h"

#define KNOWN_REQUEST_REGEX "^(announce|look|getfile|update).*"
#define ANNOUNCE_REQUEST_REGEX "^announce listen [0-9]*\\( seed \\[\\(.* [0-9]\\+ [0-9]\\+ \\w\\+\\s\\?\\)\\+\\]\\)\\?\\( leech \\[.*\\]\\)\\?\\s*$"
#define LOOK_REQUEST_REGEX "^look \\[.*\\]\\s*$"
#define GETFILE_REQUEST_REGEX "^getfile \\w\\+\\s*$"
#define UPDATE_REQUEST_REGEX "^update seed \\[.*\\] leech \\[.*\\]\\s*$"

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
	debug_log("[PARSER_LOG] Checking request validity\n");
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

char* parse_announce(char* request, char* ip, int sockfd)
{
	debug_log("[PARSER_LOG] Parsing announce request\n");
	remove_characters(request, "[]");

	char* tokens[MAX_TOKENS];
	int size_tokens = split(request, " ", tokens, MAX_TOKENS);

	int processing_listen = 0, processing_seed = 0, processing_leech = 0;
	int port;
	int i = 0;
	while (i < size_tokens)
	{
		debug_log("[PARSER_LOG] At token %d: %s\n", i, tokens[i]);
		if (strcmp("listen", tokens[i]) == 0)
		{
			debug_log("[PARSER_LOG] Processing listen\n");
			processing_listen = 1;
			processing_seed = 0;
			processing_leech = 0;

			i++;
		}
		else if (strcmp("seed", tokens[i]) == 0)
		{
			debug_log("[PARSER_LOG] Processing seed\n");
			processing_listen = 0;
			processing_seed = 1;
			processing_leech = 0;

			i++;
		}
		else if (strcmp("leech", tokens[i]) == 0)
		{
			debug_log("[PARSER_LOG] Processing leech\n");
			processing_listen = 0;
			processing_seed = 0;
			processing_leech = 1;

			i++;
		}
		else if (processing_listen)
		{
			port = atoi(tokens[i]);
			debug_log("[PARSER_LOG] Port token is %d\n", port);
			i++;
		}
		else if (processing_seed)
		{
			debug_log("[PARSER_LOG] Processing seed\n");
			char* file_name = tokens[i];
			remove_characters(file_name, "\n");
			unsigned int size = atoi(tokens[i + 1]);
			unsigned int piece_size = atoi(tokens[i + 2]);
			char* key = tokens[i + 3];
			remove_characters(key, "\n");

			add_seeder_to_file(file_name, size, piece_size, key, ip, port, sockfd);

			i += 4;
		}
		else if (processing_leech)
		{
			debug_log("[PARSER_LOG] Processing leech\n");
			char* key = tokens[i];
			remove_characters(key, "\n");
			add_leecher_to_file(key, ip, port, sockfd);

			i++;
		}
		else
			i++;
	}

	return "ok\n";
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

char* parse_look(char* request)
{
	debug_log("[PARSER_LOG] Processing look request\n");
	char* tokens[MAX_TOKENS];
	int size_tokens = split(request, " ", tokens, MAX_TOKENS);

	int filename_on = 0, filesize_on = 0;
	char* filename;
	int filesize;
	char operator;


	struct file_t** files;
	unsigned int files_size = 0;

	for (int i = 0; i < size_tokens; i++)
	{
		char* sub_tokens[MAX_TOKENS];
		char* _op[1] = { NULL };
		unsigned int size_subt = tokenize_criteria(tokens[i], "><=", sub_tokens, _op);

		// Check if sub_tokens[0] is either filename or filesize
		if (strcmp("filename", sub_tokens[0]) == 0 && sub_tokens[1])
		{
			debug_log("[PARSER_LOG] Analyze filename set to true\n");
            debug_log("[PARSER_LOG] filename: %s\n", sub_tokens[1]);
			filename = malloc(sizeof(char) * strlen(sub_tokens[1]) + 1);
			strcpy(filename, sub_tokens[1]);
			filename_on = 1;
		}
		else if (strcmp("filesize", sub_tokens[0]) == 0 && sub_tokens[1])
		{
			debug_log("[PARSER_LOG] Analyze filesize set to true\n");
            debug_log("[PARSER_LOG] filesize: %s\n", sub_tokens[1]);
			filesize = atoi(sub_tokens[1]);
			operator = _op[0][0];
			filesize_on = 1;
		}

		// Free strings from sub_tokens
		for (unsigned int j = 0; j < size_subt; j++)
		{
			free(sub_tokens[j]);
		}

		// Free strings from operator
		if (_op[0]) free(_op[0]);
	}
	if (filename_on) remove_characters(filename, "\n");
	if (filename_on && filesize_on) files = get_files_with_name_and_size(filename, filesize, operator, &files_size);
	else if (filename_on && !filesize_on) files = get_files_with_name(filename, &files_size);
	else if (!filename_on && filesize_on) files = get_files_with_size(filesize, operator, &files_size);
	else {
		files = get_all_files(&files_size);
	}

	char* message = malloc(sizeof(char) * MAX_MESSAGE_SIZE);
	strcpy(message, "list [");

	for (unsigned int i = 0; i < files_size; i++)
	{
		struct file_t* current_file = files[i];

		strcat(message, current_file->name);
		strcat(message, " ");

		unsigned int size = current_file->size;
		char size_str[64];
		sprintf(size_str, "%d", size);
		strcat(message, size_str);
		strcat(message, " ");

		unsigned int piece_size = current_file->piece_size;
		char piece_size_str[64];
		sprintf(piece_size_str, "%d", piece_size);
		strcat(message, piece_size_str);
		strcat(message, " ");

		strcat(message, current_file->key);
		if (i != files_size - 1) strcat(message, " ");
	}

	strcat(message, "]\n");

	if (filename_on) free(filename);
    if (files_size) free(files);
	return message;
}

char* parse_getfile(char* request)
{
	debug_log("[PARSER_LOG] Processing getfile request\n");

	// Get the file key
	char* file_key = strtok(request, " ");
	file_key = strtok(NULL, " ");
	remove_characters(file_key, "\n");
	struct file_t* file = get_file(file_key);

	char* message = malloc(sizeof(char) * MAX_MESSAGE_SIZE);
	strcpy(message, "peers ");
	strcat(message, file_key);
	strcat(message, " [");

	if (file)
	{
		struct peer_t* seeder;
		TAILQ_FOREACH(seeder, &file->seeders, next_peer)
		{
			strcat(message, seeder->ip);
			strcat(message, ":");

			unsigned int port = seeder->port;
			char port_str[6];
			sprintf(port_str, "%d", port);
			strcat(message, port_str);

			if (seeder->next_peer.tqe_next != NULL) strcat(message, " ");
		}
	}

	strcat(message, "]\n");

	return message;
}

char* parse_update(char* request, char* ip, int sockfd)
{
	debug_log("[PARSER_LOG] Processing update request\n");

	int port = get_peer_port(ip, sockfd);
	if (port == -1) return "nok\n";


	remove_characters(request, "[]");
	char* tokens[MAX_TOKENS];
	int size_tokens = split(request, " ", tokens, MAX_TOKENS);

	// Remove the seeder/leecher with given ip and sockfd from every files in the file list
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		remove_seeder_from_file(file->key, ip, sockfd);
		remove_leecher_from_file(file->key, ip, sockfd);
	}

	int processing_seed = 0, processing_leech = 0;
	for (int i = 0; i < size_tokens; i++)
	{
		if (strcmp("seed", tokens[i]) == 0)
		{
			processing_seed = 1;
			processing_leech = 0;
		}
		else if (strcmp("leech", tokens[i]) == 0)
		{
			processing_seed = 0;
			processing_leech = 1;
		}
		else
		{
			if (processing_seed)
			{
				struct file_t* file = get_file(tokens[i]);
				if (file) add_seeder_to_file(file->name, file->size, file->piece_size, file->key, ip, port, sockfd);
			}
			else if (processing_leech)
			{
				struct file_t* file = get_file(tokens[i]);
				if (file) add_leecher_to_file(file->key, ip, port, sockfd);
			}
		}
	}

	return "ok\n";
}
