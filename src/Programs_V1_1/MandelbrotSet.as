// Stack pointer, likely going to need it
ima cpy 255 sp

// Screen size: 64x64
// Range: X -> [-2.0, 1.0] (real)
// Range: Y -> [-1.5, 1.5] (imaginary)

// X/Y offset -> 0.046875 -> Both have a range of 3 units so its the same value
const offset 12 // 0.046875 = 0b0.00001100 = 0 . 12

// X coord -> 8.8 -> s0.s1
ima cpy -2 s0
ima cpy 0 s1


hlt