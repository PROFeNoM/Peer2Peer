#include <string.h>
#include <stdlib.h>
#include "../include/parser.h"
#include "../include/data.h"
#include "test.h"

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
    init_lists();

	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	char* actual = parse_getfile(request);
	ASSERT_ARRAY_EQUAL("peers 8905e92afeb80fc7722ec89eb0bf0966 []", actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 []"))

	free(actual);

    free_lists();

	return 1;
}

int test__parse_getfile_with_file_and_peer()
{
    init_lists();
    add_seeder_to_file("file_a.dat", 42, 1, "8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8080, 4);

    char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

	char* actual = parse_getfile(request);

	ASSERT_ARRAY_EQUAL(
			"peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8080]",
			actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8080]")
	)

	free(actual);
    free_lists();

	return 1;
}


int test__parse_getfile_with_multiple_peers()
{
    init_lists();

	char request[] = "getfile 8905e92afeb80fc7722ec89eb0bf0966";

    add_seeder_to_file("file_a.dat", 42, 1, "8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905, 4);
    add_seeder_to_file("file_a.dat", 42, 1, "8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.2", 8905, 5);

	char* actual = parse_getfile(request);
	ASSERT_ARRAY_EQUAL(
			"peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8905 127.0.0.2:8905]",
			actual,
			strlen("peers 8905e92afeb80fc7722ec89eb0bf0966 [127.0.0.1:8905 127.0.0.2:8905]")
	)

	free(actual);
    free_lists();

	return 1;
}


int test__parse_look_without_file_two_conditions()
{
    init_lists();
	char request[] = "look [filename=\"file_a.dat\" filesize>\"1048576\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list []", actual, strlen("list []"));

	free(actual);
    free_lists();
	return 1;
}

int test__parse_look_without_file_one_condition()
{
    init_lists();
	char request[] = "look [filesize>\"1048576\"]";

	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list []", actual, strlen("list []"));

	free(actual);
    free_lists();
	return 1;
}

int test__parse_look_with_file_two_conditions()
{
    init_lists();
	char request[] = "look [filename=\"file_a.dat\" filesize>\"0\"]";
    add_file_to_list("file_a.dat", 1048576, 42, "8905e92afeb80fc7722ec89eb0bf0966");
	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]", actual,
			strlen("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]"))

	free(actual);
    free_lists();

	return 1;
}

int test__parse_look_with_file_one_condition()
{
    init_lists();
	char request[] = "look [filename=\"file_a.dat\"]";
    add_file_to_list("file_a.dat", 1048576, 42, "8905e92afeb80fc7722ec89eb0bf0966");
	char* actual = parse_look(request);
	ASSERT_ARRAY_EQUAL("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]", actual,
			strlen("list [file_a.dat 1048576 42 8905e92afeb80fc7722ec89eb0bf0966]"))

	free(actual);
    free_lists();

	return 1;
}

int test__parse_update_without_keys()
{
    init_lists();
	char request[] = "update seed [] leech []";

    add_seeder_to_file("file_a.dat", 42, 1, "8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905, 4);
    add_leecher_to_file("8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905, 4);

	char* actual = parse_update(request, "127.0.0.1", 4);
	ASSERT_ARRAY_EQUAL("ok", actual, strlen("ok"))

    ASSERT_FALSE(is_seeder_of_file("8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905))
    ASSERT_FALSE(is_leecher_of_file("8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905))

    free_lists();

	return 1;
}

int test__parse_update_with_keys()
{
    init_lists();
    add_file_to_list("file_a.dat", 1048576, 42, "8905e92afeb80fc7722ec89eb0bf0966");
    add_file_to_list("file_b.dat", 1048576, 42, "8905e92a");
	char request[] = "update seed [8905e92afeb80fc7722ec89eb0bf0966 8905e92a] leech [8905e92afeb80fc7722ec89eb0bf0966]";


    add_seeder_to_file("file_a.dat", 1048576, 42, "8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905, 4);

	char* actual = parse_update(request, "127.0.0.1", 4);
	ASSERT_ARRAY_EQUAL("ok", actual, strlen("ok"))

    ASSERT_TRUE(is_seeder_of_file("8905e92afeb80fc7722ec89eb0bf0966", "127.0.0.1", 8905))
    ASSERT_TRUE(is_seeder_of_file("8905e92a", "127.0.0.1", 8905))

    free_lists();

	return 1;
}

int test__parse_announce()
{
	init_lists();

	char request[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech [8905e92afeb80fc7722ec89eb0bf0966]";

	char * actual = parse_announce(request, "138.90.17.75", 5);
	ASSERT_ARRAY_EQUAL("ok", actual, strlen("ok"))

	ASSERT_TRUE(is_file_in_list("8905e92afeb80fc7722ec89eb0bf0966"))
	ASSERT_TRUE(is_file_in_list("330a57722ec8b0bf09669a2b35f88e9e"))

	ASSERT_EQUAL(2222, get_peer_port("138.90.17.75", 5))

	ASSERT_TRUE(is_seeder_of_file("8905e92afeb80fc7722ec89eb0bf0966", "138.90.17.75", 2222))
	ASSERT_TRUE(is_seeder_of_file("330a57722ec8b0bf09669a2b35f88e9e", "138.90.17.75", 2222))
	ASSERT_TRUE(is_leecher_of_file("8905e92afeb80fc7722ec89eb0bf0966", "138.90.17.75", 2222))

	free_lists();

	return 1;
}


void test__parser_functions()
{
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
	TEST_FUNCTION(test__parse_getfile_with_file_and_peer)
    TEST_FUNCTION(test__parse_getfile_with_multiple_peers)

	//////// test for parse_look ////////
	TEST_FUNCTION(test__parse_look_without_file_two_conditions)
	TEST_FUNCTION(test__parse_look_without_file_one_condition)
	TEST_FUNCTION(test__parse_look_with_file_two_conditions)
	TEST_FUNCTION(test__parse_look_with_file_one_condition)

	//////// test for parse_update ////////
	TEST_FUNCTION(test__parse_update_without_keys)
	TEST_FUNCTION(test__parse_update_with_keys)

	//////// test for parse_announce ////////
	TEST_FUNCTION(test__parse_announce)
}