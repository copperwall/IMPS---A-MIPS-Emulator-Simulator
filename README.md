# IMPS: A MIPS Emulator written in Java

IMPS began as a project for my CPE 315 Computer Architecture course. It currently supports about 20 MIPS instructions right now, but I plan to add more as time goes on. IMPS also emulates register and pc state, and can accept input from stdin or a script file.

## Building:
```shell
 $ make
```

### Javadocs:
```shell
 $ make doc
```

## Usage:
   
###    Without script file (stdin):
      
```shell
     $ java Imps [ASM_FILE]
```

###    With script file:

```shell
    $ java Imps [ASM_FILE] [SCRIPT_FILE]
```

## List of supported operations:
1. add
1. addi
1. sub
1. and
1. or
1. sll
1. sllv
1. srl
1. srlv
1. slt
1. sw
1. lw
1. beq
1. bne
1. j
1. jal
1. jr

I'll be adding more when I can.

I'm also going to try to document my classes/constructors/methods to get some javadocs experience, stay tuned.
