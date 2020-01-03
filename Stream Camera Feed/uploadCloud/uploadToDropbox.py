import dropbox
import os

FILE_PATH = './convertedVideo.mp4'

file_size = os.path.getsize(FILE_PATH)

CHUNK_SIZE = 4 * 1024 * 1024

with open(FILE_PATH, 'rb') as f:

    dbx = dropbox.Dropbox('VBoOUgDO7KAAAAAAAAAAD3fgUjEfzIyhkd2uEptIZ9ZA2dnpCtutttr5Ct_GdeL8')
    if file_size <= CHUNK_SIZE:
        dbx.files_upload(f.read(), '/uploaded2.mp4')
    else:
        upload_session_start_result = dbx.files_upload_session_start(f.read(CHUNK_SIZE))
        cursor = dropbox.files.UploadSessionCursor(session_id=upload_session_start_result.session_id,
                                                   offset=f.tell())
        commit = dropbox.files.CommitInfo(path='/uploaded2.mp4')

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
