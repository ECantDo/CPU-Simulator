def first 0
def second 1

def Igor -12

li	1 s0
li	0 s1

li	1 s2

fib_loop:
	str	s0 0(s2) // Save value into RAM

	add	s0 s1 t0
	mv	s0 s1
	mv	t0 s0

	addi	s2 1 s2

	blt	s0 46368 fib_loop

hlt