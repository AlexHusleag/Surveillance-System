import time
import RPi.GPIO as GPIO
from camera import camera


GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(37, GPIO.IN)


def detect_motion():
    while True:
        i = GPIO.input(37)
        if i == 0:
            pass
        elif i == 1:
            print("Motion detected" * 3)
            return 1


# def detect_motion_and_record():
#     while True:
#         if detect_motion():
#             camera.record()
