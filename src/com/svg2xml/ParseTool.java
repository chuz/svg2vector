package com.svg2xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ParseTool {
    SAXParserFactory mSAXParserFactory;
    SAXParser mSAXParser;
    ParseHandler mParseHandler;

    public ParseTool() {
        // TODO Auto-generated constructor stub
        mSAXParserFactory = SAXParserFactory.newInstance();
        try {
            // 不验证 <!DOCTYPE svg
            // ..www.w3.org/Graphics/VECTOR/1.1/DTD/svg11.dtd">
            mSAXParserFactory
                    .setFeature(
                            "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                            false);
            mSAXParserFactory.setFeature(
                    "http://xml.org/sax/features/validation", false);
        } catch (SAXNotRecognizedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SAXNotSupportedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            mSAXParser = mSAXParserFactory.newSAXParser();
            mParseHandler = new ParseHandler();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public void parseFile(String fileName) {
        if (mSAXParser == null) {
            System.out.println("mSAXParser == null");
            return;
        }
        try {
            mSAXParser.reset();
            mParseHandler.setFileName(fileName);
            mSAXParser.parse(fileName, mParseHandler);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class ParseHandler extends DefaultHandler {
    public static final String TAG_SVG_HEAD = "svg";
    public static final String TAG_SVG_GROUNP = "g";
    public static final String TAG_SVG_WIDTH = "width";
    public static final String TAG_SVG_HEIGHT = "height";
    public static final String TAG_SVG_VIEWBOX = "viewBox";

    public static final String TAG_SVG_PATH = "path";
    public static final String TAG_SVG_PATHDATA = "d";
    public static final String TAG_SVG_PATHSTYLE = "style";
    public static final String TAG_SVG_PATH_OPACITY = "opacity";
    public static final String TAG_SVG_PATH_FILLCOLOR = "fill";
    public static final String TAG_SVG_PATH_FILLALPHA = "fill-opacity";
    public static final String TAG_SVG_PATH_STROKECOLOR = "stroke";
    public static final String TAG_SVG_PATH_STROKEALPHA = "stroke-opacity";
    public static final String TAG_SVG_PATH_STROKEWIDTH = "stroke-width";
    public static final String TAG_SVG_PATH_STROKEMITERLIMIT = "stroke-miterlimit";
    public static final String TAG_SVG_PATH_STROKELINEJOIN = "stroke-linejoin";
    public static final String TAG_SVG_PATH_STROKELINECAP = "stroke-linecap";

    public static final String TAG_VECTOR_HEAD = "vector";
    public static final String TAG_VECTOR_GROUNP = "group";
    public static final String TAG_VECTOR_WIDTH = "android:width";
    public static final String TAG_VECTOR_HEIGHT = "android:height";
    public static final String TAG_VECTOR_VIEWPORTWIDTH = "android:viewportWidth";
    public static final String TAG_VECTOR_VIEWPORTHEIGHT = "android:viewportHeight";

    public static final String TAG_VECTOR_PATH = "path";
    public static final String TAG_VECTOR_PATHDATA = "android:pathData";
    public static final String TAG_VECTOR_PATH_FILLCOLOR = "android:fillColor";
    public static final String TAG_VECTOR_PATH_FILLALPHA = "android:fillAlpha";
    public static final String TAG_VECTOR_PATH_STROKECOLOR = "android:strokeColor";
    public static final String TAG_VECTOR_PATH_STROKEALPHA = "android:strokeAlpha";
    public static final String TAG_VECTOR_PATH_STROKEWIDTH = "android:strokeWidth";
    public static final String TAG_VECTOR_PATH_STROKEMITERLIMIT = "android:strokeMiterLimit";
    public static final String TAG_VECTOR_PATH_STROKELINEJOIN = "android:strokeLineJoin";
    public static final String TAG_VECTOR_PATH_STROKELINECAP = "android:strokeLineCap";

    StringBuilder mContent = new StringBuilder();
    String mFileName;

    public void setFileName(String fileName) {
        mFileName = fileName;
        if (mContent.length() > 0) {
            mContent.delete(0, mContent.length() - 1);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        System.out.println("start parse file： " + mFileName);
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("file： " + mFileName + " parse finish.");
        generateVectorDrawable(mContent.toString(), mFileName);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        System.out.println("get element " + qName);
        System.out.println("    attributes");
        for (int l = 0; l < attributes.getLength(); l++) {
            System.out.println("    " + attributes.getQName(l) + "=\""
                    + attributes.getValue(l) + "\"");
        }

        if (qName.equals(TAG_SVG_HEAD)) {
            generateVectorHead(attributes);
        } else if (qName.equals(TAG_SVG_GROUNP)) {
            mContent.append("<" + TAG_VECTOR_GROUNP + ">");
            mContent.append("\n");
        } else if (qName.equals(TAG_SVG_PATH)) {
            generateVectorPath(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        System.out.println("finish element " + qName);
        if (qName.equals(TAG_SVG_HEAD)) {
            mContent.append("</" + TAG_VECTOR_HEAD + ">");
        } else if (qName.equals(TAG_SVG_GROUNP)) {
            mContent.append("</" + TAG_VECTOR_GROUNP + ">");
            mContent.append("\n");
        } else if (qName.equals(TAG_SVG_PATH)) {
            mContent.append("</" + TAG_VECTOR_PATH + ">");
            mContent.append("\n");
        }
    }

    public void generateVectorHead(Attributes attributes) {
        mContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        mContent.append("\n");
        mContent.append("<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"");
        mContent.append("\n");
        // 获取高宽
        String ws = attributes.getValue(TAG_SVG_WIDTH);
        int w = Integer.valueOf(ws.substring(0, ws.length() - 3));
        String hs = attributes.getValue(TAG_SVG_HEIGHT);
        int h = Integer.valueOf(hs.substring(0, ws.length() - 3));
        // 获取viewPort尺寸
        String[] viewbox = attributes.getValue(TAG_SVG_VIEWBOX).split(" ");
        int vw = Integer.valueOf(viewbox[2]);
        int vh = Integer.valueOf(viewbox[3]);
        mContent.append("    " + TAG_VECTOR_WIDTH + "=\"" + w + "dp\"");
        mContent.append("\n");
        mContent.append("    " + TAG_VECTOR_HEIGHT + "=\"" + h + "dp\"");
        mContent.append("\n");
        mContent.append("    " + TAG_VECTOR_VIEWPORTWIDTH + "=\"" + vw + "\"");
        mContent.append("\n");
        mContent.append("    " + TAG_VECTOR_VIEWPORTHEIGHT + "=\"" + vh + "\"");
        mContent.append("\n");
        mContent.append("    >");
        mContent.append("\n");
    }

    public void generateVectorPath(Attributes attributes) {
        mContent.append("<" + TAG_VECTOR_PATH + " ");
        mContent.append("\n");
        // 获取path的style属性
        String style = attributes.getValue(TAG_SVG_PATHSTYLE);
        if (style == null) {
            mContent.append(TAG_VECTOR_PATH_FILLCOLOR + "=\"#000000\" ");
            mContent.append("\n");
        } else {
            String[] subattri = style.split(";");
            for (int s = 0; s < subattri.length; s++) {
                System.out.println(subattri[s]);
                if (subattri[s].contains(TAG_SVG_PATH_OPACITY)) {
                    mContent.append(TAG_VECTOR_PATH_FILLALPHA + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                    mContent.append(TAG_VECTOR_PATH_STROKEALPHA + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_FILLCOLOR)) {
                    mContent.append(TAG_VECTOR_PATH_FILLCOLOR + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_FILLALPHA)) {
                    mContent.append(TAG_VECTOR_PATH_FILLALPHA + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKECOLOR)) {
                    mContent.append(TAG_VECTOR_PATH_STROKECOLOR + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKEALPHA)) {
                    mContent.append(TAG_VECTOR_PATH_STROKEALPHA + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKEWIDTH)) {
                    mContent.append(TAG_VECTOR_PATH_STROKEWIDTH + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKEMITERLIMIT)) {
                    mContent.append(TAG_VECTOR_PATH_STROKEMITERLIMIT + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKELINEJOIN)) {
                    mContent.append(TAG_VECTOR_PATH_STROKELINEJOIN + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                } else if (subattri[s].contains(TAG_SVG_PATH_STROKELINECAP)) {
                    mContent.append(TAG_VECTOR_PATH_STROKELINECAP + "=\""
                            + getStyleSubAttriValue(subattri[s]) + "\" ");
                    mContent.append("\n");
                }
            }
        }
        // 获取pathData
        mContent.append(TAG_VECTOR_PATHDATA + "=\""
                + attributes.getValue(TAG_SVG_PATHDATA) + "\"");
        mContent.append("\n");
        mContent.append(">\n");
    }

    String getStyleSubAttriValue(String subattri) {
        int index = subattri.lastIndexOf(":");
        if (index > 0) {
            return subattri.substring(index + 1, subattri.length());
        } else {
            return subattri;
        }
    }

    @SuppressWarnings("resource")
    public void generateVectorDrawable(String xml, String fileName) {
        String storePath = extractFileNameWithoutExt(fileName) + ".xml";
        File vectorFile = new File(storePath);
        if (vectorFile.exists()) {
            vectorFile.delete();
        }
        try {
            vectorFile.createNewFile();
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(vectorFile);
            outputStream.write(xml.toString().getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
