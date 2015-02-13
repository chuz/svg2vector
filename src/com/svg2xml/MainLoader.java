package com.svg2xml;

import java.io.File;
import java.util.ArrayList;

public class MainLoader {
    ParseTool mParseTool;
    public static final String SVG_SUFFIX = ".svg";
    public static final String[] DPIs = { "mdpi", "hdpi", "xhdpi", "xxhdpi",
            "xxxhdpi" };
    public static final float[] DPI_SCALEs = { 1.0f, 1.5f, 2.0f, 3.0f, 4.0f };
    public static float mDpiScale = 1.0f;
    public static boolean DBG = false;
    static final String SYMBOL_f = "-f";
    static final String SYMBOL_s = "-s";
    static final String SYMBOL_D = "-D";
    static final String SYMBOL_h = "-h";
    static final String SYMBOL_v = "-v";

    public static void main(String[] args) {
        new MainLoader(args);
    }

    public MainLoader(String[] args) {
        // TODO Auto-generated constructor stub
        mParseTool = new ParseTool();
        if (args.length > 0) {
            handleArgs(args);
        } else {
            handleDefault();
        }
        // mParseTool.parseFile("wifi.svg");
    }

    ArrayList<String> scanFile() {
        ArrayList<String> fileList = new ArrayList<>();
        File file = new File("").getAbsoluteFile();
        String[] dir;
        dir = file.list();
        if (file.list().length > 0) {
            System.out.print("get file:");
        }
        for (int i = 0; i < file.list().length; i++) {
            if (isSvgFile(dir[i])) {
                System.out.print(dir[i] + " ");
                fileList.add(dir[i]);
            }
        }
        if (fileList.size() > 0) {
            System.out.println("");
        }
        return fileList;
    }

    /**
     * 默认处理
     */
    void handleDefault() {
        ArrayList<String> fileList = scanFile();
        for (String file : fileList) {
            mParseTool.parseFile(file);
        }
    }

    /**
     * 处理输入的参数
     * @param args
     */
    void handleArgs(String[] args) {
        boolean get_F = false;
        boolean get_S = false;
        ArrayList<String> targetFileList = new ArrayList<>();
        for (int index = 0; index < args.length; index++) {
            // System.out.print(args[index]);
            // System.out.print(" ");
            if (args[index].startsWith("-")) {
                if (args[index].equals(SYMBOL_f)) {
                    get_F = true;
                    targetFileList.clear();
                    continue;
                } else if (args[index].equals(SYMBOL_s)) {
                    get_S = true;
                    continue;
                } else if (args[index].equals(SYMBOL_D)) {
                    DBG = true;
                    mParseTool.setDBG(DBG);
                    System.out.println("debug");
                    continue;
                } else if (args[index].equals(SYMBOL_h)) {
                    showHelpMessage();
                    System.exit(0);
                } else if (args[index].equals(SYMBOL_v)) {
                    showVersion();
                    System.exit(0);
                }
                get_F = false;
            }
            if (get_F) {
                if (isSvgFile(args[index])) {
                    targetFileList.add(args[index]);
                } else {
                    System.out.println("file type error: " + args[index]);
                }
            } else {
                if (isSvgFile(args[index])) {
                    targetFileList.add(args[index]);
                }
            }
            if (get_S) {
                get_S = false;
                for (int i = 0; i < DPIs.length; i++) {
                    if (args[index].equals(DPIs[i])) {
                        mDpiScale = DPI_SCALEs[i];
                    }
                }
            }
        }
        if (targetFileList.size() > 0) {
            for (String file : targetFileList) {
                mParseTool.parseFile(file);
            }
        } else if (targetFileList.size() == 0) {
            handleDefault();
        }
    }

    void showHelpMessage() {
        System.out.println(System.getProperty("java.class.path") + " ["
                + SYMBOL_f + "] [<files ...>] [" + SYMBOL_s + " <xhdpi>] ["
                + SYMBOL_D + "] [" + SYMBOL_v + "] [" + SYMBOL_h + "]");
        System.out
                .println("no args    - All files in this directory will be parsed.");
        System.out
                .println(SYMBOL_f + "         - The files will be parsed.");
        System.out
                .println(SYMBOL_s + "         - Select DPI to be converted.");
        System.out.print("             <");
        for (int i = 0; i < DPIs.length; i++) {
            System.out.print(DPIs[i]
                    + (i != DPIs.length - 1 ? " " : ""));
        }
        System.out.println(">");
        System.out
                .println(SYMBOL_D + "         - Show debug message.");
        System.out.println(SYMBOL_v + "         - Show version.");
        System.out.println(SYMBOL_h + "         - Show this help message.");
    }

    /**
     * 暂时这边改
     */
    void showVersion() {
        System.out.println("version 1.0");
    }

    boolean isSvgFile(String fileName) {
        if(fileName != null && fileName.length() > SVG_SUFFIX.length()) {
             return fileName.endsWith(SVG_SUFFIX);
        }
        return false;
    }
}
