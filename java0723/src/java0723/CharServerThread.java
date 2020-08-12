package java0723;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

// 클라이언트 접속을 유지하면서 데이터 송수신
// 클라이언트가 서버에 접속할때마다 해당객체 생성
public class CharServerThread implements Runnable {

	//클라이언트와 통신하는 소켓
	Socket child;
	
	//데이터 송/수신 객체
	ObjectInputStream ois;
	ObjectOutputStream oos;

	//사용자 ID정보
	String user_id;
	
	//쓰레드간의 데이터를 공유
	HashMap<String, ObjectOutputStream> hm;
	
	
	public CharServerThread() {
	}
	
	//*객체 생성시 소켓 정보, 해쉬 맵 정보를 가져오기
	public CharServerThread(Socket s, HashMap hm) {
	
		child=s;		
		this.hm=hm;
		
		//클라이언트 접속 IP 주소를 출력(서버확인용)
		System.out.println(child.getInetAddress()+"로 부터 연결 요청 받음!! ");
		
		try {
			// 데이터 송수신을 위한 스트림 객체 생성
			ois =new ObjectInputStream(child.getInputStream());
			oos= new ObjectOutputStream(child.getOutputStream());
			
			// 사용자의 아이디 정보를 가져와서 출력
			// 클라이언트가 가장 먼저 보내는 데이터가 아이디 값
			user_id = (String) ois.readObject();
			
			// 서버에 접속되어 있는 (방에있는) 모든 클라이언트에게 전달(브로드캐스트)
			// XXXX 님이 접속 하셨습니다
			broadcast(user_id+" 님이 접속하셨습니다");
			
			// 서버 확인용 출력
			System.out.println("접속한 클라이언트 아이디 : "+user_id);
			
			
			
			// 여러 클라이언트에게 공유되는 데이터를 동기화 처리
			// 서버가 접속하는 클라이언트 정보를 저장하는 공간 HashMap 을 동기화
			synchronized (hm) {
				// 해쉬맵에 아이디 / 출력스트림 저장
				// 모든 접속된 클라이언트가 공유해야하는 값이기 때문에 동기화 처리가 필요하다
				// 어차피 동기화 처리를 할거기 때문에 this는 필요하지 않다
				
				hm.put(this.user_id, oos);
				
				
			}
			
			
			
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}//생성자
	
	
			// 클라이언트가 보내는 모든 메세지를 받아서 전달
			@Override
			public void run() {
				
				// 전달받는 메세지
				
				String receiveData;
				
				try {
					while(true){
					
					// 클라이언트로 부터 메세지 수신
					receiveData=(String)ois.readObject();
					
					// 받은 메세지를 모든 클라이언트한테 전달 (브로드 캐스트)
					broadcast(user_id+" : "+receiveData);
					
					
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//		e.printStackTrace();
				System.out.println("클라이언트 요청 종료(강제종료)");
				}finally{
					
					//사용자 종료시 hm 저장된 정보를 제거
					synchronized (hm) {
						//사용자 ID를 사용해서  저장된 정보를 제거
						hm.remove(user_id);
						
					}
				
					// 사용자가 채팅방에서 나간 사실을 알려주기(브로드 캐스트)
					
					//서버에서 확인용
					System.out.println(user_id + "님이 나갔습니다.");
					broadcast(user_id + "대화방을 나갔습니다.");
					
					if(child != null){
						try {
							child.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}//finally
				
				
				
				
			}//run()
			
			
			
			
			
			//broadcast : 방송  : 메세지를 전달받아서  모든 클라이언트에 채팅방에 전달  
			public void broadcast(String message){
				
				// 사용자의 정보를 저장하는 HashMap 동기화 해서
				// 출력정보를 사용해서 메세지 전달
				
				synchronized (hm) {
					
					// 해쉬맵에 저장된 모든 출력스트림을 사용해서
					// 메세지를 전달		
						try {
							for(ObjectOutputStream oos:hm.values()){
								oos.writeObject(message);
								oos.flush();
							}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					
				
				
				
				
			}
				
	
	
}
