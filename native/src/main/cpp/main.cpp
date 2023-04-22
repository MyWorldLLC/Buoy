#include "main.h"

int32_t add(int32_t a, int32_t b){
    return a + b;
}


int32_t add_short(int32_t a, int16_t b){
    return a + b;
}


int32_t add_numbers_t(numbers_t* n){
    return n->a + n->b;
}

int32_t set_and_get_errno(int32_t err){
    errno = err;
    return errno;
}

int32_t get_errno(){
    return errno;
}