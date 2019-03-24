import hashlib
import random
import string


def ensure_limit_on_password():
    pwd = input("What is your desired password?")
    if not (2 < len(pwd) < 5):
        print("Passwords must have a length between 2 to 5!")
        ensure_limit_on_password()
    return pwd


def gen_salt():
    return ''.join([random.choice(string.ascii_letters + string.digits) for i in range(4)])


def hash_pass(pwd, s):
    return hashlib.sha256(s.encode() + pwd.encode()).hexdigest()


def write_to_file(usn, s, hashedpw):
    p = open("pwd.txt", "w")
    p.write(usn + ',' + s + ',' + hashedpw)
    print("User, salt, and hashed password have been saved into pwd.txt!")


user = input("What is your desire username?")
password = ensure_limit_on_password()
salt = gen_salt()
hashed_pwd = hash_pass(password, salt)
write_to_file(user, salt, hashed_pwd)



