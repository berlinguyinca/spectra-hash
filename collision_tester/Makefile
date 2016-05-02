CC = g++
RM = rm -f
CPPFLAGS = -Wall -O2
LDFLAGS =
LDLIBS = -lcrypto -lssl

TARGET = splash

all: $(TARGET)

$(TARGET): $(TARGET).cpp
	$(CC) $(CPPFLAGS) $(LDFLAGS) -o $(TARGET) splash.cpp splashIt.cpp $(LDLIBS)

clean:
	$(RM) $(TARGET)
