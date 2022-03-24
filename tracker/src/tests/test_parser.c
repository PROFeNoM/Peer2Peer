#include "../include/parser.h"
#include "test.h"

int test__get_request_type_with_unknown_request()
{
	ASSERT_EQUAL(get_request_type(""), UNKNOWN);

	return 1;
}

void test__parser_functions()
{
	TEST_FUNCTION(test__get_request_type_with_unknown_request);
}