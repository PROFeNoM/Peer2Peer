#ifndef _PARSER_H
#define _PARSER_H

enum REQUEST_T {ANNOUNCE, LOOK, GETFILE, UPDATE, INVALID, UNKNOWN};

/*
 * Get the type of the request
 * @param request - Request to identify
 */
enum REQUEST_T get_request_type(char* request);

#endif //_PARSER_H
