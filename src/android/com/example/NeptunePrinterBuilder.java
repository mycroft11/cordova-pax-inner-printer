package com.example;

import com.pax.dal.IPrinter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

public class NeptunePrinterBuilder {

    private static final String TAG = NeptunePrinterBuilder.class.getName();

    private static final EFontTypeAscii DEFAULT_FONT_ASCII = EFontTypeAscii.FONT_8_16;

    private static final EFontTypeExtCode DEFAULT_FONT_EXT_CODE = EFontTypeExtCode.FONT_16_16;

    private static final byte DEFAULT_WORD_SPACE = 2;

    private static final byte DEFAULT_LINE_SPACE = 2;

    private static final int DEFAULT_PRINTER_STEP = 5;

    private static final int DEFAULT_LEFT_INDENT = 20;

    public EFontTypeAscii eFontTypeAscii = DEFAULT_FONT_ASCII;

    public EFontTypeExtCode eFontTypeExtCode = DEFAULT_FONT_EXT_CODE;

    public byte wordSpaec = DEFAULT_WORD_SPACE;

    public byte lineSpace = DEFAULT_LINE_SPACE;

    public int printerStep = DEFAULT_PRINTER_STEP;

    public int leftIndent = DEFAULT_LEFT_INDENT;

    private IPrinter.IPinterListener mPrinterListener;


    public NeptunePrinterBuilder() {

    }

    public void seteFontTypeAscii(EFontTypeAscii eFontTypeAscii) {
        this.eFontTypeAscii = eFontTypeAscii;
    }

    public void seteFontTypeExtCode(EFontTypeExtCode eFontTypeExtCode) {
        this.eFontTypeExtCode = eFontTypeExtCode;
    }

    public void setWordSpaec(byte wordSpaec) {
        this.wordSpaec = wordSpaec;
    }

    public void setLineSpace(byte lineSpace) {
        this.lineSpace = lineSpace;
    }

    public void setPrinterStep(int printerStep) {
        this.printerStep = printerStep;
    }

    public void setLeftIndent(int leftIndent) {
        this.leftIndent = leftIndent;
    }

    public void setmPrinterListener(IPrinter.IPinterListener mPrinterListener) {
        this.mPrinterListener = mPrinterListener;
    }

//    public NeptunePrinterBuilder create() {
//
//    }

    public static NeptunePrinterBuilder build(){
        return new NeptunePrinterBuilder();
    }


}
