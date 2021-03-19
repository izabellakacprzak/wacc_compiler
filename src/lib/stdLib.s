std_msg_0:
	.word 1
	.ascii "["
std_msg_1:
	.word 2
	.ascii ", "
std_msg_2:
	.word 1
	.ascii "]"
std_msg_3:
	.word 1
	.ascii "["
std_msg_4:
	.word 2
	.ascii ", "
std_msg_5:
	.word 1
	.ascii "]"
std_msg_6:
	.word 1
	.ascii "["
std_msg_7:
	.word 2
	.ascii ", "
std_msg_8:
	.word 1
	.ascii "]"
l_fill_int:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B STDL0
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
	BEQ STDL1
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
	B STDL2
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
	BEQ STDL3
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
	B STDL4
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
	BEQ STDL6
	LDR r4, [sp]
	STR r4, [sp, #5]
	MOV r4, #1
	STRB r4, [sp, #4]
	B STDL7
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
	BEQ STDL5
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
	B STDL8
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
	BEQ STDL10
	LDR r4, [sp]
	STR r4, [sp, #5]
	MOV r4, #1
	STRB r4, [sp, #4]
	B STDL11
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
	BEQ STDL9
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
	B STDL12
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
	BEQ STDL14
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
	B STDL15
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
	BEQ STDL13
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
	B STDL16
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
	BEQ STDL18
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
	B STDL19
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
	BEQ STDL17
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
	B STDL20
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
	BEQ STDL22
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	B STDL23
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
	BEQ STDL21
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
	B STDL24
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
	BEQ STDL26
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
	B STDL27
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
	BEQ STDL25
	LDR r4, [sp, #4]
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
	B STDL28
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
	BL l_swap_int
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
	BEQ STDL29
	LDR r4, [sp, #16]
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg		
l_strcmp:
	PUSH {lr}
	SUB sp, sp, #16
	LDR r4, =0
	STR r4, [sp, #12]
	LDR r4, =0
	STR r4, [sp, #8]
	LDR r4, [sp, #20]
	LDR r4, [r4]
	LDR r5, =1
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	LDR r4, [sp, #24]
	LDR r4, [r4]
	LDR r5, =1
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
	B STDL33
STDL34:
	LDR r4, [sp, #12]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #12]
	LDR r4, [sp, #8]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #8]
STDL33:
	LDR r4, [sp, #12]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	LDR r5, [sp, #8]
	LDR r6, [sp]
	CMP r5, r6
	MOVLT r5, #1
	MOVGE r5, #0
	AND r4, r4, r5
	ADD r5, sp, #20
	LDR r6, [sp, #12]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	LDRSB r5, [r5]
	ADD r6, sp, #24
	LDR r7, [sp, #8]
	LDR r6, [r6]
	MOV r0, r7
	MOV r1, r6
	BL p_check_array_bounds
	ADD r6, r6, #4
	ADD r6, r6, r7
	LDRSB r6, [r6]
	CMP r5, r6
	MOVEQ r5, #1
	MOVNE r5, #0
	AND r4, r4, r5
	CMP r4, #1
	BEQ STDL34
	ADD r4, sp, #20
	LDR r5, [sp, #12]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	ADD r5, sp, #24
	LDR r6, [sp, #8]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	LDRSB r5, [r5]
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	ADD r5, sp, #24
	LDR r6, [sp, #8]
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	LDRSB r5, [r5]
	ADD r6, sp, #20
	LDR r7, [sp, #12]
	LDR r6, [r6]
	MOV r0, r7
	MOV r1, r6
	BL p_check_array_bounds
	ADD r6, r6, #4
	ADD r6, r6, r7
	LDRSB r6, [r6]
	SUBS r5, r5, r6
	BLVS p_throw_overflow_error
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	MOV r0, r4
	ADD sp, sp, #16
	POP {pc}
	POP {pc}
	.ltorg	
l_contains_int:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL35
STDL36:
	ADD r4, sp, #16
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	LDR r5, [sp, #12]
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ STDL37
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	B STDL38
STDL37:
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL38:
STDL35:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL36
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg	
l_contains_char:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #13]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL39
STDL40:
	ADD r4, sp, #13
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	LDRSB r5, [sp, #12]
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ STDL41
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	B STDL42
STDL41:
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL42:
STDL39:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL40
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg	
l_contains_bool:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #13]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL43
STDL44:
	ADD r4, sp, #13
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	LDRSB r5, [sp, #12]
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ STDL45
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	B STDL46
STDL45:
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL46:
STDL43:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL44
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
l_print_int_array:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =std_msg_0
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL29
STDL30:
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	MOV r0, r4
	BL p_print_int
	LDR r4, =std_msg_1
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL29:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	LDR r6, =1
	SUBS r5, r5, r6
	BLVS p_throw_overflow_error
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL30
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	MOV r0, r4
	BL p_print_int
	LDR r4, =std_msg_2
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
l_print_char_array:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =std_msg_3
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL31
STDL32:
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	MOV r0, r4
	BL putchar
	LDR r4, =std_msg_4
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL31:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	LDR r6, =1
	SUBS r5, r5, r6
	BLVS p_throw_overflow_error
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL32
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	MOV r0, r4
	BL putchar
	LDR r4, =std_msg_5
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
l_print_bool_array:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =std_msg_6
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp]
	B STDL33
STDL34:
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	MOV r0, r4
	BL p_print_bool
	LDR r4, =std_msg_7
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
STDL33:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	LDR r6, =1
	SUBS r5, r5, r6
	BLVS p_throw_overflow_error
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ STDL34
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5
	LDRSB r4, [r4]
	MOV r0, r4
	BL p_print_bool
	LDR r4, =std_msg_8
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
