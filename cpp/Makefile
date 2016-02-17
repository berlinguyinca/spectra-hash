CC = g++
RM = rm -f
CPPFLAGS = -O2
LDFLAGS = -lcrypto -lssl
LDLIBS =

TARGET = splash

all: $(TARGET)

$(TARGET): $(TARGET).cpp
	$(CC) $(CPPFLAGS) $(LDFLAGS) $(LDLIBS) -o $(TARGET) $(TARGET).cpp

clean:
	$(RM) $(TARGET)