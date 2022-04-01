JAVAC 	?= javac
JAVA 	?= java
PORT 	?= 8080

PEER	= TestPeer
TRACKER = TestTracker

SRC_DIR		= ./src
BUILD_DIR 	= ./build

JAVA_FILES	=	$(wildcard ./src/*.java)

COMPILE_OPTS = -d ./build -cp build
EXEC_OPTS = -cp build

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

clean: CLEAN_start
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
	$(MAKE) $@_start
	mkdir -p $(BUILD_DIR)
	$(JAVAC) $(COMPILE_OPTS) $(JAVA_FILES)
	$(MAKE) $@_end

############################## INSTALL RULES ##############################

peer: build
	$(JAVA) $(EXEC_OPTS) $(PEER) $(PORT)

tracker: build
	$(JAVA) $(EXEC_OPTS) $(TRACKER) $(PORT)


############################## TESTS RULES ##############################
