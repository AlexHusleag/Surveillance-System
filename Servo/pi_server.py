import ServoMotor
from socket import *
from time import ctime
import RPi.GPIO as GPIO

ServoMotor.setup()

ctrCmd = ['UP', 'DOWN']

HOST = ''
PORT = 21567
BUFSIZE = 1024
ADDR = (HOST, PORT)

tcpSerSock = socket(AF_INET, SOCK_STREAM)
tcpSerSock.bind(ADDR)
tcpSerSock.listen(5)

while True:
    print("Waiting for connection")
    tcpCliSock, addr = tcpSerSock.accept()
    print("...Connected from :", addr)
    tcpCliSock.send(bytes("Welcome to the server", "utf-8"))
    try:
        while True:

            data = tcpCliSock.recv(BUFSIZE)
            print("What did we receive", data.decode("utf-8"))
            if not data:
                print("No data")
                break
            if data.decode("utf-8") == ctrCmd[0]:
                print("Data up", data)
                ServoMotor.ServoUp()
                print("Increase", ServoMotor.cur_X)
            if data.decode("utf-8") == ctrCmd[1]:
                print("Data down", data)
                ServoMotor.ServoDown()
                print("Increase", ServoMotor.cur_X)
    except KeyboardInterrupt:
        tcpSerSock.close()
        ServoMotor.close()
        GPIO.cleanup()
        print("Error")

