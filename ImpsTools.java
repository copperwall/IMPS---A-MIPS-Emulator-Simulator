/**
 * MIPSSim: This class emulates a MIPS system
 * 
 * @author Chris Opperwall
 * @version 1.0 May 29, 2013
 */

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ImpsTools {
   
   // Instance Variables
   private int[] dataMem;
   private ArrayList<String> instMem;
   private ArrayList<Register> regFile;
   private int pc;
   private Scanner scan;

   /* Initialize dataMem
      Initialize regFile: call initReg() to set up
      Initialize instMem: Scan in all instructions?
   */
   public ImpsTools(File instructions) throws FileNotFoundException {
      dataMem = new int[8192];
      instMem = new ArrayList<String>();
      regFile = new ArrayList<Register>(32);
      pc = 0;

      initReg();
      initInstMem(instructions);
   }

   public void step(int num) {
      String opcode;

      for (int i = 0; i != num && pc < instMem.size(); i++) {
         scan = new Scanner(instMem.get(pc++));
         scan.useDelimiter("[\\s,()]+");

         while (scan.hasNext()) {
            opcode = scan.next();

            if (opcode.equals("add"))
               add(false);
            else if (opcode.equals("addi"))
               addi();
            else if (opcode.equals("and"))
               logic(true);
            else if (opcode.equals("or"))
               logic(false);
            else if (opcode.equals("slt"))
               slt();
            else if (opcode.equals("sw"))
               memIO(true);
            else if (opcode.equals("lw"))
               memIO(false);
            else if (opcode.equals("beq"))
               branch(true);
            else if (opcode.equals("bne"))
               branch(false);
            else if (opcode.equals("j"))
               jump(0);
            else if (opcode.equals("jal"))
               jump(1);
            else if (opcode.equals("jr"))
               jump(2);
         }
      }
      if (num > 0)
         System.out.println("        " + num + " instruction(s) executed");
   }

   public void dumpRegState() {
      System.out.println("\npc = " + pc);

      for (int i = 0; i < regFile.size();) {
         for (int j = 0; j < 4 && i < regFile.size(); j++) {
            System.out.print(regFile.get(i).name + " = " + 
             regFile.get(i).value + "         ");
            if (i == 0)
               System.out.print(" ");
            ++i;
         }
         System.out.println();
      }
   }

   public void memDisplay(int start, int stop) {
      while (start <= stop) {
         System.out.println("[" + start + "] = " + dataMem[start]);
         ++start;
      }
   }

   public void simReset() {
      dataMem = new int[8192];
      for (int i = 0; i < regFile.size(); i++)
         regFile.get(i).value = 0;

      System.out.println("        Simulator reset");
   }

   public void help() {
      System.out.println("\nh = show help");
      System.out.println("d = dump register state");
      System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
      System.out.println("s num = step through num instructions of the program");
      System.out.println("r = run until the program ends");
      System.out.println("m num1 num2 = display data memory from location num1 to num2");
      System.out.println("c = clear all registers, memory, and the program counter to 0");
      System.out.println("q = exit the program");
   }

   private void initReg() {
      regFile.add(new Register("$0"));
      regFile.add(new Register("$v0"));
      regFile.add(new Register("$v1"));
      regFile.add(new Register("$a0"));
      regFile.add(new Register("$a1"));
      regFile.add(new Register("$a2"));
      regFile.add(new Register("$a3"));

      for (int i = 0; i < 8; i++)
         regFile.add(new Register("$t" + i));

      for (int i = 0; i < 8; i++)
         regFile.add(new Register("$s" + i));

      regFile.add(new Register("$t8"));
      regFile.add(new Register("$t9"));
      regFile.add(new Register("$sp"));
      regFile.add(new Register("$ra"));
   }

   private void initInstMem(File instFile) throws FileNotFoundException {
      String temp;
      Scanner scan = new Scanner(instFile);

      while (scan.hasNext()) {
         temp = scan.nextLine().trim();
         if (temp.indexOf("#") != -1)
            temp = temp.substring(0, temp.indexOf("#"));
         if (temp.length() > 0)
            instMem.add(temp);
      }
   }

   // Begin private instruction methods
   
   private void add(boolean sub) {
      Register rd;
      int rs, rt;
      
      rd = getReg(scan.next());
      rs = getValue(scan.next());
      rt = getValue(scan.next());

      rd.value = sub ? rs - rt : rs + rt;
   }

   private void addi() {
      Register rd;
      int rs, immed;

      rd = getReg(scan.next());
      rs = getValue(scan.next());
      immed = scan.nextInt();

      rd.value = rs + immed;
   }

   private void logic(boolean and) {
      Register rd;
      int rs, rt;
      
      rd = getReg(scan.next());
      rs = getValue(scan.next());
      rt = getValue(scan.next());

      rd.value = and ? rs & rt : rs | rt;
   }

   private void branch(boolean beq) {
      int rs, rt;
      String label;

      rs = getValue(scan.next());
      rt = getValue(scan.next());
      label = scan.next();

      if ( beq ? rs == rt : rs != rt)
         pc = findLabel(label);
   }

   private void slt() {
      Register rd = getReg(scan.next());
      int rs, rt;

      rs = getValue(scan.next());
      rt = getValue(scan.next());

      rd.value = rs < rt ? 1 : 0;
   }

   private void memIO(boolean store) {
      Register rd = getReg(scan.next());
      int offset = scan.nextInt(), memAddr = getValue(scan.next());
      if (store)
         dataMem[memAddr + offset] = rd.value;
      else
         rd.value = dataMem[memAddr + offset];
   }

   // type decides the type of jump
   // 0 = j
   // 1 = jal
   // 2 = jr
   private void jump(int type) {
      if (type != 2) {
         if (type == 1)
            getReg("$ra").value = pc;
         pc = findLabel(scan.next());
      }
      else
         pc = getValue(scan.next());
   }

   private int findLabel(String label) {
      for (int i = 0; i < instMem.size(); i++)
         if (instMem.get(i).length() >= label.length())
            if (instMem.get(i).substring(0, label.length()).equals(label))
               return i;

      return -1;
   }

   private int getValue(String regName) {
      for (int i = 0; i < regFile.size(); i++)
         if (regFile.get(i).name.equals(regName))
            return regFile.get(i).value;

      return -1;
   }

   private Register getReg(String regName) {
      for (int i = 0; i < regFile.size(); i++)
         if (regFile.get(i).name.equals(regName))
            return regFile.get(i);

      return null;
   }

   private class Register {
      public String name;
      public int value;

      public Register(String name) {
         this.name = name;
      }
   }
}