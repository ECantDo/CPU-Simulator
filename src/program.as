ima cpy 1 r0
cpy r0 r1

:fibLoop
    imb eql r0 233 :exit

    add r0 r1 r2
    cpy r0 r1
    cpy r2 r0


    str r0 r31
    imb add r31 1 r31
    goto :fibLoop

:exit
hlt