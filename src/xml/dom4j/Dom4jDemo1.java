package xml.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Dom4jDemo1 {
    public static void main(String[] args) { }

    @Test
    public void test2() throws IOException, DocumentException {
        //1、创建SAXReader对像用于读取xml文档
        SAXReader reader = new SAXReader();
        //2、获取document对象
        Document doc = reader.read(new File("src/xml/dom4j/student.xml"));
        Element rootElement = doc.getRootElement(); //获取跟元素
        System.out.println(rootElement);
    }
}
