l_fill_int:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L0
STDL1:
	LDR r4, [sp, #12]
	ADD r5, sp, #16
	LDR r6, [sp]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL0:
	LDR r4, [sp]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L1
	LDR r4, [sp, #16]
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
l_fill_char:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #13]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L2
STDL3:
	LDRSB r4, [sp, #12]
	ADD r5, sp, #13
	LDR r6, [sp]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	STRB r4, [r5]
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL2:
	LDR r4, [sp]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L3
	LDR r4, [sp, #13]
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
l_index_of_int:
	PUSH {lr}
	SUB sp, sp, #13
	LDR r4, [sp, #21]
	LDR r4, [r4]
	STR r4, [sp, #9]
	LDR r4, =-1
	STR r4, [sp, #5]
	MOV r4, #0
	STRB r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L4
STDL5:
	LDRSB r4, [sp, #4]
	MOV r5, #0
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	ADD r5, sp, #21
	LDR r6, [sp]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	LDR r5, [r5]
	LDR r6, [sp, #17]
	CMP r5, r6
	MOVEQ r5, #1
	MOVNE r5, #0
	AND r4, r4, r5
	CMP r4, #0
	BEQ L6
	LDR r4, [sp]
	STR r4, [sp, #5]
	MOV r4, #1
	STRB r4, [sp, #4]
	B L7
STDL6:
STDL7:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL4:
	LDR r4, [sp]
	LDR r5, [sp, #9]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L5
	LDR r4, [sp, #5]
	MOV r0, r4
	ADD sp, sp, #13
	POP {pc}
	POP {pc}
	.ltorg
l_index_of_char:
	PUSH {lr}
	SUB sp, sp, #13
	LDR r4, [sp, #18]
	LDR r4, [r4]
	STR r4, [sp, #9]
	LDR r4, =-1
	STR r4, [sp, #5]
	MOV r4, #0
	STRB r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L8
STDL9:
	LDRSB r4, [sp, #4]
	MOV r5, #0
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	ADD r5, sp, #18
	LDR r6, [sp]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	LDRSB r5, [r5]
	LDRSB r6, [sp, #17]
	CMP r5, r6
	MOVEQ r5, #1
	MOVNE r5, #0
	AND r4, r4, r5
	CMP r4, #0
	BEQ L10
	LDR r4, [sp]
	STR r4, [sp, #5]
	MOV r4, #1
	STRB r4, [sp, #4]
	B L11
STDL10:
STDL11:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL8:
	LDR r4, [sp]
	LDR r5, [sp, #9]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L9
	LDR r4, [sp, #5]
	MOV r0, r4
	ADD sp, sp, #13
	POP {pc}
	POP {pc}
	.ltorg
l_max:
	PUSH {lr}
	SUB sp, sp, #12
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =-2147483648
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L12
STDL13:
	ADD r4, sp, #16
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVGT r4, #1
	MOVLE r4, #0
	CMP r4, #0
	BEQ L14
	ADD r4, sp, #16
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #4]
	B L15
STDL14:
STDL15:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL12:
	LDR r4, [sp]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L13
	LDR r4, [sp, #4]
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg
l_min:
	PUSH {lr}
	SUB sp, sp, #12
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =2147483647
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L16
STDL17:
	ADD r4, sp, #16
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #0
	BEQ L18
	ADD r4, sp, #16
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #4]
	B L19
STDL18:
STDL19:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL16:
	LDR r4, [sp]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L17
	LDR r4, [sp, #4]
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg
l_swap_int:
	PUSH {lr}
	SUB sp, sp, #4
	ADD r4, sp, #16
	LDR r5, [sp, #8]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp]
	ADD r4, sp, #16
	LDR r5, [sp, #12]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	ADD r5, sp, #16
	LDR r6, [sp, #8]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	LDR r4, [sp]
	ADD r5, sp, #16
	LDR r6, [sp, #12]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	LDR r4, [sp, #16]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
l_swap_char:
	PUSH {lr}
	SUB sp, sp, #1
	ADD r4, sp, #13
	LDR r5, [sp, #5]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	STRB r4, [sp]
	ADD r4, sp, #13
	LDR r5, [sp, #9]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	ADD r5, sp, #13
	LDR r6, [sp, #5]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	STRB r4, [r5]
	LDRSB r4, [sp]
	ADD r5, sp, #13
	LDR r6, [sp, #9]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	STRB r4, [r5]
	LDR r4, [sp, #13]
	MOV r0, r4
	ADD sp, sp, #1
	POP {pc}
	POP {pc}
	.ltorg
l_is_sorted:
	PUSH {lr}
	SUB sp, sp, #12
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, =1
	STR r4, [sp]
	B L20
STDL21:
	ADD r4, sp, #16
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	ADD r5, sp, #16
	LDR r6, [sp]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	LDR r5, [r5]
	CMP r4, r5
	MOVGT r4, #1
	MOVLE r4, #0
	CMP r4, #0
	BEQ L22
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	B L23
STDL22:
STDL23:
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL20:
	LDR r4, [sp]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L21
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg
l_min_index_from:
	PUSH {lr}
	SUB sp, sp, #16
	LDR r4, [sp, #20]
	LDR r4, [r4]
	STR r4, [sp, #12]
	LDR r4, =2147483647
	STR r4, [sp, #8]
	LDR r4, =-1
	STR r4, [sp, #4]
	LDR r4, [sp, #24]
	STR r4, [sp]
	B L24
STDL25:
	ADD r4, sp, #20
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #0
	BEQ L26
	ADD r4, sp, #20
	LDR r5, [sp]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, [sp]
	STR r4, [sp, #4]
	B L27
STDL26:
STDL27:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
STDL24:
	LDR r4, [sp]
	LDR r5, [sp, #12]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L25
	LDR r4, [sp, #8]
	MOV r0, r4
	ADD sp, sp, #16
	POP {pc}
	POP {pc}
	.ltorg
l_sort:
	PUSH {lr}
	SUB sp, sp, #12
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, =-1
	STR r4, [sp]
	B L28
STDL29:
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	BL l_min_index_from
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_swap_int
	ADD sp, sp, #12
	MOV r4, r0
	STR r4, [sp, #16]
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL28:
	LDR r4, [sp, #4]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L29
	LDR r4, [sp, #16]
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg
