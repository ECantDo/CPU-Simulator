

// Test storing into and loading from RAM
li	63 s0
str	s0 0(zero)
str	s0 1(zero)
str	s0 4(zero)

lod	t0 0(zero)
lod	t1 1(zero)
lod	t2 4(zero)

li	10 s10
str	s10 0(s10)
lod	t3 0(s10)



exit:
	hlt