#ifndef _PARSER_H
#define _PARSER_H

enum REQUEST_T {ANNOUNCE, LOOK, GETFILE, UPDATE, INVALID, UNKNOWN};

/*
 * Get the type of the request
 * @param request - Request to identify
 */
enum REQUEST_T get_request_type(char* request);

#endif //_PARSER_H


int parse_announce(char* to_parse[], int size_parse, char* seeder[], char* leech[]);

char* parse_getfile(char* request);

int copy_without_brackets(char* array[], char* argv[], int i);

int split(char message[], char* separator, char* tokens[], int max_tokens);

void print_tokens(char* token[], int nb_token);
