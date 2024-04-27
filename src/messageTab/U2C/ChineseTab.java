package messageTab.U2C;

import burp.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author bit4woo
 * @version CreateTime：2022年1月15日 下午11:07:59
 * <p>
 * 想要正确显示中文内容，有三个编码设置会影响结果：
 * 1、原始编码，通过代码尝试自动获取，但是结果可能不准确，极端情况下需要手动设置。
 * 2、转换后的编码，手动设置。
 * 3、burp设置的显示编码，显示时时用的编码，应该和转换后的编码一致。
 * <p>
 * 原始数据是byte[],但也是文本内容的某种编码的byte[].
 * @github https://github.com/bit4woo
 */
public class ChineseTab implements IMessageEditorTab {


    private ChinesePanel panel;

    private byte[] originContent;
    private String detectedCharset;
    private int charSetIndex;


    public ChineseTab(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks) {
        panel = new ChinesePanel(this);
        BurpExtender.getCallbacks().customizeUiComponent(panel);//尝试使用burp的font size
    }


    @Override
    public String getTabCaption() {
        return "Chinese";
    }

    @Override
    public Component getUiComponent() {
        return panel;
    }

    @Override
    public boolean isEnabled(byte[] content, boolean isRequest) {
        return true;
    }

    public byte[] getOriginContent() {
        return originContent;
    }

    public void setOriginContent(byte[] originContent) {
        this.originContent = originContent;
    }

    public List<String> getCharsetList() {
        String encoding = "GBK,GB2312,UTF-8,GB18030,Big5,Big5-HKSCS";
        List<String> encodingList = new ArrayList<>(Arrays.asList(encoding.split(",")));
        if (StringUtils.isNotEmpty(detectedCharset)) {
            encodingList.remove(detectedCharset);
            encodingList.add(0, detectedCharset);
        }
        return encodingList;
    }

    public String getCurrentCharSet() {
        return getCharsetList().get(charSetIndex);
    }

    public String getNextCharSet() {
        List<String> charsetList = getCharsetList();
        if (charSetIndex < charsetList.size() - 1) {
            charSetIndex++;
        } else {
            charSetIndex = 0;
        }
        return charsetList.get(charSetIndex);
    }

    @Override
    public void setMessage(byte[] content, boolean isRequest) {
        originContent = content;
        detectedCharset = BurpExtender.getHelperPlus().detectCharset(isRequest, content);
        if (StringUtils.isEmpty(detectedCharset)) {
            panel.display(content, isRequest, "UTF-8");
        } else {
            panel.display(content, isRequest, detectedCharset);
        }
    }

    /**
     * 中文下的编辑还是有问题，暂不支持。
     * 始终返回原始内容。
     */
    @Override
    public byte[] getMessage() {
        return originContent;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public byte[] getSelectedData() {
//		return txtInput.getSelectedText();
        return null;
    }


    public static void main(String[] args) {
        String aaa = "STK_7411642209636022({\"errno\":1003,\"errmsg\":\"\\u7528\\u6237\\u672a\\u767b\\u5f55\",\"errmsg_lang\":{\"zh\":\"\\u7528\\u6237\\u672a\\u767b\\u5f55\",\"en\":\"User is not logged in.\",\"zh-HK\":\"\\u7528\\u6236\\u672a\\u767b\\u9304\"},\"data\":null});";
        System.out.println(StringEscapeUtils.unescapeJava(aaa));
        System.out.println(StringEscapeUtils.unescapeJava(aaa));
    }
}