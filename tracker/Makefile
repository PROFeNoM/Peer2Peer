INCLUDE_DIR	=	./src/include
BUILD_DIR	=	./build/tracker ./build/tests
INSTALL_DIR	=	./install
TESTS_DIR	=	./src/tests

TRACKER_SRC	=	$(wildcard ./src/*.c)
TESTS_SRC	=	$(wildcard ./src/tests/*.c) $(shell echo $(TRACKER_SRC) | sed 's/.\/src\/tracker.c//')

TRACKER_OBJ	=	$(TRACKER_SRC:./src/%.c=./build/tracker/%.o)

TRACKER_BIN	=	./build/tracker/tracker
TESTS_BIN	=	./build/tests/alltests
INSTALL_BIN	=	$(TRACKER_BIN:./build/tracker/%=$(INSTALL_DIR)/%) $(TESTS_BIN:./build/tests/%=$(INSTALL_DIR)/%)

DEP			=	$(TRACKER_OBJ:%.o=%.d)

CC			=	gcc
CFLAGS		=	-Wall -Wextra -std=c99
CPPFLAGS	=	-I${INCLUDE_DIR}
TESTSFLAGS	=	-g -O0 -fprofile-arcs -ftest-coverage
THREADFLAGS	=	-pthread
HASHFLAGS	=	-lcrypto -lssl
DEPFLAGS	= 	-MT $@ -MMD -MP -MF $(@D)/$*.d

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

build: BUILD_start $(BUILD_DIR) $(TRACKER_BIN) $(TESTS_BIN) BUILD_end

install: INSTALL_start build $(INSTALL_BIN) INSTALL_end

test: TESTS_start $(TESTS_BIN) TESTS_end

clean:
	rm -rf ./build $(INSTALL_BIN) *.gcno *.gcda

############################## HELPERS RULES ##############################

.PHONY: all tracker test clean %_start %_end

.SECONDARY: $(TRACKER_OBJ) $(TRACKER_BIN)

$(BUILD_DIR):
	@mkdir -p $@

%_start:
	@echo $(COLOR)$(BOLD)$(GREEN)$* START$(NOCOLOR)

%_end:
	@echo $(COLOR)$(BOLD)$(GREEN)$* DONE$(NOCOLOR)

%.o: %.c

$(DEP):
include $(wildcard $(DEP))

############################## BUILD RULES ################################

$(TRACKER_BIN): $(TRACKER_OBJ)
	@echo $(COLOR)$(ITALIC)$(PURPLE)"\tCompilation Server BINARY"$(NOCOLOR)
	@$(CC) $(CPPFLAGS) $(CFLAGS) $(TRACKER_OBJ) -o $@ $(THREADFLAGS) $(HASHFLAGS)

build/tracker/%.o: src/%.c
	@echo $(COLOR)$(ITALIC)$(CYAN)"\tCompilation Tracker file :	$*"$(NOCOLOR)
	@$(CC) $(DEPFLAGS) $(CPPFLAGS) $(CFLAGS) -c $< -o $@ $(THREADFLAGS) $(HASHFLAGS)

############################## INSTALL RULES ##############################

install/%: build/tracker/%
	@cp $< $@
install/%: build/tests/%
	@cp $< $@

############################## TESTS RULES ##############################

$(TESTS_BIN): $(BUILD_DIR)
	@echo $(COLOR)$(ITALIC)$(PURPLE)"\tCompilation AllTests BINARY"$(NOCOLOR)
	@$(CC) $(CFLAGS) -I./src/tests/ $(CPPFLAGS) $(TESTSFLAGS) $(TESTS_SRC) -o $@ $(THREADFLAGS) $(HASHFLAGS)