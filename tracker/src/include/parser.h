#ifndef _PARSER_H
#define _PARSER_H

#define MAX_TOKENS 100
#define MAX_SIZE_TOKEN 100
#define MAX_MESSAGE_SIZE 1000

enum REQUEST_T {ANNOUNCE, LOOK, GETFILE, UPDATE, INVALID, UNKNOWN};

/*
 * Get the type of the request
 * @param request - Request to identify
 */
enum REQUEST_T get_request_type(char* request);

#endif //_PARSER_H


//int parse_announce(char* to_parse[], int size_parse, char* seeder[], char* leech[]);

char* parse_announce(char* request, char* ip);
char* parse_look(char* request);
char* parse_getfile(char* request);
char* parse_update(char* request, char* ip, unsigned int port);

int copy_without_brackets(char* array[], char* argv[], int i);

int split(char message[], char* separator, char* tokens[], int max_tokens);

void print_tokens(char* token[], int nb_token);
