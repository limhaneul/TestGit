package java0723;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.chrono.JapaneseChronology;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.soap.SOAPException;

// 멀티 캐스트 채팅을 위한 클라이언트
public class GUIChatClient extends JFrame implements ActionListener{

	
		//카드 레이아웃
		CardLayout card;
		
	
		//화면 정보-접속창
		JButton btn_connet;//접속창 버튼
		JTextField txt_server_ip; // ip 입력창
		JTextField txt_name; // 접속할 이름(아이디)

		//화면정보 - 채팅창
		JButton btn_exit;  // 채팅창에서 종료버튼
		JButton btn_send;  // 채팅창에서 보내기 버튼
		JTextArea txt_list; //메세지 출력되는곳
		JTextField txt_input; // 입력메세지
		
		//////////////////////////////////////////////////////////////////////////
		
		//채팅(통신)을 하기 위한 정보
		
		//접속할 서버의 IP주소
		String IPAddress;
		
		//포트번호
		static final int PORT=5000;
		
		//클라이언트 소켓	
		Socket client = null;
	
		//데이터 입출력 스트림 객체
		ObjectInputStream ois;
		ObjectOutputStream oos;
		
		//아이디 
		String user_id;
		
		//서버에서 보낸 메세지를 받기위한 쓰레드 객체
		
		ReceiveDataThread rdt;
		
		
		JScrollPane scroll;
		
		public GUIChatClient(){
			super("채팅클라이언트");
			
			//레이아웃 borderLayout -> cardLayout
			card = new CardLayout();		
			setLayout(card);
			
			//접속 화면
			JPanel connect=new JPanel();
			connect.setBackground(Color.YELLOW);
			connect.setLayout(new BorderLayout());
			
			
			//접속창 상단
			connect.add(new JLabel("다중 채팅 접속창",JLabel.CENTER),BorderLayout.NORTH);
			
			
			//접속창 센터
			JPanel connect_sub = new JPanel();//flowLayout 기본설정
			connect_sub.setBackground(Color.ORANGE);
			
			connect_sub.add(new JLabel("서버 아이피 :"));
			txt_server_ip = new JTextField("127.0.0.1",15);
			connect_sub.add(txt_server_ip);
			
			
			connect_sub.add(new JLabel("접속 아이디(대화명) :  "));
			txt_name = new JTextField("사용자",15);
			connect_sub.add(txt_name);
			
			connect.add(connect_sub,BorderLayout.CENTER);
			
			
			
			//접속창 하단
			btn_connet = new JButton("채팅 서버 접속");
			
			connect.add(btn_connet,BorderLayout.SOUTH);
			
			// 접속 버튼 클릭시 이벤트 처리
			
			
			
			//add(connect);
			//채팅창 화면
			JPanel chat = new JPanel();
			
			chat.setLayout(new BorderLayout());
			
			chat.setBackground(Color.CYAN);
			
			//상단
			chat.add(new JLabel("채팅 프로그램 v1.0" ,JLabel.CENTER),BorderLayout.NORTH);
			
			txt_list = new JTextArea();
			scroll=new JScrollPane(txt_list);
			
		
			chat.add(scroll,BorderLayout.CENTER);
			//센터
			
			
			//하단
			
			JPanel chat_sub =new JPanel();
			

			
		
			
			
			//패널 chat_sub의 절대 크기를 지정
			chat_sub.setPreferredSize(new Dimension(500, 60));
			
			txt_input = new JTextField(25);
			
	
			btn_send =new JButton("전송");
			btn_exit = new JButton("종료");
			
			// 메세지 입력창 이벤트 리스너 연결
			txt_input.addActionListener(this);

			//버튼 이벤트 리스너 연결
			btn_exit.addActionListener(this);
			btn_send.addActionListener(this);
			
			chat_sub.add(txt_input);
			chat_sub.add(btn_send);
			chat_sub.add(btn_exit);
			
			chat.add(chat_sub,BorderLayout.SOUTH);
			
			
			
			//버튼 클릭 이벤트
			btn_connet.addActionListener(this);
			
			//add(chat);
			
			
			// 접속화면 , 채팅화면을 카드 레이아웃에 추가
			
			add(connect,"접속창");
			add(chat , "채팅창");
			
			//카드 레이아웃의 초기화면
			card.show(this.getContentPane(),"접속창");//접속창 먼저 보이게함
			
		
			
			
			
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setBounds(750,300,450,500);
			setVisible(true);
		}//생성자
		
		// 접속에 필요한 동작 : 서버 아이피 , 포트번호 소켓 생성 + 사용자 아이디 전송 
		void init() throws Exception
		{
			//서버 아이피 주소 가져오기
			IPAddress = txt_server_ip.getText();
			
			//서버 접속 소켓 생성
			
			 client = new Socket(IPAddress,PORT);
			 
			 // 서버와 입출력 처리 위한 입출력 스트림
			oos = new ObjectOutputStream(client.getOutputStream());
			
			ois =new ObjectInputStream(client.getInputStream());
			//////////////////////////////////////////////////////////
			// 사용자 이름을 서버로 전송
			
			user_id = txt_name.getText();
			
			
			oos.writeObject(user_id);
			oos.flush();
			
			//서버가 보낸 메세지를 받기(수신)
			
			rdt=new ReceiveDataThread();
			
			Thread th = new Thread(rdt);
			th.start();
			System.out.println("클라이언트는 서버의 메시지를 수신 대기중");
			
			
			//화면 전환(접속창 -> 채팅창)
			card.show(this.getContentPane(),"채팅창");
			txt_input.requestFocus();
			
		}
		
		
		


	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//이벤트 발생 객체 정보 저장
		Object obj=e.getSource();
		
		
		//접속창 - 서버 접속 버튼 클릭
		if(obj==btn_connet){
			System.out.println("접속 버튼");
			//card.show(this.getContentPane(),"채팅창");
			//채팅 준비하는 동작
			try {
				init();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//채팅창 종료 버튼 클릭
		else if(obj == btn_exit){
			System.out.println("종료");
			System.exit(0);//프로세스 킬
		}
		
		//채팅창 - 전송버튼클릭 or 입력창에서 엔터
		else if(obj == btn_send || obj==txt_input){
			System.out.println("전송버튼");
			
			//메세지를 입력받아서 서버로 전송(서버가 모든 클라이언트 한테 방송)
			
			String sendData = txt_input.getText();
			
			
			try {
				//서버로 전송
				oos.writeObject(sendData);
				oos.flush();
				
				
				// 채팅창에 남아있는 전송 된 메세지를 초기화
				
				txt_input.setText("");
				
				txt_input.requestFocus();
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
		}else{
			
			System.out.println("에러! 해당 버튼 없음!");
		}
		
	}
	
	
		// 서버가 보낸 메세지를 받는 클래스 구현(내부클래스-함수처럼 사용됨)
	class ReceiveDataThread implements Runnable{
		// 서버에서 보낸 메세지
		
		String receiveData;
		
			@Override
			public void run() {
					try {
						while(true){
							
						
							//서버메시지를 받아서
							receiveData=(String)ois.readObject();
				
							
							//클라이언트 채팅창에(txt_list) 추가
							txt_list.append(receiveData+"\n");
							
							
						}
						
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					


			}//run
			
	}//ReceiveDataThread
		
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUIChatClient();
		
	}

}
