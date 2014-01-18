package com.horsehour.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * FileManager�ļ��������ࣺ�������ļ���д����������ɾ����������
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20110601
 */
public class FileManager {
	
	/**
	 * @param src
	 * @return ��ȡ�ļ����� 
	 */
	public static String readFile(File src){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(src));
			String line = "";
			while((line=reader.readLine()) != null)
				sb.append(line + "\r\n");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * @param src
	 * @return ��ȡ�ļ�����
	 */
	public static String readFile(String src){
		return readFile(new File(src));
	}
	
	/**
	 * @param src
	 * @param enc
	 * @return ����ָ���ı����ȡ�ļ�����
	 */
	public static String readFile(String src, String enc){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(src), enc));
			String line = "";
			while((line = reader.readLine()) != null)
				sb.append(line).append("\r\n");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * ���ļ�д����
	 * @param dest
	 * @param content
	 */
	public static void writeFile(String dest, String content){
		writeFile(dest, content, true);
	}
	
	/**
	 * ���ļ�д������
	 * @param dest
	 * @param content
	 * @param append
	 */
	public static void writeFile(String dest, String content, boolean append){
		BufferedWriter writer = null;
		File destFile = new File(dest);
		try {
			if(!destFile.exists()) 
				destFile.createNewFile();
			
			writer = new BufferedWriter(new FileWriter(destFile, append));
			writer.write(content);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * ��������ָ������д���ļ�
	 * @param dest
	 * @param content
	 * @param enc
	 */
	public static void writeFile(String dest, String content, String enc){
		BufferedWriter writer = null;
		File destFile = new File(dest);
		try {
			if(!destFile.exists()) 
				destFile.createNewFile();

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), enc));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * @param dir
	 * @return ��ȡָ��Ŀ¼�µ������ļ���
	 */
	public static File[] getFileList(String dir){
		File root = new File(dir); 
		File[] files = null;
		if(dir.isEmpty()){
			System.out.println("The directory isnot exist!");
			return null;
		}
		
		if(root.isDirectory())
			files = root.listFiles();
		else{
			files = new File[1];
			files[0] = root; 
		}
		
		return files;
	}
	
	/**
	 * ��ָ��Ŀ¼�µ�ȫ���ļ��ϲ���һ���ļ���
	 * @param dir
	 * @param dest
	 */
	public static void merge(String dir, String dest){
		File directory = new File(dir);
		File[] files = directory.listFiles();
		for(File file : files)
			writeFile(dest, readFile(file));
	}
	
	/**
	 * ������
	 * @param src
	 * @param dest
	 * @return �����ɹ�����true,���򷵻�false
	 */
	public static boolean rename(String src, String dest){
		File file = new File(src);
		return file.renameTo(new File(dest));
	}
	
	/**
	 * ɾ��Ŀ¼��ȫ���ļ�
	 * @param dir
	 */
	public static void deleteDir(String dir){
		File[] files = new File(dir).listFiles();
		for(File file : files)
			file.delete();
	}
	
	/**
	 * �����ļ�
	 * @param src The source file.
	 * @param dest The copied file.
	 */
	public static void copyFile(String src, String dest){
			FileInputStream fis  = null;
			FileOutputStream fos = null;

			try {
				fis = new FileInputStream(new File(src));
				fos = new FileOutputStream(new File(dest));

				byte[] buf = new byte[40960];
				int i = 0;
				while ((i = fis.read(buf)) != -1)
					fos.write(buf, 0, i);

				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
	}
	
	/**
	 * ����Ŀ¼
	 * @param srcDir The source directory.
	 * @param destDir The target directory.
	 * @param files The files to be copied, names but not directory.
	 */
	public static void copyFiles(String srcDir, String destDir, List<String> files){
		for(int i = 0; i < files.size(); i++)
			copyFile(srcDir + files.get(i), destDir + files.get(i));
	}
	
	
	private static final int BUF_SIZE = 51200;
	
	/**
	 * ��ѹ�ļ�
	 * @param gzFile ѹ���ļ�
	 * @param dirOutput	��ѹ���ļ�
	 * @return true if succeed, false otherwise.
	 */
	@SuppressWarnings("resource")
	public static boolean gunzipFile(File gzFile, File dirOutput) {
		// Create a buffered gzip input stream to the archive file.
		GZIPInputStream gzip_in_stream;
		FileInputStream in = null;
		try {
			in = new FileInputStream(gzFile);
			BufferedInputStream source = new BufferedInputStream (in);
			gzip_in_stream = new GZIPInputStream(source);

			String file_input_name = gzFile.getName ();
			String file_output_name = file_input_name.substring (0, file_input_name.length () - 3);

			File output_file = new File (dirOutput, file_output_name);

			byte[] input_buffer = new byte[BUF_SIZE];

			int len = 0;
			FileOutputStream out = new FileOutputStream(output_file);
			BufferedOutputStream dest = new BufferedOutputStream (out, BUF_SIZE);

			while ((len = gzip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1)
				dest.write (input_buffer, 0, len);
			
			dest.flush ();
			out.close ();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean gunzipFile(String gzFile, String dirOutput){
		return gunzipFile(new File(gzFile), new File(dirOutput));
	}
	
	/**
	 * ѹ���ļ�
	 * @param zipFile ����ļ�
	 * @param gzipFilename ѹ���ļ�����
	 * @return true if succeeds, false otherwise
	 */
	public static boolean gzipFile(String zipFile, String gzipFilename){
		try {
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(gzipFilename));
			FileInputStream in = new FileInputStream(zipFile);
			byte[] buf = new byte[BUF_SIZE];
			
			int len;
			
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			
			in.close();

			out.finish();
			out.close();
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * ���������л�Ϊ������,���浽ָ���ļ���
	 * @param o
	 * @param dest
	 */
	public static void serialize(Object o, String dest){
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(dest, false));
			oos.writeObject(o);
			oos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * �����л�����
	 * @param src
	 * @return ���л��Ķ���
	 */
	@SuppressWarnings("resource")
	public static Object deSerialize(String src){
		ObjectInputStream ois = null;
		Object o = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(src));
			o = ois.readObject();
			ois.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return o;
	}

	/**
	 * ��ȡ�ļ��еļ�ֵ��(һ��һ��,���ظ�)
	 * @param src
	 * @param enc
	 * @param map
	 */
	public static void loadResource(String src, String enc, HashMap<String,String> map){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			String line = "";
			int idx = 0;
			while((line = br.readLine()) != null){
				line = line.trim();
				idx = line.indexOf("=");
				if(idx>1)
					map.put(line.substring(0, idx), line.substring(idx+1));
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * ��ȡ�ļ��м�ֵ�ԣ�ѭ����ȡ,���ظ���,count�й���һ��
	 * @param br
	 * @param map
	 * @param count
	 */
	public static void loadResource(BufferedReader br, HashMap<String,String> map, int count){
		String line = "";
		int idx = 0;
		try {
			while((line = br.readLine()) != null && map.size() < count){
				idx = line.indexOf("=");
				if(idx != -1){
					line = line.trim();
					map.put(line.substring(0, idx), line.substring(idx+1));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * ���������ļ���Properties����
	 * @param propFile
	 * @param prop
	 */
	public static void loadResource(String propFile, Properties prop){
		FileInputStream fis;
		try {
			fis = new FileInputStream(propFile);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * ���б����ļ��е����ݵ�lines
	 * @param src
	 * @param lines
	 */
	public static void readLines(String src, List<String> lines){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			String line = "";
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;
				else
					lines.add(line);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * һ��һ������
	 * @param src
	 * @return
	 */
	public static List<Double> readLines(String src){
		List<Double> lines = new ArrayList<Double>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			String line = "";
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;
				else
					lines.add(Double.parseDouble(line));
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return lines;
	}
	
	/**
	 * ����д��
	 * @param datum
	 * @param dest
	 */
	public static void writeLines(List<Double> datum, String dest){
		StringBuffer sb = new StringBuffer();
		int m = datum.size();
		for(int i = 0; i < m; i++)
			sb.append(datum.get(i) + "\r\n");
		
		FileManager.writeFile(dest, sb.toString());
	}
	
	/**
	 * ����д��
	 * @param datum
	 * @param dest
	 */
	public static void writeLines(double[] datum, String dest){
		StringBuffer sb = new StringBuffer();
		int m = datum.length;
		for(int i = 0; i < m; i++)
			sb.append(datum[i] + "\r\n");
		
		FileManager.writeFile(dest, sb.toString());
	}

	/**
	 * ת��-���ļ��еļ�ֵ�Ա���ΪHashMap<String,Vector<String>>���ͣ������л���������ȡ��ʦ��Ϣ
	 * @param src
	 * @param enc
	 * @param dest
	 */
	public static void serializeList(String src, String enc, String dest){
		HashMap<String,String> list = new HashMap<String,String>();
		HashMap<String,Vector<String>> bulk = new HashMap<String,Vector<String>>();
		FileManager.loadResource(src,enc, list);
		Set<String> keySet = list.keySet();
		for(String key : keySet){
			String[] entries = list.get(key).split("\t");
			Vector<String> val = new Vector<String>();
			for(String entry : entries)
				val.add(entry);
			bulk.put(key, val);
		}
		serialize(bulk, dest);
	}
}