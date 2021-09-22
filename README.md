# Peer-to-Peer-File-System-P2FS
1. Introduction
The project consists of implementing in, Java, C++, C#, or Python, a Peer to Peer File System
(P2FS), over UDP and TCP. The description of the protocol(s) to implement is given Section
2 while the requirements are stated in Section 3.

2. Peer to Peer File System (P2FS)
The Peer to Peer File System (P2FS) consists of several clients and one server. The main goal
of the P2FS is to allow for users (clients/peers) to share files. For the project we are dealing
only with text files.

The role of the server is to keep track of the registered clients (peers), how they can be
reached and the list of files available from each one of them. It will use this information to
respond and inform the clients about each other upon requests.
The communications between the clients (peers) and the server is through UDP, while the
communication between the clients (peers) for file transfer is through TCP.
In the following we assume one user per client and therefore we use the terms user, client and
peer interchangeably. The protocol(s) are described below.

2.1. Registration and De-registration
The server is always available at a fixed UDP socket and listening for incoming messages and
communicates with the clients through UDP. You can fix the server UDP socket as you wish,
for instance 3000.

A new user has to register with the server before publishing or discovering what is available
for share. A message “REGISTER” is sent to the server through UDP. For registering a user
has to send his/her name (every user has a unique name), IP Address, UDP socket# it can be
reached at by the server, and the TCP socket# to be used for file transfer with peers.

REGISTER / RQ# / Name / IP Address / UDP socket# / TCP socket#

Upon reception of this message the server can accept or refuse the registration. For instance,
the registration can be denied if the provided Name is already in use. If the registration is
accepted the following message is sent to the user.
REGISTERED RQ#
If the registration is denied, the server will send the following message and provide the
reason.

REGISTER-DENIED / RQ# / Reason

The RQ# is used to refer to which “REGISTER” message this confirmation or denial
corresponds to. It is the same case of all the messages where RQ# is used.
A user can de-register by sending the following message to the server.

DE-REGISTER / RQ# / Name

If the name is already registered, the current server will remove the name and all the
information related to this user.
In case Name is not registered, for instance, the message is just ignored by the server. No
further action is taken by the server.

2.2. Publishing file related information
A registered client can publish and retrieve information about available files and where to
download them from. When files become available for share at a given client, it informs the
server with the following message.

PUBLISH / RQ# / Name / List of files

The server will add the list of files to the current list of files available from this client and
acknowledge this by sending a confirmation message to the client.

PUBLISHED / RQ#

If publication is denied, because of errors like “Name” does not exists, the server sends the
following message to the client.
PUBLISH-DENIED RQ# Reason
The client can try again by sending this “PUBLISH” message for a few times before giving
up.

If a client decides to remove a file (or a list of files) from its offering, it sends the following
message to the server.
REMOVE RQ# Name List of files to remove
The server will remove the list of files from the current list of files available from this client
and acknowledge this by sending a confirmation message to the client.

REMOVED / RQ#

If removal is denied, because of errors like “Name” does not exists, the server sends the
following message to the client.
REMOVE-DENIED RQ# Reason
The client can try again by sending the “REMOVE” message a few times before giving up.

2.3. Retrieving information from the server
A registered user can retrieve information from the server by sending different kinds of
requests. A user can retrieve for instance the names of all the other registered clients, how to
reach them using TCP and the available files by sending the following message to the server.

RETRIEVE-ALL / RQ#

For a registered user the server will responds with the names, IP addresses, TCP socket# and
available files of all registered clients.
RETRIEVE RQ# List of (Name, IP address, TCP socket#, list of available files)
For a non-registered user the server ignores the request.
A registered user can also request the information about a specific peer. For this it needs to
know the name and send the following request to server.

RETRIEVE-INFOT / RQ# / Name

For a registered user the server will responds with the name, IP addresses and TCP socket# of
the client named “Name” if it exists and is registered.
RETRIEVE-INFOT RQ# Name IP Address TCP socket# List of available files
For a non-registered user the server ignores the request. However, if the requested name does
not exists/not registered, the server will respond with.

RETRIEVE-ERROR / RQ# / Reason

The Reason could be “client does not exist/is not registered.
A user can search for a specific file by sending the following message to the server.
SEARCH-FILE RQ# File-name
If the file exists, the server responds with the names of all the registered clients from where
this file can be downloaded with all the necessary information.
SEARCH-FILE RQ# List of (Name, IP address, TCP socket#)
If the user is not registered, the search request is ignored. However, if the user is registered
but the file does not exist, the server will respond with.

SEARCH-ERROR / RQ# / Reason

2.4. File transfer between peers
Once a client knows what file to download and how to reach the peer, it will first set a TCP
connection to the peer, and then send the following message to download the file.

DOWNLOAD / RQ# / File-name

If the file exists at destination, the peer will start transferring the file in small chunks not
exceeding 200 characters using the following message (where Chunk# indicates the
order/place of the Text in the original file).

FILE / RQ# / File-name / Chunk# / Text

The last chunk of the file is carried in a special message to indicate the last portion of the file.

FILE-END / RQ# / File-name / Chunk# / Text

While receiving these messages the client who requested the file puts the chunks together to
compose again the original file. Upon complete reception of the file the client closes the TCP
connection.

If the requested file does not exist at destination or for some other reasons the contacted peer
cannot engage in a file transfer it sends the following message.

DOWNLOAD-ERROR / RQ# / Reason

2.5. Clients updating their contact information (mobility)
A registered user can always modify his/her IP address, UDP socket#, and/or TCP socket#
using the following message.

UPDATECONTACT / RQ# / Name / IP Address / UDP socket# / TCP socket#

The message is sent to the server. Upon reception of this message the server can accept the
update and reply to the user using the message.

UPDATECONFIRMED / RQ# / Name / IP Address / UDP socket# / TCP socket#

In case of denial of update, because of errors such as “Name” does not exist, the following
message is sent to the user.

UPDATEDENIED / RQ# / Name / Reason

3. Requirements
Project should be done in groups of 3 students. You should send, by September 30, your
group list including student names, ID numbers and ECE email addresses to
ferhat.khendek@concordia.ca.
Design and implement the client and server that follow the protocol(s) aforementioned. The
coding of the protocol messages is part of your design, i.e. you have to come out with the
appropriate coding of the messages. You can decide to use simple text message, etc.
The information stored in the server should be persistent, i.e. if the server crashes and is
restored it will recover all the information as before crashing.
Reporting: Server and clients should be reporting their communications with the entity to the
users of the system using a log file or printing directly into the screen. In other words, during
the demonstration, I would like to see the messages sent and received, progress and failures.
Assumptions/Error/Exception Handling
You should be aware that the description as it is does not state everything. For instance what
happens if a client receives a response with a RQ# that does not correspond to any of its
(pending) requests?
And more to be discussed in class …
