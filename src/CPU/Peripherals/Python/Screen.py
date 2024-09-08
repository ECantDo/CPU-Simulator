from lamp_display import LampDisplay
from threading import Thread

import time
import jpysocket
import socket
import atexit
import ast


class Screen:
    def __init__(self):

        self.display: LampDisplay = LampDisplay((64, 64), 16)
        self.buffer = [False] * 64 * 64
        self.display.update()
        pass

    def clear(self):
        # self.display.clear()
        self.buffer = [False] * 64 * 64
        pass

    def update(self):
        self.display.update()
        pass

    def set_pixel(self, location: tuple[int, int], color):
        # self.display.set_pixel(location, color)
        self.buffer[location[1] * 64 + location[0]] = color
        pass

    def update_display(self):
        print("Updating Display")
        for y in range(64):
            for x in range(64):
                if self.buffer[y * 64 + x]:
                    print("Pixel", x, y)
                self.display.set_pixel((x, y), self.buffer[y * 64 + x])
        pass


def screen_threading(fps: int = 30):
    screen = Screen()
    global waiting
    global running
    global msg_recv
    global execution
    running = True
    while running:

        while waiting:
            # print("Waiting")
            screen.update()
            # screen.set_pixel(1, 1, 1)
            time.sleep(1 / fps)

        print(execution)

        if execution[0] == "stop":
            running = False
            break
        print(', '.join(map(str, execution[1:])))
        exec(f"screen.{execution[0]}({'*' if len(execution) > 1 else ''}{str(', '.join(map(str, execution[1:])))})")
        screen.update()

        connection.send(jpysocket.jpyencode("ok"))
        waiting = True

    pass


def socket_handler():
    global connection
    global waiting
    global msg_recv
    global execution
    global running

    print("Socket Handler Started.")
    while running:
        waiting = True
        msg_recv = jpysocket.jpydecode(connection.recv(1024)).strip()

        # Decode Message
        print("From Client: ", msg_recv)
        if msg_recv == "Stop":
            execution = ["stop"]
        elif msg_recv == "Clear":
            execution = ["clear"]
        elif msg_recv == "Update Display":
            execution = ["update_display"]
        elif msg_recv[:2] == "PP":
            # print(msg_recv[3:])
            execution = ["set_pixel", ast.literal_eval(msg_recv[3:])]
            # print(execution)
            # if the input is 'PP [(1, 2), True]' then it will be ['set_pixel', (1, 2), True]

        waiting = False
        while not waiting and running:
            time.sleep(0.1)

        # waiting = True
        pass
    pass


def setup_socket():
    global network
    global connection
    network = socket.socket()  # Create Socket
    network.bind((host, port))  # Bind Port And Host
    network.listen(5)  # Socket is Listening
    print("Socket Is Listening....")
    connection, address = network.accept()  # Accept the Connection
    print("Connected To ", address)
    # time.sleep(3)
    msgsend = jpysocket.jpyencode("Connection Established")  # Encript The Msg
    connection.send(msgsend)  # Send Msg
    msgrecv = connection.recv(1024)  # Recieve msg
    msgrecv = jpysocket.jpydecode(msgrecv)  # Decript msg
    print("From Client: ", msgrecv)
    pass


def main():
    setup_socket()
    screen_thread = Thread(target=screen_threading)
    screen_thread.start()
    socket_handler()

    pass


def exit_handler():
    global running
    global network
    network.close()
    print("Socket Closed.")
    # input("Press Enter To Exit.")


atexit.register(exit_handler)
try:
    host: str = "localhost"
    port: int = 50505
    network: socket.socket | None = None
    connection: socket.socket | None = None
    waiting = True
    running = True
    msg_recv = ""
    execution = ["clear"]

    main()
except Exception as error:
    print(error)
    # input("Press Enter To Exit.")
