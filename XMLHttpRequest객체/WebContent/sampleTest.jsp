<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	//4.HTML에서 비동기 방식으로 요청한 요청값 전달받기
	request.setCharacterEncoding("UTF-8");

	String userid=request.getParameter("userid");
	String passwd=request.getParameter("passwd");
	
	//현재 sampleTest.jsp서버가 실행하는 페이지는 4.html 요청한 페이지로 보낼 응답할값을 마련
	String text=userid+"의"+passwd+"은 비밀이다";
	
	//클라이언트의 웹 브라우저로 출력 하면 ~웹 브라우저에서 출력한 정보를 웹브라우저가 4.html로 보낸다
	
	 out.print(text);
	
	
%>