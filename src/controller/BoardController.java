package controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.sist.msk.Action;

import board.BoardDataBean;
import test1.BoardDBBean;

public class BoardController extends Action{
	
	
	//-----------------------------------------------------------------------------------
	
	public String index(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
			 
		return "/view/index.jsp";
			}
	
	
	//------------------------------------------------------------------------------------
	
		public String list(HttpServletRequest request,
					 HttpServletResponse response)  throws Throwable { 
			String boardid = request.getParameter("boardid");
			  if (boardid==null) boardid = "1";
			 	
			  int pageSize = 5;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String pageNum = request.getParameter("pageNum");
				if (pageNum == null || pageNum == "") {
					pageNum = "1";	}
				int currentPage = Integer.parseInt(pageNum);
				int startRow = (currentPage - 1) * pageSize + 1;
				int endRow = currentPage * pageSize;
				int count = 0;
				int number = 0;
				List articleList = null;
				BoardDBBean dbPro = BoardDBBean.getInstance();
				count = dbPro.getArticleCount(boardid);  
				
				if (count > 0) {
					articleList = dbPro.getArticles(startRow, endRow, boardid);	}
				number = count - (currentPage - 1) * pageSize;
					
				int bottomLine = 3;
				int pageCount = count / pageSize
						+ (count % pageSize == 0 ? 0 : 1);
				int startPage = 1 + (currentPage - 1) / bottomLine * bottomLine;
				int endPage = startPage + bottomLine - 1;
				if (endPage > pageCount) endPage = pageCount;

				request.setAttribute("boardid", boardid);
				request.setAttribute("count", count);
				request.setAttribute("articleList", articleList);
				request.setAttribute("currentPage", currentPage);
				request.setAttribute("startPage", startPage);
				request.setAttribute("bottomLine", bottomLine);
				request.setAttribute("endPage", endPage);
				request.setAttribute("number", number);


					return "/view/list.jsp";
					} 
			

	//--------------------------------------------------------------------------------
	
	public String content(HttpServletRequest request,HttpServletResponse response)  throws Throwable { 
		
		String boardid = request.getParameter("boardid");
		  if (boardid==null) boardid = "1";
		 
		 int num = Integer.parseInt(request.getParameter("num"));
		 
		 String pageNum = request.getParameter("pageNum");
			if (pageNum == null || pageNum == "") {
				pageNum = "1";	}
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		 try { 
			BoardDBBean dbPro = BoardDBBean.getInstance();
			BoardDataBean article = dbPro.getArticle(num, boardid, "content"); 
			int ref=article.getRef(); 
			int re_step=article.getRe_step();
			int re_level=article.getRe_level();
			   
			request.setAttribute("article", article);
			request.setAttribute("pageNum", pageNum);
			
		 } catch (Exception e) {
			 e.printStackTrace();
		 	}		   
		 
		return "/view/content.jsp";
			} 
		
	
	//------------------------------------------------------------------------------------
	
	public String writeForm(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		int num=0, ref=0, re_step=0, re_level=0;
	  	String boardid = request.getParameter("boardid");
		
		 if (boardid==null||boardid.equals(""))
		 { boardid = "1"; }
		
		if (request.getParameter("num")!=null) {
		num = Integer.parseInt(request.getParameter("num"));	
		ref = Integer.parseInt(request.getParameter("ref"));
		re_step = Integer.parseInt(request.getParameter("re_step"));
		re_level = Integer.parseInt(request.getParameter("re_level"));
		
		}
	 	
		request.setAttribute("num", num);
		request.setAttribute("ref", ref);
		request.setAttribute("re_step", re_step);
		request.setAttribute("re_level", re_level);
		request.setAttribute("pageNum", 1);
		request.setAttribute("boardid", boardid);
	  
	
		return "/view/writeForm.jsp";
			} 	
	
	//------------------------------------------------------------------------------------
	
