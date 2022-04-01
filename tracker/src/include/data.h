#ifndef _DATA_H
#define _DATA_H

#include <netinet/in.h>

#define MAX_NAME_LEN 100
#define MAX_KEY_LEN 100

struct peer_t {
    char ip[INET_ADDRSTRLEN];
    unsigned int port;
};

struct peers_list_t
{
    struct peers_list_t *next;
    struct peer_t *peer;
};

struct leecher_t {
    char ip[INET_ADDRSTRLEN];
    unsigned int port;
};

struct leechers_list_t
{
    struct leechers_list_t *next;
    struct leecher_t *leecher;
};

struct files_t {
    char name[MAX_NAME_LEN];
    unsigned int size;
    unsigned int piece_size;
    char key[MAX_KEY_LEN];

    unsigned int num_peers;
    struct peers_list_t *peers;

    unsigned int num_leechers;
    struct leechers_list_t *leechers;
};

struct files_list_t
{
    struct files_list_t *next;
    struct files_t *file;
};

struct peers_list_t* peers_list;
struct leechers_list_t* leechers_list;
struct files_list_t* files_list;

void init_data();

void add_peer(char* ip, unsigned int port);
void add_leecher(char* ip, unsigned int port);
void add_file(char* name, unsigned int size, unsigned int piece_size, char* key);

struct files_list_t* get_files_list();
struct files_list_t* get_files_by_criteria(int (*criteria)(struct files_t*));

struct peers_list_t* get_peers_list();
struct peers_list_t* get_peers_having_file(char* key);

struct leechers_list_t* get_leechers_list();
struct leechers_list_t* get_leechers_having_file(char* key);

#endif //_DATA_H
