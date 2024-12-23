ima imb out 0 0 0
uscr
:loop
    imb add r0 1 r0
    imb neq r0 255 :loop
hlt