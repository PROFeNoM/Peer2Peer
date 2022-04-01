#include <string.h>
#include <stdlib.h>
#include "include/data.h"

struct peer_t
{
	char ip[INET_ADDRSTRLEN];
	unsigned int port;
};

struct peers_list_t
{
	struct peers_list_t* next;
	struct peer_t* peer;
};

struct leecher_t
{
	char ip[INET_ADDRSTRLEN];
	unsigned int port;
};

struct leechers_list_t
{
	struct leechers_list_t* next;
	struct leecher_t* leecher;
};

struct files_t
{
	char name[MAX_NAME_LEN];
	unsigned int size;
	unsigned int piece_size;
	char key[MAX_KEY_LEN];

	struct peers_list_t* peers;
	struct leechers_list_t* leechers;
};

struct files_list_t
{
	struct files_list_t* next;
	struct files_t* file;
};

struct peers_list_t* peers_list;
struct peers_list_t* peers_list_tail;
struct leechers_list_t* leechers_list;
struct leechers_list_t* leechers_list_tail;
struct files_list_t* files_list;
struct files_list_t* files_list_tail;

void init_lists()
{
	peers_list = malloc(sizeof(struct peers_list_t));
	peers_list->next = NULL;
	peers_list->peer = NULL;
	peers_list_tail = peers_list;

	leechers_list = malloc(sizeof(struct leechers_list_t));
	leechers_list->next = NULL;
	leechers_list->leecher = NULL;
	leechers_list_tail = leechers_list;

	files_list = malloc(sizeof(struct files_list_t));
	files_list->next = NULL;
	files_list->file = NULL;
	files_list_tail = files_list;
}

void add_peer(char* ip, unsigned int port)
{
	// Create a peer and add it to the list of peers peers_list
	struct peer_t* peer = malloc(sizeof(struct peer_t));
	strcpy(peer->ip, ip);
	peer->port = port;
	peers_list_tail->next = malloc(sizeof(struct peers_list_t));
	peers_list_tail = peers_list_tail->next;
	peers_list_tail->next = NULL;
	peers_list_tail->peer = peer;
}

void add_leecher(char* ip, unsigned int port)
{
	// Create a leecher and add it to the list of leechers leechers_list
	struct leecher_t* leecher = malloc(sizeof(struct leecher_t));
	strcpy(leecher->ip, ip);
	leecher->port = port;
	leechers_list_tail->next = malloc(sizeof(struct leechers_list_t));
	leechers_list_tail = leechers_list_tail->next;
	leechers_list_tail->next = NULL;
	leechers_list_tail->leecher = leecher;
}

void add_file(char* name, unsigned int size, unsigned int piece_size, char* key)
{
	// Create a file and add it to the list of files files_list
	struct files_t* file = malloc(sizeof(struct files_t));
	strcpy(file->name, name);
	file->size = size;
	file->piece_size = piece_size;
	strcpy(file->key, key);
	files_list_tail->next = malloc(sizeof(struct files_list_t));
	files_list_tail = files_list_tail->next;
	files_list_tail->next = NULL;
	files_list_tail->file = file;
}

////////////////////////////////////////////////////////////////////////////////

struct files_list_t* get_files_list()
{
	return files_list;
}

struct files_t* get_file(struct files_list_t* files)
{
	return files->file;
}

struct files_list_t* get_next_file(struct files_list_t* files)
{
	return files->next;
}

struct files_list_t* get_files_by_criteria(int (* criteres)(struct files_t*, void*), void* data)
{
	struct files_list_t* files = malloc(sizeof(struct files_list_t));
	files->next = NULL;
	files->file = NULL;

	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (criteres(current->file, data))
		{
			files_list_tail->next = malloc(sizeof(struct files_list_t));
			files_list_tail = files_list_tail->next;
			files_list_tail->next = NULL;
			files_list_tail->file = current->file;
		}
		current = current->next;
	}

	return files;
}

char* get_file_name(struct files_t* file)
{
	return file->name;
}

unsigned int get_file_size(struct files_t* file)
{
	return file->size;
}

unsigned int get_file_piece_size(struct files_t* file)
{
	return file->piece_size;
}

char* get_file_key(struct files_t* file)
{
	return file->key;
}

struct peers_list_t* get_file_peers(struct files_t* file)
{
	return file->peers;
}

struct leechers_list_t* get_file_leechers(struct files_t* file)
{
	return file->leechers;
}

////////////////////////////////////////////////////////////////////////////////

struct peers_list_t* get_peers_list()
{
	return peers_list;
}

struct peer_t* get_peer(struct peers_list_t* peers)
{
	return peers->peer;
}

struct peers_list_t* get_next_peer(struct peers_list_t* peers)
{
	return peers->next;
}

int has_key(struct files_t* file, char* key)
{
	return strcmp(file->key, key) == 0;
}

struct peers_list_t* get_peers_having_file(char* key)
{
	struct files_list_t* files = get_files_by_criteria((int (*)(struct files_t*, void*))has_key, key);
	return get_file_peers(get_file(files));
}

char* get_peer_ip(struct peer_t* peer)
{
	return peer->ip;
}

unsigned int get_peer_port(struct peer_t* peer)
{
	return peer->port;
}

////////////////////////////////////////////////////////////////////////////////

struct leechers_list_t* get_leechers_list()
{
	return leechers_list;
}

struct leecher_t* get_leecher(struct leechers_list_t* leechers)
{
	return leechers->leecher;
}

struct leechers_list_t* get_next_leecher(struct leechers_list_t* leechers)
{
	return leechers->next;
}

struct leechers_list_t* get_leechers_having_file(char* key)
{
	struct files_list_t* files = get_files_by_criteria((int (*)(struct files_t*, void*))has_key, key);
	return get_file_leechers(get_file(files));
}

char* get_leecher_ip(struct leecher_t* leecher)
{
	return leecher->ip;
}

unsigned int get_leecher_port(struct leecher_t* leecher)
{
	return leecher->port;
}