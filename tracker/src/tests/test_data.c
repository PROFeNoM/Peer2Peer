#include <string.h>
#include "../include/data.h"
#include "test.h"


int test__add_peer()
{
	add_peer("0.0.0.0", 80);

	struct peers_list_t* peers_list = get_peers_list();
	while (get_next_peer(peers_list)) peers_list = get_next_peer(peers_list);
	struct peer_t* added_peer = get_peer(peers_list);

	ASSERT_ARRAY_EQUAL("0.0.0.0", get_peer_ip(added_peer), strlen(get_peer_ip(added_peer)))
	ASSERT_EQUAL(80, get_peer_port(added_peer))

	return 1;
}

int test__add_leecher()
{
	add_leecher("0.0.0.0", 80);

	struct leechers_list_t* leechers_list = get_leechers_list();
	while (get_next_leecher(leechers_list)) leechers_list = get_next_leecher(leechers_list);
	struct leecher_t* added_leecher = get_leecher(leechers_list);

	ASSERT_ARRAY_EQUAL("0.0.0.0", get_leecher_ip(added_leecher), strlen(get_leecher_ip(added_leecher)))
	ASSERT_EQUAL(80, get_leecher_port(added_leecher))

	return 1;
}

int test__add_file()
{
	add_file("test_file", 42, 3, "test_hash");

	struct files_list_t* files_list = get_files_list();
	while (get_next_file(files_list)) files_list = get_next_file(files_list);
	struct files_t* added_file = get_file(files_list);

	ASSERT_ARRAY_EQUAL("test_file", get_file_name(added_file), strlen(get_file_name(added_file)))
	ASSERT_EQUAL(42, get_file_size(added_file))
	ASSERT_EQUAL(3, get_file_piece_size(added_file))
	ASSERT_ARRAY_EQUAL("test_hash", get_file_key(added_file), strlen(get_file_key(added_file)))

	return 1;
}

int test__get_peer_from_info()
{
	struct peer_t* peer = get_peer_from_info("0.0.0.0", 80);

	ASSERT_NOT_NULL(peer)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_peer_ip(peer), strlen(get_peer_ip(peer)))
	ASSERT_EQUAL(80, get_peer_port(peer))

	return 1;
}

int test__get_leecher_from_info()
{
	struct leecher_t* leecher = get_leecher_from_info("0.0.0.0", 80);

	ASSERT_NOT_NULL(leecher)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_leecher_ip(leecher), strlen(get_leecher_ip(leecher)))
	ASSERT_EQUAL(80, get_leecher_port(leecher))

	return 1;
}

int test__add_peer_to_file()
{
	add_peer_to_file("test_hash", get_peer_from_info("0.0.0.0", 80));

	struct files_list_t* files_list = get_files_by_criteria((int (*)(struct files_t*, void*))criteria_filename, "test_file");

	ASSERT_NOT_NULL(files_list)
	struct peer_t* peer = get_peer(get_files_peers(get_file(files_list)));
	ASSERT_NOT_NULL(peer)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_peer_ip(peer), strlen(get_peer_ip(peer)))
	ASSERT_EQUAL(80, get_peer_port(peer))

	free_files_list(files_list);

	return 1;
}

int test__add_leecher_to_file()
{
	add_leecher_to_file("test_hash", get_leecher_from_info("0.0.0.0", 80));

	struct files_list_t* files_list = get_files_by_criteria((int (*)(struct files_t*, void*))criteria_filename, "test_file");

	ASSERT_NOT_NULL(files_list)
	struct leecher_t* leecher = get_leecher(get_files_leechers(get_file(files_list)));
	ASSERT_NOT_NULL(leecher)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_leecher_ip(leecher), strlen(get_leecher_ip(leecher)))
	ASSERT_EQUAL(80, get_leecher_port(leecher))

	free_files_list(files_list);

	return 1;
}

int test__get_peers_having_file()
{
	struct peers_list_t* peers_list = get_peers_having_file("test_hash");

	ASSERT_NOT_NULL(peers_list)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_peer_ip(get_peer(peers_list)), strlen(get_peer_ip(get_peer(peers_list))))
	ASSERT_EQUAL(80, get_peer_port(get_peer(peers_list)))
	ASSERT_NULL(get_next_peer(peers_list))

	return 1;
}

int test__get_leechers_having_file()
{
	struct leechers_list_t* leechers_list = get_leechers_having_file("test_hash");

	ASSERT_NOT_NULL(leechers_list)
	ASSERT_ARRAY_EQUAL("0.0.0.0", get_leecher_ip(get_leecher(leechers_list)), strlen(get_leecher_ip(get_leecher(leechers_list))))
	ASSERT_EQUAL(80, get_leecher_port(get_leecher(leechers_list)))
	ASSERT_NULL(get_next_leecher(leechers_list))

	return 1;
}

int test__remove_peer_from_file()
{
	struct peer_t* peer = get_peer_from_info("0.0.0.0", 80);

	remove_peer_from_file("test_hash", peer);

	struct peers_list_t* peers_list = get_peers_having_file("test_hash");

	ASSERT_NULL(peers_list)

	return 1;
}

int test__remove_leecher_from_file()
{
	struct leecher_t* leecher = get_leecher_from_info("0.0.0.0", 80);

	remove_leecher_from_file("test_hash", leecher);

	struct leechers_list_t* leechers_list = get_leechers_having_file("test_hash");

	ASSERT_NULL(leechers_list)

	return 1;
}


int test__add_multiple_peer_to_file()
{
	add_peer("0.0.0.1", 2322);
	add_peer_to_file("test_hash", get_peer_from_info("0.0.0.1", 2322));

	struct files_list_t* files_list = get_files_by_criteria((int (*)(struct files_t*, void*))criteria_filename, "test_file");

	ASSERT_NOT_NULL(files_list)
	struct peer_t* peer = get_peer(get_files_peers(get_file(files_list)));
	ASSERT_NOT_NULL(peer)
	ASSERT_ARRAY_EQUAL("0.0.0.1", get_peer_ip(peer), strlen(get_peer_ip(peer)))
	ASSERT_EQUAL(2322, get_peer_port(peer))

	free_files_list(files_list);

	add_peer("0.0.0.2", 2222);
	add_peer_to_file("test_hash", get_peer_from_info("0.0.0.2", 2222));

	files_list = get_files_by_criteria((int (*)(struct files_t*, void*))criteria_filename, "test_file");

	ASSERT_NOT_NULL(files_list)
	peer = get_peer(get_files_peers(get_file(files_list)));
	ASSERT_NOT_NULL(peer)
	ASSERT_ARRAY_EQUAL("0.0.0.2", get_peer_ip(peer), strlen(get_peer_ip(peer)))
	ASSERT_EQUAL(2222, get_peer_port(peer))

	free_files_list(files_list);

	return 1;
}

void test__data_functions()
{
	init_lists();

	TEST_FUNCTION(test__add_peer)
	TEST_FUNCTION(test__add_leecher)
	TEST_FUNCTION(test__add_file)

	TEST_FUNCTION(test__get_peer_from_info)
	TEST_FUNCTION(test__get_leecher_from_info)

	TEST_FUNCTION(test__add_peer_to_file)
	TEST_FUNCTION(test__add_leecher_to_file)

	TEST_FUNCTION(test__get_peers_having_file)
	TEST_FUNCTION(test__get_leechers_having_file)

	TEST_FUNCTION(test__remove_peer_from_file)
	TEST_FUNCTION(test__remove_leecher_from_file)

	TEST_FUNCTION(test__add_multiple_peer_to_file)

	free_lists();
}