JAVAC 	?= javac
JAVA 	?= java
PORT 	?= 8080

PACKAGE = peer
SRC_PACKAGE = $(PACKAGE).src.main
TEST_PACKAGE = $(PACKAGE).src.test

PEER	= $(SRC_PACKAGE).Main

SRC_DIR		= src/main
TST_DIR		= src/test
BUILD_DIR 	= build

JAVA_FILES			=	$(wildcard ./$(SRC_DIR)/*.java)
JAVA_TEST_FILES		=	$(wildcard ./$(TST_DIR)/*.java)
JAVA_TEST_CLASSES	=   $(addprefix $(TEST_PACKAGE)., $(shell find ./$(TST_DIR) -type f -name "Test*" -exec basename {} .java ';'))

COMPILE_OPTS = -d ./build -cp build
EXEC_OPTS = -ea -cp build

COLOR				=	"\033["
GREEN				=	";32m"
CYAN				=	";36m"
PURPLE				=	";35m"
GREY				=	"1;30m"
BOLD				=	";1"
ITALIC				=	";3"
NOCOLOR				=	"\033[0m"

############################## MAIN RULES ##############################

all: build

clean:
	rm -rf $(BUILD_DIR)

############################## HELPERS RULES ##############################

.PHONY: all clean %_start %_end

.SECONDARY: $(TRACKER_OBJ) $(TRACKER_BIN)


%_start:
	@echo $(COLOR)$(BOLD)$(GREEN)$* START$(NOCOLOR)

%_end:
	@echo $(COLOR)$(BOLD)$(GREEN)$* DONE$(NOCOLOR)

############################## BUILD RULES ################################

build: $(JAVA_FILES)
	@$(MAKE) $@_start
	mkdir -p $(BUILD_DIR)
	$(JAVAC) $(COMPILE_OPTS) $(JAVA_FILES)
	@$(MAKE) $@_end

build_tests: $(JAVA_TEST_FILES)
	@$(MAKE) $@_start
	mkdir -p $(BUILD_DIR)
	$(JAVAC) $(COMPILE_OPTS) $(JAVA_TEST_FILES)
	@$(MAKE) $@_end

############################## INSTALL RULES ##############################

peer: build
	$(JAVA) $(EXEC_OPTS) $(PEER)

############################## TESTS RULES ##############################

tests: build build_tests
	@$(MAKE) $@_start
	@$(JAVA) $(EXEC_OPTS) $(TEST_PACKAGE).AllTests $(JAVA_TEST_CLASSES)
	@$(MAKE) $@_end