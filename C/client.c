#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <linux/tcp.h>
#include <termios.h>  // Pour TIOCOUTQ

#define BUFFER_SIZE 1000000

int get_unacked_data(int sock) {
    int unacked = 0;
    if (ioctl(sock, TIOCOUTQ, &unacked) == -1) {
        perror("ioctl TIOCOUTQ");
        return -1;
    }
    return unacked;
}

int main(int argc, char *argv[]) {
    if (argc != 4) {
        fprintf(stderr, "Usage: %s <ip> <port> <size>\n", argv[0]);
        exit(1);
    }

    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("socket failed");
        exit(1);
    }

    struct sockaddr_in server_addr;
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(atoi(argv[2]));
    
    if (inet_pton(AF_INET, argv[1], &server_addr.sin_addr) <= 0) {
        perror("Invalid address");
        exit(1);
    }

    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("connect failed");
        exit(1);
    }

    char welcome[256];
    if (read(sock, welcome, sizeof(welcome)) <= 0) {
        perror("read welcome failed");
        exit(1);
    }
    printf("Received: %s", welcome);

    size_t size = atol(argv[3]);
    char *buffer = malloc(size);
    memset(buffer, 'X', size);

    ssize_t bytes_written = write(sock, buffer, size);
    printf("Bytes written: %zd\n", bytes_written);
    
    int unacked = get_unacked_data(sock);
    printf("Unacked data before shutdown: %d\n", unacked);

    shutdown(sock, SHUT_WR);

    unacked = get_unacked_data(sock);
    printf("Unacked data before Connection closed: %d\n", unacked);

    char readbuf[4096];
    while (read(sock, readbuf, sizeof(readbuf)) > 0) {
        // Attendre que le serveur ferme la connexion
    }

    unacked = get_unacked_data(sock);
    printf("Unacked data after shutdown: %d\n", unacked);

    close(sock);
    free(buffer);
    return 0;
}