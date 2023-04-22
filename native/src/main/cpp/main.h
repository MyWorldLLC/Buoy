#include <cstdint>
#include <errno.h>

extern "C" {
    extern "C" int errno;

    int32_t add(int32_t a, int32_t b);
    int32_t add_short(int32_t a, int16_t b);

    typedef struct {
        int16_t a;
        int32_t b;
    } numbers_t;

    int32_t add_numbers_t(numbers_t* n);

    int32_t set_and_get_errno(int32_t err);
    int32_t get_errno();
}