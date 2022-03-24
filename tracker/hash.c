#include <openssl/md5.h>
#include <stdio.h>
#include <unistd.h>


int main()
{
    int n;
    MD5_CTX c;
    char buf[512];
    ssize_t bytes;
    unsigned char *out;

    printf("début\n");
    MD5_Init(&c);
    printf("temp_tst\n");
    bytes=read(STDIN_FILENO, buf, 512);
    printf("test2\n");
    while(bytes > 0)
    {
            printf("bytes : %ld\n", bytes);
            MD5_Update(&c, buf, bytes);
            bytes=read(STDIN_FILENO, buf, 512);
    }

    MD5_Final(out, &c);
    printf("Salut\n");

    for(n=0; n<MD5_DIGEST_LENGTH; n++)
            printf("%02x", out[n]);

    return(0);
}