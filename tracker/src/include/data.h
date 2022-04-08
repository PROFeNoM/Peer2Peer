#ifndef _DATA_H
#define _DATA_H

#include <netinet/in.h>
#include <sys/queue.h>

#define MAX_NAME_LEN 100
#define MAX_KEY_LEN 100

struct peer_t {
	char ip[INET_ADDRSTRLEN];
	unsigned int port;
	int sockfd;
	TAILQ_ENTRY(peer_t) next_peer;
};

struct file_t
{
	char name[MAX_NAME_LEN];
	unsigned int size;
	unsigned int piece_size;
	char key[MAX_KEY_LEN];

	TAILQ_ENTRY(file_t) next_file;
	TAILQ_HEAD(seed, peer_t) seeders;
	TAILQ_HEAD(leech, peer_t) leechers;
};

TAILQ_HEAD(files, file_t) files_list;

void init_files_list();

/*
 * Tell if a file is in the list.
 */
int is_file_in_list(char *key);

/*
 * Add a file to the list.
 */
void add_file_to_list(char *name, unsigned int size, unsigned int piece_size, char *key);

/*
 * Add a seeder to the list of seeders of the file.
 * If the file does not exist, it is created.
 */
void add_seeder_to_file(char* name, unsigned int size, unsigned int piece_size, char* key, char* ip, unsigned int port, int sockfd);

/*
 * Add a leecher to the list of leechers of the file.
 * If the file does not exist, it is created.
 */
void add_leecher_to_file(char* key, char* ip, unsigned int port, int sockfd);

/*
 * Tell if the given peer is a seeder of the file.
 */
int is_seeder_of_file(char* key, char* ip, unsigned int port);

/*
 * Tell if the given peer is a leecher of the file.
 */
int is_leecher_of_file(char* key, char* ip, unsigned int port);

/*
 * Remove the given peer from the list of seeders of the file.
 */
int remove_seeder_from_file(char* key, char* ip, int sockfd);

/*
 * Remove the given peer from the list of leechers of the file.
 */
int remove_leecher_from_file(char* key, char* ip, int sockfd);

/*
 * Get the file with the given key.
 */
struct file_t* get_file(char* key);

/*
 * Get all files with a given name.
 */
struct file_t** get_files_with_name(char* name, unsigned int* nb_files);

/*
 * Get all files with a given size.
 */
struct file_t** get_files_with_size(unsigned int size, char operator, unsigned int* nb_files);

/*
 * Get all files with a given name and size.
 */
struct file_t** get_files_with_name_and_size(char* name, unsigned int size, char operator, unsigned int* nb_files);

void free_files_list();

#endif //_DATA_H
