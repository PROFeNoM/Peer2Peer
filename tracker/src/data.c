#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "include/data.h"

void init_lists()
{
	printf("[LOG] Initializing files list...\n");
	TAILQ_INIT(&files_list);
	printf("[LOG] Files list initialized.\n");

	printf("[LOG] Initializing peers list...\n");
	TAILQ_INIT(&peers_list);
	printf("[LOG] Peers list initialized.\n");
}

int is_file_in_list(char* key)
{
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		if (strcmp(file->key, key) == 0)
		{
			return 1;
		}
	}
	return 0;
}

int is_peer_in_list(char* ip, int sockfd)
{
	for (struct peer_t* peer = peers_list.tqh_first; peer != NULL; peer = peer->entry.tqe_next)
	{
		if (strcmp(peer->ip, ip) == 0 && peer->sockfd == sockfd)
		{
			printf("[LOG] Peer %s:%d already in list.\n", ip, sockfd);
			return 1;
		}
	}
	return 0;
}

int get_peer_port(char* ip, int sockfd)
{
	for (struct peer_t* peer = peers_list.tqh_first; peer != NULL; peer = peer->entry.tqe_next)
	{
		if (strcmp(peer->ip, ip) == 0 && peer->sockfd == sockfd)
		{
			return (int)peer->port;
		}
	}
	return -1;
}

void add_peer_to_list(char* ip, unsigned int port, int sockfd)
{
	struct peer_t* peer = malloc(sizeof(struct peer_t));
	strcpy(peer->ip, ip);
	peer->port = port;
	peer->sockfd = sockfd;

	// Add the peer to the list
	TAILQ_INSERT_TAIL(&peers_list, peer, entry);
}

void add_file_to_list(char* name, unsigned int size, unsigned int piece_size, char* key)
{
	struct file_t* file = malloc(sizeof(struct file_t));
	strcpy(file->key, key);
	strcpy(file->name, name);
	file->size = size;
	file->piece_size = piece_size;

	// Add the file to the list
	TAILQ_INSERT_TAIL(&files_list, file, next_file);
	TAILQ_INIT(&file->seeders);
	TAILQ_INIT(&file->leechers);
}

void add_seeder_to_file(char* name, unsigned int size, unsigned int piece_size, char* key, char* ip,
		unsigned int port, int sockfd)
{
	if (!is_peer_in_list(ip, sockfd))
	{
		printf("[LOG] Adding peer %s:%d to list.\n", ip, sockfd);
		add_peer_to_list(ip, port, sockfd);
	}

	if (!is_file_in_list(key))
	{
		printf("[LOG] Adding file to list...\n");
		add_file_to_list(name, size, piece_size, key);
	}

	struct file_t* file = get_file(key);

	// Add the seeder to the file
	if (is_seeder_of_file(key, ip, port))
	{
		// The seeder is already in the list
		return;
	}
	else
	{
		printf("[LOG] Adding seeder %s %d %d to file %s...\n", ip, port, sockfd, key);
		struct peer_t* seeder = malloc(sizeof(struct peer_t));
		strcpy(seeder->ip, ip);
		seeder->port = port;
		seeder->sockfd = sockfd;
		TAILQ_INSERT_TAIL(&file->seeders, seeder, next_peer);
	}
}

void add_leecher_to_file(char* key, char* ip, unsigned int port, int sockfd)
{
	if (!is_peer_in_list(ip, sockfd))
	{
		printf("[LOG] Adding peer %s:%d to list.\n", ip, sockfd);
		add_peer_to_list(ip, port, sockfd);
	}

	if (!is_file_in_list(key))
	{
		return;
	}

	struct file_t* file = get_file(key);

	// Add the leecher to the file
	if (is_leecher_of_file(key, ip, port))
	{
		// The leecher is already in the list
		return;
	}
	else
	{
		struct peer_t* leecher = malloc(sizeof(struct peer_t));
		strcpy(leecher->ip, ip);
		leecher->port = port;
		leecher->sockfd = sockfd;
		TAILQ_INSERT_TAIL(&file->leechers, leecher, next_peer);
	}
}

int is_seeder_of_file(char* key, char* ip, unsigned int port)
{
	struct file_t* file;
	if (!is_file_in_list(key))
	{
		return 0;
	}
	else
	{
		// Get the file from the list
		file = get_file(key);
	}

	if (TAILQ_EMPTY(&file->seeders))
	{
		printf("[LOG] The list is empty.\n");
		return 0;
	}

	// Check if the seeder is in the list
	struct peer_t* seeder;
	TAILQ_FOREACH(seeder, &file->seeders, next_peer)
	{
		if (strcmp(seeder->ip, ip) == 0 && seeder->port == port)
		{
			return 1;
		}
	}
	return 0;
}

int is_leecher_of_file(char* key, char* ip, unsigned int port)
{
	struct file_t* file;
	if (!is_file_in_list(key))
	{
		return 0;
	}
	else
	{
		// Get the file from the list
		file = get_file(key);
	}

	if (TAILQ_EMPTY(&file->leechers)) return 0;

	// Check if the leecher is in the list
	struct peer_t* leecher;
	TAILQ_FOREACH(leecher, &file->leechers, next_peer)
	{
		if (strcmp(leecher->ip, ip) == 0 && leecher->port == port)
		{
			return 1;
		}
	}
	return 0;
}

