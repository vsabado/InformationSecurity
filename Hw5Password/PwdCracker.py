import hashlib
import time
from brute import brute


def run_cracker(length, letters, numbers, symbols, salt, pwd):
    print("Algorithm is running.......")
    count = 0
    start = time.time()
    for i in brute(length=length, letters=letters, numbers=numbers, symbols=symbols):
        count += 1
        if hashlib.sha256(salt.encode() + i.encode()).hexdigest() == pwd:
            end = time.time()
            print('Password found to be: ' + i)
            print('Attempts')
            print(count)
            print('Time')
            print(end - start)
            continue


def user_interface(salt, hashed_pwd):
    choice = input("Option 'a' for parts 1 and 2, option 'b' for part 3, option 'c' for part 4 \n")
    if choice == 'a':
        run_cracker(4, True, False, False, salt, hashed_pwd)

    elif choice == 'b':
        run_cracker(4, True, True, False, salt, hashed_pwd)

    elif choice == 'c':
        run_cracker(4, True, True, True, salt, hashed_pwd)


file = open("pwd.txt", "r")
password = file.read()
user, salt, hashed_pwd = password.split(',')
user_interface(salt, hashed_pwd)
