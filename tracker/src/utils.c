#include <stdio.h>
#include <stdarg.h>
#include <string.h>

#include "include/utils.h"

int verbosity_flag = 0;

void debug_log(const char* fmt, ...)
{
	if (verbosity_flag)
	{
		va_list args;
		va_start(args, fmt);
		vfprintf(stdout, fmt, args);
		va_end(args);
	}
}

void set_verbosity(int mode) {
	verbosity_flag = mode;
	debug_log("Activating verbose mode\n", mode);
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

	return i;
}