#include <string.h>
#include <stdlib.h>
#include "../include/data.h"
#include "test.h"


int test__is_file_in_list_without_file()
{
    init_lists();

    ASSERT_FALSE(is_file_in_list("random_key"))

    free_lists();

    return 1;
}

int test__is_file_in_list_with_file()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file2", 42, 1, "test_hash2");

    ASSERT_TRUE(is_file_in_list("test_hash"))
    ASSERT_TRUE(is_file_in_list("test_hash2"))

    free_lists();

    return 1;
}

int test__get_peer_port_without_peer()
{
    init_lists();

    ASSERT_EQUAL(-1, get_peer_port("192.16.17.232", 2))

    free_lists();

    return 1;
}


int test__get_peer_port_with_leecher()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");
    add_leecher_to_file("test_hash", "127.0.0.1", 2222, 4);
    add_leecher_to_file("test_hash", "127.0.0.2", 2222, 4);

    ASSERT_EQUAL(2222, get_peer_port("127.0.0.1", 4))
    ASSERT_EQUAL(2222, get_peer_port("127.0.0.2", 4))

    free_lists();

    return 1;
}



int test__get_peer_port_with_seeder()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");
    add_seeder_to_file("test_hash", 42, 1, "test_hash2", "127.0.0.1", 2222, 4);

    ASSERT_EQUAL(2222, get_peer_port("127.0.0.1", 4))
    ASSERT_TRUE(is_file_in_list("test_hash2"))

    free_lists();

    return 1;
}

int test__is_seeder_of_file_without_seeder()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");

    ASSERT_FALSE(is_seeder_of_file("test_hash", "218.0.0.1", 2222))

    free_lists();

    return 1;
}

int test__is_seeder_of_file_with_seeder()
{
    init_lists();

    add_seeder_to_file("test_file", 42, 1, "test_hash", "127.0.0.1", 2222, 4);

    ASSERT_TRUE(is_seeder_of_file("test_hash", "127.0.0.1", 2222))

    free_lists();

    return 1;
}

int test__is_leecher_of_file_without_leecher()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");

    ASSERT_FALSE(is_leecher_of_file("test_hash", "127.0.0.1", 2222))

    free_lists();

    return 1;
}

int test__is_leecher_of_file_with_leecher()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");
    add_leecher_to_file("test_hash", "127.0.0.1", 2222, 4);

    ASSERT_TRUE(is_leecher_of_file("test_hash", "127.0.0.1", 2222))

    free_lists();

    return 1;
}

int test__remove_seeder_from_file_without_seeder()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");

    remove_seeder_from_file("test_hash", "127.0.0.1", 4);

    free_lists();

    return 1;
}

int test__remove_seeder_from_file_with_seeder()
{
    init_lists();

    add_seeder_to_file("test_file", 42, 1, "test_hash", "127.0.0.1", 2222, 4);
    remove_seeder_from_file("test_hash", "127.0.0.1", 4);

    ASSERT_FALSE(is_seeder_of_file("test_hash", "127.0.0.1", 2222))

    free_lists();

    return 1;
}

int test__remove_leecher_from_file_without_leecher()
{
    init_lists();

    //add_file_to_list("test_file", 42, 1, "test_hash");

    remove_leecher_from_file("test_hash", "127.0.0.1", 4);

    free_lists();

    return 1;
}

int test__remove_leecher_from_file_with_leecher()
{
    init_lists();

    add_file_to_list("test_file", 42, 1, "test_hash");
    add_leecher_to_file("test_hash", "127.0.0.1", 2222, 4);
    remove_seeder_from_file("test_hash", "127.0.0.1", 4);

    free_lists();

    return 1;
}

