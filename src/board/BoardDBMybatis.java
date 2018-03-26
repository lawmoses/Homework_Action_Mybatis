package board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class BoardDBMybatis extends MybatisConnector {

	private final String namespace = "ldg.mybatis"; //쓰면 해당되는 아이디가 그룹핑된다??
	
	//BoardDBMybatis 싱글톤 만드는 이유는 리스트 액션에 수정해주기 위해
	private static BoardDBMybatis instance = new BoardDBMybatis();
	private BoardDBMybatis() {}
	public static BoardDBMybatis getInstance() {
		return instance; 
	}
	
	SqlSession sqlSession; //역할은????
	
	public int getArticleCount(String boardid) {
		int x = 0;
		sqlSession = sqlSession();
		System.out.println(sqlSession);
		Map<String, String> map = new HashMap<String, String>(); //파라미터 전송을 map으로 한다.
		map.put("boardid", boardid);
		x = sqlSession.selectOne(namespace + ".getArticleCount", map); //파라미터에 보드id가 있으므로 마지막에 추가, map추가
			//왜 오브젝트?? 왜? .찍고 붙이나? //xml의 겟아티클카운트 부른다. 앞에 네임스페이스가있어야한다.
		sqlSession.close();
		return x;
	}

	
	
	public List getArticles(int startRow, int endRow, String boardid) {
		
		sqlSession = sqlSession();
		Map map = new HashMap(); //파라미터 전송을 map으로 한다.
		map.put("startRow", startRow);
		map.put("endRow", endRow);
		map.put("boardid", boardid);
		
		//List를 만들어줘야한다.
		List li = sqlSession.selectList(namespace + ".getArticles", map); 
		//파라미터에 보드id가 있으므로 마지막에 추가, map추가
		// 왜 오브젝트? 리스트? 이해안됨
		//왜 오브젝트?? 왜? .찍고 붙이나? //xml의 겟아티클카운트 부른다. 앞에 네임스페이스가있어야한다.
		//연결은 리스트엑션에서 한다.
		sqlSession.close();
		return li;
	}
	
	
	public void insertArticle(BoardDataBean article) {								//??BoardDataBean article
		
		sqlSession = sqlSession();
		//article이 오브젝트니까 위에서 처럼 맵은 필요없다.
		int number = sqlSession.selectOne(namespace +".getNextNumber", article);
		//번호표 받는 쿼리와 링크 시킴

		//getNum=0이라는 뜻은? 무슨뜻?
		if (article.getNum()!=0) {
			//답글쓰기 부분과 링크 할 거임
			sqlSession.update(namespace +".updateRe_step", article); //update는 새로 데이터 넣은게 아니고, 새로 ref...restep을 조정한 것이다.
			//?? 신제품 주문,제품 동시 출시
			article.setRe_level(article.getRe_level()+1);
			article.setRe_step(article.getRe_step()+1);
			//여기까지 답글부분이다
		} else {
			//새글쓰기 부분
			//새글은 알이레벨은 영, 알이스텝도 0이다.
			article.setRef(number); //새글이니까 위의  number에서 가져옴, ref를 number으로 한것. 새글일 경우 ref와 number와 같은 번호이다
			article.setRe_level(0);
			article.setRe_step(0);
		}	
		article.setNum(number);
		
		sqlSession.insert(namespace+".insertBoard",article);
		sqlSession.commit();
		sqlSession.close();	
	}
	
	
	public BoardDataBean getArticle //게시물 볼때 컨텐트이면, readcount 하고, 
		(int num , String boardid, String chk) { //chk는 컨텐트일때는 리드카운트 추가해야하고, 그 외에거는 리드카운트 추가.(게시말 몇번 봤는가)
	
		/*sql="update board set readcount=readcount+1 "
				+ "where num = ? and boardid = ?";
	
		sql="select * from board where num = ? and boardid = ?";*/
	
		sqlSession = sqlSession();
		Map map = new HashMap(); //파라미터 전송을 map으로 한다.
		map.put("num", num);	//num의 의미 - 보드아이디랑 상관없이 프라이머리 키이다
		map.put("boardid", boardid);
		//chk는 컨텐트일때는 리드카운트 추가해야하고, 그 외에거는 리드카운트 추가.(게시말 몇번 봤는가)
		if (chk.equals("content")) {										//??"content"
			sqlSession.update(namespace + ".addReadCount", map);
		}
		
		//List를 만들어줘야한다.
		BoardDataBean article = sqlSession.selectOne(namespace + ".getArticle", map); 
		//파라미터에 보드id가 있으므로 마지막에 추가, map추가
		// 왜 오브젝트? 리스트? 이해안됨
		//왜 오브젝트?? 왜? .찍고 붙이나? //xml의 겟아티클카운트 부른다. 앞에 네임스페이스가있어야한다.
		//연결은 리스트엑션에서 한다.
		sqlSession.commit();
		sqlSession.close();
		return article;	
	}
	
	
	
	public int updateArticle(BoardDataBean article) {
		//update할때는 리절트타입 쓰면 안되. 쓰면 에러난다. 왜냐하면, 오라클에서는 DML을 수행하면 오라클 자체내에서 DML카운터를 올려준다. 오라클에서 아예 올려준다.
		//그래서 update, insert, delete는 리절트 타입을 쓰먄 안되. 올려주기 때문에.
		//데이터 업데이트,딜리트,수정 후에는 데이터베이스에서 알아서 수를 리턴해준다.
		//마라마터 타입을 해쉬맵으로 쓰면 보낼때 맵으로 보내면 되는 것이다.
		//String sql = "update board set writer=?,email=?, subject=? ,content=? where num=? and passwd = ?";
		
		sqlSession = sqlSession();
		//List를 만들어줘야한다.
		int chk = sqlSession.update(namespace + ".updateArticle", article); 
		//파라미터에 보드id가 있으므로 마지막에 추가, map추가
		// 왜 오브젝트? 리스트? 이해안됨
		//왜 오브젝트?? 왜? .찍고 붙이나? //xml의 겟아티클카운트 부른다. 앞에 네임스페이스가있어야한다.
		//연결은 리스트엑션에서 한다.
		sqlSession.commit();
		sqlSession.close();
		return chk;	
			
	}
	
	
	public int deleteArticle(int num, String passwd, 
			String boardid) throws Exception {
	
		sqlSession = sqlSession();												//??sqlSession
		Map map = new HashMap(); //파라미터 전송을 map으로 한다.
		map.put("num", num);	//num의 의미 - 보드아이디랑 상관없이 프라이머리 키이다
		map.put("passwd", passwd);
		map.put("boardid", boardid);
		//List를 만들어줘야한다.
		int chk = sqlSession.delete(namespace + ".deleteArticle", map); 		//??어느거는 updata 어느거는delete
		//파라미터에 보드id가 있으므로 마지막에 추가, map추가
		// 왜 오브젝트? 리스트? 이해안됨
		//왜 오브젝트?? 왜? .찍고 붙이나? //xml의 겟아티클카운트 부른다. 앞에 네임스페이스가있어야한다.
		//연결은 리스트엑션에서 한다.
		sqlSession.commit();
		sqlSession.close();
		return chk;	

	}
}
	
	

//------------삭제한 쿼리문

//select boardser.nextval from dual

		//update board set re_step=re_step+1
		// where ref= ? and re_step> ? and boardid = ?
		
		/*insert into board(num,writer,email,subject,
				 passwd,reg_date,
		ref,re_step,re_level,content,ip, boardid, filename, filesize)
				values(?,?,?,?,?,sysdate,?,?,?,?,?, ?, ?, ?)	*/
