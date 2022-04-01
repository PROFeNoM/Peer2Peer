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

	if (peers_list == NULL)
	{
		peers_list = malloc(sizeof(struct peers_list_t));
		peers_list->next = NULL;
		peers_list->peer = peer;
		peers_list_tail = peers_list;
	}
	else
	{
		peers_list_tail->next = malloc(sizeof(struct peers_list_t));
		peers_list_tail->next->next = NULL;
		peers_list_tail->next->peer = peer;
		peers_list_tail = peers_list_tail->next;
	}
}

void add_leecher(char* ip, unsigned int port)
{
	// Create a leecher and add it to the list of leechers leechers_list
	struct leecher_t* leecher = malloc(sizeof(struct leecher_t));
	strcpy(leecher->ip, ip);
	leecher->port = port;

	if (leechers_list == NULL)
	{
		leechers_list = malloc(sizeof(struct leechers_list_t));
		leechers_list->next = NULL;
		leechers_list->leecher = leecher;
		leechers_list_tail = leechers_list;
	}
	else
	{
		leechers_list_tail->next = malloc(sizeof(struct leechers_list_t));
		leechers_list_tail->next->next = NULL;
		leechers_list_tail->next->leecher = leecher;
		leechers_list_tail = leechers_list_tail->next;
	}
}

void add_file(char* name, unsigned int size, unsigned int piece_size, char* key)
{
	// Create a file and add it to the list of files files_list
	struct files_t* file = malloc(sizeof(struct files_t));
	strcpy(file->name, name);
	file->size = size;
	file->piece_size = piece_size;
	strcpy(file->key, key);
	file->peers = NULL;
	file->leechers = NULL;

	if (files_list == NULL)
	{
		files_list = malloc(sizeof(struct files_list_t));
		files_list->next = NULL;
		files_list->file = file;
		files_list_tail = files_list;
	}
	else
	{
		files_list_tail->next = malloc(sizeof(struct files_list_t));
		files_list_tail->next->next = NULL;
		files_list_tail->next->file = file;
		files_list_tail = files_list_tail->next;
	}
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
	struct files_list_t* files_tail = files;

	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (current->file && criteres(current->file, data))
		{
			if (files->file == NULL)
			{
				files->file = current->file;
			}
			else
			{
				files_tail->next = malloc(sizeof(struct files_list_t));
				files_tail = files_tail->next;
				files_tail->next = NULL;
				files_tail->file = current->file;
			}
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

void add_peer_to_file(char* key, struct peer_t* peer)
{
	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (current->file && strcmp(current->file->key, key) == 0)
		{
			struct peers_list_t* peers = current->file->peers;
			while (peers != NULL)
			{
				if (strcmp(peers->peer->ip, peer->ip) == 0 && peers->peer->port == peer->port)
				{
					return;
				}
				peers = peers->next;
			}

			peers = malloc(sizeof(struct peers_list_t));
			peers->next = current->file->peers;
			peers->peer = peer;
			current->file->peers = peers;

			return;
		}
		current = current->next;
	}
}

void add_leecher_to_file(char* key, struct leecher_t* leecher)
{
	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (current->file && strcmp(current->file->key, key) == 0)
		{
			struct leechers_list_t* leechers = current->file->leechers;
			while (leechers != NULL)
			{
				if (strcmp(leechers->leecher->ip, leecher->ip) == 0 && leechers->leecher->port == leecher->port)
				{
					return;
				}
				leechers = leechers->next;
			}

			leechers = malloc(sizeof(struct leechers_list_t));
			leechers->next = current->file->leechers;
			leechers->leecher = leecher;
			current->file->leechers = leechers;
			return;
		}
		current = current->next;
	}
}

void remove_peer_from_file(char* key, struct peer_t* peer)
{
	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (current->file && strcmp(current->file->key, key) == 0)
		{
			struct peers_list_t* peers = current->file->peers;
			struct peers_list_t* previous = NULL;
			while (peers != NULL)
			{
				if (strcmp(peers->peer->ip, peer->ip) == 0 && peers->peer->port == peer->port)
				{
					if (previous == NULL)
					{
						current->file->peers = peers->next;
					}
					else
					{
						previous->next = peers->next;
					}
					free(peers);
					return;
				}
				previous = peers;
				peers = peers->next;
			}
			return;
		}
		current = current->next;
	}
}

