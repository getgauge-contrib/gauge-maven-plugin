package com.thoughtworks.gauge.maven.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Util {
    public static void InheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine());
                }
            }
        }).start();
    }
}
