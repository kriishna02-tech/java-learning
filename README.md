# 01 - HTTP Server

I'm building a simple HTTP server in Java from scratch to understand how servers, TCP connections, sockets, and HTTP work.

---

## 1. What are we building?

We are building a small program that behaves like a web server.

Normally, when a browser or another client wants a webpage, it sends a request to a server.

For example:

```text
Client → Server
"Give me the resource at /"
```

The server then sends a response:

```text
Server → Client
"HTTP/1.1 200 OK"
"Here is the requested content."
```

Our Java program will do a very small version of this.

For testing, we use `curl`.

```text
curl → Java HTTP Server
```

---

## 2. Our first Java program

We started with a simple Java program:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("HTTP Server Project");
    }
}
```

We compiled it:

```powershell
javac Main.java
```

and ran it:

```powershell
java Main
```

Output:

```text
HTTP Server Project
```

This confirmed that Java was working correctly and our project structure was working.

---

# 3. ServerSocket

We then created a `ServerSocket`:

```java
ServerSocket serverSocket = new ServerSocket(8080);
```

`ServerSocket` is a Java class used to listen for incoming **TCP connections**.

The number `8080` is the port.

The operating system manages the port, and our Java program asks the OS to allow it to listen on that port.

Think of the port like a door:

```text
My computer

       Port 8080
          ↓
    ┌─────────────┐
    │ Java Server │
    └─────────────┘
```

---

# 4. localhost:8080

We test our server using:

```powershell
curl.exe http://localhost:8080
```

`localhost` means:

```text
my own computer
```

`8080` means:

```text
port 8080
```

So:

```text
localhost:8080
```

means:

> Port 8080 on my own computer.

---

# 5. accept()

Next we used:

```java
var clientSocket = serverSocket.accept();
```

`accept()` waits until a client connects to the server.

For example:

```text
curl
  │
  │ connect to localhost:8080
  ▼
ServerSocket
  │
  │ accept()
  ▼
Socket
```

When the client connects, `accept()` returns a `Socket`.

---

# 6. Socket

A `Socket` represents the connection between our server and a particular client.

```java
var clientSocket = serverSocket.accept();
```

Here:

```text
serverSocket
     ↓
waits for connections

clientSocket
     ↓
represents one specific client connection
```

If multiple clients connect, each client can have its own socket.

---

# 7. TCP vs HTTP

This was an important concept.

### TCP

TCP provides the connection between the client and server.

```text
Client ───────── TCP connection ───────── Server
```

### HTTP

HTTP is the protocol used to communicate through that connection.

For example:

```text
Client → HTTP request
Server → HTTP response
```

So:

```text
TCP
↓
provides the connection

HTTP
↓
defines how the client and server communicate
```

---

# 8. InputStream

Once we have a client socket, we can receive data from the client.

We get an `InputStream`:

```java
var input = clientSocket.getInputStream();
```

Think of it as:

```text
InputStream
    ↓
receive data from client
```

We used:

```java
int data = input.read();
```

`read()` reads one byte at a time.

For example, the HTTP request starts with:

```text
GET / HTTP/1.1
```

The first character is:

```text
G
```

The numeric value of `G` is:

```text
71
```

So:

```java
int data = input.read();
System.out.println(data);
```

gave:

```text
71
```

We then used:

```java
System.out.println((char) data);
```

which converted the numeric value into the character:

```text
G
```

---

# 9. Reading multiple bytes

One call to:

```java
input.read();
```

reads only one byte.

So if the client sends:

```text
GET / HTTP/1.1
```

the stream contains many bytes:

```text
G → E → T →   → / →   → H → T → T → P → ...
```

Calling `read()` repeatedly lets us read the request byte by byte.

We used a loop to keep reading.

The important idea is:

```text
read()
 ↓
one byte

read()
 ↓
next byte

read()
 ↓
