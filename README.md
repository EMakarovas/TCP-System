# TCP-System
A system with a server, a reader, and a writer communicating through TCP.

# Usage
The TCPServer waits for user input in the form of "offset,numberOfValues". Whenever the input is provided, the server retrieves a new corresponding sequence from the TCPWriter and then sends it to the TCPReader on request.
