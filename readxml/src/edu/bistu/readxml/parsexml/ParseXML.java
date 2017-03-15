package edu.bistu.readxml.parsexml;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import org.dom4j.*; 
import org.dom4j.io.*;

import com.csvreader.CsvWriter;

import edu.bistu.readxml.patent.Patent;

/**
* 类的描述 
* XML专利文本解析，提取专利文件中的专利名，专利号，专利摘要，专利权利说明等信息放入txt文件中，
* 并将txt文件放入相应的文件夹
* @author  ZH
* @Time 2017-13-13:00:00
*
*/

public class ParseXML {
	
	/** 专利所在路径 */
	final static String patentPath = "C:/paper/patentclassifier/patent";
	/** 专利存储所在路径 */
	final static String csvFilePath = "C:/patent.csv";
	final static String txtFolderPath = "C:/";
	/** 提取内容所在路径 */
	final static String titlePath = "/cn-patent-document/cn-bibliographic-data/invention-title";
	final static String KindPath = "/cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text";
	final static String pubNumCountryPath = "/cn-patent-document/cn-bibliographic-data/cn-publication-reference/document-id/country";
	final static String pubNumDocNumberPath = "/cn-patent-document/cn-bibliographic-data/cn-publication-reference/document-id/doc-number";
	final static String pubNumKindPath = "/cn-patent-document/cn-bibliographic-data/cn-publication-reference/document-id/kind";
	final static String abstractPath = "/cn-patent-document/cn-bibliographic-data/abstract";
	final static String claimTextPath = "/cn-patent-document/application-body/claims/claim/claim-text";
	
	
	public static void main(String[] args){
		
		createPatentDir();

		parseXMLFile(patentPath);

		System.out.println("Finish");
	}
	
	
	//递归读文件
    public static void parseXMLFile(String filepath) {
    	try {
    		
        	File file = new File(filepath);
            if (!file.isDirectory()) {//文档读取信息
        		Patent patent = new Patent();
            	patent = parseXML(file.getPath());
            	writePatentTXT(patent);
//            	writePatentCSV(patent);

            } else if (file.isDirectory()) {//文件夹进入文件夹
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                	parseXMLFile(filepath + "\\" + filelist[i]);
                }

            }

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	//解析XML
	public static Patent parseXML(String fileName){
		
		Patent patent = new Patent();
		try {
			File file = new File(fileName); 
			SAXReader reader = new SAXReader(); 
			//忽略dtd验证
			reader.setEntityResolver(new IgnoreDTDEntityResolver());
			Document doc = reader.read(file);
			//获得根节点
			Element root = doc.getRootElement();
			
			getElementList(root,patent);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return patent;
	}
	
	//递归遍历子节点
    public static void getElementList(Element element,Patent patent) {
    
        List elements = element.elements(); 
        //去掉字符穿里面的空白符等
        //title
        if(element.getName().equals("invention-title") && element.getPath().equals(titlePath)){
    	    patent.setTitle(element.getTextTrim().replaceAll("[ *|\\s*|\\|/|:|\\*|\\?|\"|<|>|\\|]*", ""));
        } 
        //kind
        else if (element.getName().equals("text") && element.getPath().equals(KindPath)) { 
        	patent.appendKind(element.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        //pubNum
        else if (element.getName().equals("country") && element.getPath().equals(pubNumCountryPath)) { 
        	patent.appendPubNum(element.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        else if(element.getName().equals("doc-number") && element.getPath().equals(pubNumDocNumberPath)){
        	patent.appendPubNum(element.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        else if(element.getName().equals("kind") && element.getPath().equals(pubNumKindPath)){
        	patent.appendPubNum(" ");
        	patent.appendPubNum(element.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        //absContent
        else if(element.getName().equals("abstract")&& element.getPath().equals(abstractPath)){
        	Element elemeChild = element.element("p");
        	patent.setAbsContent(elemeChild.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        //claimText
        else if(element.getName().equals("claim-text") && element.getPath().equals(claimTextPath)){
        	patent.appendClaimText(element.getTextTrim().replaceAll("[ *|\\s*]*", ""));
        }
        else { 
            //有子元素 
            for (Iterator it = elements.iterator(); it.hasNext();) { 
                Element elem = (Element) it.next(); 
                //递归遍历 
                getElementList(elem,patent); 
            } 
        }
    } 
    
//    //将专利信息写入csv文件
//    public static void writePatentCSV(Patent patent) {
//    	try{
//    		File file = new File(csvFilePath);
//    		FileWriter fw = new FileWriter(file, true);
//	        CsvWriter wr =new CsvWriter(fw,',');  
//	        String[] contents = {patent.getTitle(),patent.getPubNum(),patent.getAbsContent(),patent.getClaimText()};
//	        wr.writeRecord(contents);  
//	        wr.close();  
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    }
    
    //将专利信息写入txt文件
    public static void writePatentTXT(Patent patent) {
    	try{
    		
    		String dirName = patent.getKind().substring(0, 1);
    		String fileName = patent.getKind().substring(0, 4) + "_"+ patent.getPubNum().substring(0, 11)+ "_" + patent.getTitle();
    		String fileContent = patent.getAbsContent() + "|" + patent.getClaimText();
    		File file =new File(txtFolderPath + "/patent/" + dirName + "/" + fileName + ".txt");
    		System.out.println(txtFolderPath + "/patent/" + dirName + "/" + fileName + ".txt");
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		 Writer out = new FileWriter(file);
    		 out.write(fileContent);
    		 out.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    //创建输出专利文献的文件夹
    public static void createPatentDir(){
		createDir(txtFolderPath+"/patent");
		for(char kind = 'A'; kind <= 'H'; kind++){
			createDir(txtFolderPath+"/patent/"+kind);
		}
    }
    
    //创建文件夹
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {// 判断目录是否存在
			System.out.println("创建目录失败，目标目录已存在！");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
			destDirName = destDirName + File.separator;
		}
		if (dir.mkdirs()) {// 创建目标目录
			System.out.println("创建目录成功！" + destDirName);
			return true;
		} else {
			System.out.println("创建目录失败！");
			return false;
		}
	}
	
//	// 创建单个文件
//	public static boolean createFile(String filePath) {
//		File file = new File(filePath);
//		if (file.exists()) {// 判断文件是否存在
//			System.out.println("目标文件已存在" + filePath);
//			return false;
//		}
//		if (filePath.endsWith(File.separator)) {// 判断文件是否为目录
//			System.out.println("目标文件不能为目录！");
//			return false;
//		}
//		if (!file.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
//			// 如果目标文件所在的文件夹不存在，则创建父文件夹
//			System.out.println("目标文件所在目录不存在，准备创建它！");
//			if (!file.getParentFile().mkdirs()) {// 判断创建目录是否成功
//				System.out.println("创建目标文件所在的目录失败！");
//				return false;
//			}
//		}
//		try {
//			if (file.createNewFile()) {// 创建目标文件
//				System.out.println("创建文件成功:" + filePath);
//				
//				return true;
//			} else {
//				System.out.println("创建文件失败！");
//				return false;
//			}
//		} catch (IOException e) {// 捕获异常
//			e.printStackTrace();
//			System.out.println("创建文件失败！" + e.getMessage());
//			return false;
//		}
//	}
    
    
}
