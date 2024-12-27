#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h>
#include <errno.h>

#define SERVER_PORT 12345
#define BUFFER_SIZE 4096

int sock;

void handle_sigint(int sig) {
    (void)sig;
    close(sock);
    printf("\nServeur arrêté proprement\n");
    exit(0);
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <port>\n", argv[0]);
        exit(1);
    }

    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("socket failed");
        exit(1);
    }

    struct sockaddr_in address;
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(atoi(argv[1]));

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("bind failed");
        exit(1);
    }

    if (listen(server_fd, 3) < 0) {
        perror("listen failed");
        exit(1);
    }

    printf("Server listening on port %s\n", argv[1]);

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t client_len = sizeof(client_addr);
        int client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &client_len);
        
        if (client_fd < 0) {
            perror("accept failed");
            continue;
        }

        char *welcome = "220 Welcome\r\n";
        write(client_fd, welcome, strlen(welcome));

        char buffer[4096];
        ssize_t total_bytes = 0;
        ssize_t bytes_read;

        while ((bytes_read = read(client_fd, buffer, sizeof(buffer))) > 0) {
            total_bytes += bytes_read;
        }

        printf("Total bytes received: %zd\n", total_bytes);
        close(client_fd);
    }
}