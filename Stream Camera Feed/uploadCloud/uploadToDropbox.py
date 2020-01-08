import dropbox
import os

# FILE_PATH = './convertedVideo.mp4'

# file_size = os.path.getsize(FILE_PATH)

CHUNK_SIZE = 4 * 1024 * 1024

def uploadToDropbox(FILE_PATH):
    file_size = os.path.getsize(FILE_PATH)

    with open(FILE_PATH, 'rb') as f:

        print(FILE_PATH)

        dbx = dropbox.Dropbox('VBoOUgDO7KAAAAAAAAAAJlp7JRdIrCeGK-_Xw_TYj0DjpOK7kB7s0y-UIcFqpoHg')

        if file_size <= CHUNK_SIZE:
            dbx.files_upload(f.read(), "/" + FILE_PATH)
        else:
            upload_session_start_result = dbx.files_upload_session_start(f.read(CHUNK_SIZE))
            cursor = dropbox.files.UploadSessionCursor(session_id=upload_session_start_result.session_id,
                                                       offset=f.tell())
            commit = dropbox.files.CommitInfo(path="/" + FILE_PATH)

            while f.tell() < file_size:
                if (file_size - f.tell()) <= CHUNK_SIZE:
                    print(dbx.files_upload_session_finish(f.read(CHUNK_SIZE),
                                                          cursor,
                                                          commit))
                else:
                    dbx.files_upload_session_append(f.read(CHUNK_SIZE),
                                                    cursor.session_id,
                                                    cursor.offset)
                    cursor.offset = f.tell()

        dbx.users_get_current_account()
        for entry in dbx.files_list_folder('').entries:
            print(entry.name)
