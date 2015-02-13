package com.svg2xml;

import java.io.File;
import java.util.ArrayList;

public class MainLoader {
    ParseTool mParseTool;
    public static final String[] DPIs = { "mhdpi", "hdpi", "xhdpi", "xxhdpi",
            "xxxhdpi" };
    public static final float[] DPI_SCALEs = { 1.0f, 1.5f, 2.0f, 3.0f, 4.0f };
    public static float mDpiScale = 1.0f;
    public static final String SVG_SUFFIX = ".svg";

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
        for (int i = 0; i < file.list().length; i++) {
            if (isSvgFile(dir[i])) {
                System.out.print("get file " + dir[i] + ",");
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
     * 处理输出的参数
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
                if (args[index].equals("-f")) {
                    get_F = true;
                    targetFileList.clear();
                    continue;
                } else if (args[index].equals("-s")) {
                    get_S = true;
                    continue;
                } else if (args[index].equals("-h")) {
                    System.out.println(System.getProperty("java.class.path")
                            + " [-f] [<files ...>] [-s <xhdpi>] [-h]");
                    System.out
                            .println("no args    - all files in this directory will be parsed.");
                    System.out
                            .println("-f         - the files will be parsed.");
                    System.out
                            .println("-s         - Select DPI to be converted.");
                    System.out.print("             <");
                    for (int i = 0; i < DPIs.length; i++) {
                        System.out.print(DPIs[i]
                                + (i != DPIs.length - 1 ? " " : ""));
                    }
                    System.out.println(">");
                    System.out.println("-h         - show this help message.");
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
        System.out.print("\n");
        if (targetFileList.size() > 0) {
            for (String file : targetFileList) {
                mParseTool.parseFile(file);
            }
        } else if (targetFileList.size() == 0) {
            handleDefault();
        }
    }

    boolean isSvgFile(String fileName) {
        if(fileName != null && fileName.length() > SVG_SUFFIX.length()) {
             return fileName.endsWith(SVG_SUFFIX);
        }
        return false;
    }
}