next byte
```

Eventually the individual bytes form the complete request.

---

# 10. HTTP Request

When we ran:

```powershell
curl.exe http://localhost:8080
```

our Java server was able to read the HTTP request.

We saw:

```text
GET / HTTP/1.1
Host: localhost:8080
User-Agent: curl/8.20.0
Accept: */*
```

This was important because we were no longer just guessing what HTTP was.

We actually saw the data being sent by the client.

---

# 11. HTTP Request Line

The first line was:

```text
GET / HTTP/1.1
```

It contains three important pieces:

```text
GET     /     HTTP/1.1
│       │        │
│       │        └── HTTP version
│       └─────────── requested path/resource
└─────────────────── HTTP method
```

### GET

`GET` is an HTTP method.

It means the client wants to retrieve/read a resource.

Example:

```text
GET /
```

means approximately:

> Give me the resource at `/`.

### `/`

`/` is the requested path/resource.

Later, our server could have different paths:

```text
GET /       → home
GET /hello  → hello page
GET /about  → about page
```

This will eventually lead us to routing.

### HTTP/1.1

This tells the server which HTTP version the client is using.

---

# 12. Host Header

We saw:

```text
Host: localhost:8080
```

The `Host` header tells the server which host and port the request is intended for.

In our case:

```text
localhost → our computer
8080      → our server's port
```

It is information contained inside the HTTP request.

It does not itself make the server listen on the port.

---

# 13. OutputStream

To send data back to the client, we use:

```java
var output = clientSocket.getOutputStream();
```

Think of it as:

```text
InputStream
    ↓
receive from client

OutputStream
    ↓
send to client
```

So:

```text
             Java Server
                 │
        ┌────────┴────────┐
        │                 │
 InputStream        OutputStream
    RECEIVE              SEND
        │                 │
        ▼                 ▼
      Client             Client
```

---

# 14. Our First HTTP Response

Initially we tried sending:

```text
Hello java server!
```

by itself.

That was not a proper HTTP response.

We learned that the client expects HTTP-formatted data.

So we changed it to:

```java
output.write(
    "HTTP/1.1 200 OK\r\n\r\nHello java server!".getBytes()
);
```

Now the response has an HTTP status line followed by the response body.

The response looks conceptually like:

```text
HTTP/1.1 200 OK

Hello java server!
```

---

# 15. What does 200 OK mean?

```text
HTTP/1.1 200 OK
```

means the request was successfully handled.

For now, our server always responds with `200 OK`.

Later we will learn other HTTP status codes.

---

# 16. What does \r\n\r\n mean?

Our response contains:

```text
\r\n\r\n
```

This represents the separation between the HTTP headers and the response body.

Conceptually:

```text
HTTP/1.1 200 OK
                ↓
          end of headers
                ↓

Hello java server!
```

The blank line tells the client:

> The headers are finished. The body starts now.

---

# 17. Closing the connection

After sending the response, we used:

```java
clientSocket.close();
```

This closes the connection with that particular client.

So the basic flow is currently:

```text
1. Start ServerSocket
        ↓
2. Wait using accept()
        ↓
3. Client connects
        ↓
4. Get a Socket
        ↓
5. Read request using InputStream
        ↓
6. Send response using OutputStream
        ↓
7. Close the client Socket
```

---

# 18. Current Code

Our current basic server looks like:

```java
import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("Server started on port 8080");

        var clientSocket = serverSocket.accept();
        System.out.println("Client connected");

        var input = clientSocket.getInputStream();

        int data;

        do {
            data = input.read();

            if (data != -1) {
                System.out.println((char) data);
            }

        } while (data != -1);

        var output = clientSocket.getOutputStream();

        output.write(
            "HTTP/1.1 200 OK\r\n\r\nHello java server!".getBytes()
        );

        output.flush();

        clientSocket.close();
    }
}
```

---

# 19. Current Mental Model

The most important thing I've learned so far is the complete flow:

```text
                    MY COMPUTER

              ┌───────────────────┐
              │   Java Program    │
              │                   │
              │ ServerSocket      │
              │     port 8080     │
              └─────────┬─────────┘
                        │
                  waits for TCP
                   connection
                        │
                        ▼
                     Socket
                   /        \
                  /          \
          InputStream     OutputStream
             RECEIVE          SEND
                ↑               ↓
                │               │
                │               │
              HTTP REQUEST → HTTP RESPONSE
                │               │
                │               │
                └────── curl ───┘
```

When I run:

```powershell
curl.exe http://localhost:8080
```

the process is:

```text
curl
 ↓
connect to localhost:8080
 ↓
TCP connection
 ↓
serverSocket.accept()
 ↓
clientSocket
 ↓
curl sends HTTP request
 ↓
InputStream reads request
 ↓
Java creates HTTP response
 ↓
OutputStream sends response
 ↓
curl receives response
```

---

# 20. What I should be able to explain

At this point I should be able to explain:

* What `ServerSocket` does
* What a port is
* What `localhost` means
* What `accept()` does
* What a `Socket` represents
* Difference between `InputStream` and `OutputStream`
* Difference between TCP and HTTP
* What an HTTP request is
* What `GET` means
* What `/` means
* What `HTTP/1.1` means
* What the `Host` header is
* What `HTTP/1.1 200 OK` means
* Why `\r\n\r\n` is used
* How curl communicates with our Java program

---

# 21. Important Learning Rule

I don't want to learn this project by memorizing code.

The learning cycle I'm following is:

```text
Build
  ↓
Get stuck
  ↓
Understand the problem
  ↓
Learn the required concept
  ↓
Implement
  ↓
Test
  ↓
Explain it in my own words
  ↓
Refactor
  ↓
Continue
```

The goal is:

> **I should eventually be able to build a similar server myself and explain why each important piece exists.**
