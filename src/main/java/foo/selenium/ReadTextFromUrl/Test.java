package foo.selenium.ReadTextFromUrl;

import java.util.Map;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Test {
	static Selenium s = new Selenium();
	
    public static void main( String[] args ) throws Throwable {
    	String url = "https://drive.google.com";
    	String url2 = "https://drive.google.com/drive/u/1/folders/0B66IZWExLatJTTFDQ3gxaVlMbXc";
    	String file = "output.txt";
    	Scanner teclado = new Scanner (System. in);
        System.out.println("mail: ");
        String user = teclado.next();
        System.out.println("pass: ");
        String pass = teclado.next();
        teclado.close();
    	s.run();
        s.goTo(url);
        Thread.sleep(3000);
        s.login(user,pass);
        s.goTo(url2);
        Thread.sleep(3000);
        Map<String,String> res = s.getList();
        s.print(res, file);
        s.end();
    }
}