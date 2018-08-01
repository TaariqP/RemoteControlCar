//
// Created by Taariq Pala on 01/08/2018.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <winsock2.h>

#define PORT 5555

int setup_client(void) {
    int sockfd, portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    char buffer[256];
    int n;


    printf("Client attempting to connect");
    try {
        sockfd = socket(AF_INET, SOCK_STREAM, 0);
        if (sockfd < 0) {
            printf("Error creating socket");
        }

        server = gethostbyname("192.168.1.21");
        if (server == NULL) {
            printf("Host not found (NULL)");
            exit(0);
        }

        serv_addr.sin_family = AF_INET;
        serv_addr.sin_por = htons(portno);

        bcopy((char *) server->h_addr, (char *) &serv_addr.sin_addr.s_addr, server->h_length);

        if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
            error("ERROR connecting to server");
        }

        bzero(buffer, 256);
        n = read(sockfd, buffer, 255);

        if (n < 0) {
            error("ERROR reading from socket");
        }

        printf("%s\n", buffer);
    }
}
