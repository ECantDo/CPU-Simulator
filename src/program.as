ima cpy 1 r0
cpy r0 r1

ima cpy 4 r2

:fibLoop
    ima eql 0 r2 :exit

    add r0 r1 r3
    cpy r0 r1
    cpy r3 r0

    goto :fibLoop

:exit
    hlt