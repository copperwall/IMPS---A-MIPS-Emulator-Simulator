import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Imps {
   public static void main(String[] args) throws FileNotFoundException {
      ImpsTools sys = new ImpsTools(new File(args[0]));
      Scanner scan, stringScan;
      String input;
      File script;

      // To echo the command there could be two scanners
      if (args.length == 2) {
         script = new File(args[1]);
         scan = new Scanner(script);
      }
      else
         scan = new Scanner(System.in);
      System.out.print("mips> ");
      
      while (scan.hasNext()) {
         input = scan.nextLine();
         if (args.length == 2)
            System.out.println(input);
         stringScan = new Scanner(input);
         input = stringScan.next();
         if (input.equals("d"))
            sys.dumpRegState();
         else if (input.equals("h"))
            sys.help();
         else if (input.equals("s")) {
            if (stringScan.hasNextInt())
               sys.step(stringScan.nextInt());
            else
               sys.step(1);
         }
         else if (input .equals("r"))
            sys.step(-1);
         else if (input.equals("m"))
            sys.memDisplay(stringScan.nextInt(), stringScan.nextInt());
         else if (input.equals("c"))
            sys.simReset();
         else if (input.equals("q"))
            break;
         System.out.print("\nmips> ");
      }
   }
}