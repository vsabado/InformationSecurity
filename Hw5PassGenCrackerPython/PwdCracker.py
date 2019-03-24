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
            finish = time.time()
            print('Password found to be: ' + i + ', Attempts: ' + str(count) + ", Time : " + str(finish - start))
            break


def analyze_pass(index):
    file = open("pwd" + str(index) + ".txt", "r")
    password = file.read()
    user, salt, hashed_pwd = password.split(',')
    size = 4
    if index == 1:
        print("Checking pwd1.txt......")
        run_cracker(size, True, False, False, salt, hashed_pwd)
    elif index == 2:
        print("Checking pwd2.txt......")
        run_cracker(size, True, False, False, salt, hashed_pwd)
    elif index == 3:
        print("Checking pwd3.txt......")
        run_cracker(size, True, True, False, salt, hashed_pwd)
    elif index == 4:
        print("Checking pwd4.txt......")
        run_cracker(size, True, True, True, salt, hashed_pwd)
    user_interface()


def user_interface():
    print("=====================================================================================================")
    choice = input("Please choose a part: 1, 2, 3, 4, q to quit \n")
    if choice == '1':
        analyze_pass(int(choice))
    elif choice == '2':
        analyze_pass(int(choice))
    elif choice == '3':
        analyze_pass(int(choice))
    elif choice == '4':
        analyze_pass(int(choice))
    elif choice == 'q':
        return


# Driver
user_interface()
