#include <stdlib.h>
#include <stdio.h>
#include <regex.h>

#include "include/parser.h"

#define KNOWN_REQUEST_REGEX "^(announce|look|getfile|update).*"
#define ANNOUNCE_REQUEST_REGEX "^announce listen [0-9]*\\( seed \\[\\(.* [0-9]\\+ [0-9]\\+ \\w\\+\\s\\?\\)\\+\\]\\)\\?\\( leech \\[.*\\]\\)\\?\\s*$"
#define LOOK_REQUEST_REGEX "^look \\[.*\\]\\s*$"
#define GETFILE_REQUEST_REGEX "^getfile \\w\\+\\s*$"

void error_tmp(char *msg) {
    perror(msg);
    exit(1);
}

/*
 * Check if the request is among the known request types
 * @param request - Request to check
 */
int is_request_type_known(char *request) {
    regex_t regex;
    int reti;

    // Compile regular expression
    reti = regcomp(&regex, KNOWN_REQUEST_REGEX, REG_EXTENDED);
    if (reti) error_tmp("Error compiling regex\n");

    // Execute regular expression
    reti = regexec(&regex, request, 0, NULL, 0);
    if (!reti) {
        regfree(&regex);
        return 1;
    } else if (reti == REG_NOMATCH) {
        regfree(&regex);
        return 0;
    } else {
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
int is_request_valid(char *request, enum REQUEST_T request_t) {
    regex_t regex;
    int reti;

    // Compile regular expression
    switch (request_t) {
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
            break;
        case INVALID:
        case UNKNOWN:
            regfree(&regex);
            return 0;
            break;
    }
    if (reti) error_tmp("Error compiling regex\n");

    // Execute regular expression
    reti = regexec(&regex, request, 0, NULL, 0);
    if (!reti) {
        regfree(&regex);
        return 1;
    } else if (reti == REG_NOMATCH) {
        regfree(&regex);
        return 0;
    } else {
        regfree(&regex);
        error_tmp("Error executing regex\n");
        return 0;
    }
}

enum REQUEST_T get_request_type(char *request) {
    if (!is_request_type_known(request)) return UNKNOWN;

    enum REQUEST_T potential_request_type;
    switch (request[0]) {
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