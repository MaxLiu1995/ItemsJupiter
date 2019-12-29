package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 在没有测试的情况下去访问，出现异常情况
		DBConnection connection =DBConnectionFactory.getConnection();
		try {
			HttpSession session = request.getSession(false);
			//起初设为false表示未登陆
			JSONObject result = new JSONObject();
			if(session != null) {
				String userId = session.getAttribute("user_id").toString();
				result.put("status","OK").put("user_id", userId).put("name",connection.getFullname(userId));
			}else {
				result.put("status", "invalid session");
				response.setStatus(403);
			}
			RpcHelper.writeJsonObject(response, result);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			
			JSONObject  body = RpcHelper.readJSONObject(request);
			String userId = body.getString("user_id");
			String password = body.getString("password");
			
			JSONObject result = new JSONObject();
			if(connection.verifyLogin(userId,password)) {
				HttpSession session  = request.getSession();
				session.setAttribute("user_id", userId);
				session.setMaxInactiveInterval(600);
				//expire time
				result.put("status","OK").put("user_id",userId).put("name",connection.getFullname(userId));
				
				
			}else {
				result.put("status","user doesn't exist");
				response.setStatus(401);
			}
			RpcHelper.writeJsonObject(response,result);
			//将结果保存到response里面
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
			//手动关闭connection
		}
		
		
	}

}
