CC = g++
RM = rm -f
CPPFLAGS = -Wall -O2 $(EXTRA_CPPFLAGS)
LDFLAGS = $(EXTRA_LDFLAGS)
LDLIBS = -lssl -lcrypto 

TARGET = splash

all: $(TARGET)

$(TARGET): src/$(TARGET).cpp
	$(CC) $(CPPFLAGS) $(LDFLAGS) -o $(TARGET) src/*.cpp $(LDLIBS)

clean:
	$(RM) $(TARGET) $(TARGET)_test tests/*.gch

test:
	$(CC) $(CPPFLAGS) $(LDFLAGS) -o $(TARGET) -I src -o $(TARGET)_test src/$(TARGET).cpp tests/test_$(TARGET).cpp $(LDLIBS) && \
	./$(TARGET)_test