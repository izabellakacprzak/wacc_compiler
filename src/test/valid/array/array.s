.data

msg_0:
	.word 4
	.ascii " = {"
msg_1:
	.word 2
	.ascii ", "
msg_2:
	.word 1
	.ascii "}"
msg_3:
	.word 82
	.ascii "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"
msg_4:
	.word 44
	.ascii "ArrayIndexOutOfBoundsError: negative index\n\0"
msg_5:
	.word 45
	.ascii "ArrayIndexOutOfBoundsError: index too large\n\0"
msg_6:
	.word 5
	.ascii "%.*s\0"
msg_7:
	.word 3
	.ascii "%d\0"
msg_8:
	.word 3
	.ascii "%p\0"
msg_9:
	.word 1
	.ascii "\0"
msg_10:
	.word 82
	.ascii "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"
msg_11:
	.word 44
	.ascii "ArrayIndexOutOfBoundsError: negative index\n\0"
msg_12:
	.word 45
	.ascii "ArrayIndexOutOfBoundsError: index too large\n\0"
msg_13:
	.word 5
	.ascii "%.*s\0"
msg_14:
	.word 3
	.ascii "%d\0"
msg_15:
	.word 3
	.ascii "%p\0"
msg_16:
	.word 1
	.ascii "\0"

.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r0, =44
	BL malloc
	MOV r4, r0
	LDR r5, =0
	STR r5, [r4, #4]
	LDR r5, =0
	STR r5, [r4, #8]
	LDR r5, =0
	STR r5, [r4, #12]
	LDR r5, =0
	STR r5, [r4, #16]
	LDR r5, =0
	STR r5, [r4, #20]
	LDR r5, =0
	STR r5, [r4, #24]
	LDR r5, =0
	STR r5, [r4, #28]
	LDR r5, =0
	STR r5, [r4, #32]
	LDR r5, =0
	STR r5, [r4, #36]
	LDR r5, =0
	STR r5, [r4, #40]
	LDR r5, =10
	STR r4, [r5]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	BEQ L0
L1:
	LDR r4, [sp]
	STR r4, [r5]
	LDR r4, [sp]
	LDR r4, =1
	ADDS r4, r4, r4
	BLVS p_throw_overflow_error
	STR r4, [sp]
L0:
	LDR r4, [sp]
	LDR r4, =10
	CMP r4, r4
	MOVLT r4, #1
	MOVGE r4, #0
	BEQ L1
	LDR r4, [sp, #4]
	MOV r0, r4
	BL p_print_reference
	LDR r4, =msg_0
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp]
	BEQ L2
L3:
	ADD r4, sp, #4
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	MOV r0, r4
	BL p_print_int
	LDR r4, [sp]
	LDR r4, =9
	CMP r4, r4
	MOVLT r4, #1
	MOVGE r4, #0
	BEQ L4
	LDR r4, =msg_1
	MOV r0, r4
	BL p_print_string
	B L5
L4:
L5:
	LDR r4, [sp]
	LDR r4, =1
	ADDS r4, r4, r4
	BLVS p_throw_overflow_error
	STR r4, [sp]
L2:
	LDR r4, [sp]
	LDR r4, =10
	CMP r4, r4
	MOVLT r4, #1
	MOVGE r4, #0
	BEQ L3
	LDR r4, =msg_2
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	ADD sp, sp, #8
	LDR r0, =0
	POP {pc}
	.ltorg
p_throw_overflow_error:
	LDR r0, =msg_10
	BL p_throw_runtime_error
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #0
	BL exit
p_check_array_bounds:
	PUSH {lr}
	CMP r0, #0
	LDRLT r0, =msg_11
	BLLT p_throw_runtime_error
	LDR r1, [r1]
	CMP r0, r1
	LDRCS r0, =msg_12
	BLCS p_throw_runtime_error
	POP {pc}
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_13
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_14
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_reference:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_15
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_16
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
