#include "../include/parser.h"
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

int test__get_request_type_valid_update_request()
{
    char request[] = "update seed [] leech [8905e92afeb80fc7722ec89eb0bf0966]";

    enum REQUEST_T actual = get_request_type(request);
    ASSERT_EQUAL(UPDATE, actual)

    return 1;
}

void test__parser_functions()
{
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

    TEST_FUNCTION(test__get_request_type_valid_getfile_request)
    TEST_FUNCTION(test__get_request_type_invalid_getfile_request)

    TEST_FUNCTION(test__get_request_type_valid_update_request)
}