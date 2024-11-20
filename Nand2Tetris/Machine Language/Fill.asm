// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(START) // Puts screen address into R[0]
@SCREEN
D=A
@R0
M=D

(KBDINPUT)
@KBD // Checks the keybord input. if(kbd > 0) a key is pressed , else (kbd == 0) and no key is pressed
D=M
@BLACK // Jumps to BLACK to change screen color
D;JGT
@WHITE // Jumps to WHITE to change screen color
D;JEQ

(BLACK) // Puts in R[1] black value
@R1
M=-1
@CHANGESCREEN // Jumps to the method that canges screen according to color
0;JMP

(WHITE) // Puts in R[1] white value
@R1
M=0
@CHANGESCREEN // Jumps to the method that canges screen according to color
0;JMP

(CHANGESCREEN)
@R1 // Checks wether the color is black or white
D=M
@R0
A=M	// What screen pixel are we on
M=D	// coloring the pixel

@R0
D=M+1	// Moving to the next RAM
@KBD
D=A-D	// Checks when we filled every pixel, when we reach to the keybord RAM(if D < 0)

@R0
M=M+1	// Move to the next pixel

@CHANGESCREEN
D;JGT // if(d > 0) we have pixels remaining to color, else we are done

@START // Starting over
0;JMP













