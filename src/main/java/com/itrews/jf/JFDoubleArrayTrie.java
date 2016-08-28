package com.itrews.jf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JFDoubleArrayTrie {

	private static class Node {
		int code = 0;
		int depth = 0;
		int left = 0;
		int right = 0;

		public String toString() {
			return "Node{" + "code=" + code + ", depth=" + depth + ", left=" + left + ", right=" + right + '}';
		}
	}

	private static class SimpTrad {
		String simp = "";
		String trad = "";

		public SimpTrad (String simp,String trad){
			this.simp = simp;
			this.trad = trad;
		}
		
		public String toString() {
			return "simp:" + simp + "->trad:" + trad;
		}
	}

	public static int[] simparray = null;// 繁体对应简体
	public static int[] tradarray = null;// 简体对应繁体

	/**
	 * 双数组变量
	 */
	public int[] base;
	public int[] check;
	public boolean[] user;
	public SimpTrad[] values;
	public int allocSize = 0;
	public int size = 0;

	// 临时变量
	private List<String> key;
	
	private int nextCheckPos = 0;  //记录插入的数据的位置
	
	public static void main(String[] args) {
		JFDoubleArrayTrie jdf = new JFDoubleArrayTrie();
		initChar("E:/词典/data/简繁体/jfcontrast.txt");  //初始化简繁体字对应关系
		String simppath = "E:/词典/data/简繁体/simplified.txt";
		/*List<String[]> list = getSortList(simppath);
		for (String[] ss : list) {
			System.out.println("ss:" + ss[0] + "->" + ss[1]);
		}*/
		
		jdf.initSimpWord(simppath);
		String sw = "三極體和皇后運算元伺服器個人數位助理";
		/*int i = jdf.search(sw);
		if(i > 0){
			System.out.println("i:"+i +" value:" + jdf.values[i].simp + "->" + jdf.values[i].trad );
		}else {
			System.out.println("i：" + i);
		}
		*/
		
		//jdf.test();
		
		sw = jdf.replace(sw, true);
		
		System.out.println(sw);
	}
	
	

	public boolean test(){
		for(int i = 0 ; i < base.length ;i++){
			if(base[i] < 0) {
				System.out.println("i:"+i +" value:" + values[-base[i] - 1].simp + "->" + values[-base[i] - 1].trad + " check[i]:" + check[i] + "->" + (char) (check[i] - 2));
			}
		}
		return false;
	}
	
	/**
	 * 简繁体替换
	 * @date 2016年8月10日
	 * @author carbon
	 * @param sw
	 * @param issimp
	 * @return
	 */
	public String replace(String sw, boolean issimp ) {
		if(sw == null || "".equals(sw)){
			return sw;
		}
		StringBuilder sb = new StringBuilder();
		char[] chararray = sw.toCharArray();
		for(int i = 0 ; i < chararray.length ; i ++){  //起始位置
			int begin = 1;
			int b = 0;
			boolean add = false;
			for(int j = i; j <chararray.length ; j ++){
				b = begin + chararray[j] + 1;
				if(base[begin] < 0 ){
					int n = base[begin];
					sb.append(values[-n - 1].trad);
					i = j - 1;
					add = true;
					break;
				}else if(begin == check[b]){
					begin = base[b];
				}else {
					break;
				}
			}
			if(base[begin] < 0 && !add){
				int n = base[begin];
				sb.append(values[-n - 1].trad);
				i = chararray.length ;
				add = true;
			}
			if(!add){
				if(issimp) {  //如果是简体
					sb.append((char)simparray[chararray[i]]);
				}else {
					sb.append((char)tradarray[chararray[i]]);
				}
			}
		}
		return sb.toString();
	}
	
	public int testSearch(String sw) {
		int result = -1;
		if(sw != null && !"".equals(sw)){
			System.out.println("sw:" +sw);
			char[] chararray = sw.toCharArray();
			int begin = 1;
			int b = 0;
			for(char c : chararray) {
				b = begin + c + 1;
				if(begin == check[b]){
					begin = base[b];
				}else {
					return result ;
				}
			}
			int n = base[begin];
			if(n < 0 && begin == check[begin]){
				return -n -1;
			}
			
		}
		return result;
	}
	
	
	/**
	 * 初始化双数组b trie
	 * @date 2016年8月9日
	 * @author carbon
	 * @param wordpath
	 */
	public void initSimpWord(String wordpath) {
		List<String[]> list = getSortList(wordpath);
		List<String> tempkey = new ArrayList<String>(list.size());
		SimpTrad[] tempvalues = new SimpTrad[list.size()];
		for(int index= 0; index < list.size(); index ++) {
			String[] v = list.get(index);
			tempkey.add(v[0]);
			//System.out.println("v[0]:" + v[0]);
			tempvalues[index] = new SimpTrad(v[0],v[1]);
		}
		values = tempvalues;
		key = tempkey;
		
		bulid();
	}
	
	private void bulid(){
		reSize(66555);
		base[0] = 1;
		Node root_node = new Node();
		root_node.left = 0; 
		root_node.right = key.size();
		root_node.depth = 0;
		
		List<Node> siblings = new ArrayList<Node>();
		
		fetch(root_node,siblings);
		insert(siblings);
		
		user = null;
	}

	/**
	 * 计算parent 下一级别单个词的左右间距
	 * @date 2016年8月8日
	 * @author carbon
	 * @param parent
	 * @param siblings
	 * @return
	 */
	public int fetch(Node parent ,List<Node> siblings){
		int pre = 0;  //上一个note
		for(int i = parent.left; i < parent.right ; i ++){
			String tmp = key.get(i);
			//System.out.println("parent.depth:"+parent.depth);
			if(tmp.length() < parent.depth) {
				continue;
			}
			int cur = 0;
			if(tmp.length() != parent.depth) {
				cur = tmp.charAt(parent.depth) + 1;
			}
			if(cur < 0){
				return -1;
			}
			if(pre != cur || siblings.size() == 0){
				Node temp_node = new Node();
				temp_node.left = i;
				temp_node.code = cur;
				temp_node.depth = parent.depth + 1;
				if(siblings.size() != 0) {
					siblings.get(siblings.size() - 1).right = i;
				}
				siblings.add(temp_node);
			}
			pre = cur;
		}
		if(siblings.size() != 0) {
			siblings.get(siblings.size() - 1).right = parent.right;
		}
		//System.out.println("int pre = 0;  //上一个note:"+siblings.size());
		return siblings.size();
	}
	
	public int insert(List<Node> siblings) {
		int begin = 0;
		int pos = Math.max(siblings.get(0).code, nextCheckPos - 1);
		int nonzero_num = 0;
		boolean first = true;
		
		if(allocSize < pos) {
			reSize(pos + 1);
		}
		
		while (true) {
			pos ++;
			if(allocSize < pos) {
				reSize(pos + 100); 
			}
			if(check[pos] != 0){
				nonzero_num++;  //统计当前区间里使用量
				continue;
			}else if(first) {
				nextCheckPos = pos;
				first = false;
			}
			begin = pos - siblings.get(0).code ;
			if(allocSize < (begin + siblings.get(siblings.size() - 1).code)){
				reSize(Math.max((int)(allocSize * 1.05), begin + siblings.get(siblings.size() - 1).code+1));
			}
			if(user[begin]) {
				continue;
			}
			for(int i = 0 ; i < siblings.size() ; i ++) {
				if(check[begin + siblings.get(i).code] != 0) {
					continue;
				}
			}
			break;
		}
		/**
		 * 如果已经使用的check 超过95 了。从下一个范围开始查找
		 */
		if(1.0 * nonzero_num / pos - nextCheckPos + 1 > 0.95){
			nextCheckPos = pos;
		}
		user[begin] = true;
		if(size < (begin + siblings.get(siblings.size() - 1).code)){
			size = begin + siblings.get(siblings.size() - 1).code;
		}
		
		//开始设置 check 数组
		for(int i = 0 ; i < siblings.size() ; i ++){
			check[begin + siblings.get(i).code] = begin;
		}
		/**
		 * 找下一个Node节点
		 */
		//System.out.println("siblings.size():" + siblings.size());
		for(int i = 0 ; i < siblings.size() ; i ++){
			List<Node> nextsib = new ArrayList<Node>();
			int cur = fetch(siblings.get(i), nextsib);
			if(cur == 0) {  //如果没有子节点
				base[begin + siblings.get(i).code] = -siblings.get(i).left - 1;  //-1 是为了解决 -0 0 的问题。后面需要再补回来
			}else {
				int h = insert(nextsib);
				base[begin + siblings.get(i).code] = h;
			}
		}
		return begin;
		
	}
	
	/**
	 * 初始化按字对应的
	 * @date 2016年8月8日
	 * @author carbon
	 * @param charpath
	 */
	public static void initChar(String charpath) {
		int[] tempsimparray = new int[Character.MAX_VALUE]; // 繁体对应简体
		int[] temptradarray = new int[Character.MAX_VALUE]; // 简体对应繁体

		for (int i = 0; i < tempsimparray.length; i++) {
			tempsimparray[i] = i;
			temptradarray[i] = i;
		}

		List<String> templist = getReadList(charpath);
		for (String s : templist) {
			char[] chararray = s.toCharArray();
			tempsimparray[chararray[1]] = chararray[0];
			temptradarray[chararray[0]] = chararray[1];
		}
		simparray = tempsimparray;
		tradarray = temptradarray;
	}

	/**
	 * 重置数组的大小
	 * 
	 * @date 2016年8月8日
	 * @author carbon
	 * @param newsize
	 */
	private void reSize(int newsize) {
		int[] base2 = new int[newsize];
		int[] check2 = new int[newsize];
		boolean[] user2 = new boolean[newsize];

		if (allocSize > 0) {
			System.arraycopy(user, 0, user2, 0, allocSize);
			System.arraycopy(check, 0, check2, 0, allocSize);
			System.arraycopy(base, 0, base2, 0, allocSize);
		}

		base = base2;
		check = check2;
		user = user2;

		allocSize = newsize;
	}

	/**
	 * 处理文本文件并排序
	 * 
	 * @date 2016年8月5日
	 * @author carbon
	 * @param path
	 * @return
	 */
	public static List<String[]> getSortList(String path) {
		List<String[]> list = new ArrayList<String[]>();
		List<String> tempsimp = getReadList(path);
		for (String s : tempsimp) {
			if (!s.startsWith("#") && !"".equals(s.trim())) {
				list.add(s.split("="));
			}
		}
		Collections.sort(list, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String o2[]) {
				return o1[0].compareTo(o2[0]);
			}
		});
		return list;
	}

	/**
	 * 按行读取文本文件
	 * 
	 * @date 2016年8月5日
	 * @author carbon
	 * @param path
	 * @return
	 */
	public static List<String> getReadList(String path) {
		/*List<String> tempsimp = new ArrayList<>();
		try {
			FileReader fr = new FileReader(new File(path));
			LineNumberReader lnr = new LineNumberReader(fr);
			String s = null;
			while ((s = lnr.readLine()) != null) {
				tempsimp.add(s);
			}
			lnr.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempsimp;*/
		
		return getStreamReadList(path);
	}
	
	public static List<String> getStreamReadList(String path) {
		List<String> tempsimp = new ArrayList<String>();
		try {
			InputStreamReader isr = new InputStreamReader(JFDoubleArrayTrie.class.getResourceAsStream(path));
			LineNumberReader lnr = new LineNumberReader(isr);
			String s = null;
			while ((s = lnr.readLine()) != null) {
				tempsimp.add(s);
			}
			lnr.close();
			isr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempsimp;
	}

}
