package com.svg2xml;

public class MainLoader {
    static MainLoader mMainLoader;

    public static void main(String[] args) {
        System.out.println("HI");
        mMainLoader = new MainLoader();
        for (int index = 0; index < args.length; index++) {
            System.out.println(args[index]);
            System.out.println(mMainLoader
                    .extractFileNameWithoutExt(args[index]));
        }
        System.out.println(mMainLoader.extractFileNameWithoutExt("wifi.svg"));

        ParseTool parseTool = new ParseTool();
        parseTool.parseFile("wifi.svg");
    }

    String extractFileNameWithoutExt(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        } else {
            return filename;
        }
    }
}
