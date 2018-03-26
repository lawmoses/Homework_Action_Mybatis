package test1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import board.BoardDataBean;
import oracle.net.aso.p;

public class BoardDBBean {

	private static BoardDBBean instance = new BoardDBBean();

	private BoardDBBean() {

	}

	public static BoardDBBean getInstance() {
		return instance;
	}

	public static Connection getConnection() {
		Connection con = null;
		try {
			String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
			String dbId = "scott";
			String dbPass = "tiger";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(jdbcUrl, dbId, dbPass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public void close(Connection con, ResultSet rs, PreparedStatement pstmt) {

		if (rs != null)
			try {
				rs.close();
			} catch (SQLException ex) {
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException ex) {
			}
		if (con != null)
			try {
				con.close();
			} catch (SQLException ex) {
			}

	}

	public void insertArticle(BoardDataBean article) {
		String sql = "";
		Connection con = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int number = 0;
		try {
			pstmt = con.prepareStatement("select boardser.nextval "
					+ "from dual");
			rs = pstmt.executeQuery();
			if (rs.next())
				number = rs.getInt(1)+1;
			else 	number = 1;
		//	========================
			int num = article.getNum();
			int ref = article.getRef();
			int re_step = article.getRe_step();
			int re_level = article.getRe_level();
			if (num != 0) 
			{sql = "update board set re_step=re_step+1 "
				+ "where ref= ? and re_step> ? and boardid = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_step);
				pstmt.setString(3, article.getBoardid());
				pstmt.executeUpdate();
				re_step = re_step + 1;
				re_level = re_level + 1;
				} else { //2 : 답글쓰기====================
					ref = number;re_step = 0;re_level = 0;
					}
			
			//=========== 아래 수정하세요!!!!
			
			
			
			
			
			sql = "insert into board(num,writer,email,subject,"
					+ " passwd,reg_date,";
			sql += "ref,re_step,re_level,content,ip, boardid, filename, filesize) "
					+ "values(?,?,?,?,?,sysdate,?,?,?,?,?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number);
			pstmt.setString(2, article.getWriter());
			pstmt.setString(3, article.getEmail());
			pstmt.setString(4, article.getSubject());
			pstmt.setString(5, article.getPasswd());
			pstmt.setInt(6, ref);
			pstmt.setInt(7, re_step);
			pstmt.setInt(8, re_level);
			pstmt.setString(9, article.getContent());
			pstmt.setString(10, article.getIp());
			pstmt.setString(11, article.getBoardid());
			pstmt.setString(12, article.getFilename());
			pstmt.setInt(13,  article.getFilesize());
			pstmt.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			close(con, rs, pstmt);
		}

	}

	public int getArticleCount(String boardid) {
		int x = 0;
		String sql = "select nvl(count(*),0) " + "from board where boardid = ?";
		Connection con = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int number = 0;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, boardid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				x = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(con, rs, pstmt);
		}

		return x;

	}

	public List getArticles(int startRow, int endRow, String boardid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List articleList = null;
		String sql = "";
		try {
			conn = getConnection();
			sql = " select * from "
					+ "( select rownum rnum ,a.* "
					+ " from (select num,writer,email,subject,passwd,"
					+ "reg_date,readcount,ref,re_step,re_level,content,"
					+ "ip from board where boardid = ? order by ref desc , re_step) "
					+ "	a ) where rnum  between ? and ?  ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardid);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				articleList = new ArrayList();
				do {
					BoardDataBean article = new BoardDataBean();
					article.setNum(rs.getInt("num"));
					article.setWriter(rs.getString("writer"));
					article.setEmail(rs.getString("email"));
					article.setSubject(rs.getString("subject"));
					article.setPasswd(rs.getString("passwd"));
					article.setReg_date(rs.getTimestamp("reg_date"));
					article.setReadcount(rs.getInt("readcount"));
					article.setRef(rs.getInt("ref"));
					article.setRe_step(rs.getInt("re_step"));
					article.setRe_level(rs.getInt("re_level"));
					article.setContent(rs.getString("content"));
					article.setIp(rs.getString("ip"));
					articleList.add(article);
				} while (rs.next());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(conn, rs, pstmt);
		}
		return articleList;
	}
	
	
	public BoardDataBean getArticle
	(int num , String boardid, String chk) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDataBean article = null;	
		String sql="";
		try {
			conn = getConnection();
			
			if(chk.equals("content")) {
			sql="update board set readcount=readcount+1 "
					+ "where num = ? and boardid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, boardid);
			pstmt.executeUpdate();
			}
			
			
			
			sql="select * from board where num = ? and boardid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, boardid);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				article = new BoardDataBean();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setReg_date(rs.getTimestamp("reg_date"));
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setRe_step(rs.getInt("re_step"));
				article.setRe_level(rs.getInt("re_level"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));			}	
			}catch (Exception e) {e.printStackTrace();
		} finally {	close(conn, rs, pstmt);	}
		  return article;	}
	

	
	
	
	public int updateArticle(BoardDataBean article) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int chk = 0;
		try {
			conn = getConnection();
		String sql = "update board set writer=?,email=?,"
		+ " subject=? ,content=? where num=? and passwd = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, article.getWriter());
		pstmt.setString(2, article.getEmail());
		pstmt.setString(3, article.getSubject());
		pstmt.setString(4, article.getContent());
		pstmt.setInt(5, article.getNum());
		pstmt.setString(6, article.getPasswd());
		chk= pstmt.executeUpdate();
				
		}catch (Exception e) {e.printStackTrace();
		} finally {	close(conn, null, pstmt);	}
		  return chk;	}
		
	
	public int deleteArticle(int num, String passwd, 
			String boardid) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql="delete from board where num=? "
				+ " and passwd = ?";
		int x = -1;
		try {	conn = getConnection();
	    pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				pstmt.setString(2, passwd);
		x=pstmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {	
			close(conn, rs, pstmt);
		}		return x;	}	
	
}
