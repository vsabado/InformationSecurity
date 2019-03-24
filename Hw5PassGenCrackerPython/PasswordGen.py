import hashlib
import random
import string


def ensure_limit_on_password(index):
    if index == 1:
        print("Part 1: all lower case")
    elif index == 2:
        print("Part 2: lowercase and uppercase")
    elif index == 3:
        print("Part 3 : lowercase, uppercase, numbers")
    elif index == 4:
        print("Part 4: lowercase, uppercase, numbers, and symbols")

    pwd = input("What is your desired password? \n")
    if not (2 < len(pwd) < 5):
        print("Passwords must have a length between 2 to 5!")
        ensure_limit_on_password(index)
    return pwd


def gen_salt():
    return ''.join(random.choice(string.ascii_letters + string.digits) for i in range(4))


def hash_pass(pwd, s):
    return hashlib.sha256(s.encode() + pwd.encode()).hexdigest()


def write_to_file(usn, s, hashedpw, index):
    p = open("pwd" + str(index) + ".txt", "w")
    p.write(usn + ',' + s + ',' + hashedpw)
    print("User, salt, and hashed password have been saved into pwd" + str(index) + ".txt!")


def get_info(user, index):
    password = ensure_limit_on_password(index)
    salt = gen_salt()
    print("Salt generated: " + salt)
    hashed_pwd = hash_pass(password, salt)
    write_to_file(user, salt, hashed_pwd, index)


user = input("What is your desired username? \n")
print("Create 4 sets of data for each part of the homework")
for i in range(1, 5):
    print("===============================================================================================")
    get_info(user, i)
