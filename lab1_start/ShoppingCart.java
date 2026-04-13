import backEnd.*;
import java.util.Scanner;
import java.lang.Thread;
import java.io.RandomAccessFile;
import java.io.File;

public class ShoppingCart {
    private RandomAccessFile log;
    private static void print(Wallet wallet, Pocket pocket) throws Exception {
        System.out.println("Your current balance is: " + wallet.getBalance() + " credits.");
        System.out.println(Store.asString());
        System.out.println("Your current pocket is:\n" + pocket.getPocket());
    }

    private static String logState(Wallet wallet, Pocket pocket) throws Exception {
        return ("Your current balance is: " + wallet.getBalance() + " credits.\n"
        + Store.asString() + "\n" +
        "Your current pocket is:\n" + pocket.getPocket() + "\n");
    }

    private static void addLog(RandomAccessFile log, String message) throws Exception {
        log.seek(log.length());
        log.writeBytes(message + "\n");
    }

    private static String scan(Scanner scanner) throws Exception {
        System.out.print("What do you want to buy? (type quit to stop) ");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();
        Scanner scanner = new Scanner(System.in);

        RandomAccessFile log = new RandomAccessFile(new File("log.txt"), "rw");
        String logEntry = "";
        addLog(log, "\n");
        logEntry += "-------------------------------------------------------------------------\n";

        print(wallet, pocket);
        logEntry += logState(wallet, pocket);

        String product = scan(scanner);
        logEntry += "User input: " + product + "\n";


        while(!product.equals("quit")) {
            /* TODO:
               - check if the amount of credits is enough, if not stop the execution.
               - otherwise, withdraw the price of the product from the wallet.
               - add the name of the product to the pocket file.
               - print the new balance.
            */
           
           int price = Store.getProductPrice(product);
           if(wallet.safeWithdraw(price)){
                pocket.addProduct(product);
           }
           else{
            System.out.println("Not enough credit");
            logEntry += "Not enough credit \n";
           }

            // Just to print everything again...
            print(wallet, pocket);
            logEntry += logState(wallet, pocket);
            product = scan(scanner);
        }
        logEntry += "-------------------------------------------------------------------------";
        addLog(log, logEntry);
    }

}
