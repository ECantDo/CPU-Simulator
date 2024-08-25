//R31 -> ram index


ima cpy 3 r1
cal :addValue

ima cpy 19 r1
cal :addValue

ima cpy 1 r1
cal :addValue


ima cpy 0 r31

cal :removeValue
cpy r1 r2

cal :removeValue
cpy r1 r3

cal :removeValue
cpy r1 r4

hlt


:addValue
    str r1 r31
    ima add 1 r31 r31
    ret

:removeValue
    lod r31 r1
    ima add 1 r31 r31
    ret

