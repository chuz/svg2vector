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
    static String SAVE_DIR = "vector";

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

        File vectorSaveDir = new File(SAVE_DIR);
        if (vectorSaveDir.exists() && vectorSaveDir.isFile()) {
            vectorSaveDir.delete();
        }
        if (!vectorSaveDir.exists()) {
            vectorSaveDir.mkdir();
        }
    }

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
    float mDpiScale = 1.0f;

    static final String FOUR_SPACE = "    ";
    int mEmptyGroupCount = 0;
    int mElementCount = 0;
    StringBuilder mSpaceBuilder = new StringBuilder();

    public ParseHandler() {
        // TODO Auto-generated constructor stub

    }

    public void setFileName(String fileName) {
        mFileName = fileName;
        mDpiScale = MainLoader.mDpiScale;
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
        System.out.println("    attributes:");
        mElementCount++;
        for (int l = 0; l < attributes.getLength(); l++) {
            System.out.println("    " + attributes.getQName(l) + "=\""
                    + attributes.getValue(l) + "\"");
        }

        if (qName.equals(TAG_SVG_HEAD)) {
            generateVectorHead(attributes);
            mElementCount--;
        } else if (qName.equals(TAG_SVG_GROUNP)) {
            mEmptyGroupCount++;
            contentAppendWithSpace("<" + TAG_VECTOR_GROUNP + ">");
            contentAppendWithSpace(0,"\n");
        } else if (qName.equals(TAG_SVG_PATH)) {
            mEmptyGroupCount = 0;
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
            if (mEmptyGroupCount > 0) {
                mEmptyGroupCount--;
                mContent.delete(mContent.length() - 2 - TAG_VECTOR_GROUNP.length() - generateSpaces(mElementCount).length() - 1, mContent.length());
            } else {
                contentAppendWithSpace("</" + TAG_VECTOR_GROUNP + ">");
                contentAppendWithSpace(0,"\n");
            }
            mElementCount--;
        } else if (qName.equals(TAG_SVG_PATH)) {
            contentAppendWithSpace("</" + TAG_VECTOR_PATH + ">");
            contentAppendWithSpace(0,"\n");
            mElementCount--;
        }
    }

    public void generateVectorHead(Attributes attributes) {
        mContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        contentAppendWithSpace(0,"\n");
        mContent.append("<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"");
        contentAppendWithSpace(0,"\n");
        // 获取高宽
        String ws = attributes.getValue(TAG_SVG_WIDTH);
        int w = 0;
        try {
            w = Integer.valueOf(ws.substring(0, ws.length() - 2));
        } catch (Exception e) {
            // TODO: handle exception
            w = Integer.valueOf(ws);
        }
        String hs = attributes.getValue(TAG_SVG_HEIGHT);
        int h = 0;
        try {
            h = Integer.valueOf(hs.substring(0, hs.length() - 2));
        } catch (Exception e) {
            // TODO: handle exception
            h = Integer.valueOf(hs);
        }
        // 获取viewPort尺寸，没有则默认使用高宽
        int vw = 0, vh = 0;
        if (attributes.getValue(TAG_SVG_VIEWBOX) != null) {
            String[] viewbox = attributes.getValue(TAG_SVG_VIEWBOX).split(" ");
            vw = Integer.valueOf(viewbox[2]);
            vh = Integer.valueOf(viewbox[3]);
        } else {
            vw = w;
            vh = h;
        }
        contentAppendWithSpace(TAG_VECTOR_WIDTH + "=\"" + (int)(w / mDpiScale) + "dp\"");
        contentAppendWithSpace(0,"\n");
        contentAppendWithSpace(TAG_VECTOR_HEIGHT + "=\"" + (int)(h / mDpiScale) + "dp\"");
        contentAppendWithSpace(0,"\n");
        contentAppendWithSpace(TAG_VECTOR_VIEWPORTWIDTH + "=\"" + vw + "\"");
        contentAppendWithSpace(0,"\n");
        contentAppendWithSpace(TAG_VECTOR_VIEWPORTHEIGHT + "=\"" + vh + "\">");
//        contentAppendWithSpace(0,"\n");
//        contentAppendWithSpace(">");
        contentAppendWithSpace(0,"\n");
    }

    public void generateVectorPath(Attributes attributes) {
        contentAppendWithSpace("<" + TAG_VECTOR_PATH + " ");
        contentAppendWithSpace(0,"\n");
        mElementCount++;
        // 获取path的style属性
        String style = attributes.getValue(TAG_SVG_PATHSTYLE);
        if (style == null) {
            handleSimpleAttriValueInPath(attributes, mContent);
        } else {
            handleStyleSubAttriValue(style, mContent);
        }
        // 获取pathData
        contentAppendWithSpace(TAG_VECTOR_PATHDATA + "=\""
                + attributes.getValue(TAG_SVG_PATHDATA) + "\"");
//        contentAppendWithSpace(0,"\n");
        contentAppendWithSpace(">\n");
        mElementCount--;
    }

    /**
     * 处理path里面除pathdata之外的属性
     * @param attributes
     * @param content
     */
    void handleSimpleAttriValueInPath(Attributes attributes, StringBuilder content) {
        //alpha属性同时影响fillAlpha 和 strokeAlpha
        boolean getFillAlphaAttri = false, getStrokeAlphaAttri = false;
        float fillAlphaValue = 0f, strokeAlphaValue = 0f;
        for (int index = 0; index < attributes.getLength(); index++) {
            if (attributes.getQName(index).equals(TAG_SVG_PATH_OPACITY)) {
                fillAlphaValue = Float.valueOf(attributes.getValue(index));
                strokeAlphaValue = fillAlphaValue;
                getFillAlphaAttri = true;
                getStrokeAlphaAttri = true;
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_FILLCOLOR)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_FILLCOLOR + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_FILLALPHA)) {
                getFillAlphaAttri = true;
                if (attributes.getValue(index).equals("null")) {
                    fillAlphaValue = 1.0f;
                } else {
                    fillAlphaValue = Float.valueOf(attributes.getValue(index));
                }
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKECOLOR)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKECOLOR + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKEALPHA)) {
                getStrokeAlphaAttri = true;
                if (attributes.getValue(index).equals("null")) {
                    fillAlphaValue = 1.0f;
                } else {
                    strokeAlphaValue = Float.valueOf(attributes.getValue(index));
                }
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKEWIDTH)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKEWIDTH + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKEMITERLIMIT)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKEMITERLIMIT + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKELINEJOIN)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKELINEJOIN + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (attributes.getQName(index).equals(TAG_SVG_PATH_STROKELINECAP)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKELINECAP + "=\""
                        + getStyleSubAttriValue(attributes.getValue(index)) + "\" ");
                contentAppendWithSpace(0,"\n");
            }
        }
        if (getFillAlphaAttri) {
            contentAppendWithSpace(TAG_VECTOR_PATH_FILLALPHA + "=\"" + fillAlphaValue
                    + "\" ");
            contentAppendWithSpace(0,"\n");
        }
        if (getStrokeAlphaAttri) {
            contentAppendWithSpace(TAG_VECTOR_PATH_STROKEALPHA + "=\"" + strokeAlphaValue
                    + "\" ");
            contentAppendWithSpace(0,"\n");
        }
    }

    /**
     * 处理path里面出现style的情况
     * @param style
     * @param content
     */
    void handleStyleSubAttriValue(String style, StringBuilder content) {
        String[] subattri = style.split(";");
        //alpha属性同时影响fillAlpha 和 strokeAlpha
        boolean getFillAlphaAttri = false, getStrokeAlphaAttri = false;
        float fillAlphaValue = 0f, strokeAlphaValue = 0f;
        for (int s = 0; s < subattri.length; s++) {
            System.out.println("        " + subattri[s]);
            if (subattri[s].contains(TAG_SVG_PATH_OPACITY)) {
                if (getStyleSubAttriValue(subattri[s]).equals("null")) {
                    fillAlphaValue = 1.0f;
                } else {
                    fillAlphaValue = Float.valueOf(getStyleSubAttriValue(subattri[s]));
                }
                strokeAlphaValue = fillAlphaValue;
                getFillAlphaAttri = true;
                getStrokeAlphaAttri = true;
            } else if (subattri[s].contains(TAG_SVG_PATH_FILLCOLOR)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_FILLCOLOR + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (subattri[s].contains(TAG_SVG_PATH_FILLALPHA)) {
                getFillAlphaAttri = true;
                if (getStyleSubAttriValue(subattri[s]).equals("null")) {
                    fillAlphaValue = 1.0f;
                } else {
                    fillAlphaValue = Float.valueOf(getStyleSubAttriValue(subattri[s]));
                }
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKECOLOR)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKECOLOR + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKEALPHA)) {
                getStrokeAlphaAttri = true;
                if (getStyleSubAttriValue(subattri[s]).equals("null")) {
                    strokeAlphaValue = 1.0f;
                } else {
                    strokeAlphaValue = Float.valueOf(getStyleSubAttriValue(subattri[s]));
                }
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKEWIDTH)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKEWIDTH + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKEMITERLIMIT)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKEMITERLIMIT + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKELINEJOIN)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKELINEJOIN + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            } else if (subattri[s].contains(TAG_SVG_PATH_STROKELINECAP)) {
                contentAppendWithSpace(TAG_VECTOR_PATH_STROKELINECAP + "=\""
                        + getStyleSubAttriValue(subattri[s]) + "\" ");
                contentAppendWithSpace(0,"\n");
            }
        }
        if (getFillAlphaAttri) {
            contentAppendWithSpace(TAG_VECTOR_PATH_FILLALPHA + "=\"" + fillAlphaValue
                    + "\" ");
            contentAppendWithSpace(0,"\n");
        }
        if (getStrokeAlphaAttri) {
            contentAppendWithSpace(TAG_VECTOR_PATH_STROKEALPHA + "=\"" + strokeAlphaValue
                    + "\" ");
            contentAppendWithSpace(0,"\n");
        }
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
        File vectorFile = new File(ParseTool.SAVE_DIR + "/" + storePath);
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

    void contentAppendWithSpace(String xml) {
        contentAppendWithSpace(mElementCount, xml);
    }

    void contentAppendWithSpace(int spaceNum, String xml) {
        mContent.append(generateSpaces(spaceNum) + xml);
    }

    String generateSpaces(int num) {
        if (mSpaceBuilder.length() > 0) {
            mSpaceBuilder.delete(0, mContent.length() - 1);
        }
        for (int i = 0; i < num; i++) {
            mSpaceBuilder.append(FOUR_SPACE);
        }
        return mSpaceBuilder.toString();
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
