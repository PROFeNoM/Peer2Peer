#include <string.h>
#include <stdlib.h>
#include "../include/parser.h"
#include "../include/data.h"
#include "test.h"
/*
int test__get_request_type_with_unknown_request()
{
	ASSERT_EQUAL(UNKNOWN, get_request_type(""))

	return 1;
}

int test__get_request_type_with_valid_seed_only_announce_request()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(ANNOUNCE, actual)

	return 1;
}

int test__get_request_type_with_valid_seed_only_announce_request_one_file()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(ANNOUNCE, actual)

	return 1;
}

int test__get_request_type_with_invalid_seed_only_announce_request_incomplete()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_with_valid_seed_leech_announce_request()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech [8905e92afeb80fc7722ec89eb0bf0966 330a57722ec8b0bf09669a2b35f88e9e]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(ANNOUNCE, actual)

	return 1;
}

int test__get_request_type_with_valid_seed_empty_leech_array_announce_request()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech []";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(ANNOUNCE, actual)

	return 1;
}

int test__get_request_type_with_invalid_seed_empty_leech_announce_request()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech ";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_with_invalid_seed_leech_announce_request_double_spaces_between_files()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966  file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech ";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_with_invalid_seed_leech_announce_request_double_spaces_between_fields()
{
	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024  8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech ";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_with_valid_leech_only_announce_request()
{
	char request[] = "announce listen 2222 leech [8905e92afeb80fc7722ec89eb0bf0966 330a57722ec8b0bf09669a2b35f88e9e]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(ANNOUNCE, actual)

	return 1;
}

int test__get_request_type_valid_look_request()
{
	char request[] = "look [filename=”file_a.dat” filesize>”1048576”]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(LOOK, actual)

	return 1;
}

int test__get_request_type_invalid_look_request()
{
	char request[] = "look (filename=”file_a.dat” filesize>”1048576”)";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

/*
int test__get_request_type_invalid_look_request_2()
{
    char request[] = "look [filename=”file_a.dat” filesize>”1048576]";

    enum REQUEST_T actual = get_request_type(request);
    ASSERT_EQUAL(INVALID, actual)

    return 1;
}
*/
/*
int test__get_request_type_valid_getfile_request()
{
	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(GETFILE, actual)

	return 1;
}

int test__get_request_type_invalid_getfile_request()
{
	char request[] = "getfile [8905e92afeb80fc7722ec89eb0bf0966]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_invalid_getfile_request_2()
{
	char request[] = "getfile 8 905e92afeb80fc7722ec89eb0bf0966";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__get_request_type_valid_update_request()
{
	char request[] = "update seed [] leech [8905e92afeb80fc7722ec89eb0bf0966]";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(UPDATE, actual)

	return 1;
}

int test__get_request_type_invalid_update_request()
{
	char request[] = "update seed [] leech (8905e92afeb80fc7722ec89eb0bf0966)";

	enum REQUEST_T actual = get_request_type(request);
	ASSERT_EQUAL(INVALID, actual)

	return 1;
}

int test__parse_getfile_without_file()
{
	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	char* actual = parse_getfile(request);
	ASSERT_ARRAY_EQUAL("peers 8905e92afeb80fc7722ec89eb0bf0966 []", actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 []"));

	free(actual);

	return 1;
}

int test__parse_getfile_with_file_and_peer()
{
	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	char* actual = parse_getfile(request);
	ASSERT_ARRAY_EQUAL(
			"peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8905]",
			actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8905]")
	)

	free(actual);

	return 1;
}

int test__parse_getfile_with_multiple_peers()
{
	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	char* actual = parse_getfile(request);
	ASSERT_ARRAY_EQUAL(
			"peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.2:8905 127.0.0.1:8905]",
			actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.2:8905 127.0.0.1:8905]")
	)

	free(actual);

	return 1;
}

int test__parse_look_without_file_two_conditions()
{
	char request[] = "look [filename=\"file_a.dat\" filesize>\"1048576\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list []", actual, strlen("list []"));

	free(actual);

	return 1;
}

int test__parse_look_without_file_one_condition()
{
	char request[] = "look [filesize>\"1048576\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list []", actual, strlen("list []"));

	free(actual);

	return 1;
}

int test__parse_look_with_file_two_conditions()
{
	char request[] = "look [filename=\"file_a.dat\" filesize>\"0\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]", actual,
			strlen("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]"));

	free(actual);

	return 1;
}

int test__parse_look_with_file_one_condition()
{
	char request[] = "look [filename=\"file_a.dat\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]", actual,
			strlen("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]"));

	free(actual);

	return 1;
}

int test__parse_update_without_keys()
{
	char request[] = "update seed [] leech []";

	char* actual = parse_update(request, "127.0.0.1", 8905);
	ASSERT_ARRAY_EQUAL("ok", actual, strlen("ok"))

	struct peer_t* peer = get_peer_from_info("127.0.0.1", 8905);
	struct leecher_t* leecher = get_leecher_from_info("127.0.0.1", 8905);

	int found_peer = 0;
	struct peers_list_t* peers = get_peers_having_file("8905e92afeb80fc7722ec89eb0bf0966");
	while (peers != NULL)
	{
		struct peer_t* current_peer = get_peer(peers);
		if (current_peer == peer)
		{
			found_peer = 1;
			break;
		}
		peers = get_next_peer(peers);
	}
	ASSERT_EQUAL(0, found_peer)

	int found_leecher = 0;
	struct leechers_list_t* leechers = get_leechers_having_file("8905e92afeb80fc7722ec89eb0bf0966");
	while (leechers != NULL)
	{
		struct leecher_t* current_leecher = get_leecher(leechers);
		if (current_leecher == leecher)
		{
			found_leecher = 1;
			break;
		}
		leechers = get_next_leecher(leechers);
	}
	ASSERT_EQUAL(0, found_leecher)

	return 1;
}

int test__parse_update_with_keys()
{
	char request[] = "update seed [8905e92afeb80fc7722ec89eb0bf0966 8905e92a] leech [8905e92afeb80fc7722ec89eb0bf0966]";

	char* actual = parse_update(request, "127.0.0.1", 8905);
	ASSERT_ARRAY_EQUAL("ok", actual, strlen("ok"))

	struct peer_t* peer = get_peer_from_info("127.0.0.1", 8905);
	struct leecher_t* leecher = get_leecher_from_info("127.0.0.1", 8905);

	int found_peer = 0;
	struct peers_list_t* peers = get_peers_having_file("8905e92afeb80fc7722ec89eb0bf0966");
	while (peers != NULL)
	{
		struct peer_t* current_peer = get_peer(peers);
		if (current_peer == peer)
		{
			found_peer = 1;
			break;
		}
		peers = get_next_peer(peers);
	}
	ASSERT_EQUAL(1, found_peer)

	int found_leecher = 0;
	struct leechers_list_t* leechers = get_leechers_having_file("8905e92afeb80fc7722ec89eb0bf0966");
	while (leechers != NULL)
	{
		struct leecher_t* current_leecher = get_leecher(leechers);
		if (current_leecher == leecher)
		{
			found_leecher = 1;
			break;
		}
		leechers = get_next_leecher(leechers);
	}
	ASSERT_EQUAL(1, found_leecher)

	return 1;
}
*/
void test__parser_functions()
{
	/*
	init_lists();

	//////// test for get_request_type ////////

	TEST_FUNCTION(test__get_request_type_with_unknown_request)

	TEST_FUNCTION(test__get_request_type_with_valid_seed_only_announce_request)
	TEST_FUNCTION(test__get_request_type_with_valid_seed_only_announce_request_one_file)
	TEST_FUNCTION(test__get_request_type_with_invalid_seed_only_announce_request_incomplete)
	TEST_FUNCTION(test__get_request_type_with_valid_seed_leech_announce_request)
	TEST_FUNCTION(test__get_request_type_with_valid_seed_empty_leech_array_announce_request)
	TEST_FUNCTION(test__get_request_type_with_invalid_seed_empty_leech_announce_request)
	TEST_FUNCTION(test__get_request_type_with_invalid_seed_leech_announce_request_double_spaces_between_files)
	TEST_FUNCTION(test__get_request_type_with_invalid_seed_leech_announce_request_double_spaces_between_fields)
	TEST_FUNCTION(test__get_request_type_with_valid_leech_only_announce_request)

	TEST_FUNCTION(test__get_request_type_valid_look_request)
	TEST_FUNCTION(test__get_request_type_invalid_look_request)
	//TEST_FUNCTION(test__get_request_type_invalid_look_request_2)

	TEST_FUNCTION(test__get_request_type_valid_getfile_request)
	TEST_FUNCTION(test__get_request_type_invalid_getfile_request)
	TEST_FUNCTION(test__get_request_type_invalid_getfile_request_2)

	TEST_FUNCTION(test__get_request_type_valid_update_request)
	TEST_FUNCTION(test__get_request_type_invalid_update_request)


	//////// test for parse_getfile ////////

	TEST_FUNCTION(test__parse_getfile_without_file)
	add_peer("127.0.0.1", 8905);
	add_file("file_a.dat", 1048576, 42, "8905e92afeb80fc7722ec89eb0bf0966");
	add_peer_to_file("8905e92afeb80fc7722ec89eb0bf0966", get_peer_from_info("127.0.0.1", 8905));
	TEST_FUNCTION(test__parse_getfile_with_file_and_peer)
	add_peer("127.0.0.2", 8905);
	add_peer_to_file("8905e92afeb80fc7722ec89eb0bf0966", get_peer_from_info("127.0.0.2", 8905));
	TEST_FUNCTION(test__parse_getfile_with_multiple_peers)

	//////// test for parse_look ////////
	TEST_FUNCTION(test__parse_look_without_file_two_conditions)
	TEST_FUNCTION(test__parse_look_without_file_one_condition)
	TEST_FUNCTION(test__parse_look_with_file_two_conditions)
	TEST_FUNCTION(test__parse_look_with_file_one_condition)

	//////// test for parse_update ////////
	TEST_FUNCTION(test__parse_update_without_keys)
	TEST_FUNCTION(test__parse_update_with_keys)

	free_lists();
	*/
}