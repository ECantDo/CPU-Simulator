

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
// RAM seems to be working

// Lets try plotting a few pixels to the screen :)
li	513 t6 // Turn pixel (1, 2) on
out	t6 0

// Try some branching
mv	t6 t5
beq	t5 t6 label

	// This should not run
li 	0x3F3F t6 // Turn pixel (63, 63) on
out	t6 0

label:
li	0x0505 t6 // Turn pixel (5, 5) on
out	t6 0

li	0xC000 t0 // Load screen buffer
out	t0 0

exit:
	hlt