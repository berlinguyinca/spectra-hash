CC = g++
RM = rm -f
CPPFLAGS = -Wall -O2
LDFLAGS =
LDLIBS = -lcrypto -lssl

TARGET = splash

all: $(TARGET)

$(TARGET): src/$(TARGET).cpp
	$(CC) $(CXXFLAGS) $(LDFLAGS) -o $(TARGET) src/*.cpp $(LDLIBS)

clean:
	$(RM) $(TARGET)
