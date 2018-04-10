package chatserver;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
public class BBS {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String user;
    String line;
    SQL sql;
    static Vector handlers=new Vector(10);
    
    public BBS(Socket socket,SQL sql,BufferedReader in,PrintWriter out) throws IOException{
        this.socket = socket;
        this.in=in;
        this.out=out;
        this.sql=sql;
    }
    public void login(){
        try{
            do{
            out.print("user:(輸入/signup 註冊帳號)");
            out.flush();
            user=in.readLine();
            System.out.println(user);
            if(user.equals("/signup")){
                signup();
                return;
            }
            out.print("password:");
            out.flush();
            String password=in.readLine();
            boolean log=sql.searchuser(user, password);
            if(!log){
                   out.print("帳號或密碼錯誤\r\n");
                   out.flush();
            }
            else{
                out.print("登入成功!\r\n");
                out.flush();
                break;
            }
            }while(true);
        }
        catch(Exception e){}
    }
    public void signup() throws IOException {
        out.print("註冊會員!\r\n");
        out.flush();
        out.print("請輸入帳號:");
        out.flush();
        String users=in.readLine();
        out.print("請輸入密碼:");
        out.flush();
        String passwords=in.readLine();
        out.print("確認密碼:");
        out.flush();
        String passwordc=in.readLine();
        out.print("請輸入姓名:");
        out.flush();
        String name=in.readLine();
        out.print("請輸入生日(XXXX-XX-XX):");
        out.flush();
        String birthday=in.readLine();
        if(sql.checkuser(users)){
            out.print("帳號已存在\r\n");
            out.flush();
            signup();
            return;
        }
        if(!passwords.equals(passwordc)){
            out.print("確認密碼與密碼不符\r\n");
            out.flush();
            signup();
            return;
        }
        if(!(birthday.length()==10 && birthday.charAt(4) == '-' && birthday.charAt(7) == '-')){
            out.print("生日格式錯誤\r\n");
            out.flush();
            signup();
            return;
        }
        sql.adduser(users, passwords, name, birthday);
        user=users;
        out.print("註冊成功!\r\n");
        out.flush();
        
    }
    public void article() throws IOException{
        while (true) {            
            out.print("文章編號\t作者ID\t標題\t\t\t\t日期\r\n");
            out.flush();
            sql.printarticlelist(out);
            out.print("輸入文章編號閱讀(輸入/back返回選單 輸入/search搜尋文章 輸入/post發表文章 輸入/del 刪除文章):");
            out.flush();
            String id =in.readLine();
            if(id.equals("/back")){
                return;
            }
            if(id.equals("/search")){
                search();
                return;
            }
            if(id.equals("/post")){
                post();
                return;
            }
            if(id.equals("/del")){
                out.print("請輸入欲刪除之文章編號:");
                out.flush();
                String key=in.readLine();
                sql.delarticle(user, key, in, out);
                return;
            }
            sql.printarticle(id, out);
            out.print("按下 b 繼續..\r\n");
            out.flush();
            int trash=in.read();
        }
    }
    public void post() throws IOException{
        out.print("請輸入標題:\r\n");
        out.flush();
        String title=in.readLine();
        out.print("請輸入內容(輸入/article end/結束文章):\r\n");
        out.flush();
        String tmp,article="";
        while(!(tmp=in.readLine()).equals("/article end/"))
            article+=tmp+"\r\n";
        sql.postarticle(user, title, article);
    }
    public void search() throws IOException{
        out.print("(1)作者 (2)標題 請輸入所以項目:\r\n");
        out.flush();
        String k=in.readLine();
        out.print("請輸入索引:\r\n");
        out.flush();
        String key=in.readLine();
        switch(k){
            case "1":
                out.print("文章編號\t作者ID\t標題\t\t\t\t日期\r\n");
                out.flush();
                sql.searchbyuser(key,out);
                out.print("輸入文章編號閱讀(輸入/back返回選單 輸入/search搜尋文章 輸入/post發表文章 輸入/del 刪除文章):");
                out.flush();
                String id =in.readLine();
                if(id.equals("/back")){
                    return;
                }
                if(id.equals("/search")){
                    search();
                    return;
                }
                if(id.equals("/post")){
                    post();
                    return;
                }
                if(id.equals("/del")){
                    out.print("請輸入欲刪除之文章編號:");
                    out.flush();
                    key=in.readLine();
                    sql.delarticle(user, key, in, out);
                    return;
                }
                sql.printarticle(id, out);
                out.print("按下 b 繼續..\r\n");
                out.flush();
                int trash=in.read();
                break;
            case "2":
                out.print("文章編號\t作者ID\t標題\t\t\t\t日期\r\n");
                out.flush();
                sql.searchbytitle(key,out);
                out.print("輸入文章編號閱讀(輸入/back返回選單 輸入/search搜尋文章 輸入/post發表文章 輸入/del 刪除文章):");
                out.flush();
                id =in.readLine();
                if(id.equals("/back")){
                    return;
                }
                if(id.equals("/search")){
                    search();
                    return;
                }
                if(id.equals("/post")){
                    post();
                    return;
                }
                if(id.equals("/del")){
                    out.print("請輸入欲刪除之文章編號:");
                    out.flush();
                    key=in.readLine();
                    sql.delarticle(user, key, in, out);
                    return;
                }
                sql.printarticle(id, out);
                out.print("按下 b 繼續..\r\n");
                out.flush();
                trash=in.read();
                break;
            default:
                break;
        }
        article();
    }
    
    public String getuser(){
        return user;
    }
    public void setting()throws IOException{
        out.print("(1)個人資料(2)刪除帳號\r\n");
        out.print("請輸入編號前往:");
        out.flush();
        String key=in.readLine();
        switch(key){
            case "1":
                profile();
                break;
            case "2":
                out.print("確定要刪除帳號?(Y/N)\r\n");
                out.flush();
                key=in.readLine();
                if(key.equals("Y") || key.equals("y"))
                    sql.deluser(user);
                in.close();
                out.close();
                socket.close();
                break;
            default:
                break;
                
        }
    }
    public void profile() throws IOException{
        sql.profile(user, out);
        out.print("輸入/back 返回\r\n");
        out.print("輸入/set 修改資料\r\n");
        out.flush();
        String key=in.readLine();
        if(key.equals("/set")){
            out.print("(1)修改密碼(2)修改姓名(3)修改生日\r\n");
            out.print("輸入編號前往\r\n");
            out.flush();
            key=in.readLine();
            switch(key){
            case "1":
                out.print("輸入新密碼:");
                out.flush();
                key=in.readLine();
                out.print("確認新密碼:");
                out.flush();
                String keyn=in.readLine();
                if(!key.equals(keyn)){
                    out.print("新密碼與確認密碼不相符");
                    out.flush();
                    break;
                }
                sql.setpassword(user, key);
                break;
            case "2":
                out.print("輸入新姓名:");
                out.flush();
                key=in.readLine();
                sql.setname(user, key);
                break;
            case "3":
                out.print("輸入新生日:");
                out.flush();
                key=in.readLine();
                if(!(key.length()==10 && key.charAt(4) == '-' && key.charAt(7) == '-')){
                    out.print("生日格式錯誤\r\n");
                    out.flush();
                    break;
                }
                sql.setbirthday(user, key);
                break;
            default:
                break;
            }
        }
    }
}
    

