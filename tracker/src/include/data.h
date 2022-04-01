#ifndef _DATA_H
#define _DATA_H

#define MAX_NAME_LEN 100
#define MAX_KEY_LEN 100

struct peer_t {
    char ip[INET_ADDRSTRLEN];
    unsigned int port;
}

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

#endif //_DATA_H
