import RPi.GPIO as GPIO
import time

cur_X = 0


def setup():
    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(12, GPIO.OUT)
    global servo
    servo = GPIO.PWM(12,50)
    servo.start(2.5)
    servo.ChangeDutyCycle(2.5)
    # start pwn with Duty Cycle 2% ---> Pulse with = 2 % * 20ms = 0.4 ms
    # Create PWN on pin 12 with frequency 50 HZ ---> period 20 ms


def ServoUp():
    global cur_X
    cur_X += 2.5
    if cur_X > 12:
        cur_X = 12.5
    servo.ChangeDutyCycle(cur_X)
    time.sleep(1)


def ServoDown():
    global cur_X
    cur_X -= 2.5
    if cur_X < 2.5:
        cur_X = 2.5
    servo.ChangeDutyCycle(cur_X)
    time.sleep(1)


def close():
    servo.stop()


# if __name__ == "__main__":
#     setup()