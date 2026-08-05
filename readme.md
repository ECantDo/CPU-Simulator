# CPU-Simulator

A custom CPU architecture — 16-bit datapath, 32-bit instruction words — built from three pieces:

1. **A custom ISA** — a small RISC-style instruction set
2. **An assembler** (Java) — compiles `.as` source files into raw 32-bit machine code
3. **A CPU emulator** (Java) — executes the assembled machine code

The end goal is a **working CPU built out of Redstone in Minecraft**. The assembler's output can be converted into a
Minecraft schematic, where each 32-bit instruction word is encoded as totems-in-barrels (used as ROM) and dropped
straight into the world via WorldEdit.

```
 .as source  ──[Assembler]──▶  .bin (machine code)  ──[BuildToSchematic.py]──▶  .schem (Minecraft ROM)
                    │
                    └──[CPU Emulator]──▶  runs the program in software, for testing before it ever touches Minecraft
```

---

## Why

Building a CPU in Redstone is slow to iterate on - every change to a program means re-wiring or re-loading ROM in-game.
Critically, running the CPU in Minecraft, takes ages; the clock rate that I can manage *at best* in the game is 5Hz, but
I have not made mine quite that fast, compare that to the 5 Ghz that most modern CPUs can run at, and it's no question
better to design, iterate, and test things with an emulator. This project lets you write, assemble, and test programs
entirely outside Minecraft (in the emulator) before generating the actual ROM data to drop into the world.

---

## The ISA

Full opcode table, encoding formats, and instruction reference:

| Mnemonic | Description                                                                                                   |
|----------|---------------------------------------------------------------------------------------------------------------|
| HLT      | Halt; stop the CPU running                                                                                    |
| ADD      | Add two numbers                                                                                               |
| SUB      | Subtract two numbers                                                                                          |
| XOR      | Exclusive or; binary operate on two numbers                                                                   |
| OR       | Or; binary operate on two numbers                                                                             |
| AND      | And; binary operate on two numbers                                                                            |
| XNOR     | Exclusive Nor; binary operate on two numbers                                                                  |
| NOR      | Nor; binary operate on two numbers                                                                            |
| NAND     | Nand; binary operate on two numbers                                                                           |
| SL       | Logical shift left                                                                                            |
| SRA      | Arithmic shift right                                                                                          |
| SR       | Logical shift right                                                                                           |
| <hr>     | <hr>                                                                                                          |
| ADDI     | Add immediate; add two numbers where one is an immediate                                                      |
| ADDF     | Add with flags, basically add with carry, but it uses the CPU carry flag to decide if it should use the carry |
| SUBI     | Subtract immediate; subtract two numbers where one is an immediate                                            |
| LI       | Load immediate; loads in a 16 bit binary number                                                               |
| XORI     | Exclusive or; binary operate on two numbers where one is an immediate                                         |
| ORI      | Or; binary operate on two numbers where one is an immediate                                                   |
| ANDI     | And; binary operate on two numbers where one is an immediate                                                  |
| XNORI    | Exclusive Nor; binary operate on two numbers where one is an immediate                                        |
| NORI     | Nor; binary operate on two numbers where one is an immediate                                                  |
| NANDI    | Nand; binary operate on two numbers where one is an immediate                                                 |
| SLI      | Logical shift left where the shift ammount is an immediate                                                    |
| SRAI     | Arithmic shift right where the shift ammount is an immediate                                                  |
| SRI      | Logical shift right where the shift ammount is an immediate                                                   |
| <hr>     | <hr>                                                                                                          |
| BEQ      | Branch if equal                                                                                               |
| BNE      | Branch if not equal                                                                                           |
| BLT      | Branch if less than                                                                                           |
| BGE      | Branch if greater than or equal                                                                               |
| BLTU     | Branch if less than, unsigned                                                                                 |
| BGEU     | Branch if greather than or equal, unsigned                                                                    |
| <hr>     | <hr>                                                                                                          |
| JAL      | Jump and link                                                                                                 |
| JALR     | Jump and link register; use the address stored in the register as the jump location                           |
| <hr>     | <hr>                                                                                                          |
| STR      | Store register to RAM                                                                                         |
| LOD      | Load from RAM to register                                                                                     |
| <hr>     | <hr>                                                                                                          |
| IN       | Take an input from IO                                                                                         |
| OUT      | Send an output to IO                                                                                          |

Quick summary:

- **Datapath / register width:** 16 bits — registers, ALU, and immediates like `LI`'s operand are all 16-bit values.
- **Instruction width:** 32 bits — every instruction is encoded as a single 32-bit word (opcode + register selects +
  immediate bits, where applicable).
- **Registers:** 32 general-purpose registers, `r0`–`r31`. RISC-V-style aliases are also supported (`zero`, `ra`, `sp`,
  `gp`, `tp`, `t0`–`t6`, `s0`–`s11`, `a0`–`a7`) and can be used interchangeably with `rN` names.
