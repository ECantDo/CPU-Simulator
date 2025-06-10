import sys
import mcschematic
from Barrels import barrels

save_path: str = ("E:/Desktop/Extra Minecraft Versions/"
                  "fabric-loader-0.15.11-1.21/config/worldedit"
                  "/schematics/CPU2_Programs/")
if not save_path.endswith("/") and "/" in save_path:
    save_path += "/"
elif not save_path.endswith("\\") and "\\" in save_path:
    save_path += "\\"

instruction_count = 2 ** 16


def main():
    file_name = sys.argv[1]
    instructions = get_file_contents(file_name)
    build_to_schem(file_name[file_name.find("/") + 1 if "/" in file_name else 0:-4], instructions)
    pass


def build_to_schem(file_name: str, instructions: list[int], file_path: str = save_path) -> None:
    schem = mcschematic.MCSchematic()

    for i in range(0, len(instructions), 4):
        value = [0] * 32
        for j in range(4):
            print(f"{instructions[i + j]:032b}")
            for bit in range(32):
                value[bit] |= ((instructions[i + j] >> bit) & 1) << j
                pass
            pass

        print(value)
        coords = get_coordinates(512)
        print(coords)
        # Todo: remove prints; place barrels, get height offset (every odd x coord)
        exit(0)
        pass

    # schem.save(file_path, file_name.split("/")[-1], mcschematic.Version.JE_1_21)
    print(f"Saved to: {save_path}{file_name}")
    pass


def get_cell_location(instruction_number: int) -> tuple[int, int]:
    barrel_index = instruction_number // 4  # one barrel holds 4 instructions
    row = barrel_index // 64
    col = barrel_index % 64

    # Reverse column if row is odd (zigzag)
    # if row % 2 == 1:
    #     col = 63 - col
    print(f"{row = } {col = }")
    return row, col


def get_coordinates(instruction_number: int) -> tuple[int, int]:
    location = get_cell_location(instruction_number)

    x = location[0]
    z = location[1]

    odd = x & 1 == 1
    x = x // 2 * 3
    z = z * 2 + 6

    if odd:
        z = -z

    # print(f"{x = }\t{z = }")
    return x, z
    pass


def get_file_contents(file_path: str) -> list[int]:
    global instruction_count
    with open(file_path) as f:
        file_contents = f.readlines()

    file_contents = [int(line.strip(), 2) for line in file_contents]

    while len(file_contents) < instruction_count:
        file_contents.append(0)
        continue

    return file_contents


if __name__ == "__main__":
    main()
