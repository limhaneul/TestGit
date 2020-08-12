package java0723;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//ChatServer클래스 : 채팅서버
public class ChatServer {

	// 서버 소켓
	ServerSocket server;
	
	// 소켓 
	Socket chlid;

	// 포트 번호
	static final int PORT=5000;
	
	// 쓰레드가 공유할 데이터  HashMap : k - 아이디 , v - 출력스트림
	HashMap<String,ObjectOutputStream> hm;
	
	
	// 서버 객체 생성시 서버 연결후 대기
	public ChatServer(){
				
				try {
					// 포트 지정 서버소켓 생성
					// 소켓 객체 생성 실패시 프로세스 종료
					server=new ServerSocket(PORT);
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
					System.exit(0);
				}
				
				System.out.println("****채팅 서버 OPEN*****");
				System.out.println("서버는 클라이언트 요청 대기중......");
				
				
				// 쓰레드(클라이언트 소켓) 간의 데이터 공유 객체
				
					hm = new HashMap<String,ObjectOutputStream>();
				
				
				
				try {
					
					while(true){
						//서버가 클라이언트 요청 대기(무한 루프)
						chlid = server.accept(); 
						
						//사용자 접속과 상관 없이 데이터 송 수신 처리(멀티 쓰레딩)
						//= > Thread 객체 생성 ( 연결 된 소켓 정보, 공유데이터)
						CharServerThread cst = new CharServerThread(chlid,hm);
						
						Thread  th  =new Thread(cst);
						
						th.start();
						
						
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
			
				
				}
				
		
		
	}
	
	
	
	public static void main(String[] args) {

		new ChatServer();
	
	}

}