	public String writePro(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		
		String pageNum = request.getParameter("pageNum");
		
		String boardid = request.getParameter("boardid");
		
		if (pageNum == null || pageNum == "") pageNum = "1";
		
		BoardDataBean article = new BoardDataBean();
		if (request.getParameter("num") != null
		&& !request.getParameter("num").equals("")) {
		article.setNum(Integer.parseInt(request.getParameter("num")));
		article.setRef(Integer.parseInt(request.getParameter("ref")));
		article.setRe_step(Integer.parseInt(request.getParameter("re_step")));
		article.setRe_level(Integer.parseInt(request.getParameter("re_level")));
		}
		article.setWriter(request.getParameter("writer"));
		article.setEmail(request.getParameter("email"));
		article.setSubject(request.getParameter("subject"));
		article.setPasswd(request.getParameter("passwd"));
		article.setContent(request.getParameter("content"));
		article.setBoardid(request.getParameter("boardid"));
		article.setIp(request.getRemoteAddr());
			System.out.println(article);
		
			BoardDBBean dbPro = BoardDBBean.getInstance();
		dbPro.insertArticle(article);
		request.setAttribute("pageNum", pageNum);
		response.sendRedirect(request.getContextPath()+"/board/list?pageNum="+pageNum+"&boardid="+boardid);
			
			return null;
			} 
	
	

	//------------------------------------------------------------------------------------
	
	public String writeFormUpload(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		int num = 0, ref = 0, re_step = 0, re_level = 0;
		String boardid = request.getParameter("boardid");

		if (boardid == null || boardid.equals("")) {
			boardid = "1";
		}

		if (request.getParameter("num") != null) {
			num = Integer.parseInt(request.getParameter("num"));
			ref = Integer.parseInt(request.getParameter("ref"));
			re_step = Integer.parseInt(request.getParameter("re_step"));
			re_level = Integer.parseInt(request.getParameter("re_level"));

		}

		request.setAttribute("num", num);
		request.setAttribute("ref", ref);
		request.setAttribute("re_step", re_step);
		request.setAttribute("re_level", re_level);
		request.setAttribute("pageNum", 1);
		request.setAttribute("boardid", boardid);

		return "/view/writeFormUpload.jsp";
			} 
	
	//------------------------------------------------------------------------------------
	
	public String writeProUpload(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		String realFolder = "";// 웹 어플리케이션상의 절대 경로
		String encType = "euc-kr"; // 엔코딩타입
		int maxSize = 5 * 1024 * 1024; // 최대 업로될 파일크기 5Mb
		ServletContext context = request.getServletContext();
		realFolder = context.getRealPath("fileSave");
		MultipartRequest multi = null;
		multi = new MultipartRequest(request, realFolder, maxSize, encType,
			new DefaultFileRenamePolicy());
		Enumeration files = multi.getFileNames();
		String filename = "";
		File file = null;
		if (files.hasMoreElements()) {
			String name = (String) files.nextElement();
			filename = multi.getFilesystemName(name);
			String original = multi.getOriginalFileName(name);
			String type = multi.getContentType(name);
			file = multi.getFile(name);	}
		
		String pageNum = multi.getParameter("pageNum");
		String boardid = multi.getParameter("boardid");
		if (pageNum == null || pageNum == "") pageNum = "1";
		BoardDataBean article = new BoardDataBean();
		if (multi.getParameter("num") != null
		&& !multi.getParameter("num").equals("")) {
		article.setNum(Integer.parseInt(multi.getParameter("num")));
		article.setRef(Integer.parseInt(multi.getParameter("ref")));
		article.setRe_step(Integer.parseInt(multi.getParameter("re_step")));
		article.setRe_level(Integer.parseInt(multi.getParameter("re_level")));
		}article.setWriter(multi.getParameter("writer"));
		article.setEmail(multi.getParameter("email"));
		article.setSubject(multi.getParameter("subject"));
		article.setPasswd(multi.getParameter("passwd"));
		article.setContent(multi.getParameter("content"));
		article.setBoardid(multi.getParameter("boardid"));
		article.setIp(request.getRemoteAddr());
			
		if (file != null)	{
			article.setFilename(filename);
			article.setFilesize((int) file.length());
		}
		else {
			article.setFilename(" ");
			article.setFilesize(0);
		}
		
		System.out.println(article);
		BoardDBBean dbPro = BoardDBBean.getInstance();
		dbPro.insertArticle(article);
		request.setAttribute("pageNum", pageNum);
		response.sendRedirect(request.getContextPath()+"/board/list?pageNum="+pageNum+"&boardid="+boardid);
			return null;
			} 
	

	
	//------------------------------------------------------------------------------------
	
