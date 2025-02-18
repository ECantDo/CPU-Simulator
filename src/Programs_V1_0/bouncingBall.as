// Not much point to make this without peripherals
cscr
// reg 0 -> pos xtr 
// reg 1 -> pos y
// reg 2 -> x dir | reg 4 -> Def value
// reg 3 -> y dir | reg 5 -> Def value


ima cpy 1 r4
ima cpy 1 r5

cpy r4 r2
cpy r5 r3


ima cpy 33 r10
ima cpy 0 r11

:rightWall
    out r10 r11 0
    imb add r11 1 r11
    imb neq r11 21 :rightWall

ima cpy 0 r10

:bottomWall
    out r10 r11 0
    imb add r10 1 r10
    imb neq r10 33 :bottomWall


:loop
    :leftEdge
    imb grt r0 0 :rightEdge
    cpy r4 r2
    goto :topEdge

    :rightEdge
    imb lst r0 32 :topEdge
    not r2 r2
    ima add 1 r2 r2

    :topEdge
    imb grt r1 0 :bottomEdge
    cpy r5 r3
    goto :move

    :bottomEdge
    imb lst r1 20 :move
    not r3 r3
    ima add 1 r3 r3


    :move
    imb or r1 64 r7
    out r0 r7 0


    add r0 r2 r0
    add r1 r3 r1

    out r0 r1 0
    uscr

    goto :loop
