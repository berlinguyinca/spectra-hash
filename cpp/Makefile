CC = g++
RM = rm -f
CPPFLAGS = -g -Wall -O2
LDFLAGS =
LDLIBS = -lcrypto -lssl

TARGET = splash

all: $(TARGET)

$(TARGET): $(TARGET).cpp
	$(CC) $(CPPFLAGS) $(LDFLAGS) $(LDLIBS) -o $(TARGET) splash.cpp splashIt.cpp

clean:
	$(RM) $(TARGET)