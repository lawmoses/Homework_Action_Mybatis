package board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class MybatisDao extends MybatisConnector{

	private final String namespace = "ldg.mybatis";
	
	private static MybatisDao instance = new MybatisDao();
	
	private MybatisDao () {} //선생님 코드에는 없는 것
	
	public static MybatisDao getInstance() {
		return instance;
	}
	
	SqlSession sqlSession;
	
	public List<BoardDataBean> selelcBoard() {
		sqlSession = sqlSession();
		System.out.println("selectboard");
		
		try {
			return sqlSession.selectList(namespace + ".boardList");
		} finally {
			sqlSession.close();
		}
	}

	public List<BoardDataBean> selelcBoard(int num) {
		sqlSession = sqlSession();
		System.out.println("selectboard"); //내가 넣은 코드
		Map map = new HashMap();
		map.put("num", num);
		try {
			return sqlSession.selectList(namespace + ".boardList", map);
		} finally {
			sqlSession.close();
		}
	}
	
	public List<BoardDataBean> selelcBoard(String boardid) {
		sqlSession = sqlSession();
		System.out.println("selectboard");
		Map map = new HashMap();
		map.put("boardid", boardid);
		try {
			return sqlSession.selectList(namespace + ".boardList", map);
		} finally {
			sqlSession.close();
		}
	}
	
}
