#ifndef _DATA_H
#define _DATA_H

#include <netinet/in.h>
#include <stddef.h>

#define MAX_NAME_LEN 100
#define MAX_KEY_LEN 100

struct peer_t;

struct peers_list_t;

struct leecher_t;

struct leechers_list_t;

struct files_t;

struct files_list_t;

struct peers_list_t* peers_list = NULL;
struct leechers_list_t* leechers_list = NULL;
struct files_list_t* files_list = NULL;

void add_peer(char* ip, unsigned int port);
void add_leecher(char* ip, unsigned int port);
void add_file(char* name, unsigned int size, unsigned int piece_size, char* key);

struct files_list_t* get_files_list();
struct files_list_t* get_files_by_criteria(int (* criteria)(struct files_t*));
char* get_file_name(struct files_t* file);
unsigned int get_file_size(struct files_t* file);
unsigned int get_file_piece_size(struct files_t* file);
char* get_file_key(struct files_t* file);

struct peers_list_t* get_peers_list();
struct peers_list_t* get_peers_having_file(char* key);
char* get_peer_ip(struct peer_t* peer);
unsigned int get_peer_port(struct peer_t* peer);

struct leechers_list_t* get_leechers_list();
struct leechers_list_t* get_leechers_having_file(char* key);
char* get_leecher_ip(struct leecher_t* leecher);
unsigned int get_leecher_port(struct leecher_t* leecher);

#endif //_DATA_H