- **Instruction categories:**
	- ALU ops: `add`, `sub`, `xor`, `or`, `and`, `xnor`, `nor`, `nand`
	- Barrel shifter: `sl`, `sra`, `sr`
	- Immediate versions of the above: `addi`, `subi`, `xori`, `ori`, `andi`, `xnori`, `nori`, `nandi`, `sli`, `srai`,
	  `sri`
		- Immediate values are 14 bit, with sign extension, only `li` loads in a 16 bit value. The 32-bit opcode is
		  missing the two needed bits for it to be the full word length
	- `li` — load immediate
	- `addf` — add with flags
	- Branches: `beq`, `bne`, `blt`, `bge`, `bltu`, `bgeu`
	- Jumps: `jal`, `jalr`
	- Memory: `lod`, `str`
	- I/O: `in`, `out`
	- `hlt`

---

## Assembly Language

### Syntax

- One instruction per line.
- `//` starts a comment (rest of line is ignored).
- Parentheses are stripped, so `addi(s0, 4, s2)` and `addi s0 4 s2` are equivalent.
- Labels are declared with a trailing colon on their own line (`loop:`) and can be used as branch/jump targets - the
  assembler resolves them to relative offsets.
- Constants are declared with `def NAME VALUE` and are substituted everywhere they appear before assembly. A constant
  name can't collide with an existing opcode mnemonic.

```asm
def MAX 10          // constant

loop:
    addi s0 1 s0     // s0 = s0 + 1
    blt s0 MAX loop  // branch back to loop while s0 < MAX
    hlt
```

### Pseudo-Instructions

These expand into real instructions before assembly:

| Pseudo           | Expands to           |
|------------------|----------------------|
| `mv rs rd`       | `add zero rs rd`     |
| `nop`            | `add zero zero zero` |
| `neg rs rd`      | `sub zero rs rd`     |
| `beqz rs imm`    | `beq rs zero imm`    |
| `bnez rs imm`    | `bne rs zero imm`    |
| `blez rs imm`    | `bge zero rs imm`    |
| `bgez rs imm`    | `bge rs zero imm`    |
| `bltz rs imm`    | `blt rs zero imm`    |
| `bgtz rs imm`    | `blt zero rs imm`    |
| `bgt rs rt imm`  | `blt rt rs imm`      |
| `ble rs rt imm`  | `bge rt rs imm`      |
| `bgtu rs rt imm` | `bltu rt rs imm`     |
| `bleu rs rt imm` | `bgeu rt rs imm`     |
| `j imm`          | `jal zero imm`       |
| `jal imm`        | `jal ra imm`         |
| `ret`            | `jalr zero 0 ra`     |

### Numeric literals

Immediate values can accept decimal, `0x` hex, `0b` binary, or a leading-`0` octal, e.g. `10`, `0xA`, `0b1010`, `012`.

---

## Usage

### 1. Assemble a program

Compile the assembler sources, then run `BuildToSchematic` (or call `Build.build(path)` directly) pointing at a `.as`
file:

```bash
javac -d out $(find src/Assembler -name "*.java")
java -cp out Assembler.BuildToSchematic
```

This produces a `.bin` file next to your source, containing the program as newline-separated 32-bit binary strings (e.g.
`0b11000101000000010000000000000001`).

> Currently, the input file path is hardcoded in `BuildToSchematic.main` - update `file_name` there, or call
> `Build.build("path/to/file.as")` from your own entry point.

### 2. Run it in the emulator

Load the resulting `.bin` (or the `int[]` returned directly from `Build.build(...)`) into the CPU emulator to test the
program in software.

### 3. Turn it into a Minecraft schematic

`Build.runBuildToSchem(filePath)` shells out to `runBuildToSchem.bat`, which invokes `BuildToSchematic.py`. That script
packs every 4 instructions into a barrel's totem slots (bit-per-item-count) and lays them out in a grid, ready to be
`//schem load`ed via WorldEdit into the in-game ROM.

**Requirements for this step:**

- Python 3 with [`mcschematic`](https://pypi.org/project/mcschematic/) installed
- Update the hardcoded `save_path` in `BuildToSchematic.py` to your own WorldEdit schematics folder

### Alternately

Use `src/Main.java`, and manually set `String programPath = ...;` to the program you want to run. `Main.java`
automatically compiles the program, and runs it without needing to go through each process.

---

## Status / TODO

- [ ] Parameterize hardcoded paths (`BuildToSchematic.java`, `BuildToSchematic.py`, `runBuildToSchem.bat`) instead of
  editing source
- [ ] Finish `build_to_schem` (currently has debug prints and an early `exit(0)`)
- [ ] Add proper CLI args / build tool (Maven or Gradle) instead of manual `javac`
- [ ] Write up the emulator side of this README once it's stable