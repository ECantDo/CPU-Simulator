// Stack pointer, likely going to need it
ima cpy 255 sp

// Screen size: 64x64
// Range: X -> [-2.0, 1.0] (real)
// Range: Y -> [1.5, -1.5] (imaginary)

// X/Y offset -> 0.046875 -> Both have a range of 3 units so its the same value
const offset 12 // 0.046875 = 0b0.00001100 = 0 . 12

// X coord -> 8.8 -> s0.s1
ima cpy -2 s0
ima cpy 0 s1
// Y coord -> 8.8 -> s2.s3
ima cpy 1 s2
ima cpy 0x80 s3

hlt


:square_88
// 8.8
// a0.a1
// return in a0.a1

:add_88
// 8.8
// A = a0.a1
// B = a2.a3
// return in a0.a1
    add a1 a3 t0
