#ifndef _PARSER_H
#define _PARSER_H

#define MAX_TOKENS 100
#define MAX_MESSAGE_SIZE 1000

enum REQUEST_T {ANNOUNCE, LOOK, GETFILE, UPDATE, INVALID, UNKNOWN};

/*
 * Get the type of the request
 * @param request - Request to identify
 */
enum REQUEST_T get_request_type(char* request);

char* parse_announce(char* request, char* ip, int sockfd);
char* parse_look(char* request);
char* parse_getfile(char* request);
char* parse_update(char* request, char* ip, int sockfd);

int split(char message[], char* separator, char* tokens[], int max_tokens);

void set_parser_verbosity(int verbosity);

#endif //_PARSER_H