package com.tumms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        HashMap<String, ArrayList<Item>> users = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter name:");
            String name = scanner.nextLine();

            ArrayList<Item> items = users.get(name);
            if (items == null){
                items = new ArrayList<>();
                users.put(name, items);
            }

            boolean isLoggedIn = true;
            while (isLoggedIn) {
                System.out.println("1. Create to-do item");
                System.out.println("2 Check/Uncheck to-do item");
                System.out.println("3. List all to-do items");
                System.out.println("4. Logout");

                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        System.out.println("Enter your to-to item");
                        String text = scanner.nextLine();
                        Item item = new Item(text, false);
                        items.add(item);
                        break;
                    case "2":
                        System.out.println("Which item do you want to toggle?");
                        int i = Integer.valueOf(scanner.nextLine());
                        Item item2 = items.get(i - 1);
                        item2.isDone = !item2.isDone;
                        break;
                    case "3":
                        for (int j = 0; j < items.size(); j++) {
                            Item item3 = items.get(j);
                            int num = j + 1;
                            String checkbox = "[ ]";
                            if (item3.isDone) {
                                checkbox = "[x]";
                            }

//                            System.out.println(checkbox + " " + num + ". " + item3.text);
                            System.out.printf("%s %s", checkbox, num, item3.text);

                        }
                        break;
                    case "4":
                        isLoggedIn = false;
                        break;

                    default:
                        System.out.println("Invalid option.");


                }
            }
        }

    }
}
