import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	public static void main(String[] args) throws IOException {
        Socket s = new Socket(InetAddress.getLocalHost(), 9876);
        
        
        PrintWriter out =
                new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                s.getOutputStream())));

        //PrintWriter out = new PrintWriter(s.getOutputStream());
        
        BufferedReader in =
                new BufferedReader(
                new InputStreamReader(
                s.getInputStream()));

        Scanner sc;
        sc = new Scanner(System.in);
        System.out.print("Enter message: ");
        String str = sc.nextLine();
        
        out.println(str);
        out.flush();
        
        str = in.readLine();
        System.out.println(str);

        
        out.close();
        in.close();
        s.close();
        //Added comment
    }
}
