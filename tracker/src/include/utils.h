#ifndef _UTILS_H
#define _UTILS_H

void set_verbosity(int mode);

void debug_log(const char* fmt, ...);

int split(char message[], char* separator, char* tokens[], int max_tokens);

#endif //_UTILS_H