void remove_leecher_from_file(char* key, struct leecher_t* leecher)
{
	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		if (current->file && strcmp(current->file->key, key) == 0)
		{
			struct leechers_list_t* leechers = current->file->leechers;
			struct leechers_list_t* previous = NULL;
			while (leechers != NULL)
			{
				if (strcmp(leechers->leecher->ip, leecher->ip) == 0 && leechers->leecher->port == leecher->port)
				{
					if (previous == NULL)
					{
						current->file->leechers = leechers->next;
					}
					else
					{
						previous->next = leechers->next;
					}
					free(leechers);
					return;
				}
				previous = leechers;
				leechers = leechers->next;
			}
			return;
		}
		current = current->next;
	}
}

struct peers_list_t* get_files_peers(struct files_t* file)
{
	return file->peers;
}

struct leechers_list_t* get_files_leechers(struct files_t* file)
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

struct peer_t* get_peer_from_info(char* ip, unsigned int port)
{
	struct peers_list_t* current = peers_list;
	while (current != NULL)
	{
		if (current->peer && strcmp(current->peer->ip, ip) == 0 && current->peer->port == port)
			return current->peer;
		current = current->next;
	}

	return NULL;
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
	if (get_file(files))
	{
		struct peers_list_t* peers = get_files_peers(get_file(files));
		while (files != NULL)
		{
			struct files_list_t* tmp = files;
			files = files->next;
			free(tmp);
		}
		return peers;
	}
	return NULL;
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

struct leecher_t* get_leecher_from_info(char* ip, unsigned int port)
{
	struct leechers_list_t* current = leechers_list;
	while (current != NULL)
	{
		if (current->leecher && strcmp(current->leecher->ip, ip) == 0 && current->leecher->port == port)
			return current->leecher;
		current = current->next;
	}

	return NULL;
}

struct leechers_list_t* get_next_leecher(struct leechers_list_t* leechers)
{
	return leechers->next;
}

struct leechers_list_t* get_leechers_having_file(char* key)
{
	struct files_list_t* files = get_files_by_criteria((int (*)(struct files_t*, void*))has_key, key);
	if (get_file(files))
	{
		struct leechers_list_t* leechers = get_files_leechers(get_file(files));
		while (files != NULL)
		{
			struct files_list_t* tmp = files;
			files = files->next;
			free(tmp);
		}
		return leechers;
	}
	return NULL;
}

char* get_leecher_ip(struct leecher_t* leecher)
{
	return leecher->ip;
}

unsigned int get_leecher_port(struct leecher_t* leecher)
{
	return leecher->port;
}

////////////////////////////////////////////////////////////////////////////////

void free_peers_list(struct peers_list_t* peers)
{
	struct peers_list_t* current = peers;
	while (current != NULL)
	{
		struct peers_list_t* next = current->next;
		if (current->peer) free(current->peer);
		free(current);
		current = next;
	}
	peers = NULL;
}

void free_leechers_list(struct leechers_list_t* leechers)
{
	struct leechers_list_t* current = leechers;
	while (current != NULL)
	{
		struct leechers_list_t* next = current->next;
		if (current->leecher) free(current->leecher);
		free(current);
		current = next;
	}
	leechers = NULL;
}

void free_files_list(struct files_list_t* files)
{
	while (files != NULL)
	{
		struct files_list_t* tmp = files;
		files = files->next;
		free(tmp);
	}
	files = NULL;
}

void free_lists()
{
	struct files_list_t* current = files_list;
	while (current != NULL)
	{
		struct files_list_t* next = current->next;
		// Free peers
		if (current->file)
		{
			struct peers_list_t* peers = current->file->peers;
			while (peers != NULL)
			{
				struct peers_list_t* tmp = peers;
				peers = peers->next;
				free(tmp);
				tmp = NULL;
			}
			current->file->peers = NULL;

			struct leechers_list_t* leechers = current->file->leechers;
			while (leechers != NULL)
			{
				struct leechers_list_t* tmp = leechers;
				leechers = leechers->next;
				free(tmp);
				tmp = NULL;
			}
			current->file->leechers = NULL;
		}
		free(current->file);
		free(current);
		current = next;
	}
	files_list = NULL;

	free_peers_list(peers_list);
	free_leechers_list(leechers_list);
}