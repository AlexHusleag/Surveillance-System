import getch
from mail import mail
from camera import camera


char = getch.getch()
while True:
    if char == 'a':
        mail.sendEmail()
        camera.record()
        break




