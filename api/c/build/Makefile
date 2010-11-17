#win32 default
OS = win32
GLIB_INCLUDES_BASE = ../../../lib/glib
LIB_EXT = .dll
EXT = .exe
LIB_PREFIX = 

UNAME = $(shell uname -s)

ifeq ($(UNAME),Linux)
 OS = linux
 GLIB_INCLUDES_BASE = /usr/local
 LIB_EXT = .so
 EXT = 
 LIB_PREFIX = lib
endif

ifeq ($(UNAME),AIX)
 OS = aix
 Q32 = -q32
 ARCH = -G
 GLIB_INCLUDES_BASE = /usr/local
 LIB_EXT = .so
 EXT =
 LIB_PREFIX = lib
endif

ifeq ($(UNAME),SunOS)
 OS = solaris
 ARCH = -G
 GLIB_INCLUDES_BASE = /usr/local
 LIB_EXT = .so
 EXT =
 LIB_PREFIX = lib
endif


# INCLUDES
GLIB_INCLUDES = -I $(GLIB_INCLUDES_BASE)/include/glib-2.0 -I $(GLIB_INCLUDES_BASE)/include/glib-2.0/glib -I $(GLIB_INCLUDES_BASE)/include/glib-2.0/gio -I $(GLIB_INCLUDES_BASE)/include/glib-2.0/gobject -I $(GLIB_INCLUDES_BASE)/lib/glib-2.0/include
XCODE_INCLUDES = -I ../xcode/inc

# LIBRARY PATHS
XCODE_LIB_DIR =  ../../../lib/$(OS)
XCODE_LIB = -L $(XCODE_LIB_DIR)
GLIB_LIB = -L $(GLIB_INCLUDES_BASE)/lib

CC = gcc
CFLAGS = -Wall -g

CSOURCE = $(shell ls ../xcode/src/)

LIBRARY = $(LIB_PREFIX)xcode

# Build SDK
all: clean  directories $(LIBRARY)

# Build all tools
all_tools: clean_tools tools_directories TOOLS_COMPILE

directories:
	@printf "Creating obj and lib directories..."
	mkdir -p ../obj ../../../lib/$(OS)
	@printf "done\n"

tools_directories:
	@printf "Creating $(OS) tools directory..."
	mkdir -p ../../../tools/c/$(OS)
	@printf "done\n"

$(CSOURCE):
	$(CC) -c $(CFLAGS) ../xcode/src/$*.c -o ../obj/$*.o  $(XCODE_INCLUDES) $(GLIB_INCLUDES)

TOOLS_COMPILE: _idna _normalize _punycode _unicode _bidi
	


$(LIBRARY): $(CSOURCE)
	$(CC) -shared -o $(XCODE_LIB_DIR)/$(LIBRARY)$(LIB_EXT) ../obj/*.o $(GLIB_LIB) -lm -lglib-2.0 -lpthread
	ln -fs $(XCODE_LIB_DIR)/$@$(LIB_EXT) $(XCODE_LIB_DIR)/$@.a

# EXAMPLES TO LINK DLL/.so file WITH YOUR CLIENT PROGRAM
#	$(CC) -o ../obj/TestNormalizationConformance$(EXT) ../obj/TestNormalizationConformance.o $(XCODE_LIB) $(GLIB_LIB) -lxcode -lm -lglib-2.0
#	$(CC) -o ../obj/TestIdnaConformance$(EXT) ../obj/*.o $(XCODE_LIB) $(GLIB_LIB) -lxcode -lm -lglib-2.0
#	$(CC) -o ../obj/TestIdna$(EXT) ../obj/TestIdna.o $(XCODE_LIB) $(GLIB_LIB) -lxcode -lm -lglib-2.0



################
# TOOLS COMPILES
################
_idna:
	$(CC) -o ../../../tools/c/$(OS)/idna$(EXT) ../test/idna/idna.c $(XCODE_INCLUDES) $(GLIB_INCLUDES) $(XCODE_LIB) $(GLIB_LIB) -lxcode  -lm -lglib-2.0

_normalize:
	$(CC) -o ../../../tools/c/$(OS)/normalize$(EXT) ../test/normalize/normalize.c $(XCODE_INCLUDES) $(GLIB_INCLUDES) $(XCODE_LIB) $(GLIB_LIB) -lxcode -lm -lglib-2.0

 _punycode:
	$(CC) -o ../../../tools/c/$(OS)/punycode$(EXT) ../test/punycode/punycode.c $(XCODE_INCLUDES) $(GLIB_INCLUDES) $(XCODE_LIB) $(GLIB_LIB) -lxcode  -lm -lglib-2.0

_unicode:
	$(CC) -o ../../../tools/c/$(OS)/unicode$(EXT) ../test/unicode/unicode.c $(XCODE_INCLUDES) $(GLIB_INCLUDES) $(XCODE_LIB) $(GLIB_LIB) -lxcode  -lm -lglib-2.0

_bidi:
	$(CC) -o ../../../tools/c/$(OS)/bidi$(EXT) $(CFLAGS) ../test/bidi/bidi.c $(XCODE_INCLUDES) $(GLIB_INCLUDES) $(XCODE_LIB) $(GLIB_LIB) -lxcode  -lm -lglib-2.0

################
# CLEAN
################
clean:
	rm -rf ../obj; rm -f $(XCODE_LIB_DIR)/$(LIBRARY).*

clean_tools:
	rm -rf ../../../tools/c/$(OS)
