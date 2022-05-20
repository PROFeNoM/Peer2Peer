JAVAC 			?= javac
JAVA 			?= java

PACKAGE = peer
SRC_PACKAGE = $(PACKAGE)
TEST_PACKAGE = $(PACKAGE)

MAIN_CLASS	= $(SRC_PACKAGE).Main
MAIN_GUI_CLASS	= $(SRC_PACKAGE).gui.MainWindow

SRC_DIR		= src/main
TST_DIR		= src/test
BUILD_DIR 	= build
LIB_DIR		= lib

JUNIT_PATH ?= $(LIB_DIR)/junit-platform-console-standalone-1.8.2.jar

CLASS_PATH = $(BUILD_DIR)
TST_CLASS_PATH = $(JUNIT_PATH):$(CLASS_PATH)

JAVA_FILES			=	$(shell find ./$(SRC_DIR) -type f -name "*.java")
JAVA_TEST_FILES		=	$(shell find ./$(TST_DIR) -type f -name "*.java")

TST_EXEC_OPTS = -cp $(TST_CLASS_PATH)

COLOR	=	"\033["
GREEN	=	";32m"
CYAN	=	";36m"
PURPLE	=	";35m"
GREY	=	"1;30m"
BOLD	=	";1"
ITALIC	=	";3"
NOCOLOR	=	"\033[0m"

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
	$(JAVAC) -d $(BUILD_DIR) $(JAVA_FILES)
	@$(MAKE) $@_end

build-tests: build $(JAVA_TEST_FILES)
	@$(MAKE) $@_start
	$(JAVAC) -d $(BUILD_DIR) -cp $(TST_CLASS_PATH) $(JAVA_TEST_FILES)
	@$(MAKE) $@_end

############################## INSTALL RULES ##############################

peer: build
	$(JAVA) -cp $(CLASS_PATH) $(MAIN_CLASS)

gui: build
	$(JAVA) -cp $(CLASS_PATH) $(MAIN_GUI_CLASS)

############################## TESTS RULES ##############################

test: build-tests
	$(JAVA) -jar $(JUNIT_PATH) -cp $(CLASS_PATH) -p $(PACKAGE)
