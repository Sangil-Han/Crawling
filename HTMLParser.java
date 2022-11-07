package musinsa;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import musinsa.DBConn;

public class HTMLParser { 
	public static void main(String[] args) throws Exception{
		Connection conn = Jsoup.connect("https://www.musinsa.com/category/014001");
		Document doc = conn.get();
		doc.select(".reply");
		Elements elements = doc.select(".li_box");
		for (int i = 0; i < elements.size(); i++) {
			Element el = elements.get(i);
//			System.out.println(el);
			String no = el.attr("data-no");
			String title = el.selectFirst(".item_title").text();
			String info = el.selectFirst(".list_info").text();
			String price = el.selectFirst(".price").text();
			String link = el.selectFirst(".list_info a").attr("href");
//			Element img = el.selectFirst(".list_img img");
			
			Map<String, String> map = new HashMap<>();
			map.put("no", no);
			map.put("title", title);
			map.put("info", info);
			map.put("price", price);
			map.put("link", link);
			System.out.println(map);
			
			saveDB(map);
//			saveFile(no, img.attr("data-original"));
			System.out.println(no + "번 작업 완료");
		}
	}
	
	static void saveFile(String no, String imgSrc) throws Exception{ // 지정위치에 데이터 저장
		URL url = new URL(imgSrc);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		File file = new File("C:\\mu", no); // "C:\\mu" >> 저장할 드라이브, 파일명 지정 
		if(!file.exists()) {
			file.mkdirs();
		}
		
		file = new File(file, "thumb.jpg");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		
		int b = 0;
		while((b = bis.read()) != -1) {
			bos.write(b);
		}
		bos.close();
	}
	
	static void saveDB(Map<String, String> map) throws Exception{ // DB에 정보 담는 메서드
		PreparedStatement pstmt = DBConn.getConnection().prepareStatement(
				"INSERT INTO MUSINSA_TBL VALUES(?, ?, ?, ?, ?)");
		pstmt.setString(1,  map.get("no"));
		pstmt.setString(2,  map.get("title"));
		pstmt.setString(3,  map.get("info"));
		pstmt.setString(4,  map.get("price"));
		pstmt.setString(5,  map.get("link"));
		pstmt.executeUpdate();
		pstmt.close();
	}
}