void remove_seeder_from_file(char* key, char* ip, int sockfd)
{
	struct file_t* file;
	if (!is_file_in_list(key))
	{
		return;
	}
	else
	{
		// Get the file from the list
		file = get_file(key);
	}

	// Remove the seeder from the list
	struct peer_t* seeder;
	TAILQ_FOREACH(seeder, &file->seeders, next_peer)
	{
		printf("[LOG] Comparing ip %s with %s, and socket %d with %d\n", seeder->ip, ip, seeder->sockfd, sockfd);
		if (strcmp(seeder->ip, ip) == 0 && seeder->sockfd == sockfd)
		{
			printf("[LOG] Removing seeder %s %d %d from file %s\n", seeder->ip, seeder->port, seeder->sockfd, key);
			TAILQ_REMOVE(&file->seeders, seeder, next_peer);
			free(seeder);
		}
	}
}

void remove_leecher_from_file(char* key, char* ip, int sockfd)
{
	struct file_t* file;
	if (!is_file_in_list(key))
	{
		return;
	}
	else
	{
		// Get the file from the list
		file = get_file(key);
	}

	// Remove the leecher from the list
	struct peer_t* leecher;
	TAILQ_FOREACH(leecher, &file->leechers, next_peer)
	{
		printf("[LOG] Comparing ip %s with %s, and socket %d with %d\n", leecher->ip, ip, leecher->sockfd, sockfd);
		if (strcmp(leecher->ip, ip) == 0 && leecher->sockfd == sockfd)
		{
			printf("[LOG] Removing leecher %s %d %d from file %s\n", leecher->ip, leecher->port, leecher->sockfd, key);
			TAILQ_REMOVE(&file->leechers, leecher, next_peer);
		}
	}
}

struct file_t* get_file(char* key)
{
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		if (strcmp(file->key, key) == 0)
			return file;
	}
	return NULL;
}

int count_files_with_name(char* name)
{
	int count = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		printf("[LOG] [Counting files with name] File name %s and comparing with %s => %s\n", file->name, name,
				strcmp(file->name, name) == 0 ? "yes" : "no");
		count += strcmp(file->name, name) == 0;
	}
	return count;
}

struct file_t** get_files_with_name(char* name, unsigned int* nb_files)
{
	// Allocate the right amount of memory
	*nb_files = count_files_with_name(name);
	struct file_t** files = malloc(sizeof(struct file_t*) * (*nb_files));

	// Add corresponding files to the pointer of pointer
	int files_idx = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		if (strcmp(file->name, name) == 0)
			files[files_idx++] = file;
	}

	return files;
}

int count_files_with_size(unsigned int size, char operator)
{
	int count = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		switch (operator)
		{
		case '=':
			count += file->size == size;
			break;
		case '>':
			count += file->size > size;
			break;
		case '<':
			count += file->size < size;
			break;
		default:
			return 0;
		}
	}

	return count;
}

struct file_t** get_files_with_size(unsigned int size, char operator, unsigned int* nb_files)
{
	// Allocate the right amount of memory
	*nb_files = count_files_with_size(size, operator);
	if (*nb_files == 0) return NULL;
	struct file_t** files = malloc(sizeof(struct file_t*) * (*nb_files));

	// Add corresponding files to the pointer of pointer
	int files_idx = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		switch (operator)
		{
		case '=':
			if (file->size == size) files[files_idx++] = file;
			break;
		case '>':
			if (file->size > size) files[files_idx++] = file;
			break;
		case '<':
			if (file->size < size) files[files_idx++] = file;
			break;
		default:
			break;
		}
	}

	return files;
}

int count_files_with_name_and_size(char* name, unsigned int size, char operator)
{
	int count = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		switch (operator)
		{
		case '=':
			count += file->size == size && (strcmp(file->name, name) == 0);
			break;
		case '>':
			count += file->size > size && (strcmp(file->name, name) == 0);
			break;
		case '<':
			count += file->size < size && (strcmp(file->name, name) == 0);
			break;
		default:
			return 0;
		}
	}

	return count;
}

struct file_t** get_files_with_name_and_size(char* name, unsigned int size, char operator, unsigned int* nb_files)
{
	// Allocate the right amount of memory
	*nb_files = count_files_with_name_and_size(name, size, operator);
	if (*nb_files == 0) return NULL;
	struct file_t** files = malloc(sizeof(struct file_t*) * (*nb_files));

	// Add to the list
	int files_idx = 0;
	for (struct file_t* file = files_list.tqh_first; file != NULL; file = file->next_file.tqe_next)
	{
		switch (operator)
		{
		case '=':
			if (file->size == size && (strcmp(file->name, name) == 0)) files[files_idx++] = file;
			break;
		case '>':
			if (file->size > size && (strcmp(file->name, name) == 0)) files[files_idx++] = file;
			break;
		case '<':
			if (file->size < size && (strcmp(file->name, name) == 0)) files[files_idx++] = file;
			break;
		default:
			break;
		}
	}

	return files;
}

void free_lists()
{
	struct file_t* file = files_list.tqh_first;
	while (file != NULL)
	{
		struct file_t* next_file = file->next_file.tqe_next;
		// Free the seeders list
		struct peer_t* seed = TAILQ_FIRST(&file->seeders), * next_seed;
		while (seed != NULL)
		{
			next_seed = TAILQ_NEXT(seed, next_peer);
			free(seed);
			seed = next_seed;
		}
		// Free the leechers list
		struct peer_t* leech = TAILQ_FIRST(&file->leechers), * next_leech;
		while (leech != NULL)
		{
			next_leech = TAILQ_NEXT(leech, next_peer);
			free(leech);
			leech = next_leech;
		}
		free(file);
		file = next_file;
	}

	// Free the peers list
	struct peer_t* peer = TAILQ_FIRST(&peers_list);
	while (peer != NULL)
	{
		struct peer_t* next_peer = TAILQ_NEXT(peer, next_peer);
		free(peer);
		peer = next_peer;
	}

}