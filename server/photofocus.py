from flask import Flask, abort, request, send_file
from werkzeug import secure_filename
import hashlib
import os
import sqlite3

app = Flask(__name__)
DATABASE = 'database.db'
UPLOAD_FOLDER = 'uploaded/'
SERVER_IP = '0.0.0.0'
SERVER_PORT = 5000
DEBUG = True

def get_db():
    db = sqlite3.connect(DATABASE)
    db.row_factory = sqlite3.Row
    db.text_factory = str
    return db

def init_db():
    with app.app_context():
        db = get_db()
        with app.open_resource('schema.sql') as f:
            db.cursor().executescript(f.read())
            db.commit()

def query_db(query, args=[], one=False):
    cur = get_db().execute(query, args)
    rv = cur.fetchall()
    return (rv[0] if rv else None) if one else rv

def generate_hash(text):
    return hashlib.sha1(text).hexdigest()

def get_user_id_name(username):
    rv = query_db('''select user_id from users where username = ?''',
                  [username], one=True)
    return rv[0] if rv else None

def get_user_id_token(auth_token):
    rv = query_db('''select user_id from users where auth_token = ?''',
                  [auth_token], one=True)
    return rv[0] if rv else None

def check_logged_in():
    auth_token = request.headers.get('auth_token', '')
    username = query_db('''select username from users where auth_token = ?''',
                        [auth_token], one=True)
    if username is None:
        abort(401)

@app.route('/photos', methods=['GET', 'POST'])
def photos():
    check_logged_in()
    if request.method == 'GET':
        photos = query_db('''select * from photos where latitude >= ? and
                          latitude <= ? and longitude >= ? and longitude <= ?''',
                          [request.args['min_latitude'], request.args['max_latitude'],
                           request.args['min_longitude'], request.args['max_longitude']])
        return str([dict(p) for p in photos]).replace("\'", "\"")
    else:
        user_id = get_user_id_token(request.headers['auth_token'])
        photo = request.files['photo']
        mimetype = photo.content_type
        latitude = request.form['latitude']
        longitude = request.form['longitude']
        db = get_db()
        db.execute('''insert into photos (mimetype, latitude, longitude, user_id)
                   values (?, ?, ?, ?)''',
                   [mimetype, latitude, longitude, user_id])
        photo_id = str(db.execute('''select last_insert_rowid()''').fetchone()[0])
        filepath = UPLOAD_FOLDER + photo_id
        photo.save(filepath)
        db.commit()
        return 'Photo added!'

@app.route('/photos/<int:photo_id>', methods=['GET'])
def show_photo(photo_id):
    check_logged_in()
    if request.args['show'] == 'image':
        mimetype = query_db('''select mimetype from photos where photo_id = ?''',
                            [photo_id], one=True)[0]
        return send_file(UPLOAD_FOLDER + str(photo_id), mimetype=mimetype)
    elif request.args['show'] == 'metadata':
        metadata = query_db('''select * from photos where photo_id = ?''',
                            [photo_id])
        return str([dict(m) for m in metadata]).replace("\'", "\"")

@app.route('/comments', methods=['GET', 'POST'])
def comments():
    check_logged_in()
    if request.method == 'GET':
        comments = query_db('''select * from comments where photo_id = ?''',
                            [request.args['photo_id']])
        return str([dict(c) for c in comments]).replace("\'", "\"")
    else:
        user_id = get_user_id_token(request.headers['auth_token'])
        db = get_db()
        db.execute('''insert into comments (body, user_id, photo_id) values (?, ?, ?)''',
                   [request.form['body'], user_id, request.form['photo_id']])
        db.commit()
        return 'Comment added!'

@app.route('/notes', methods=['GET', 'POST'])
def notes():
    check_logged_in()
    if request.method == 'GET':
        notes = query_db('''select * from notes where photo_id = ?''',
                         [request.args['photo_id']])
        return str([dict(n) for n in notes]).replace("\'", "\"")
    else:
        user_id = get_user_id_token(request.headers['auth_token'])
        db = get_db()
        db.execute('''insert into notes (body, user_id, photo_id) values (?, ?, ?)''',
                   [request.form['body'], user_id, request.form['photo_id']])
        db.commit()
        return 'Note added!'

@app.route('/login', methods=['POST'])
def login():
    error = None
    user = query_db('''select * from users where username = ?''',
                    [request.form['username']], one=True)
    if user is None:
        error = 'Invalid username'
    elif generate_hash(request.form['password']) != user['pw_hash']:
        error = 'Invalid password'
    return error if error else user['auth_token']

@app.route('/register', methods=['POST'])
def register():
    error = None
    if not request.form['username']:
        error = 'No username provided'
    elif not request.form['password']:
        error = 'No password provided'
    elif get_user_id_name(request.form['username']) is not None:
        error = 'Username already taken'
    else:
        db = get_db()
        pw_hash = generate_hash(request.form['password'])
        auth_token = generate_hash(request.form['username'] + pw_hash)
        db.execute('''insert into users (username, pw_hash, auth_token) values (?, ?, ?)''',
                   [request.form['username'], pw_hash, auth_token])
        db.commit()
    return error if error else 'Registered!'

if __name__ == '__main__':
    init_db()
    app.run(host=SERVER_IP, port=SERVER_PORT, debug=DEBUG)
