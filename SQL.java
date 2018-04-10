/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class SQL {
    private String classname = "com.mysql.jdbc.Driver";
    private String jdbcurl = "資料庫連結字串";
    private String sqlusername = "資料庫帳號";
    private String sqlpassword = "資料庫密碼'";
    Connection con=null;
    Statement statement;
    public SQL(){
        try{
            Class.forName(classname).newInstance();
            con = DriverManager.getConnection(jdbcurl,sqlusername,sqlpassword);
            statement=con.createStatement();
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public boolean searchuser(String user,String password){
        try{
            ResultSet rs = statement.executeQuery("Select password from user where user = '"+user+"'");
            rs.next();
            if(rs.getString("password").equals(password)){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            System.out.print(e);
            return false;
        }
    }
    public boolean checkuser(String user){
        try{
            System.out.println("Select * user WHERE user = '"+user+"'");
            ResultSet rs = statement.executeQuery("Select * from user where user = '"+user+"'");
            rs.next();
            if(rs.getString("user").equals(user)){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    public void adduser(String user,String password,String name,String birthday){
        String s="INSERT INTO user(user, password, authority, name, birthday) VALUES ('"+user+"','"+password+"',0,'"+name+"','"+birthday+"')";
        try{
            System.out.println(s);
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void printarticlelist(PrintWriter out){

        try{
            ResultSet rs = statement.executeQuery("Select * from article");
            while(rs.next()){
                out.print(rs.getString("articleID")+"\t"+rs.getString("user")+"\t"+rs.getString("title")+"\t"+rs.getString("date")+"\r\n");
                out.flush();
            }
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void printarticle(String id,PrintWriter out){
       try{
            ResultSet rs = statement.executeQuery("Select * from article where articleID = '"+id+"'");
            rs.next();
            out.print("作者:"+rs.getString("user")+"\r\n");
            out.flush();
            out.print("標題:"+rs.getString("title")+"\r\n");
            out.flush();
            out.print("時間:"+rs.getString("date")+"\r\n\r\n");
            out.flush();
            out.print(rs.getString("article")+"\r\n");
            out.flush();
            }
        catch(Exception e){
            System.out.print(e);
            out.print("查無此文章\r\n");
            out.flush();
        } 
    }
    public void postarticle(String user,String title,String article){
        try{
            Date now=new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = formatter.format(now);
            String s="INSERT INTO article(user, date, title, article) VALUES ('"+user+"','"+date+"','"+title+"','"+article+"')";
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void searchbyuser(String user,PrintWriter out){
        try {
            String s="Select * from article where user = '"+user+"'";
            ResultSet rs=statement.executeQuery(s);
            while(rs.next()){
                out.print(rs.getString("articleID")+"\t"+rs.getString("user")+"\t"+rs.getString("title")+"\t"+rs.getString("date")+"\r\n");
                out.flush();
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    public void searchbytitle(String title,PrintWriter out){
        try {
            String s="Select * from article where title LIKE '%"+title+"%'";
            ResultSet rs=statement.executeQuery(s);
            while(rs.next()){
                out.print(rs.getString("articleID")+"\t"+rs.getString("user")+"\t"+rs.getString("title")+"\t"+rs.getString("date")+"\r\n");
                out.flush();
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    public void profile(String user,PrintWriter out){
        try{
            ResultSet rs = statement.executeQuery("Select * from user where user = '"+user+"'");
            rs.next();
            out.print("帳號:"+rs.getString("user")+"\r\n");
            out.print("姓名:"+rs.getString("name")+"\r\n");
            out.print("生日:"+rs.getString("birthday")+"\r\n");
            out.flush();
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void setpassword(String user,String newpassword){
        try{
            String s="UPDATE `user` SET password = '"+newpassword+"' WHERE user ='"+user+"'";
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void setname(String user,String name){
        try{
            String s="UPDATE `user` SET name = '"+name+"' WHERE user ='"+user+"'";
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void setbirthday(String user,String birthday){
        try{
            String s="UPDATE `user` SET birthday = '"+birthday+"' WHERE user ='"+user+"'";
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void deluser(String user){
        try{
            String s="DELETE FROM `user` WHERE user = '"+user+"'";
            statement.executeUpdate(s);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void delarticle(String user,String articleid,BufferedReader in,PrintWriter out){
        try{
            ResultSet rs=getarticle(articleid);
            if(rs!=null){
                rs.next();
                String articleuser=rs.getString("user");
                if(user.equals(articleuser)){
                    out.print("確定要刪除文章?(Y/N)");
                    out.flush();
                    String key=in.readLine();
                    if(key.equals("Y") || key.equals("y")){
                        String s="DELETE FROM `article` WHERE articleID = '"+articleid+"'";
                        statement.executeUpdate(s);
                    }
                }
                else
                {
                    out.print("您不是文章所有者!\r\n");
                    out.flush();
                    return;
                }
            }
        }
        catch(Exception e){
            System.out.print(e);
            out.print("查無此文章\r\n");
            out.flush();
            return;
        }
    }
    public ResultSet getarticle(String articleid){
        ResultSet rs=null;
        try{

            String s="Select * from article where articleID = '"+articleid+"'";
            rs= statement.executeQuery(s);
             return rs;
        }
        catch(Exception e){
            System.out.print(e);
            return rs;
        }
    }
}
