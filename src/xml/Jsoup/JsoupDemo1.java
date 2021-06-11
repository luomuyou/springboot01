package xml.Jsoup;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/*
 * JSoup快速入门
 */
public class JsoupDemo1 {

    public static void main(String[] args) throws IOException {
//        test1();
    }
    @Test
    public void test1() throws IOException {
        //        2、获取DOMcument对象
//          获取student.xml文挡的path，对xml问档进行解析
        String path = JsoupDemo1.class.getClassLoader().getResource("xml/Jsoup/student.xml").getPath();
        System.out.println("path="+path);
        Document document = Jsoup.parse(new File(path), "utf-8");
        System.out.println("Document就是解析xml文件的字符串内容的表现形式："+document);
//        3、获取元素对象Element
        Elements names = document.getElementsByTag("name");//就是我们js中的获取html文挡标签元素的方法
        System.out.println(names.size());   //得到两个student标签对象的name标签
        Element name1 = names.get(0);//得到两个student对象标签中的name标签对象
        String text = name1.text(); //得到name标签对象的值
        System.out.println("第一个元素姓名为："+text);
        /**
         * Jsoup 工具类，解析html或xml文档，返回document对象
         *      Jsoup.parse(new File(path), "utf-8");   以某种编码的方式解析文件，parse方法有多个重载方法
         *      Jsoup.parse(new URL("https://www.csdn.net/"),10000);    //网络资源文件，和超时时间，可以用来做网页爬虫
         * Document 文档对象，内存中的dom树
         * Eelements 元素Element对象集合，ArrayList<Element>
         * Element  元素对象
         * Node 节点对象
         */
    }
    @Test
    public void test2() throws IOException {
        //获得网页文档
        Document document = Jsoup.parse(new URL("https://www.csdn.net/"),100000);
        System.out.println(document);
//        document.select('#input');  //相当于js中的document.querySelector('#.');选择器，根据里面的字符来决定
    }
    @Test
    public void test3() throws IOException, XpathSyntaxErrorException {
        String path = JsoupDemo1.class.getClassLoader().getResource("xml/Jsoup/student.xml").getPath();
        Document document = Jsoup.parse(new File(path), "utf-8");
        JXDocument jxd = new JXDocument(document);
        String str1 = "//student[@number]";            //查询student标签中有number属性的Element对象集合
        String str2 = "//student[@number=stu1]";       //查询student标签中有number属性且属性值为stu1的Element对象集合
        String str3 = "//student[@number=stu2]/html()";
        List<JXNode> lists = jxd.selN(str3);
        for(JXNode node:lists){
            System.out.println(node);
        }
        System.out.println("==========================");
        JXNode jxn = jxd.selNOne(str1);    //获取节点对象
        System.out.println(jxn);
        Element ele = jxn.getElement();     //JXNode对象传换为Element节点对象

        String number = ele.attr("number");                //获取number的属性值
        System.out.println("获取id的属性值"+number);
        Elements children = ele.children();        //获取元素下的所有子对象元素
        System.out.println("获取元素下的所有子对象元素"+children);
        String tag = ele.tagName();                //获取元素的标签名
        System.out.println("获取元素的标签名"+tag);
        String text = ele.text();                  //获取元素的标签体内容
        System.out.println("获取元素的标签体内容"+text);

    }

    /**
     * 选择器
     * @throws IOException
     * @throws XpathSyntaxErrorException
     */
    @Test
    public void test4() throws IOException, XpathSyntaxErrorException {
        String path = JsoupDemo1.class.getClassLoader().getResource("xml/Jsoup/student.xml").getPath();
        Document document = Jsoup.parse(new File(path), "utf-8");
        Element ele = document.selectFirst("students");

        Elements children = ele.children();        //获取元素下的所有子对象元素
        System.out.println("获取元素下的所有子对象元素"+children);
        String tag = ele.tagName();                //获取元素的标签名
        System.out.println("获取元素的标签名"+tag);
        String text = ele.text();                  //获取元素的标签体内容
        System.out.println("获取元素的标签体内容"+text);

    }

    /**
     *  对象的使用：
     *             1. Jsoup：工具类，可以解析html或xml文档，返回Document
     *                 * parse：解析html或xml文档，返回Document
     *                     * parse​(File in, String charsetName)：解析xml或html文件的。
     *                     * parse​(String html)：解析xml或html字符串
     *                     * parse​(URL url, int timeoutMillis)：通过网络路径获取指定的html或xml的文档对象
     *             2. Document：文档对象。代表内存中的dom树
     *                 * 获取Element对象
     *                     * getElementById​(String id)：根据id属性值获取唯一的element对象
     *                     * getElementsByTag​(String tagName)：根据标签名称获取元素对象集合
     *                     * getElementsByAttribute​(String key)：根据属性名称获取元素对象集合
     *                     * getElementsByAttributeValue​(String key, String value)：根据对应的属性名和属性值获取元素对象集合
     *             3. Elements：元素Element对象的集合。可以当做 ArrayList<Element>来使用
     *             4. Element：元素对象
     *                 1. 获取子元素对象
     *                     * getElementById​(String id)：根据id属性值获取唯一的element对象
     *                     * getElementsByTag​(String tagName)：根据标签名称获取元素对象集合
     *                     * getElementsByAttribute​(String key)：根据属性名称获取元素对象集合
     *                     * getElementsByAttributeValue​(String key, String value)：根据对应的属性名和属性值获取元素对象集合
     *
     *                 2. 获取属性值
     *                     * String attr(String key)：根据属性名称获取属性值
     *                 3. 获取文本内容
     *                     * String text():获取文本内容
     *                     * String html():获取标签体的所有内容(包括字标签的字符串内容)
     *             5. Node：节点对象
     *                 * 是Document和Element的父类
     *
     *
     *         * 快捷查询方式：
     *             1. selector:选择器
     *                 * 使用的方法：Elements    select​(String cssQuery)
     *
     *                    cssQuery按照以前css选择器名字来做
     *
     *
     *                  * 语法：参考Selector类中定义的语法
     *             2. XPath：XPath即为XML路径语言，它是一种用来确定XML（标准通用标记语言的子集）文档中某部分位置的语言
     *                 * 使用Jsoup的Xpath需要额外导入jar包。
     *                 * 查询w3cshool参考手册，使用xpath的语法完成查询
     */

}
