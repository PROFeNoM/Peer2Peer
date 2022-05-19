JAVAC 	?= javac
JAVA 	?= java
PORT 	?= 8080

PACKAGE = peer
SRC_PACKAGE = $(PACKAGE)

PEER	= $(SRC_PACKAGE).Main

SRC_DIR		= src/peer
BUILD_DIR 	= build

JAVA_FILES			=	$(shell find ./$(SRC_DIR) -type f -name "*.java")

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

############################## INSTALL RULES ##############################

peer: build
	$(JAVA) $(EXEC_OPTS) $(PEER)
