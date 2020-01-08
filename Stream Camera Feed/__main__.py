import getch
from camera import camera


char = getch.getch()
while True:
    if char == 'a':
        camera.record()
        break




