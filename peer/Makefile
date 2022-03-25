COLOR				=	"\033["
GREEN				=	";32m"
CYAN				=	";36m"
PURPLE				=	";35m"
GREY				=	"1;30m"
BOLD				=	";1"
ITALIC				=	";3"
NOCOLOR				=	"\033[0m"

############################## MAIN RULES ##############################

all: 

clean: CLEAN_start
	rm -rf *.class

############################## HELPERS RULES ##############################

.PHONY: all clean %_start %_end

.SECONDARY: $(TRACKER_OBJ) $(TRACKER_BIN)


%_start:
	@echo $(COLOR)$(BOLD)$(GREEN)$* START$(NOCOLOR)

%_end:
	@echo $(COLOR)$(BOLD)$(GREEN)$* DONE$(NOCOLOR)

############################## BUILD RULES ################################

############################## INSTALL RULES ##############################

############################## TESTS RULES ##############################
