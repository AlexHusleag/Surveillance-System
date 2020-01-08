import face_recognition
import numpy as np
import cv2

alex_face = face_recognition.load_image_file("dataset/Alex.png")
alex_face_encoding = face_recognition.face_encodings(alex_face)[0]

iosif_face = face_recognition.load_image_file("dataset/Iosif.jpg")
iosif_face_encoding = face_recognition.face_encodings(iosif_face)[0]

known_face_encodings = [
    alex_face_encoding,
    iosif_face_encoding
]

known_face_names = [
    "Alex",
    "Iosif"
]

face_locations = []
face_encodings = []
face_names = []


def process_frame(frame):
    # print("Frame is", frame.array)
    # # Resize frame of video to 1/4 size for faster face recognition processing
    # small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)

    # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
    # rgb_small_frame = small_frame[:, :, ::-1]

    # Find all the faces and face encodings in the current frame of video

    output = np.empty((300, 500, 3), dtype=np.uint8)
    output = frame

    # print("Output is", output.array)


    face_locations = face_recognition.face_locations(output)
    face_encodings = face_recognition.face_encodings(output, face_locations)

    face_names = []

    for face_encoding in face_encodings:
        # See if the face is a match for the known face(s)
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding)
        name = "Unknown"

        # If a match was found in known_face_encodings, just use the first one.
        if True in matches:
            first_match_index = matches.index(True)
            name = known_face_names[first_match_index]
            face_names.append(name)
        else:
            face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
            best_match_index = np.argmin(face_distances)
            face_names.append(name)

    return [face_locations, face_names]