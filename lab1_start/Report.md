## Part 0

clean with: 
```console
make clean
```
compile with:
```console
make all
```
reset wallet and pocket with:
```console
make resetFiles
```
run with:
```console
java ShoppingCart
```

## Part 1
### What is the shared resource? Who is sharing it?
Both the pocket.txt file and the wallet.txt file are shared resources, the system running the instances is sharing the resources (locally saved txt files).

### What is the root of the problem?
Time of check to time of use; there is a race condition allowing multiple users/instances to pass a check using an old value (time of check), and then updating the value before using it (time of use), effectively overriding the previous update. 

### Explain in detail how you can attack this system.
You can attack the system by sending commands in quick succession, passing the checks before an update is made. Example is buying something, then buying something else before the transaction is completed but after the balance check is made.

Let's say 2 terminals are open, one initiates a purchase of a car, while the other initiates a purchase of a pen shortly after. If the pen passes the check after the car, but before the car transaction updates the balance, the price of the pen will be used with the old balance to update the new balance, overriding the car purchase. The user will have a car and a pen, having only paid for a pen.

### Provide the program output and result, explaining the interleaving to achieve them.
Running above scenario gives the following result in the logs:


-------------------------------------------------------------------------
Your current balance is: 30000 credits.
car	30000
book	100
pen	40
candies	1

Your current pocket is:

User input: car
Your current balance is: 0 credits.
car	30000
book	100
pen	40
candies	1

Your current pocket is:
car

-------------------------------------------------------------------------
-------------------------------------------------------------------------
Your current balance is: 30000 credits.
car	30000
book	100
pen	40
candies	1

Your current pocket is:

User input: pen
Your current balance is: 29960 credits.
car	30000
book	100
pen	40
candies	1

Your current pocket is:
car
pen

-------------------------------------------------------------------------

As we see in the bottom, the balance goes from 0 to 29960 credits because of a successful exploit.

## Part 2
### Were there other APIs or resources suffering from possible races? If so, please explain them and update the APIs to eliminate any race problems.

Any time a shared resource is accessed, there should be a lock, otherwise there can be a race condition. In Wallet.setBalance() and Wallet.getBalance(), the wallet.txt file is accessed without locks. The same applies with Pocket.getPocket() and Pocket.addProduct().

### Why are these protections enough and at the same time not too excessive?

The locks prevents other threads/processes to access and read/modify a shared resource while another is using it for an operation. If a process tries to acquire an occupied lock, it is blocked until it is available again. If the critical section (the code executed while holding the lock) is kept to a minimum, the performance won't be affected more than it absolutely has to. The lock prevents race conditions, which is the minimal protection needed, and keeping the critical section short will prevent it from becoming too excessive.