package com.algo.trading.gui.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Slf4j
public class Utilities {

    /**
     * Escapes back-slash and single quote for JS string literal.
     */
    public static String strReplace(String element) {
        return element.replace("\\", "\\\\").replace("'", "\\'");
    }

    /**
     * Dumps basic DOM info for every <input> element – debug helper.
     */
    public static void dumpDom(Document doc) {
        if (doc == null) return;
        NodeList ins = doc.getElementsByTagName("input");
        for (int i = 0; i < ins.getLength(); i++) {
            Element element = (Element) ins.item(i);
            log.debug("[DOM] <input id='{}' name='{}' type='{}' class='{}'>",
                    element.getAttribute("id"), element.getAttribute("name"),
                    element.getAttribute("type"), element.getAttribute("class"));
        }
    }
}