	public String updateForm(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		String boardid = request.getParameter("boardid");
		  if (boardid==null) boardid = "1";
		  String pageNum = request.getParameter("pageNum");
			if (pageNum == null || pageNum == "") {
				pageNum = "1";	}
			
			
			  int num = Integer.parseInt(request.getParameter("num"));
			  
			      BoardDBBean dbPro = BoardDBBean.getInstance();
			      BoardDataBean
			      article =  dbPro.getArticle(num, boardid, "update");
			      request.setAttribute("article", article);
	//주의!! 선생님 코드에 없음
			      request.setAttribute("pageNum", pageNum);
		
		
		return "/view/updateForm.jsp";
			} 

	
	
	//------------------------------------------------------------------------------------
	
	public String updatePro(HttpServletRequest request,
			 HttpServletResponse response)  throws Throwable { 
		String boardid = request.getParameter("boarid");
		  if (boardid==null) boardid = "1";
		  String pageNum = request.getParameter("pageNum");
			if (pageNum == null || pageNum == "") {
				pageNum = "1";	}
		
		// <jsp:useBean id="article"  class="board.BoardDataBean">
		//		 <jsp:setProperty name="article" property="*"/>
		// </jsp:useBean>
		
			BoardDataBean article = new BoardDataBean();
			if (request.getParameter("num") != null
					&& !request.getParameter("num").equals("")) {
			article.setNum(Integer.parseInt(request.getParameter("num")));
			article.setRef(Integer.parseInt(request.getParameter("ref")));
			article.setRe_step(Integer.parseInt(request.getParameter("re_step")));
			article.setRe_level(Integer.parseInt(request.getParameter("re_level")));
			}
			
			article.setWriter(request.getParameter("writer"));
			article.setEmail(request.getParameter("email"));
			article.setSubject(request.getParameter("subject"));
			article.setPasswd(request.getParameter("passwd"));
			article.setContent(request.getParameter("content"));
			article.setBoardid(request.getParameter("boardid"));
			article.setIp(request.getRemoteAddr());  
			System.out.println(article); 
		  
			BoardDBBean  dbPro = BoardDBBean.getInstance();
		 	int chk= dbPro.updateArticle(article); 
		 	request.setAttribute("chk", chk);
		 	request.setAttribute("pageNum", pageNum);
		 	
		return "/view/updatePro.jsp";
			} 
	
	
	
	
	

	//------------------------------------------------------------------------------------
		
	public String deleteForm(HttpServletRequest request,
				 HttpServletResponse response)  throws Throwable { 
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("num", num);
		return "/view/deleteForm.jsp";
				} 
	
	
	//------------------------------------------------------------------------------------
		
	public String deletePro(HttpServletRequest request,
				 HttpServletResponse response)  throws Throwable { 
		String boardid = request.getParameter("boarid");
		  if (boardid==null) boardid = "1";
		  String pageNum = request.getParameter("pageNum");
			if (pageNum == null || pageNum == "") {
				pageNum = "1";	}
		
		  int num = Integer.parseInt(request.getParameter("num"));
		  String passwd = request.getParameter("passwd");
		  BoardDBBean dbPro = BoardDBBean.getInstance();
		  int check = dbPro.deleteArticle(num, passwd, boardid); 
		  request.setAttribute("pageNum", pageNum);
		  request.setAttribute("check", check);
		return "/view/deletePro.jsp";
				} 
		
	
	

}
