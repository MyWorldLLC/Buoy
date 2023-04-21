#include <cstdint>

extern "C" {
    int32_t add(int32_t a, int32_t b);
    int32_t add_short(int32_t a, int16_t b);

    typedef struct {
        int32_t a;
        int16_t b;
    } numbers_t;

    int32_t add_numbers_t(numbers_t* n);
}