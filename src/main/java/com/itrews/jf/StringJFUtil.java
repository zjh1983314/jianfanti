package com.itrews.jf;

public class StringJFUtil {

	private static JFDoubleArrayTrie simp2traddat = null;
	private static JFDoubleArrayTrie trad2simpdat = null;
	private static PinYinDoubleArrayTrie pydat = null; //拼音的转换
	
	private final static String simp2tradpath = "/traditional.txt";  //简体对应繁体的词表
	private final static String trad2simppath = "/simplified.txt";//繁体对应简体的词表
	private final static String jfpath = "/jfcontrast.txt"; //
	private final static String pinyinchar = "/pinyin.txt";
	private final static String pinyinword = "/polyphone.txt";
	
	
	static {
		//简繁体互相转换初始化
		JFDoubleArrayTrie.initChar( jfpath);
		JFDoubleArrayTrie temps2t = new JFDoubleArrayTrie();
		temps2t.initSimpWord(simp2tradpath);
		JFDoubleArrayTrie tempt2s = new JFDoubleArrayTrie();
		tempt2s.initSimpWord(trad2simppath);
		//拼音转换的初始化
		PinYinDoubleArrayTrie.initChar(pinyinchar); 
		PinYinDoubleArrayTrie temppy = new PinYinDoubleArrayTrie();
		temppy.initPinyinWord(pinyinword);
		
		simp2traddat = temps2t;
		trad2simpdat = tempt2s;
		pydat = temppy;
	}
	
	/**
	 * 简体转繁体
	 * @date 2016年8月10日
	 * @author carbon
	 * @return
	 */
	public static String simp2trad(String sw){
		return simp2traddat.replace(sw, false);
	}
	
	/**
	 * 繁体转简体
	 * @date 2016年8月10日
	 * @author carbon
	 * @param sw
	 * @return
	 */
	public static String trad2simp(String sw){
		return trad2simpdat.replace(sw, true);
	}
	
	/**
	 * 汉字转换为拼音，不能识别的汉字直接用原文
	 * @date 2016年8月11日
	 * @author carbon
	 * @param sw
	 * @return
	 */
	public static String pinyin(String sw) {
		return pydat.replace(sw);
	}
	
	public static void main(String[] args) {
		/*String sw = "三極體和皇后運算元伺服器個人數位助理後天";
		String jt = trad2simp(sw);
		System.out.println("jt:"+jt);
		String ft = simp2trad(sw);
		System.out.println("ft:"+ft);*/
		System.out.println(System.getProperty("user.dir") );
		test();
	}
	
	public static void test(){
		String ss = "在南非遊艇産業逐步成熟的過程中我註意到了南非製造的職體遊艇雙體遊艇在噹地的遊艇産業中絕對昰一箇不容忽畧的重要纏成部分甚至可以説昰南非遊艇製造業的驕傲。南非的職體遊艇包括職體颿舩咊雙體動力遊艇兩種)佔全國遊艇齣口量的近80%。遊艇産業咊葡萄酒産業樣,都存在着新舊世界之分。南非作爲新世界中的一員在職體遊艇的製造與齣口方麵已經開始挑戰灋國i童箇舊世界中最具資歷的對手了。近幾年來很多國際知名設計研髮糰隊相繼來到南非在此設立工怍室竝建造自己的工力求打造擁有國際品牌的雙體遊艇一些製造商已經提前完成了譬標南非生産的優質積體遊艇在最近的多箇國際遊艇展上紛紛亮相.得到了業內的廣汎讚譽與多項殊萊。其實最值得肎定的要算來自客戶的訢賞.Sunso Jl一全毬最大的休閑遊艇租賃公司.在23箇國傢咊地區運營竝筦理着]000多艘遊艇.南非生産的雙體遊艇也被納入其龐大的舩隊之中。在印度洋咊加COUJMN專欄勒比海.客人都能夠乗坐南非遊艇體驗暢亯碧海藍天。";

		String s = trad2simp(ss);
		ss = s;
		System.out.println("s:" + s);
		long stime = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			simp2trad(ss);
		}
		System.out.println("数组方式执行时间ws：" + (System.currentTimeMillis() - stime));
	}
	
}
