// r0 -> Quotient
// r1 -> Remainder

// r2 -> Numerator
// r3 -> Denominator

// r4 -> Shift Amount


ima cpy 69 r2
ima cpy 7 r3

cal :divide
hlt

:divide
    // Set output regs to 0
    ima cpy 0 r0
    cpy r0 r1

    ima cpy 16 r4 // shift 7 right (+1 for the loop) (8ths-bit place is to say shifting down)

    :shifting
        imb eql r4 8 :exit // If the shifting is done

        imb sub r4 1 r4

        imb lsh r0 1 r0
        imb lsh r1 1 r1

        bsh r2 r4 r5

        imb and r5 1 r5
        or r1 r5 r1

        :condSub
            lst r1 r3 :shifting
            imb or r0 1 r0
            sub r1 r3 r1

            goto :shifting

    :exit
        ret