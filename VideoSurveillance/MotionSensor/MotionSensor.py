import RPi.GPIO as GPIO


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
