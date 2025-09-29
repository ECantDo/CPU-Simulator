def first 0
def second 1

def Igor -12

li	second s0
li	first s1

li	0x1 s2
li	46368 s3

fib_loop: // This is an annoying comment (maybe)
	str	s0 0(s2) // Save value into RAM

	add	s0 s1 t0
	mv	s0 s1
	mv	t0 s0

	addi	s2 1 s2

	bltu	s0 s3 fib_loop

hlt