int test__get_files_with_name_without_file()
{
    init_lists();

    unsigned int nb_files = 0;
    struct file_t** files = get_files_with_name("test_file", &nb_files);
    ASSERT_EQUAL(0, nb_files)

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_name_with_files()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 42, 1, "test_hash23");
    struct file_t** files = get_files_with_name("test_file", &nb_files);

    ASSERT_EQUAL(2, nb_files)
    ASSERT_EQUAL(get_file("test_hash"), files[0])
    ASSERT_EQUAL(get_file("test_hash2"), files[1])

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_size_equal()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 42, 1, "test_hash23");
    add_file_to_list("test_file2", 22, 1, "test_hash4");
    struct file_t** files = get_files_with_size(42, '=', &nb_files);

    ASSERT_EQUAL(3, nb_files)
    ASSERT_EQUAL(get_file("test_hash"), files[0])
    ASSERT_EQUAL(get_file("test_hash2"), files[1])
    ASSERT_EQUAL(get_file("test_hash23"), files[2])

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_size_superior()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 43, 1, "test_hash23");
    add_file_to_list("test_file2", 22, 1, "test_hash4");
    struct file_t** files = get_files_with_size(42, '>', &nb_files);

    ASSERT_EQUAL(1, nb_files)
    ASSERT_EQUAL(get_file("test_hash23"), files[0])

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_size_inferior()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 43, 1, "test_hash23");
    add_file_to_list("test_file2", 22, 1, "test_hash4");
    struct file_t** files = get_files_with_size(42, '<', &nb_files);

    ASSERT_EQUAL(1, nb_files)
    ASSERT_EQUAL(get_file("test_hash4"), files[0])

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_name_and_size_equal()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 42, 1, "test_hash23");

    struct file_t** files = get_files_with_name_and_size("test_file", 42, '=', &nb_files);

    ASSERT_EQUAL(2, nb_files)
    ASSERT_EQUAL(get_file("test_hash"), files[0])
    ASSERT_EQUAL(get_file("test_hash2"), files[1])

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_name_and_size_superior()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 42, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 43, 1, "test_hash23");

    struct file_t** files = get_files_with_name_and_size("test_file", 42, '>', &nb_files);

    ASSERT_EQUAL(0, nb_files)

    free(files);
    free_lists();

    return 1;
}

int test__get_files_with_name_and_size_inferior()
{
    init_lists();

    unsigned int nb_files = 0;
    add_file_to_list("test_file", 41, 1, "test_hash");
    add_file_to_list("test_file", 42, 1, "test_hash2");
    add_file_to_list("test_file2", 43, 1, "test_hash23");

    struct file_t** files = get_files_with_name_and_size("test_file", 42, '<', &nb_files);

    ASSERT_EQUAL(1, nb_files)
    ASSERT_EQUAL(get_file("test_hash"), files[0])

    free(files);
    free_lists();

    return 1;
}

void test__data_functions()
{
    TEST_FUNCTION(test__is_file_in_list_without_file)
    TEST_FUNCTION(test__is_file_in_list_with_file)

    TEST_FUNCTION(test__get_peer_port_without_peer)
    TEST_FUNCTION(test__get_peer_port_with_leecher)
    TEST_FUNCTION(test__get_peer_port_with_seeder)

    TEST_FUNCTION(test__is_seeder_of_file_without_seeder)
    TEST_FUNCTION(test__is_seeder_of_file_with_seeder)

    TEST_FUNCTION(test__is_leecher_of_file_without_leecher)
    TEST_FUNCTION(test__is_leecher_of_file_with_leecher)

    TEST_FUNCTION(test__remove_seeder_from_file_without_seeder)
    TEST_FUNCTION(test__remove_seeder_from_file_with_seeder)

    TEST_FUNCTION(test__remove_leecher_from_file_without_leecher)
    TEST_FUNCTION(test__remove_leecher_from_file_with_leecher)

    TEST_FUNCTION(test__get_files_with_name_without_file)
    TEST_FUNCTION(test__get_files_with_name_with_files)

    TEST_FUNCTION(test__get_files_with_size_equal)
    TEST_FUNCTION(test__get_files_with_size_superior)
    TEST_FUNCTION(test__get_files_with_size_inferior)

    TEST_FUNCTION(test__get_files_with_name_and_size_equal)
    TEST_FUNCTION(test__get_files_with_name_and_size_superior)
    TEST_FUNCTION(test__get_files_with_name_and_size_inferior)
}