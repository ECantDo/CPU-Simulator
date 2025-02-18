ima cpy 1 r1
cpy r1 r2
// Need to have set values in the registers - could be populated with junk
ima cpy 2 r31

ima imb str 1 0
ima imb str 1 1

:fibLoop
    imb eql r1 233 :exit

    add r1 r2 r3
    cpy r1 r2
    cpy r3 r1

    // store in RAM the sum, into address stored in r31
    str r1 r31
    imb add r31 1 r31
    goto :fibLoop

:exit
hlt