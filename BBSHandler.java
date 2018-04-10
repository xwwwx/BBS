/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.nashorn.internal.runtime.ScriptingFunctions;
import sun.net.www.content.audio.x_aiff;

public class BBSHandler extends Thread {
    BBS bbs;
    static Vector[] chat_handlerx={new Vector(10),new Vector(10),new Vector(10),new Vector(10),new Vector(10)};
    static ArrayList game_room = new ArrayList();
    private BufferedReader in;  
    private PrintWriter out ;
    private Socket socket;
    private String user;
    boolean winflag=false;
    String[][] game=new String[5][5];
    public BBSHandler(Socket socket,SQL sql) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"big5"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"big5"));
        this.socket=socket;
        bbs=new BBS(socket,sql,in,out);
         
     }
     public void run() {
        bbs.login();
        user=bbs.getuser();
        try{
         while(true){
            out.print("(1) 討論區\r\n");
            out.flush();
            out.print("(2) 聊天室\r\n");
            out.flush();
            out.print("(3) 遊戲區\r\n");
            out.flush();
            out.print("(4) 帳號設定\r\n");
            out.flush();
            out.print("(5) 離開\r\n");
            out.flush();
            out.print("輸入代號前往:");
            out.flush();
            String key=in.readLine();
            switch(key){
                case "1":
                    bbs.article();
                    break;
                case "2":
                    chat();
                    break;
                case "3":
                    game();
                    break;
                case "4":
                    bbs.setting();
                    break;
                case "5":
                    out.close();
                    in.close();
                    socket.close();
                    break;
                default:
                    break;
            }
        }
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
     public void chat(){
        String line;
        Vector handlers=chat_handlerx[0];
            synchronized(handlers) {      
            handlers.addElement(this);  
            }
            out.print("歡迎來到頻道0!\r\n");
            out.print("輸入/chX 轉換頻道(X=0~4)\r\n");
            out.print("輸入/quit 離開聊天室\r\n");
            out.flush();
            try {      
                while(!(line = in.readLine()).equalsIgnoreCase("/quit")) {   
                    if(line.length() == 4 && line.substring(0,3).equals("/ch")){
                        if(Integer.parseInt(line.substring(3)) >= 0 && Integer.parseInt(line.substring(3)) <= 4)
                            synchronized(handlers) {
                                handlers.removeElement(this);
                                handlers=chat_handlerx[Integer.parseInt(line.substring(3))];
                                handlers.addElement(this);  
                                out.print("歡迎來到頻道"+line.substring(3)+"!\r\n");
                                out.print("輸入/chX 轉換頻道(X=0~4)\r\n");
                                out.print("輸入/quit 離開聊天室\r\n");
                                out.flush();
                            }
                        continue;
                    }
                    for(int i = 0; i < handlers.size(); i++) {     
                        synchronized(handlers) {               
                            BBSHandler handler =            
                                (BBSHandler)handlers.elementAt(i);
                            if(handler==this){
                                continue;
                            }
                            handler.out.println(user+":"+line + "\r");    
                            handler.out.flush();    
                        }   
                    }      
                }  
            } catch(IOException ioe) {      
                System.out.println(ioe);
                line="";
            }
                synchronized(handlers) {       
                    handlers.removeElement(this);   
                }      
     }
     public void game() {
        
        Vector room;
        int p1,p2,r,c;
        String g="",s;
        BBSHandler opponent = null;
        for(int i=0;i<game.length;i++){
            for(int j=0;j<game[i].length;j++){
                game[i][j]=" ";
            }
        }
        try{
            if(game_room.isEmpty()){
                out.print("開新房間1\r\n");
                out.flush();
                game_room.add(new Vector());
                room = (Vector)game_room.get(0);
                p1=room.size();
                }
            else{
                
                Vector tmp;
                tmp = (Vector)game_room.get(game_room.size()-1);
                if(tmp.size() < 2){
                    out.print("加入房間\r\n");
                    out.flush();
                    room=tmp;
                    p1=room.size();
                }
                else{
                    out.print("開新房間2\r\n");
                    out.flush();
                    game_room.add(new Vector());
                    room = (Vector)game_room.get(game_room.size()-1);
                    p1=room.size();
                }
            }
            room.addElement(this);
            
            if(room.size() < 2){
                synchronized(this){
                out.print("等待玩家加入\r\n");
                out.print(room.size());
                out.print(game_room.size());
                out.flush();
                this.wait();
                }
            }
            else{
                for(int i=0;i<room.size();i++){
                    if (room.get(i) != this){
                        opponent = (BBSHandler) room.get(i);
                    }
                }
                synchronized(opponent){
                opponent.notify();
                }
            }
            for(int i=0;i<room.size();i++){
                if (room.get(i) != this){
                    opponent = (BBSHandler) room.get(i);
                }
            }
            
            opponent.out.print("5X5圈圈叉叉遊戲開始!\r\n");
            opponent.out.flush();
            if(p1 == 0){
               out.print("你先開始\r\n!");
               out.flush(); 
               s="O";
               
               for(int i=0;i<game.length;i++){
                    g="";
                    for(int j=0;j<game[i].length;j++){
                       g+= game[i][j];
                       if(j != game[i].length-1){
                           g+= "|";
                       }
                    }
                    out.print(g+"\r\n");
                    if(i != game[i].length-1){
                        out.print("----------\r\n");
                    }
                    out.flush();
                }
            }
            else{
                synchronized(this){
                    s="X";
                    out.print("對方先!\r\n");
                    out.flush();
                    this.wait();
                }
            }
            String line;
            while(true) {
                if (winflag) {
                    game_room.remove(this);
                    break;
                }
                out.print("請輸入你要下的座標(ex: 0,0)!\r\n");
                out.flush();
                line=in.readLine();
                if(game[(int)(line.charAt(0)-'0')][(int)(line.charAt(2)-'0')].equals(" ")){
                    game[(int)(line.charAt(0)-'0')][(int)(line.charAt(2)-'0')]=s;
                    opponent.game[(int)(line.charAt(0)-'0')][(int)(line.charAt(2)-'0')]=s;
                }
                out.print("\r\n");
                opponent.out.print("\r\n");
                out.flush();
                opponent.out.flush();
                for(int i=0;i<game.length;i++){
                    g="";
                    for(int j=0;j<game[i].length;j++){
                       g+= game[i][j];
                       if(j != game[i].length-1){
                           g+= "|";
                       }
                    }
                    out.print(g+"\r\n");
                    opponent.out.print(g+"\r\n");
                    if(i != game[i].length-1){
                        out.print("----------\r\n");
                        opponent.out.print("----------\r\n");
                    }
                    out.flush();
                    opponent.out.flush();
                }
                for( r=0;r<game.length;r++){
                    for( c=0;c<game[r].length;c++){
                        if(game[r][c] != s){
                            break;
                        }
                    }
                    if(c == game[r].length){
                        winflag=true;
                    }
                }
                for( r=0;r<game.length;r++){
                    for( c=0;c<game[r].length;c++){
                        if(game[c][r] != s){
                            break;
                        }
                    }
                    if(c == game[r].length){
                        winflag=true;
                    }
                }
                for( r=0;r<game.length;r++){
                    if(game[r][r] != s){
                        break;
                    }
                }
                if(r == game.length){
                    winflag=true;
                }
                for( r=game.length-1;r>=0;r--){
                    if(game[game.length-1-r][r] != s){
                        break;
                    }
                }
                if(r == -1){
                    winflag=true;
                }
                if(winflag){
                    synchronized(opponent){
                        out.print("你贏了!\r\n");
                        out.flush();
                        winflag=false;
                        opponent.out.print("你輸了!\r\n");
                        opponent.out.flush();
                        opponent.winflag=true;
                        opponent.notify();
                        game_room.remove(this);
                        break;
                    }
                }
                 synchronized(opponent){
                    opponent.notify();
                }
                synchronized(this){
                    this.wait();
                }
            }
            Vector tmp;
            for(int i=0;i<game_room.size();i++){
                tmp=(Vector)game_room.get(i);
                if(tmp.isEmpty()){
                    game_room.remove(i);
                }
            }
        }
        catch(Exception e){
            System.out.print(e);
        }
     }
}