import cv2
from imutils.video import VideoStream


capture = cv2.VideoCapture(0)
capture.set(3, 480)
capture.set(4, 360)
capture.set(5, 20)

print(capture.get(3))
print(capture.get(4))
fourcc = cv2.VideoWriter_fourcc(*'H264')
video_writer = cv2.VideoWriter("video.mp4", fourcc, 20, (int(capture.get(3)), int(capture.get(4))))

while capture.isOpened():
    ret, frame = capture.read()
    # print(type(frame))
    if ret:
        video_writer.write(frame)
        cv2.imshow('Video Stream', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break


capture.release()
video_writer.release()
cv2.destroyAllWindows()