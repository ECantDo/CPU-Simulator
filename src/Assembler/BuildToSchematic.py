import sys
import mcschematic

save_path: str = ("E:/Desktop/Extra Minecraft Versions/"
                  "fabric-loader-0.15.11-1.21/config/worldedit"
                  "/schematics/CPU_Programs/")
if not save_path.endswith("/") and "/" in save_path:
    save_path += "/"
elif not save_path.endswith("\\") and "\\" in save_path:
    save_path += "\\"


def main():
    file_name = sys.argv[1]
    instructions = get_file_contents(file_name)
    build_to_schem(file_name[file_name.find("/") + 1 if "/" in file_name else 0:-4], instructions)
    pass


def build_to_schem(file_name: str, instructions: list[int], file_path: str = save_path) -> None:
    schem = mcschematic.MCSchematic()

    for i in range(256):
        instruction: int = 0
        if i < len(instructions):
            instruction: int = instructions[i]
        cords: tuple[int, int] = get_coordinates(i)
        for y, bit in enumerate(f"{instruction:32b}"):
            if bit == "1":
                block = "redstone_wall_torch[lit=false,facing=east]"
            else:
                block = "sea_lantern"

            schem.setBlock((cords[0], y * -2, cords[1]), block)

    schem.save(file_path, file_name, mcschematic.Version.JE_1_20)
    print(f"Saved to: {save_path}{file_name}")
    pass


def get_cell_location(instruction_number: int) -> tuple[int, int]:
    row = instruction_number % 8
    depth = instruction_number // 8
    # print(f"{row = }\t{depth = }")
    return row, depth


def get_coordinates(instruction_number: int) -> tuple[int, int]:
    location = get_cell_location(instruction_number)

    z = location[0]
    x = location[1]

    x = x * 2 + 2

    z_mult = 6
    if z % 2 == 0:
        z = z * z_mult
    else:
        z = (z - 1) * z_mult + 2

    # print(f"{x = }\t{z = }")
    return x, z
    pass


def get_file_contents(file_path: str) -> list[int]:
    with open(file_path) as f:
        file_contents = f.readlines()

    file_contents = [int(line.strip()[2:-1], 2) for line in file_contents]
    return file_contents


if __name__ == "__main__":
    main()
