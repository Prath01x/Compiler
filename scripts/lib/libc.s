	.text
	.globl _start
_start:
	jal     main
	move    $a0, $v0
	li      $v0, 17
	syscall

	.globl malloc
malloc:
	li      $v0, 9
	syscall
	jr      $ra

	.globl get_framebuffer
get_framebuffer:
	la      $a0, 0x10010000 # address of static data
	jr      $ra

	.globl getchar
getchar:
	li      $v0, 12
	syscall
	jr      $ra

	.globl putchar
putchar:
	li      $v0, 11
	syscall
	jr      $ra
